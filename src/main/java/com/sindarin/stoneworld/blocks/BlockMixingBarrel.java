package com.sindarin.stoneworld.blocks;

import com.sindarin.stoneworld.blocks.tiles.TileMixingBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.fluids.FluidUtil;

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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.getHeldItem(handIn).getItem() == Items.STICK) { return ((TileMixingBarrel)worldIn.getTileEntity(pos)).doRecipe(); }
        else if (player.getHeldItem(handIn).getItem() == Items.AIR)
        {
            player.setHeldItem(handIn, ((TileMixingBarrel)worldIn.getTileEntity(pos)).extractItem(0, 64, worldIn.isRemote));
            if (player.getHeldItem(handIn).getItem() != Items.AIR) worldIn.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1f, 1); //If this picked up an item, play a pickup sound
            return ActionResultType.SUCCESS;
        }
        else if (FluidUtil.interactWithFluidHandler(player, handIn, worldIn, pos, hit.getFace())) return ActionResultType.SUCCESS;
        return ActionResultType.SUCCESS;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return state.get(lightLevel);
    }

    @Override //Deprecated, but Mojang still calls for it
    public int getLightValue(BlockState state) {
        return state.get(lightLevel);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(lightLevel);
    }


    //Methods regarding shape. Since our block has the same shape as a cauldron, get its shape info

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Blocks.CAULDRON.getShape(state, worldIn, pos, context);
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Blocks.CAULDRON.getShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return Blocks.CAULDRON.getRaytraceShape(state, worldIn, pos);
    }
}