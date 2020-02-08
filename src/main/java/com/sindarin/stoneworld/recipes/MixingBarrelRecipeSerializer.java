package com.sindarin.stoneworld.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class MixingBarrelRecipeSerializer<T extends MixingBarrelRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements net.minecraft.item.crafting.IRecipeSerializer<T> {

    final MixingBarrelRecipeSerializer.IFactory<T> factory;

    public MixingBarrelRecipeSerializer(MixingBarrelRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        //Read the recipe
        String group = JSONUtils.getString(json, "group", "");
        ResourceLocation fluid1RS = ResourceLocation.create(JSONUtils.getString(json, "fluid1", "minecraft:empty"), ':');
        int fluid1Amount = JSONUtils.getInt(json, "fluid1Amount", 1000); //Default to one bucket of fluid
        ResourceLocation fluid2RS = ResourceLocation.create(JSONUtils.getString(json, "fluid2", "minecraft:empty"), ':');
        int fluid2Amount = JSONUtils.getInt(json, "fluid2Amount", 1000);
        ResourceLocation fluidResultRS = ResourceLocation.create(JSONUtils.getString(json, "fluidResult", "minecraft:empty"), ':');
        int fluidResultAmount = JSONUtils.getInt(json, "fluidResultAmount", 1000);
        ResourceLocation itemResultRS = ResourceLocation.create(JSONUtils.getString(json, "itemResult", null), ':'); //If no item has been provided, return null. This equals an empty itemStack
        int itemResultAmount = JSONUtils.getInt(json, "itemResultAmount", 1); //Default to one item
        //Create ingredients from it
        FluidStack fluid1 = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid1RS), fluid1Amount);
        FluidStack fluid2 = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid2RS), fluid2Amount);
        FluidStack fluidResult = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidResultRS), fluidResultAmount);
        ItemStack itemResult = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResultRS), itemResultAmount);
        //Return the recipe
        return this.factory.create(recipeId, group, fluid1, fluid2, fluidResult, itemResult);
    }

    @Nullable
    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {

    }


    public interface IFactory<T extends MixingBarrelRecipe> {
        T create(ResourceLocation resourceLocation, String group, FluidStack fluid1, FluidStack fluid2, FluidStack result, ItemStack resultItem);
    }
}
