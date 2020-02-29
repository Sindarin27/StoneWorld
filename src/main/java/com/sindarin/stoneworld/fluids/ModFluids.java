package com.sindarin.stoneworld.fluids;

import com.sindarin.stoneworld.StoneWorld;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModFluids {
    public static final Fluid GUANO = null, SOLUTION = null;
    public static final FlowingFluid FLOWING_GUANO = null, FLOWING_SOLUTION = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Fluid> event) {
        event.getRegistry().registerAll(
                new FluidGuano.Source().setRegistryName(StoneWorld.MOD_ID, "guano"),
                new FluidGuano.Flowing().setRegistryName(StoneWorld.MOD_ID, "flowing_guano"),
                new FluidSolution.Source().setRegistryName(StoneWorld.MOD_ID, "solution"),
                new FluidSolution.Flowing().setRegistryName(StoneWorld.MOD_ID, "flowing_solution")
        );
    }
}
