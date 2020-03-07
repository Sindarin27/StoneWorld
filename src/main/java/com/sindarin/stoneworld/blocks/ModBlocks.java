package com.sindarin.stoneworld.blocks;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.fluids.ModFluids;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, StoneWorld.MOD_ID);

    public static final RegistryObject<Block>
            //Ores
            SULFUR_ORE = BLOCKS.register("sulfur_ore", () ->
            new Block(Block.Properties.create(Material.ROCK)
                    .hardnessAndResistance(3.0F)
                    .harvestLevel(0)
                    .harvestTool(ToolType.PICKAXE)
            )),
            TUNGSTEN_ORE = BLOCKS.register("tungsten_ore", () ->
                    new Block(Block.Properties.create(Material.ROCK)
                            .hardnessAndResistance(4.0F)
                            .harvestLevel(2)
                            .harvestTool(ToolType.PICKAXE)
                    )),
    //Misc blocks
    CARVED_MELON = BLOCKS.register("carved_melon", () ->
            new CarvedMelonBlock(Block.Properties.from(Blocks.CARVED_PUMPKIN))
    ),
            MIXING_BARREL = BLOCKS.register("mixing_barrel", () ->
                    new BlockMixingBarrel(Block.Properties.create(Material.WOOD)
                            .hardnessAndResistance(1.5F)
                            .harvestTool(ToolType.AXE))
            ),
            GUANO_STALACTITE = BLOCKS.register("guano_stalactite", () ->
                    new BlockDrippingStalactite(() -> ModFluids.FLOWING_GUANO)
            ),
    //Fluids
    //TODO: Make transparent
    GUANO = BLOCKS.register("guano", () ->
            new FlowingFluidBlock(() -> ModFluids.FLOWING_GUANO, Block.Properties.create(Material.WATER)
                    .doesNotBlockMovement()
                    .hardnessAndResistance(100F)
                    .noDrops()
            )
    ),
            SOLUTION = BLOCKS.register("solution", () ->
                    new FlowingFluidBlock(() -> ModFluids.FLOWING_SOLUTION, Block.Properties.create(Material.WATER)
                            .doesNotBlockMovement()
                            .hardnessAndResistance(100F)
                            .noDrops()
                    )
            ),
            GRAPE_BUSH = BLOCKS.register("grape_bush", () ->
                    new GrapeBushBlock(Block.Properties.create(Material.PLANTS)
                            .tickRandomly()
                            .doesNotBlockMovement()
                            .sound(SoundType.SWEET_BERRY_BUSH)
                    )
            );

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(GRAPE_BUSH.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(GUANO.get(), RenderType.getWaterMask());
        RenderTypeLookup.setRenderLayer(SOLUTION.get(), RenderType.getWaterMask());
    }
}
