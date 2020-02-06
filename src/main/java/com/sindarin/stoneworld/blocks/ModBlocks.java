package com.sindarin.stoneworld.blocks;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import com.sindarin.stoneworld.client.tiles.MixingBarrelRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModBlocks {
    public static final Block sulfur_ore = null;
    public static final Block tungsten_ore = null;
    public static final BlockMixingBarrel mixing_barrel = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                //Sulfur
                new Block(Block.Properties.create(Material.ROCK)
                        .hardnessAndResistance(3.0F)
                        .harvestLevel(0)
                        .harvestTool(ToolType.PICKAXE)
                ).setRegistryName(StoneWorld.MOD_ID, "sulfur_ore"),
                //Tungsten
                new Block(Block.Properties.create(Material.ROCK)
                        .hardnessAndResistance(4.0F)
                        .harvestLevel(2)
                        .harvestTool(ToolType.PICKAXE)
                ).setRegistryName(StoneWorld.MOD_ID, "tungsten_ore"),
                //Mixing Barrel
                new BlockMixingBarrel().setRegistryName(StoneWorld.MOD_ID, "mixing_barrel")
        );
    }

    @SubscribeEvent
    public static void registerTESR(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileMixingBarrel.class, new MixingBarrelRenderer());
    }
}
