package com.sindarin.stoneworld;

import com.sindarin.stoneworld.blocks.ModBlocks;
import com.sindarin.stoneworld.fluids.ModFluids;
import com.sindarin.stoneworld.items.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.sindarin.stoneworld.StoneWorld.MOD_ID;

@Mod(MOD_ID)

public class StoneWorld {
    public static final String MOD_ID = "stoneworld";
    public StoneWorld() {
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModFluids.FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
