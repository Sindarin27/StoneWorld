package com.sindarin.stoneworld.entities;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.client.render.entity.ItemMedusaRenderer;
import com.sindarin.stoneworld.client.render.entity.StatueVillagerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = StoneWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(StoneWorld.MOD_ID)
public class ModEntities {
    public final static EntityType<EntityStatueVillager> villager_statue = null;
    public final static EntityType<DroppedMedusaEntity> dropped_medusa = null;
    public static HashMap petrifiedByLiving = new HashMap(0);

    @SubscribeEvent
    public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityType.Builder.create(EntityStatueVillager::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true).build(StoneWorld.MOD_ID+":villager_statue").setRegistryName(StoneWorld.MOD_ID, "villager_statue"),
                EntityType.Builder.create(DroppedMedusaEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).build(StoneWorld.MOD_ID+":dropped_medusa").setRegistryName(StoneWorld.MOD_ID, "dropped_medusa")
        );

        petrifiedByLiving.put(VillagerEntity.class, new EntityStatueVillager.PetrificationHandler());
    }

    @SubscribeEvent
    public static void registerClient(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(villager_statue, renderManager -> new StatueVillagerRenderer(renderManager, (IReloadableResourceManager) Minecraft.getInstance().getResourceManager()));
        RenderingRegistry.registerEntityRenderingHandler(dropped_medusa, renderManager -> new ItemMedusaRenderer(renderManager, Minecraft.getInstance().getItemRenderer()));
    }
}
