package com.sindarin.stoneworld.blocks;

import com.sindarin.stoneworld.entities.ModEntities;
import com.sindarin.stoneworld.entities.spi.IPetrificationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Random;
import java.util.function.Supplier;

public class BlockDrippingStalactite extends Block {

    Supplier<? extends Fluid> fluid;

    public BlockDrippingStalactite(Supplier<? extends Fluid> fluidSupplier) {
        super(Properties.create(Material.ROCK)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(0)
                .notSolid()
                .tickRandomly()
                .hardnessAndResistance(1.5F)
        );
        this.fluid = fluidSupplier;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        //spawn a dripping particle under the stalactite
        //worldIn.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
        //Check 5 blocks beneath the stalactite for fluidHandlers
        if (!worldIn.isRemote) {
            worldIn.spawnParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1, 0.0D, 0.0D, 0.0D, 1.0D);
            for (int blocksDown = 1; blocksDown <= 5; blocksDown++) {
                if (worldIn.getBlockState(pos.down(blocksDown)).getBlock() == Blocks.AIR)
                    continue; //This is air, check next block
                if (worldIn.getTileEntity(pos.down(blocksDown)) != null) {
                    //If TE available, check if it's a proper target and if so, fill it
                    TileEntity targetTile = worldIn.getTileEntity(pos.down(blocksDown)); //Save this tileEntity as the target
                    LazyOptional<IFluidHandler> possibleTarget = targetTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.UP);
                    if (possibleTarget.isPresent()) {
                        IFluidHandler target = possibleTarget.orElse(null); //Can't ever be null as we already checked if it's present
                        target.fill(new FluidStack(fluid.get(), 500), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
                return; //Neither air nor fluid receiving TE, return
            }
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldReader, BlockPos pos) {
        return worldReader.getBlockState(pos.up()).isSolid(); //If the block above is solid, this is a valid position.
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState stateOther, IWorld world, BlockPos pos, BlockPos posOther) {
        return !this.isValidPosition(state, world, pos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, direction, stateOther, world, pos, posOther);
    }

    /*
     * Shape logic
     */
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public void onEntityCollision(BlockState p_196262_1_, World world, BlockPos p_196262_3_, Entity entity) {
        if (entity instanceof LivingEntity) {
            if (ModEntities.petrifiedByLiving.containsKey(entity.getClass())) {
                LivingEntity livingEntity = (LivingEntity)entity;
                IPetrificationHandler petrificationHandler = (IPetrificationHandler)ModEntities.petrifiedByLiving.get(entity.getClass());
                petrificationHandler.getPetrified(livingEntity);
            }
        }
        super.onEntityCollision(p_196262_1_, world, p_196262_3_, entity);
    }

    VoxelShape shape = VoxelShapes.or(
            makeCuboidShape(3.0D, 13.0D, 3.0D, 13.0D, 16.0D, 13.0D),
            makeCuboidShape(4.0D, 10.0D, 4.0D, 12.0D, 16.0D, 12.0D),
            makeCuboidShape(5.0D, 8.0D, 5.0D, 11.0D, 16.0D, 11.0D),
            makeCuboidShape(6.0D, 5.0D, 6.0D, 10.0D, 16.0D, 10.0D));
}
