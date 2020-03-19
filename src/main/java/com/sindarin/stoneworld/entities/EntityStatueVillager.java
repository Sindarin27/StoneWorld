package com.sindarin.stoneworld.entities;

import com.sindarin.stoneworld.entities.spi.IPetrificationHandler;
import com.sindarin.stoneworld.entities.spi.IPetrifiedCreature;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

public class EntityStatueVillager extends VillagerEntity implements IPetrifiedCreature<VillagerEntity> {
    EntityStatueVillager(EntityType<? extends VillagerEntity> type, World world) {
        super(type, world);
    }

    private final static int particleCountForConversion = 20;
    @Override
    public IPetrificationHandler<VillagerEntity> getPetrificationHandler() {
        return new PetrificationHandler();
    }

    public static class PetrificationHandler implements IPetrificationHandler<VillagerEntity> {
        @Override
        public IPetrifiedCreature<VillagerEntity> getPetrified(VillagerEntity entity) {
            Random random = new Random();
            for (int i = 0; i < particleCountForConversion; i++) {
                entity.getWorld().addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()), entity.getPosXRandom(1), entity.getPosYRandom(), entity.getPosZRandom(1), random.nextDouble() * 0.5, random.nextDouble() * 0.5, random.nextDouble() * 0.5);
            }
            CompoundNBT compound = new CompoundNBT();
            entity.writeWithoutTypeId(compound); //Read the nbt from the old entity
            EntityStatueVillager petrified = ModEntities.villager_statue.create(entity.world); //Create a petrified entity in the same world
            petrified.read(compound); //Have this new entity read the old entity's data
            petrified.setUniqueId(MathHelper.getRandomUUID(petrified.rand)); //Give it its own uuid
            entity.remove(); //Remove the old entity from the world
            petrified.world.addEntity(petrified);
            return petrified;
        }

        @Override
        public VillagerEntity getRevived(IPetrifiedCreature<VillagerEntity> petrified) {
            EntityStatueVillager statue = (EntityStatueVillager)petrified;
            CompoundNBT compound = new CompoundNBT();
            statue.writeWithoutTypeId(compound); //Read the nbt from the old entity
            VillagerEntity entity = (VillagerEntity) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft", "villager"))).create(statue.world);
            entity.read(compound); //Have this new entity read the old entity's data
            entity.setUniqueId(MathHelper.getRandomUUID(statue.rand)); //Give it its own uuid
            statue.remove(); //Remove the old entity from the world
            entity.world.addEntity(entity);
            return entity;
        }
    }

    @Override
    protected SoundEvent getVillagerYesNoSound(boolean p_213721_1_) { return null; }


    @Override
    public int getShakeHeadTicks() { return 0; } //Statues don't shake heads

    @Override
    public void setShakeHeadTicks(int p_213720_1_) {} //Statues don't shake heads

    @Override
    public SoundEvent getYesSound() { return null; } //Statues don't say yes

    @Override
    public boolean wantsMoreFood() { return false; } //Before other villagers start feeding statues

    @Override
    public boolean canSpawnGolems(long p_223350_1_) { return false; } //Don't involve statues in the choosing of your golem president

    /*
     * From here on common classes among all statue entities
     * Due to java limitations it's sadly not possible to inherit from a second class or override from an interface afaik. If possible, please make a PR or lmk.
     */

    @Override
    protected boolean canTriggerWalking() { return false; }

    @Override
    @ParametersAreNonnullByDefault
    public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) { return false; } //Right-clicking statues doesn't do anything

    @Override
    protected void updateAITasks() {} //Statues don't have tasks

    @Override
    public boolean hasNoGravity() { return false; } //They do have gravity however (yet it seems this doesn't get read when AI is disabled)

    @Override
    public void handleStatusUpdate(byte p_70103_1_) { } //No matter what happens, a petrified entity should not display particles etc

    @Override
    @ParametersAreNonnullByDefault
    protected void damageEntity(DamageSource source, float damage) {
        damage = IPetrifiedCreature.calculateDamage(source, damage);
        super.damageEntity(source, damage);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) { } //Don't knock statues back

    @Override
    //Only drop loot if killed by a player with a pickaxe
    protected void dropLoot(DamageSource source, boolean p_213354_2_) {
        if (source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity damager = (PlayerEntity)source.getTrueSource();
            if (damager.getHeldItemMainhand().getToolTypes().contains(ToolType.PICKAXE)) {
                super.dropLoot(source, p_213354_2_);
            }
        }
    }

    /*
     *  Sounds
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) { return SoundEvents.BLOCK_STONE_HIT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_STONE_BREAK; }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() { return null; }

    @Override
    protected SoundEvent getFallSound(int p_184588_1_) { return SoundEvents.BLOCK_STONE_FALL; }

    @Override
    public float getPitch(float p_195050_1_) {
        return 1.0F;
    }

    /*
     * Give entity hard collision
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.isAlive() ? this.getBoundingBox() : null;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void applyEntityCollision(Entity p_70108_1_) { }

    @Override
    public float getCollisionBorderSize() {
        return 0.0F;
    }


    @Override
    protected void pushOutOfBlocks(double p_213282_1_, double p_213282_3_, double p_213282_5_) {} //Don't react to being pushed out of blocks

    @Override
    protected void collideWithEntity(Entity p_82167_1_) { } //Don't react to people pushing us around

    @Override
    protected void collideWithNearbyEntities() { }

    @Override
    @ParametersAreNonnullByDefault
    public void move(MoverType moverType, Vec3d p_213315_2_) {
        if (moverType == MoverType.PISTON || moverType == MoverType.SELF) super.move(moverType, p_213315_2_);
    }

    @Override
    public boolean canBeCollidedWith() { return false; }

    @Override
    public void onCollideWithPlayer(PlayerEntity p_70100_1_) { }
}
