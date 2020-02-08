package com.sindarin.stoneworld.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MixingBarrelOutput {
    public ItemStack resultItem;
    public FluidStack resultFluid;
    public MixingBarrelOutput(ItemStack resultItem, FluidStack resultFluid) {
        this.resultItem  = resultItem;
        this.resultFluid = resultFluid;
    }
}
