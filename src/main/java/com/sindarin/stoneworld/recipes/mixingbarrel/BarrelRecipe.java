package com.sindarin.stoneworld.recipes.mixingbarrel;

import com.sindarin.stoneworld.recipes.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;


public abstract class BarrelRecipe implements IRecipe<RecipeWrapper> {
    //public static final IRecipeType<BarrelRecipe> mixing_barrel = IRecipeType.register("mixing_barrel");
    final IRecipeType<?> type;
    final ResourceLocation id;
    final String group;
    public FluidStack fluid1;
    public FluidStack fluid2;
    public ItemStack inputItem;
    public FluidStack fluidOut;
    public ItemStack resultItem;

    public BarrelRecipe(IRecipeType<BarrelRecipe> type, ResourceLocation id, String group, FluidStack fluid1, FluidStack fluid2, ItemStack inputItem, FluidStack fluidOut, ItemStack resultItem) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.fluid1 = fluid1;
        this.fluid2 = fluid2;
        this.inputItem = inputItem;
        this.fluidOut = fluidOut;
        this.resultItem = resultItem;
    }

    @Override
    public ItemStack getCraftingResult(RecipeWrapper inv) {
        return resultItem;
    } //Our recipe gives the result item

    @Override
    public boolean canFit(int width, int height) { return true; } //Mixing barrel recipes always fit in a grid even though no grid exists

    @Override
    public ItemStack getRecipeOutput() {
        return resultItem;
    } //Like I said, it gives the result item

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public IRecipeSerializer<?> getSerializer() { return ModRecipes.MIXING_BARREL; }

    @Override
    public IRecipeType<?> getType() { return type; }

    public abstract MixingBarrelOutput getResult(FluidStack fluid, FluidStack fluid1);

    public abstract boolean matches(FluidStack fluid, FluidStack fluid1, ItemStack item);
}