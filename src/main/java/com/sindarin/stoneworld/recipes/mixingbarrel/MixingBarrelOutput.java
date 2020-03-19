package com.sindarin.stoneworld.recipes.mixingbarrel;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MixingBarrelOutput {
    public ItemStack itemOut;
    public FluidStack fluidOut;
    public int itemConsumeAmount;

    public MixingBarrelOutput(ItemStack itemOut, FluidStack fluidOut, int itemConsumeAmount) {
        this.itemOut = itemOut;
        this.fluidOut = fluidOut;
        this.itemConsumeAmount = itemConsumeAmount;
    }
}
