package com.sindarin.stoneworld.items;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.ModBlocks;
import com.sindarin.stoneworld.fluids.ModFluids;
import com.sindarin.stoneworld.util.ItemTab;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModItems {
    //Items
    public static final Item sulfur_dust = null;
    public static final Item tungsten_ingot = null;
    public static final Item tungsten_dust = null;

    //Buckets
    public static final Item guano_bucket = null, solution_bucket = null;
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
                new BlockItem(ModBlocks.tungsten_ore, new BlockItem.Properties().group(tab)).setRegistryName(ModBlocks.tungsten_ore.getRegistryName()),
                new BlockItem(ModBlocks.mixing_barrel, new BlockItem.Properties().group(tab)).setRegistryName(ModBlocks.mixing_barrel.getRegistryName()),
                new BlockItem(ModBlocks.guano_stalactite, new BlockItem.Properties().group(tab)).setRegistryName(ModBlocks.guano_stalactite.getRegistryName()),

                //Buckets for fluids
                new BucketItem(() -> ModFluids.GUANO, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(tab)).setRegistryName(StoneWorld.MOD_ID, "guano_bucket"),
                new BucketItem(() -> ModFluids.SOLUTION, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(tab)).setRegistryName(StoneWorld.MOD_ID, "solution_bucket")
        );

        tab.createIcon();
    }
}
