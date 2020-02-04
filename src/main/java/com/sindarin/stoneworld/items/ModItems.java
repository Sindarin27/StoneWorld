package com.sindarin.stoneworld.items;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.ModBlocks;
import com.sindarin.stoneworld.util.ItemTab;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Dictionary;
import java.util.List;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModItems {
    public static final Item sulfur_dust = null;
    public static List<Item> items;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ItemGroup tab = new ItemTab();

        event.getRegistry().registerAll(
                //Resource items
                new Item(new Item.Properties().group(tab)).setRegistryName(StoneWorld.MOD_ID, "sulfur_dust"),
                new Item(new Item.Properties().group(tab)).setRegistryName(StoneWorld.MOD_ID, "tungsten_ingot"),
                new Item(new Item.Properties().group(tab)).setRegistryName(StoneWorld.MOD_ID, "tungsten_dust"),

                //Blocks for items
                new BlockItem(ModBlocks.sulfur_ore, new BlockItem.Properties().group(tab)).setRegistryName(ModBlocks.sulfur_ore.getRegistryName()),
                new BlockItem(ModBlocks.tungsten_ore, new BlockItem.Properties().group(tab)).setRegistryName(ModBlocks.tungsten_ore.getRegistryName())
        );

        tab.createIcon();
    }
}
