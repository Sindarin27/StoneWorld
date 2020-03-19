package com.sindarin.stoneworld.recipes.mixingbarrel;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ItemStompingRecipe extends BarrelRecipe {
    public static final IRecipeType<BarrelRecipe> recipeType = IRecipeType.register("barrel_item_stomping");
    public final int stompsNeeded;

    public ItemStompingRecipe(ResourceLocation id, String group, FluidStack fluid1, FluidStack fluid2, ItemStack inputItem, FluidStack result, ItemStack resultItem, int stompsNeeded) {
        super(recipeType, id, group, fluid1, fluid2, inputItem, result, resultItem);
        this.stompsNeeded = stompsNeeded;
    }

    @Override
    public MixingBarrelOutput getResult(FluidStack fluidIn1, FluidStack fluidIn2) {
        FluidStack fluidOut = new FluidStack(this.fluidOut.getFluid(), fluidIn1.getAmount() + fluidIn2.getAmount() + this.fluidOut.getAmount());
        return new MixingBarrelOutput(resultItem, fluidOut, inputItem.getCount());
    }

    @Override
    public boolean matches(FluidStack fluidIn1, FluidStack fluidIn2, ItemStack itemIn) {
        boolean correctFluidOrEmpty1 = fluidIn1.isEmpty() || fluidIn1.isFluidEqual(this.fluidOut);
        boolean correctFluidOrEmpty2 = fluidIn2.isEmpty() || fluidIn2.isFluidEqual(this.fluidOut);
        return correctFluidOrEmpty1 && correctFluidOrEmpty2 && itemIn.isItemEqual(inputItem);
    }

    @Override
    public boolean matches(RecipeWrapper inv, World worldIn) {
        return inv.getStackInSlot(0).isItemEqual(inputItem);
    }

    public static class Serializer<T extends ItemStompingRecipe> extends BarrelRecipeSerializer {
        private final IFactory factory;

        public Serializer(IFactory factory) {
            this.factory = factory;
        }

        @Override
        public ItemStompingRecipe read(ResourceLocation recipeId, JsonObject json) {
            BarrelRecipeBaseValues baseValues = readBaseValues(recipeId, json);
            int stompsNeeded = JSONUtils.getInt(json, "stompsNeeded", 4);
            return factory.create(recipeId, baseValues.group, baseValues.fluid1, baseValues.fluid2, baseValues.inputItem, baseValues.result, baseValues.resultItem, stompsNeeded);
        }

        public interface IFactory<T extends ItemStompingRecipe> {
            T create(ResourceLocation resourceLocation, String group, FluidStack fluid1, FluidStack fluid2, ItemStack itemIn, FluidStack fluidOut, ItemStack resultItem, int stompsNeeded);
        }

    }
}
