package com.sindarin.stoneworld.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.world.LightType;
import net.minecraftforge.client.model.animation.TileEntityRendererFast;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class MixingBarrelRenderer extends TileEntityRenderer<TileMixingBarrel> {
    final Float PxSz = 1/16F; //Constant how big one pixel is
    final Float sideInset = 2 * PxSz; //How far the fluid is inset on the sides (or outset if negative)
    final Float bottomInset = 4 * PxSz; //Where the bottom of the mixing barrel is
    final Float topInset = 1 * PxSz; //Where the top of the mixing barrel is

    @Override
    public void render(TileMixingBarrel mixingBarrel, double x, double y, double z, float partialTicks, int destroyStage) {
        //Prepare renderer with a few settings for item
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        //Draw the item if there is any
        ItemStack stack = mixingBarrel.getStackInSlot(0);
        if (!stack.isEmpty()) {
            GlStateManager.translated(0.5, 0.5, 0.5);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        }
        GlStateManager.popMatrix();

        //Prepare renderer with a bunch of settings for fluids
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        bindTexture(net.minecraft.client.renderer.texture.AtlasTexture.LOCATION_BLOCKS_TEXTURE); //Bind the texture atlas
        Tessellator tessellator = Tessellator.getInstance();

        //Fluid drawing buffer
        BufferBuilder fluidBuffer = tessellator.getBuffer();
        fluidBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        //Draw all tanks from bottom up
        int fluidLayer = 0; //The current layer in mb that next fluid will start from
        for (int i=0; i < mixingBarrel.getTanks(); i++) {
            FluidStack fluidStack = mixingBarrel.getFluidInTank(i);
            if (fluidStack != null && !fluidStack.isEmpty()) {
                Fluid fluid = fluidStack.getFluid(); //Find out what fluid is in the tank

                fluidLayer += fluidStack.getAmount(); //Move up the amount of mb's of this layer

                Float percentFull = (float)fluidLayer / mixingBarrel.getTotalCapacity(); //Find out how full the barrel is now
                Float layerHeight = (1 - topInset - bottomInset) * percentFull + bottomInset;
                //Get the sprite of the fluid
                TextureAtlasSprite still = Minecraft.getInstance().getTextureMap().getAtlasSprite(fluid.getAttributes().getStill(fluidStack).toString());

                //Figure out the color & light
                int color = fluid.getAttributes().getColor();
                float r = ((color >> 16) & 0xFF) / 255f;
                float g = ((color >> 8) & 0xFF) / 255f;
                float b = ((color >> 0) & 0xFF) / 255f;
                int light = Math.max(fluid.getAttributes().getLuminosity(), getWorld().getLightFor(LightType.BLOCK, mixingBarrel.getPos()))  * 16; //Luminosity is in light levels. By multiplying by 16 we get a 0-256 level. Also, figure out whether the fluid itself gives off more light than the outside light, and if so, use that.
                    //getWorld().getLightFor(LightType.BLOCK, mixingBarrel.getPos()) * 16; //This is the one for dimmer liquids
                int skylight = getWorld().getLightFor(LightType.SKY, mixingBarrel.getPos()) * 16; //Same as above, but then for the sky light

                //Put fluid into buffer
                drawFluidLayer(fluidBuffer, still, layerHeight, skylight, light, r, g, b, 0.8F);
            }
        }
        tessellator.draw(); //Draw all fluids

        //Finish renderer
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
    }

    void drawFluidLayer(BufferBuilder fluidBuffer, TextureAtlasSprite tex, Float height, int skylight, int light, float r, float g, float b, float opacity) {
        fluidBuffer //Bottom right corner
                .pos(sideInset, height,1 - sideInset)
                .tex(tex.getMinU(),tex.getMaxV())
                .lightmap(skylight, light)
                .color(r,g,b,opacity)
                .endVertex();
        fluidBuffer //Top right corner
                .pos(1 - sideInset, height,1 - sideInset)
                .tex(tex.getMaxU(),tex.getMaxV())
                .lightmap(skylight, light)
                .color(r,g,b,opacity)
                .endVertex();
        fluidBuffer //Top left corner
                .pos(1 - sideInset, height,sideInset)
                .tex(tex.getMaxU(),tex.getMinV())
                .lightmap(skylight, light)
                .color(r,g,b,opacity)
                .endVertex();
        fluidBuffer //Bottom left corner
                .pos(sideInset, height,sideInset)
                .tex(tex.getMinU(),tex.getMinV())
                .lightmap(skylight, light)
                .color(r,g,b,opacity)
                .endVertex();
    }


}
