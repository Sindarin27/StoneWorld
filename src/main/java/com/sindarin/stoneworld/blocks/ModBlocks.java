package com.sindarin.stoneworld.blocks;

import com.sindarin.stoneworld.StoneWorld;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModBlocks {
    public static final Block sulfur_ore = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        //In here you pass in all block instances you want to register.
        //Make sure you always set the registry name.
        event.getRegistry().registerAll(
                new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F).harvestLevel(0).harvestTool(ToolType.PICKAXE)).setRegistryName(StoneWorld.MOD_ID, "sulfur_ore")
        );
    }
}
