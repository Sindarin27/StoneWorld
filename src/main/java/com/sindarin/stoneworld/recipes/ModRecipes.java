package com.sindarin.stoneworld.recipes;

import com.sindarin.stoneworld.StoneWorld;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModRecipes {
    public static final IRecipeSerializer MIXING_BARREL = null;

    @SubscribeEvent
    public static void onRegisterRecipeSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().registerAll(
                new MixingBarrelRecipeSerializer<>(MixingBarrelRecipe::new).setRegistryName(StoneWorld.MOD_ID, "mixing_barrel")
        );
    }
}
