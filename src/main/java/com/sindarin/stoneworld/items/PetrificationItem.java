package com.sindarin.stoneworld.items;

import com.sindarin.stoneworld.entities.DroppedMedusaEntity;
import com.sindarin.stoneworld.entities.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PetrificationItem extends Item {
    //Duration of one tick, in seconds
    private final float TICK = 1/20f;

    public PetrificationItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holder, int slot, boolean selected) {

        if(stack.getTag() != null && stack.getTag().contains("time")) {
            float remainingTime = stack.getTag().getFloat("time");
            remainingTime -= TICK;
            if (remainingTime <= 0) {
                System.out.println("Guess who just got PETRIFIED");
            }
            stack.getTag().putFloat("time", remainingTime);
        }

        super.inventoryTick(stack, world, holder, slot, selected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        //If it's just the time that went down, don't do the bob animation.
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
                && (getRemainingTime(oldStack) > getRemainingTime(newStack) || slotChanged);
    }

    protected float getRemainingTime(ItemStack stack) {
        if (stack.getTag() == null || !stack.getTag().contains("time")) return 0;
        return stack.getTag().getFloat("time");
    }

    //If the stack already has a distance and time tag, then it has a custom entity to replace it on drop
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains("distance") && stack.getTag().contains("time");
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        System.out.println("Replacing item with medusa");
        ItemEntity newItemEntity = new DroppedMedusaEntity(ModEntities.dropped_medusa, world);
        newItemEntity.setItem(itemstack);
        if (location.getClass() == ItemEntity.class) {
            System.out.println("Getting owner and thrower");
            newItemEntity.setOwnerId(((ItemEntity)location).getOwnerId());
            newItemEntity.setThrowerId(((ItemEntity)location).getThrowerId());
        }
        newItemEntity.setPosition(location.getPosX(), location.getPosY(), location.getPosZ());
        newItemEntity.setMotion(location.getMotion());
        newItemEntity.setPickupDelay(40);
        return newItemEntity;
    }
}
