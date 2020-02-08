package com.sindarin.stoneworld.blocks;

import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockMixingBarrel extends Block implements IForgeBlock {
    TileMixingBarrel tileEntity;
    public static final IntegerProperty lightLevel = BlockStateProperties.LEVEL_0_15; //The current light level of the block

    public BlockMixingBarrel() {
        super(Block.Properties.create(Material.WOOD)
                .hardnessAndResistance(1.5F)
                .harvestTool(ToolType.AXE)
                .lightValue(15)
        );
        this.setDefaultState(this.stateContainer.getBaseState().with(lightLevel, Integer.valueOf(0))); //Default state has no light
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
            else if (player.getHeldItem(handIn).getItem() == Items.STICK) { return ((TileMixingBarrel)worldIn.getTileEntity(pos)).doRecipe(); }
            else if (player.getHeldItem(handIn).getItem() == Items.AIR)
            {
                player.setHeldItem(handIn, ((TileMixingBarrel)worldIn.getTileEntity(pos)).extractItem(0, 64, false));
                return true;
            }
            return true;
        }

        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        return state.get(lightLevel);
    }

    @Override //Deprecated, but overriding the other one doesn't have effect on anything but the block itself. Idiot MC lighting engine.
    public int getLightValue(BlockState state) {
        return state.get(lightLevel);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(lightLevel);
    }
}