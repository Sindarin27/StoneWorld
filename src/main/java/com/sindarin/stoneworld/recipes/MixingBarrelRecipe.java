package com.sindarin.stoneworld.recipes;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Resource;

public class MixingBarrelRecipe implements IRecipe<RecipeWrapper> {
    public static final IRecipeType<MixingBarrelRecipe> mixing_barrel = IRecipeType.register("mixing_barrel");
    final IRecipeType<?> type;
    final ResourceLocation id;
    final String group;
    public FluidStack fluid1;
    public FluidStack fluid2;
    public Fluid result;

    public MixingBarrelRecipe(ResourceLocation id, String group, FluidStack fluid1, FluidStack fluid2, Fluid result) {
        type = mixing_barrel;
        this.id = id;
        this.group = group;
        this.fluid1 = fluid1;
        this.fluid2 = fluid2;
        this.result = result;
    }

    @Override
    public boolean matches(RecipeWrapper inv, World worldIn) { return true; } //Our recipe always matches if requested this way, used for finding a recipe

    @Override
    public ItemStack getCraftingResult(RecipeWrapper inv) { return ItemStack.EMPTY; } //But it just gives an empty item

    @Override
    public boolean canFit(int width, int height) { return true; } //Mixing barrel recipes always fit in a grid even though no grid exists

    @Override
    public ItemStack getRecipeOutput() { return ItemStack.EMPTY; } //And like I said, it just gives an empty item

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public IRecipeSerializer<?> getSerializer() { return ModRecipes.MIXING_BARREL; }

    @Override
    public IRecipeType<?> getType() { return type; }

    public boolean matches(FluidStack testFluid1, FluidStack testFluid2) {
        boolean fluid1Correct = fluid1.isFluidEqual(testFluid1);
        boolean fluid2Correct = fluid2.isFluidEqual(testFluid2);

        //If one fluid is incorrect, then maybe we're checking them in the wrong order
        if (!fluid1Correct || !fluid2Correct) {
            FluidStack tempSwapStack = testFluid1; testFluid1 = testFluid2; testFluid2 = tempSwapStack; //Do a switcheroo and then check them again
            fluid1Correct = fluid1.isFluidEqual(testFluid1);
            fluid2Correct = fluid2.isFluidEqual(testFluid2);
        }

        //We don't care about the exact amount, but about the ratio. If fluid1/fluid2 is the same as testFluid1/testFluid2, the recipe is equal
        float correctRatio = (float)fluid1.getAmount() / fluid2.getAmount();
        float testRatio = (float)testFluid1.getAmount() / testFluid2.getAmount();
        boolean ratioCorrect = (correctRatio == testRatio);
        return (fluid1Correct && fluid2Correct && ratioCorrect);
    }

    public FluidStack getResult(FluidStack fluid1, FluidStack fluid2) {
        //We could do a check on whether this recipe is correct, but let's just assume whoever is activating this method knows what they're doing
        int amount = fluid1.getAmount() + fluid2.getAmount();
        return new FluidStack(result, amount);
    }
}
