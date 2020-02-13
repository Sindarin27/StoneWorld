package com.sindarin.stoneworld.fluids;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import com.sindarin.stoneworld.client.render.MixingBarrelRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModFluids {
    public static final Fluid GUANO = new FluidGuano.Source();
    public static final FlowingFluid FLOWING_GUANO = new FluidGuano.Flowing();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Fluid> event) {
        event.getRegistry().registerAll(
                new FluidGuano.Source().setRegistryName(StoneWorld.MOD_ID, "guano"),
                new FluidGuano.Flowing().setRegistryName(StoneWorld.MOD_ID, "flowing_guano")
        );
    }
}
