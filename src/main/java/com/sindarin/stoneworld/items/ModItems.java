package com.sindarin.stoneworld.items;

import com.sindarin.stoneworld.StoneWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModItems {
    public static final Item sulfur_dust = null;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(StoneWorld.MOD_ID, "sulfur_dust")
        );
    }
}
