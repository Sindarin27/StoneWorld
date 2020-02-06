package com.sindarin.stoneworld.blocks;

import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.fluids.FluidUtil;

public class BlockMixingBarrel extends Block implements IForgeBlock {
    public BlockMixingBarrel() {
        super(Block.Properties.create(Material.WOOD)
                .hardnessAndResistance(1.5F)
                .harvestTool(ToolType.AXE)
        );
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; } //Our mixing barrel has a tile entity

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileMixingBarrel();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            if (FluidUtil.interactWithFluidHandler(player, handIn, worldIn, pos, hit.getFace())) return true;
            return true;
        }

        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
