package com.sindarin.stoneworld.blocks.tiles;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.blocks.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

import static net.minecraftforge.fml.relauncher.libraries.LibraryManager.setup;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModTiles {
    @ObjectHolder("stoneworld:mixing_barrel")
    public static TileEntityType<TileMixingBarrel> mixing_barrel;

    @SubscribeEvent
    public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        // Register TileEntityTypes
        event.getRegistry().register(
                //Create the TileEntityType for the mixing barrel
                TileEntityType.Builder.create(TileMixingBarrel::new, ModBlocks.mixing_barrel).build(null).setRegistryName(StoneWorld.MOD_ID, "mixing_barrel")
        );
    }
}
