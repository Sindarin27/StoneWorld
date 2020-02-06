package com.sindarin.stoneworld.blocks.tiles;

import com.sindarin.stoneworld.recipes.MixingBarrelRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public class TileMixingBarrel extends TileEntity implements IForgeTileEntity, IFluidHandler {
    protected FluidTank[] tanks;

    public final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this).cast();
    RecipeWrapper recipeWrapper;

    public TileMixingBarrel()
    {
        super(ModTiles.mixing_barrel);
        recipeWrapper = new RecipeWrapper(new ItemStackHandler()); //Get ourselves a useless always-empty recipe wrapper that's just needed for requesting recipes
        //Make our two tanks
        tanks = new FluidTank[] {new FluidTank(10000), new FluidTank(10000)};
    }

    //Find out how many tanks this tile has
    @Override
    public int getTanks() {
        return tanks.length;
    }

    //Get the fluid that is in tank [tank]
    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return tanks[tank].getFluid();
    }

    //Find out how many mb of fluid are allowed to be in tank [tank]
    @Override
    public int getTankCapacity(int tank) {
        return tanks[tank].getCapacity();
    }

    //Check whether the fluid [stack] can be inserted into tank [tank]
    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tanks[tank].isFluidValid(stack);
    }

    //(Attempt to) fill [resource] into the tanks
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int tankToFill = -1;

        //Find the tank to fill
        for (int i = 0; i < tanks.length; i++) {
            IFluidTank tank = tanks[i];
            //Only fill if a tank can take this fluid, and it's the same fluid it contains (or if it's empty), and if it's not full yet
            if (tank.isFluidValid(resource)
                    && (tank.getFluid().isFluidEqual(resource) || tank.getFluidAmount() == 0)
                    && (tank.getCapacity() > tank.getFluidAmount())
            ) {
                tankToFill = i;
                break;
            }
        }
        //If any tank has been found, fill it
        if (tankToFill != -1) {
            markDirty();
            world.notifyBlockUpdate(pos, this.getBlockState(), this.getBlockState(), 0); //Notify that this block updated (though its state did not change)
            return tanks[tankToFill].fill(resource, action);
        }
        //No tank has been found, return that we did not do anything
        return 0;
    }

    //(Attempt to) drain the fluid [resource]
    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        int tankToDrain = -1;

        //Find the tank to drain by FILO principle
        for (int i = tanks.length - 1; i >= 0; i--) {
            IFluidTank tank = tanks[i];
            if (tank.getFluid().isFluidEqual(resource)) {
                tankToDrain = i;
                break;
            }
        }
        //If any tank has been found, drain it
        if (tankToDrain != -1) {
            markDirty();
            world.notifyBlockUpdate(pos, this.getBlockState(), this.getBlockState(), 0); //Notify that this block updated (though its state did not change)
            return tanks[tankToDrain].drain(resource, action);
        }
        //No tank has been found, return that we did not do anything
        return FluidStack.EMPTY;
    }

    //(Attempt to) drain [maxDrain] mb of fluid
    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int tankToDrain = -1;

        //Find the tank to drain by FILO principle
        for (int i = tanks.length - 1; i >= 0; i--) {
            IFluidTank tank = tanks[i];
            if (tank.getFluidAmount() > 0) {
                tankToDrain = i;
                break;
            }
        }
        //If any tank has been found, drain it
        if (tankToDrain != -1) {
            markDirty();
            world.notifyBlockUpdate(pos, this.getBlockState(), this.getBlockState(), 0); //Notify that this block updated (though its state did not change)
            return tanks[tankToDrain].drain(maxDrain, action);
        }
        //No tank has been found, return that we did not do anything
        return FluidStack.EMPTY;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        for (int i = 0; i < tanks.length; i++) {
            tanks[i].readFromNBT(compound.getCompound("tank"+i));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        for (int i = 0; i < tanks.length; i++) {
            compound.put("tank"+i, tanks[i].writeToNBT(new CompoundNBT()));
        }
        return compound;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHandler.cast();
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        SUpdateTileEntityPacket packet = new SUpdateTileEntityPacket(this.pos, this.getType().hashCode(), this.write(new CompoundNBT()));
        return packet;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        CompoundNBT compound = packet.getNbtCompound();
        this.read(compound);
    }

    @Override
    public net.minecraft.nbt.CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    @Override
    public boolean hasFastRenderer() { return true; }

    //Get the total capacity of the TE (aka the capacity of all tanks combined)
    public int getTotalCapacity() {
        int totalCapacity = 0;
        for (FluidTank tank : tanks) {
            totalCapacity += tank.getCapacity(); //Add the capacity of each tank
        }
        return totalCapacity;
    }

    public boolean doRecipe() {
        List<MixingBarrelRecipe> recipes = world.getRecipeManager().getRecipes(MixingBarrelRecipe.mixing_barrel, recipeWrapper, world); //Get a list of the possible recipes for a mixing barrel
        for (MixingBarrelRecipe recipe : recipes) { //Find a matching recipe from the recipe list
            if (recipe.matches(tanks[0].getFluid(), tanks[1].getFluid())) {
                //Find out what fluid we get as a result
                FluidStack result = recipe.getResult(tanks[0].getFluid(), tanks[1].getFluid());
                //Empty both tanks
                tanks[0].setFluid(FluidStack.EMPTY);
                tanks[1].setFluid(FluidStack.EMPTY);
                //Fill tanks with result
                int restAmount = result.getAmount() - this.fill(result, FluidAction.EXECUTE); //Fill the first tank with the result
                if (restAmount > 0) { //If there's more than 1 tank worth of result, fill the second tank with the rest
                    result.setAmount(restAmount);
                    this.fill(result, FluidAction.EXECUTE);
                }
                //Done!
                return true;
            }
        }
        return false; //No recipe done, return false
    }
}
