package com.sindarin.stoneworld.blocks.tiles;

import com.sindarin.stoneworld.blocks.BlockMixingBarrel;
import com.sindarin.stoneworld.recipes.mixingbarrel.BarrelRecipe;
import com.sindarin.stoneworld.recipes.mixingbarrel.FluidMixingRecipe;
import com.sindarin.stoneworld.recipes.mixingbarrel.ItemStompingRecipe;
import com.sindarin.stoneworld.recipes.mixingbarrel.MixingBarrelOutput;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public class TileMixingBarrel extends TileEntity implements IForgeTileEntity, IFluidHandler, IItemHandler, ITickableTileEntity {
    private final int particlesPerFluidCount = 4; //How many particles to spawn per fluid on mixing
    private final float timeBetweenStomps = 0.2f; //How much time should be between two stomps
    private final float TICK = 1 / 20f;
    private FluidTank[] tanks;
    private ItemStackHandler itemReturnStack;

    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this).cast();
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> this).cast();
    private RecipeWrapper recipeWrapper;

    private float timeSinceLastStomp;
    private float progress;
    public TileMixingBarrel()
    {
        super(ModTiles.MIXING_BARREL);
        itemReturnStack = new ItemStackHandler();
        recipeWrapper = new RecipeWrapper(itemReturnStack);
        //Make our two tanks
        tanks = new FluidTank[] {new FluidTank(10000), new FluidTank(10000)};
    }

    //Call when an entity is in the barrel to 'crush' the contents
    public void stomp() {
        if (timeSinceLastStomp > timeBetweenStomps) {
            BarrelRecipe recipe = getRecipe();
            if (recipe instanceof ItemStompingRecipe) {
                progress++;
                if (((ItemStompingRecipe) recipe).stompsNeeded <= progress) {
                    getWorld().playSound(null, getPos(), SoundEvents.field_226132_ag_, SoundCategory.BLOCKS, 1, 1);
                    MixingBarrelOutput result = recipe.getResult(tanks[0].getFluid(), tanks[1].getFluid());
                    HandleOutput(result);
                    progress = 0;
                } else {
                    getWorld().playSound(null, getPos(), SoundEvents.field_226130_ae_, SoundCategory.BLOCKS, 1, 1);
                }
            }
        }
        timeSinceLastStomp = 0f;
    }

    @Override
    public void tick() {
        timeSinceLastStomp += TICK;
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

    //Synchronize blockstate, light level, etc
    private void Synchronize() {
        markDirty();
        BlockState oldBlockState = getBlockState();
        BlockState newBlockState = getBlockState().with(BlockMixingBarrel.lightLevel, getLightValueFromTop(1));
        if (world != null) {
            world.notifyBlockUpdate(pos, oldBlockState, newBlockState, 3);
            world.setBlockState(pos, newBlockState, 3); //Notify that this block updated with its new light value
        }
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
            int filled = tanks[tankToFill].fill(resource, action);
            Synchronize();
            return filled;
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
            FluidStack drained = tanks[tankToDrain].drain(resource, action);
            Synchronize();
            return drained;
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
            FluidStack drained = tanks[tankToDrain].drain(maxDrain, action);
            Synchronize();
            return drained;
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
        itemReturnStack.deserializeNBT(compound.getCompound("itemSlot"));
        progress = compound.getFloat("progress");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        for (int i = 0; i < tanks.length; i++) {
            compound.put("tank"+i, tanks[i].writeToNBT(new CompoundNBT()));
        }
        compound.put("itemSlot", itemReturnStack.serializeNBT());
        compound.putFloat("progress", progress);
        return compound;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHandler.cast();
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
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
    public int getSlots() {
        return itemReturnStack.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return itemReturnStack.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        Synchronize();
        return itemReturnStack.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        Synchronize();
        return itemReturnStack.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return itemReturnStack.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return itemReturnStack.isItemValid(slot, stack);
    }

    public int getLightValueFromTop(int levelFromTop) {
        if (levelFromTop > tanks.length) return 0; //The bottom of the barrel does not give off light
        Fluid fluid = tanks[tanks.length - levelFromTop].getFluid().getFluid();
        if (fluid == Fluids.EMPTY) return getLightValueFromTop(levelFromTop + 1); //If empty, move one layer down
        int fluidLum = fluid.getAttributes().getLuminosity();
        if (fluidLum == 0) return (int)Math.floor(0.6 * getLightValueFromTop(levelFromTop + 1)); //If this fluid doesn't give off light, then reduce the light for the next one that does give off light
        return fluidLum;
    }

    public void HandleOutput(MixingBarrelOutput output) {
        FluidStack resultFluid = output.fluidOut;
        ItemStack resultItem = output.itemOut;

        //Change the block if this is the server
        if (!world.isRemote) {
            //Remove as many items as specified
            itemReturnStack.extractItem(0, output.itemConsumeAmount, false);
            //Empty both tanks
            tanks[0].setFluid(FluidStack.EMPTY);
            tanks[1].setFluid(FluidStack.EMPTY);
            //Fill as long as fluid remains to be filled and there is still room for extra fluid
            while (resultFluid.getAmount() > 0 && this.EmptyTankRemaining()) {
                resultFluid.setAmount(resultFluid.getAmount() - this.fill(resultFluid, FluidAction.EXECUTE));
            }
            //Set the itemReturnStack's item to the resulted item, and drop any extras
            ItemStack restItem = itemReturnStack.insertItem(0, resultItem, false);
            ItemEntity item = new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, restItem);
            world.addEntity(item);
        }
        //If not a server, spam particles and play sound
        else {
            Random random = new Random();
            for (int i = 0; i < particlesPerFluidCount; i++) {
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, tanks[0].getFluid().getFluid().getDefaultState().getBlockState()), pos.getX() + random.nextDouble(), pos.getY() + 1, pos.getZ() + random.nextDouble(), 0, random.nextDouble(), 0);
            }
            for (int i = 0; i < particlesPerFluidCount; i++) {
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, tanks[1].getFluid().getFluid().getDefaultState().getBlockState()), pos.getX() + random.nextDouble(), pos.getY() + 1, pos.getZ() + random.nextDouble(), 0, random.nextDouble(), 0);
            }
            for (int i = 0; i < particlesPerFluidCount; i++) {
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, resultFluid.getFluid().getDefaultState().getBlockState()), pos.getX() + random.nextDouble(), pos.getY() + 1, pos.getZ() + random.nextDouble(), 0, random.nextDouble(), 0);
            }
        }
        //Changed contents of the barrel, so synchronize
        Synchronize();
    }

    public ActionResultType doRecipe() {
        BarrelRecipe recipe = getRecipe();
        //If a mixing recipe is inserted, execute the recipe
        if (recipe instanceof FluidMixingRecipe) {
            //Find out what fluid we get as a result
            MixingBarrelOutput result = recipe.getResult(tanks[0].getFluid(), tanks[1].getFluid());
            HandleOutput(result);
            world.playSound(null, getPos(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.BLOCKS, 0.5F, 1);
            //Done!
                return ActionResultType.SUCCESS;
            }
        return ActionResultType.FAIL; //Failed doing any recipe, but still give success as we finished handling
    }

    private BarrelRecipe getRecipe() {
        //Generate a list of all recipes for this block
        List<BarrelRecipe> recipes = world.getRecipeManager().getRecipes(FluidMixingRecipe.recipeType, recipeWrapper, world);
        recipes.addAll(world.getRecipeManager().getRecipes(ItemStompingRecipe.recipeType, recipeWrapper, world));
        for (BarrelRecipe recipe : recipes) { //Find a matching recipe from the recipe list
            if (recipe.matches(tanks[0].getFluid(), tanks[1].getFluid(), itemReturnStack.getStackInSlot(0))) {
                return recipe;
            }
        }
        return null;
    }

    boolean EmptyTankRemaining() {
        for (FluidTank tank:tanks) {
            if (tank.isEmpty()) { return true; } //If any tank is empty, then yes, there is an empty tank
        }
        return false; //None of the tanks returned true, so no tank is empty
    }

    //Get the total capacity of the TE (aka the capacity of all tanks combined)
    public int getTotalCapacity() {
        int totalCapacity = 0;
        for (FluidTank tank : tanks) {
            totalCapacity += tank.getCapacity(); //Add the capacity of each tank
        }
        return totalCapacity;
    }
}
