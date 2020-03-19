package com.sindarin.stoneworld.fluids;

import com.sindarin.stoneworld.StoneWorld;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, StoneWorld.MOD_ID);
    public static final RegistryObject<Fluid>
            GUANO = FLUIDS.register("guano", FluidGuano.Source::new),
            SOLUTION = FLUIDS.register("solution", FluidSolution.Source::new),
            GRAPEJUICE = FLUIDS.register("grapejuice", FluidGrapeJuice.Source::new);
    public static final RegistryObject<FlowingFluid>
            FLOWING_GUANO = FLUIDS.register("flowing_guano", FluidGuano.Flowing::new),
            FLOWING_SOLUTION = FLUIDS.register("flowing_solution", FluidSolution.Flowing::new),
            FLOWING_GRAPEJUICE = FLUIDS.register("flowing_grapejuice", FluidGrapeJuice.Flowing::new);
}
