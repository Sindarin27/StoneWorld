package com.sindarin.stoneworld.client.render.entity;

import com.sindarin.stoneworld.StoneWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.ZombieVillagerRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class StatueVillagerRenderer extends VillagerRenderer {
    private static final ResourceLocation overlay = new ResourceLocation("minecraft", "textures/entity/petrified_villager/overlay.png");

    public StatueVillagerRenderer(EntityRendererManager rendererManager, IReloadableResourceManager resourceManager) {
        super(rendererManager, resourceManager);
        this.addLayer(new StatueLayerRenderer<>(this, overlay));
        //Remove the original villager occupation texture and add our own
        this.layerRenderers.removeIf(layer -> layer instanceof VillagerLevelPendantLayer);
        this.addLayer(new VillagerLevelPendantLayer(this, resourceManager, "petrified_villager"));
    }
}
