package com.sindarin.stoneworld.items;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.ModBlocks;
import com.sindarin.stoneworld.fluids.ModFluids;
import com.sindarin.stoneworld.util.ItemTab;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, StoneWorld.MOD_ID);
    private static final ItemGroup tab = new ItemTab();
    //Items
    //Resource items
    public static final RegistryObject<Item>
            SULFUR_DUST = ITEMS.register("sulfur_dust", () -> new Item(new Item.Properties().group(tab))),
            TUNGSTEN_INGOT = ITEMS.register("tungsten_ingot", () -> new Item(new Item.Properties().group(tab))),
            TUNGSTEN_DUST = ITEMS.register("tungsten_dust", () -> new Item(new Item.Properties().group(tab))),
    //Petrification item
            MEDUSA = ITEMS.register("medusa", () -> new PetrificationItem(new Item.Properties().group(tab).maxStackSize(1))),
    //Food items
            GRAPES = ITEMS.register("grapes", () -> new BlockNamedItem(ModBlocks.GRAPE_BUSH.get(), new Item.Properties().group(tab).food(Foods.SWEET_BERRIES))),
    //Block items
            TUNGSTEN_ORE = ITEMS.register("tungsten_ore", () -> new BlockItem(ModBlocks.TUNGSTEN_ORE.get(), new BlockItem.Properties().group(tab))),
            SULFUR_ORE = ITEMS.register("sulfur_ore", () -> new BlockItem(ModBlocks.SULFUR_ORE.get(), new BlockItem.Properties().group(tab))),
            MIXING_BARREL = ITEMS.register("mixing_barrel", () -> new BlockItem(ModBlocks.MIXING_BARREL.get(), new BlockItem.Properties().group(tab))),
            GUANO_STALACTITE = ITEMS.register("guano_stalactite", () -> new BlockItem(ModBlocks.GUANO_STALACTITE.get(), new BlockItem.Properties().group(tab))),
            CARVED_MELON = ITEMS.register("carved_melon", () -> new CarvedMelonItem(new ArmorItem.Properties().group(tab))),
    //Buckets
    GUANO_BUCKET = ITEMS.register("guano_bucket", () -> new BucketItem(ModFluids.GUANO::get, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(tab))),
            SOLUTION_BUCKET = ITEMS.register("solution_bucket", () -> new BucketItem(ModFluids.SOLUTION::get, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(tab))),
            GRAPEJUICE_BUCKET = ITEMS.register("grapejuice_bucket", () -> new BucketItem(ModFluids.GRAPEJUICE::get, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(tab)));

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        tab.createIcon();
    }
}
