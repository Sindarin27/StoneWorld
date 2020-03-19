package com.sindarin.stoneworld.recipes.mixingbarrel;

import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class FluidMixingRecipe extends BarrelRecipe {
    public static final IRecipeType<BarrelRecipe> recipeType = IRecipeType.register("barrel_fluid_mixing");
    private final boolean caresAboutItem; //If false, mix even if there is still an item inside

    public FluidMixingRecipe(ResourceLocation id, String group, FluidStack fluid1, FluidStack fluid2, ItemStack inputItem, FluidStack result, ItemStack resultItem, boolean caresAboutItem) {
        super(recipeType, id, group, fluid1, fluid2, inputItem, result, resultItem);
        this.caresAboutItem = caresAboutItem;
    }


    @Override
    public boolean matches(RecipeWrapper inv, World worldIn) {
        return true;
    }//inv.getStackInSlot(0).isItemEqual(inputItem); } //Our recipe matches if the item is the same, no matter the count

    public boolean matches(FluidStack testFluid1, FluidStack testFluid2, ItemStack testItem) {
        boolean fluid1Correct = fluid1.isFluidEqual(testFluid1);
        boolean fluid2Correct = fluid2.isFluidEqual(testFluid2);
        boolean itemCorrect = !caresAboutItem || inputItem.isItemEqual(testItem) || (inputItem.isEmpty() && testItem.isEmpty());

        //If one fluid is incorrect, then maybe we're checking them in the wrong order
        if (!fluid1Correct || !fluid2Correct) {
            FluidStack tempSwapStack = testFluid1;
            testFluid1 = testFluid2;
            testFluid2 = tempSwapStack; //Do a switcheroo and then check them again
            fluid1Correct = fluid1.isFluidEqual(testFluid1);
            fluid2Correct = fluid2.isFluidEqual(testFluid2);
        }

        //We don't care about the exact amount, but about the ratio. If fluid1/fluid2 is the same as testFluid1/testFluid2, the recipe is equal
        float correctFluidRatio = (float) fluid1.getAmount() / fluid2.getAmount();
        float correctItemRatio = (float) inputItem.getCount() / fluid2.getAmount();
        float testFluidRatio = (float) testFluid1.getAmount() / testFluid2.getAmount();
        float testItemRatio = (float) testItem.getCount() / testFluid2.getAmount();
        boolean fluidRatioCorrect = (correctFluidRatio == testFluidRatio) || fluid1.getFluid() == Fluids.EMPTY || fluid2.getFluid() == Fluids.EMPTY;
        boolean ItemRatioCorrect = !caresAboutItem || (correctItemRatio == testItemRatio) || inputItem == ItemStack.EMPTY;
        return (fluid1Correct && fluid2Correct && itemCorrect && fluidRatioCorrect && ItemRatioCorrect);
    }

    public MixingBarrelOutput getResult(FluidStack fluid1Input, FluidStack fluid2Input) {
        //We could do a check on whether this recipe is correct, but let's just assume whoever is activating this method knows what they're doing
        int amountInput = fluid1Input.getAmount() + fluid2Input.getAmount();
        float resultMultiplier = (float) amountInput / (fluid1.getAmount() + fluid2.getAmount());
        int fluidAmountOutput = (int) Math.floor(fluidOut.getAmount() * resultMultiplier);
        int itemAmountOutput = (int) Math.floor(resultItem.getCount() * resultMultiplier);

        FluidStack fluidOutput = new FluidStack(fluidOut.getFluid(), fluidAmountOutput);
        ItemStack itemOutput = new ItemStack(resultItem.getItem(), itemAmountOutput);

        return new MixingBarrelOutput(itemOutput, fluidOutput, 0);
    }

    public static class Serializer<T extends FluidMixingRecipe> extends BarrelRecipeSerializer {
        private final IFactory factory;

        public Serializer(IFactory factory) {
            this.factory = factory;
        }

        @Override
        public FluidMixingRecipe read(ResourceLocation recipeId, JsonObject json) {
            boolean caresAboutItem = JSONUtils.getBoolean(json, "caresAboutItems");
            BarrelRecipeBaseValues baseValues = readBaseValues(recipeId, json);

            return factory.create(recipeId, baseValues.group, baseValues.fluid1, baseValues.fluid2, baseValues.inputItem, baseValues.result, baseValues.resultItem, caresAboutItem);
        }

        public interface IFactory<T extends FluidMixingRecipe> {
            T create(ResourceLocation resourceLocation, String group, FluidStack fluid1, FluidStack fluid2, ItemStack itemIn, FluidStack fluidOut, ItemStack resultItem, boolean caresAboutItem);
        }

    }

}
