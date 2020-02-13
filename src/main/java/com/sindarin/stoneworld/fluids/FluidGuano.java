package com.sindarin.stoneworld.fluids;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.ModBlocks;
import com.sindarin.stoneworld.items.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class FluidGuano extends ForgeFlowingFluid {

    protected FluidGuano() {
        super(new Properties(
                () -> ModFluids.GUANO,
                () -> ModFluids.FLOWING_GUANO,
                FluidAttributes.builder(
                        new ResourceLocation(StoneWorld.MOD_ID, "block/guano_still"),
                        new ResourceLocation(StoneWorld.MOD_ID, "block/guano_flowing")
                )
                .viscosity(800)
                .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
                )
                .block(() -> ModBlocks.guano));
    }

    @Override
    public Item getFilledBucket() {
        return ModItems.guano_bucket;
    }

    public static class Source extends FluidGuano
    {
        public Source()
        { }

        @Override
        public boolean isSource(IFluidState state)
        {
            return true;
        }

        @Override
        public int getLevel(IFluidState p_207192_1_)
        {
            return 8;
        }
    }

    public static class Flowing extends FluidGuano
    {
        public Flowing()
        { }

        @Override
        protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder)
        {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        @Override
        public int getLevel(IFluidState state)
        {
            return state.get(LEVEL_1_8);
        }

        @Override
        public boolean isSource(IFluidState state)
        {
            return false;
        }
    }
}
