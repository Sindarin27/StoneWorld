package com.sindarin.stoneworld.fluids;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.ModBlocks;
import com.sindarin.stoneworld.items.ModItems;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import javax.annotation.Nullable;

public abstract class FluidGrapeJuice extends ForgeFlowingFluid {

    protected FluidGrapeJuice() {
        super(new Properties(
                        ModFluids.GRAPEJUICE::get,
                        ModFluids.FLOWING_GRAPEJUICE::get,
                        FluidAttributes.builder(
                                new ResourceLocation(StoneWorld.MOD_ID, "block/generalfluid_still"),
                                new ResourceLocation(StoneWorld.MOD_ID, "block/generalfluid_flowing")
                        )
                                .overlay(new ResourceLocation(StoneWorld.MOD_ID, "block/generalfluid_overlay"))
                                .viscosity(3000)
                                .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
                                .color(0x994700a4)
                )
                        .block(() -> (FlowingFluidBlock) ModBlocks.GRAPEJUICE.get())
                        .bucket(ModItems.GRAPEJUICE_BUCKET)
                        .levelDecreasePerBlock(2)
        );
    }

    @Nullable
    @Override
    protected IParticleData getDripParticleData() {
        return new BlockParticleData(ParticleTypes.BLOCK, getBlockState(getDefaultState()));
    }

    @Override
    public boolean isIn(Tag<Fluid> fluidTag) {
        if (fluidTag == FluidTags.WATER) return true; //Pretend we're water for swimming & stuff
        return super.isIn(fluidTag);
    }

    public static class Source extends FluidGrapeJuice {
        public Source() {
        }

        @Override
        public boolean isSource(IFluidState state) {
            return true;
        }

        @Override
        public int getLevel(IFluidState p_207192_1_) {
            return 8;
        }
    }

    public static class Flowing extends FluidGrapeJuice {
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
