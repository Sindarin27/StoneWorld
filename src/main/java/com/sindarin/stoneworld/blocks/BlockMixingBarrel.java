package com.sindarin.stoneworld.blocks;

import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockMixingBarrel extends Block implements IForgeBlock {
    TileMixingBarrel tileEntity;
    public BlockMixingBarrel() {
        super(Block.Properties.create(Material.WOOD)
                .hardnessAndResistance(1.5F)
                .harvestTool(ToolType.AXE)
        );
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; } //Our mixing barrel has a tile entity

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        this.tileEntity = new TileMixingBarrel();
        return tileEntity;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (!worldIn.isRemote) {
            if (FluidUtil.interactWithFluidHandler(player, handIn, worldIn, pos, hit.getFace())) return true;
            if (player.getHeldItem(handIn).getItem() == Items.STICK) { return ((TileMixingBarrel)worldIn.getTileEntity(pos)).doRecipe(); }
            return true;
        }

        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
