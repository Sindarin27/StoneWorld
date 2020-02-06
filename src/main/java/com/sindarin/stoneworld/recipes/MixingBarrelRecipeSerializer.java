package com.sindarin.stoneworld.recipes;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.recipebook.RecipeWidget;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.DatagramPacket;
import java.util.Map;

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

        //Create fluids from it
        FluidStack fluid1 = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid1RS), fluid1Amount);
        FluidStack fluid2 = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid2RS), fluid2Amount);
        Fluid fluidResult = ForgeRegistries.FLUIDS.getValue(fluidResultRS);

        //Return the recipe
        return this.factory.create(recipeId, group, fluid1, fluid2, fluidResult);
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
        T create(ResourceLocation resourceLocation, String group, FluidStack fluid1, FluidStack fluid2, Fluid result);
    }
}
