package com.sindarin.stoneworld.recipes.mixingbarrel;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public abstract class BarrelRecipeSerializer<T extends BarrelRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements net.minecraft.item.crafting.IRecipeSerializer<T> {

    BarrelRecipeBaseValues readBaseValues(ResourceLocation recipeId, JsonObject json) {
        String group = JSONUtils.getString(json, "group", "");

        FluidStack fluidIn1 = readFluidStack(json, "fluidIn1");
        FluidStack fluidIn2 = readFluidStack(json, "fluidIn2");
        FluidStack fluidOut = readFluidStack(json, "fluidOut");
        ItemStack itemIn = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "itemIn"), false);
        ItemStack itemOut = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "itemOut"), false);

        return new BarrelRecipeBaseValues(group, fluidIn1, fluidIn2, fluidOut, itemIn, itemOut);
    }


    public FluidStack readFluidStack(JsonObject json, String id) {
        JsonObject fluidJson = JSONUtils.getJsonObject(json, id);
        ResourceLocation fluidRS = ResourceLocation.create(JSONUtils.getString(fluidJson, "fluid", "minecraft:empty"), ':');
        int amount = JSONUtils.getInt(fluidJson, "amount", 1000);
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidRS);

        if (fluid == null) {
            throw new JsonSyntaxException("Unknown fluid '" + fluidRS + "'");
        }

        return new FluidStack(fluid, amount);
    }

    public class BarrelRecipeBaseValues {
        public FluidStack fluid1;
        public FluidStack fluid2;
        public ItemStack inputItem;
        public FluidStack result;
        public ItemStack resultItem;
        public String group;

        BarrelRecipeBaseValues(String group, FluidStack fluid1, FluidStack fluid2, FluidStack result, ItemStack inputItem, ItemStack resultItem) {
            this.group = group;
            this.fluid1 = fluid1;
            this.fluid2 = fluid2;
            this.inputItem = inputItem;
            this.result = result;
            this.resultItem = resultItem;
        }
    }

    @Nullable
    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {

    }
}
