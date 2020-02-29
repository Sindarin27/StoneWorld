package com.sindarin.stoneworld.fluids;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.ModBlocks;
import com.sindarin.stoneworld.items.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class FluidSolution extends ForgeFlowingFluid {

    protected FluidSolution() {
        super(new Properties(
                () -> ModFluids.SOLUTION,
                () -> ModFluids.FLOWING_SOLUTION,
                FluidAttributes.builder(
                        new ResourceLocation(StoneWorld.MOD_ID, "block/guano_still"),
                        new ResourceLocation(StoneWorld.MOD_ID, "block/guano_flowing")
                )
                        .overlay(new ResourceLocation(StoneWorld.MOD_ID, "block/guano_overlay"))
                        .viscosity(1500)
                        .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
                        .color(0xA19C6B)
        )
                .block(() -> ModBlocks.solution)
                .bucket(() -> ModItems.solution_bucket)
                .levelDecreasePerBlock(2)
        );
    }

    @Override
    public boolean isIn(Tag<Fluid> fluidTag) {
        if (fluidTag == FluidTags.WATER) return true; //Pretend we're water for swimming & stuff
        return super.isIn(fluidTag);
    }

    public static class Source extends FluidSolution {
        public Source() {
        }

        @Override
        public boolean isSource(IFluidState state) {
            return true;
        }

        @Override
        public int getLevel(IFluidState p_207192_1_) {
            return 6;
        }
    }

    public static class Flowing extends FluidSolution {
        public Flowing() {
        }

        @Override
        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        @Override
        public int getLevel(IFluidState state) {
            return state.get(LEVEL_1_8);
        }

        @Override
        public boolean isSource(IFluidState state) {
            return false;
        }
    }
}
