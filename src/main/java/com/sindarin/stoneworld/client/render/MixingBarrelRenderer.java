package com.sindarin.stoneworld.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import com.sindarin.stoneworld.recipes.MixingBarrelOutput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.world.LightType;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class MixingBarrelRenderer extends TileEntityRenderer<TileMixingBarrel> {
    static final Float PxSz = 1/16F; //Constant how big one pixel is
    static final Float sideInset = 2 * PxSz; //How far the fluid is inset on the sides (or outset if negative)
    static final Float bottomInset = 4 * PxSz; //Where the bottom of the mixing barrel is
    static final Float topInset = 1 * PxSz; //Where the top of the mixing barrel is

    public MixingBarrelRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TileMixingBarrel mixingBarrel, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i1, int i2) {
        //Draw the item
        ItemStack stack = mixingBarrel.getStackInSlot(0);
        if (!stack.isEmpty()) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.5, 0.5);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, i1, i2, matrixStack, iRenderTypeBuffer);
            matrixStack.pop();
        }
        //Draw the fluids
        matrixStack.push();
        RenderType renderType = RenderType.translucent(); //How convenient, tranlucent already binds the block atlas
        IVertexBuilder builder = iRenderTypeBuffer.getBuffer(renderType);
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
                TextureAtlasSprite still = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluid.getAttributes().getStillTexture(mixingBarrel.getWorld(), mixingBarrel.getPos()));
                //Figure out the color & light
                int color = fluid.getAttributes().getColor();
                float r = ((color >> 16) & 0xFF) / 255f;
                float g = ((color >> 8) & 0xFF) / 255f;
                float b = ((color >> 0) & 0xFF) / 255f;
                int light = Math.max(fluid.getAttributes().getLuminosity(), mixingBarrel.getWorld().getLightFor(LightType.BLOCK, mixingBarrel.getPos())) * 16; //Luminosity is in light levels. By multiplying by 16 we get a 0-256 level. Also, figure out whether the fluid itself gives off more light than the outside light, and if so, use that.
                //getWorld().getLightFor(LightType.BLOCK, mixingBarrel.getPos()) * 16; //This is the one for dimmer liquids
                int skylight = mixingBarrel.getWorld().getLightFor(LightType.SKY, mixingBarrel.getPos()) * 16; //Same as above, but then for the sky light

                //Put fluid into buffer
                drawFluidLayer(builder, matrixStack, still, layerHeight, skylight, light, r, g, b, 0.8F, i2);
            }


        }

        matrixStack.pop();
    }

    void drawFluidLayer(IVertexBuilder builder, MatrixStack matrixStack, TextureAtlasSprite tex, Float height, int skylight, int light, float r, float g, float b, float opacity, int overlay) {
        Matrix4f posMatrix = matrixStack.getLast().getPositionMatrix();
        builder //Bottom right corner
                .pos(posMatrix, sideInset, height,1 - sideInset)
                .color(r,g,b,opacity)
                .tex(tex.getMinU(),tex.getMaxV())
                .lightmap(skylight, light)
                .normal(0,1,0)
                .overlay(overlay)
                .endVertex();
        builder //Top right corner
                .pos(posMatrix, 1 - sideInset, height,1 - sideInset)
                .color(r,g,b,opacity)
                .tex(tex.getMaxU(),tex.getMaxV())
                .lightmap(skylight, light)
                .normal(0,1,0)
                .overlay(overlay)
                .endVertex();
        builder //Top left corner
                .pos(posMatrix, 1 - sideInset, height,sideInset)
                .color(r,g,b,opacity)
                .tex(tex.getMaxU(),tex.getMinV())
                .lightmap(skylight, light)
                .normal(0,1,0)
                .overlay(overlay)
                .endVertex();
        builder //Bottom left corner
                .pos(posMatrix, sideInset, height,sideInset)
                .color(r,g,b,opacity)
                .tex(tex.getMinU(),tex.getMinV())
                .lightmap(skylight, light)
                .normal(0,1,0)
                .overlay(overlay)
                .endVertex();
    }
}
