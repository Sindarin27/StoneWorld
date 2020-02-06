package com.sindarin.stoneworld.client.tiles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.TileEntityRendererFast;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class MixingBarrelRenderer extends TileEntityRendererFast<TileMixingBarrel> {
    final Float PxSz = 1/16F; //Constant how big one pixel is
    final Float sideInset = 2 * PxSz; //How far the fluid is inset on the sides (or outset if negative)
    final Float bottomInset = 4 * PxSz; //Where the bottom of the mixing barrel is
    final Float topInset = 1 * PxSz; //Where the top of the mixing barrel is

    @Override
    public void renderTileEntityFast(TileMixingBarrel mixingBarrel, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer) {
        int fluidLayer = 0; //What mb-layer the renderer is currently handling
        for (int i=0; i < mixingBarrel.getTanks(); i++) {
            FluidStack fluidStack = mixingBarrel.getFluidInTank(i);
            if (fluidStack != null && !fluidStack.isEmpty()) {
                Fluid fluid = fluidStack.getFluid(); //Find out what fluid is in the tank

                fluidLayer += fluidStack.getAmount(); //Move up the amount of mb's of this layer

                Float percentFull = (float)fluidLayer / mixingBarrel.getTotalCapacity(); //Find out how full the barrel is now
                Float layerHeight = (1 - topInset - bottomInset) * percentFull + bottomInset;
                //Get the sprite of the fluid
                TextureAtlasSprite still = Minecraft.getInstance().getTextureMap().getAtlasSprite(fluid.getAttributes().getStill(fluidStack).toString());

                //Figure out the color
                int color = fluid.getAttributes().getColor();
                float r = ((color >> 16) & 0xFF) / 255f;
                float g = ((color >> 8) & 0xFF) / 255f;
                float b = ((color >> 0) & 0xFF) / 255f;

                buffer.setTranslation(x,y,z);
                buffer //Bottom right corner
                        .pos(sideInset, layerHeight,1 - sideInset)
                        .color(r,g,b,0.9F)
                        .tex(still.getMinU(),still.getMaxV())
                        .lightmap(240, 0)
                        .endVertex();
                buffer //Top right corner
                        .pos(1 - sideInset, layerHeight,1 - sideInset)
                        .color(r,g,b,0.9F)
                        .tex(still.getMaxU(),still.getMaxV())
                        .lightmap(240, 0)
                        .endVertex();
                buffer //Top left corner
                        .pos(1 - sideInset, layerHeight,sideInset)
                        .color(r,g,b,0.9F)
                        .tex(still.getMaxU(),still.getMinV())
                        .lightmap(240, 0)
                        .endVertex();
                buffer //Bottom left corner
                        .pos(sideInset, layerHeight,sideInset)
                        .color(r,g,b,0.9F)
                        .tex(still.getMinU(),still.getMinV())
                        .lightmap(240, 0)
                        .endVertex();
            }
        }
    }



}
