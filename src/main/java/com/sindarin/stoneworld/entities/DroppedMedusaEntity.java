package com.sindarin.stoneworld.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DroppedMedusaEntity extends ItemEntity {
    public DroppedMedusaEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_) {
        super(p_i50217_1_, p_i50217_2_);
        this.ignoreFrustumCheck = true;
        this.setNoDespawn();
    }

    @Override
    //Tick the item itself even when dropped
    public void tick() {
        getItem().inventoryTick(world, getEntity(), 0, true);
        super.tick();
    }


    @Override
    public boolean cannotPickup() {
        CompoundNBT tag = this.getItem().getTag();
        return (tag != null && tag.contains("expanded") || super.cannotPickup());
    }

    @Override
    public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}
