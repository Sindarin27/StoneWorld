package com.sindarin.stoneworld.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class StatueLayerRenderer<T extends CreatureEntity, U extends EntityModel<T>> extends LayerRenderer<T,U> {
    private final ResourceLocation overlay;
    public StatueLayerRenderer(IEntityRenderer<T, U> entityRenderer, ResourceLocation overlay) {
        super(entityRenderer);
        this.overlay = overlay;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, T entity, float v, float v1, float v2, float v3, float v4, float v5) {
        renderCutoutModel(this.getEntityModel(), overlay, matrixStack, iRenderTypeBuffer, i, entity, 1, 1, 1);
    }
}
