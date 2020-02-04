package com.sindarin.stoneworld.blocks.tiles;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class TileMixingBarrel extends TileEntity implements IFluidHandler {
    protected FluidTank[] tanks;

    public final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this).cast();

    public TileMixingBarrel()
    {
        super(ModTiles.mixing_barrel);

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

    @Nullable @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHandler.cast();
        }

        return super.getCapability(capability, facing);
    }
}
