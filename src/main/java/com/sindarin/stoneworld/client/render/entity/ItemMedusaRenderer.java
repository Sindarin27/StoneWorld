package com.sindarin.stoneworld.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sindarin.stoneworld.StoneWorld;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ItemMedusaRenderer extends ItemRenderer {
    private int currentFrame = 0;
    private int ticksSinceLastFrame = 0;

    public ItemMedusaRenderer(EntityRendererManager rendererManager, net.minecraft.client.renderer.ItemRenderer itemRenderer) {
        super(rendererManager, itemRenderer);
        rendererManager.textureManager.bindTexture(new ResourceLocation(StoneWorld.MOD_ID, "textures/block/petrification"));
    }

    @Override
    public void render(ItemEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225623_6_) {
        super.render(entity, p_225623_2_, p_225623_3_, matrix, buffer, p_225623_6_);
        CompoundNBT tag = entity.getItem().getTag();
        if (tag != null && tag.contains("expanded")) {
            float radius = tag.getFloat("expanded");
            matrix.push();
            RenderType renderType = RenderType.getEntityTranslucent(new ResourceLocation(StoneWorld.MOD_ID, "textures/block/petrification.png"));
            IVertexBuilder builder = buffer.getBuffer(renderType);
            //Inside
            /*
            drawPlaneXorY(builder, matrix, sprite, -radius, -radius, -radius, radius, -radius, radius, OverlayTexture.NO_OVERLAY);
            drawPlaneXorY(builder, matrix, sprite, radius, radius, -radius, -radius, radius, radius, OverlayTexture.NO_OVERLAY);
            drawPlaneXorY(builder, matrix, sprite, -radius, -radius, radius, -radius, radius, -radius, OverlayTexture.NO_OVERLAY);
            drawPlaneXorY(builder, matrix, sprite, radius, -radius, -radius, radius, radius, radius, OverlayTexture.NO_OVERLAY);
            drawPlaneZ(builder, matrix, sprite, radius, -radius, -radius, -radius, radius, -radius, OverlayTexture.NO_OVERLAY);
            drawPlaneZ(builder, matrix, sprite, -radius, -radius, radius, radius, radius, radius, OverlayTexture.NO_OVERLAY);*/
            if (++ticksSinceLastFrame > 2) {
                if (++currentFrame > 15) currentFrame = 0;
                ticksSinceLastFrame = 0;
            }
            float minV = (float) currentFrame / 16;
            float maxV = (float) (currentFrame + 1) / 16;
            //Outside
            drawPlaneZ(builder, matrix, minV, maxV, radius, -radius, radius, -radius, radius, radius, OverlayTexture.NO_OVERLAY);
            drawPlaneZ(builder, matrix, minV, maxV, -radius, -radius, -radius, radius, radius, -radius, OverlayTexture.NO_OVERLAY);
            drawPlaneXorY(builder, matrix, minV, maxV, radius, -radius, -radius, -radius, -radius, radius, OverlayTexture.NO_OVERLAY);
            drawPlaneXorY(builder, matrix, minV, maxV, radius, radius, radius, -radius, radius, -radius, OverlayTexture.NO_OVERLAY);
            drawPlaneXorY(builder, matrix, minV, maxV, -radius, radius, radius, -radius, -radius, -radius, OverlayTexture.NO_OVERLAY);
            drawPlaneXorY(builder, matrix, minV, maxV, radius, radius, -radius, radius, -radius, radius, OverlayTexture.NO_OVERLAY);
            matrix.pop();
        }
    }


    private void drawPlaneXorY(IVertexBuilder builder, MatrixStack matrixStack, float vMin, float vMax, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int overlay) {
        Matrix4f posMatrix = matrixStack.getLast().getMatrix();
        builder
                .pos(posMatrix, minX, minY, maxZ)
                .color(255, 255, 255, 255)
                .tex(0, vMax)
                .overlay(overlay)
                .lightmap(240, 127)
                .normal(0, 1, 0)
                .endVertex();
        builder
                .pos(posMatrix, maxX, maxY, maxZ)
                .color(255, 255, 255, 255)
                .tex(1, vMax)
                .overlay(overlay)
                .lightmap(240, 127)
                .normal(0, 1, 0)
                .endVertex();
        builder
                .pos(posMatrix, maxX, maxY, minZ)
                .color(255, 255, 255, 255)
                .tex(1, vMin)
                .overlay(overlay)
                .lightmap(240, 127)
                .normal(0, 1, 0)
                .endVertex();
        builder
                .pos(posMatrix, minX, minY, minZ)
                .color(255, 255, 255, 255)
                .tex(0, vMin)
                .overlay(overlay)
                .lightmap(240, 127)
                .normal(0, 1, 0)
                .endVertex();
    }

    private void drawPlaneZ(IVertexBuilder builder, MatrixStack matrixStack, float vMin, float vMax, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int overlay) {
        Matrix4f posMatrix = matrixStack.getLast().getMatrix();
        builder
                .pos(posMatrix, minX, maxY, minZ)
                .color(255, 255, 255, 255)
                .tex(0, vMax)
                .overlay(overlay)
                .lightmap(240, 127)
                .normal(0, 1, 0)
                .endVertex();
        builder
                .pos(posMatrix, maxX, maxY, maxZ)
                .color(255, 255, 255, 255)
                .tex(1, vMax)
                .overlay(overlay)
                .lightmap(240, 127)
                .normal(0, 1, 0)
                .endVertex();
        builder
                .pos(posMatrix, maxX, minY, maxZ)
                .color(255, 255, 255, 255)
                .tex(1, vMin)
                .overlay(overlay)
                .lightmap(240, 127)
                .normal(0, 1, 0)
                .endVertex();
        builder
                .pos(posMatrix, minX, minY, minZ)
                .color(255, 255, 255, 255)
                .tex(0, vMin)
                .overlay(overlay)
                .lightmap(240, 127)
                .normal(0, 1, 0)
                .endVertex();
    }

}
