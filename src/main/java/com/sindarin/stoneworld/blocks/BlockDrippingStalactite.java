package com.sindarin.stoneworld.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
}
