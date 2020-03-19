package com.sindarin.stoneworld.items;

import com.sindarin.stoneworld.entities.DroppedMedusaEntity;
import com.sindarin.stoneworld.entities.ModEntities;
import com.sindarin.stoneworld.entities.spi.IPetrificationHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PetrificationItem extends Item {
    //Duration of one tick, in seconds
    private final float TICK = 1/20f;
    private final float EXPAND_SPEED = 10; //Expands 10 blocks per second

    public PetrificationItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holder, int slot, boolean selected) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("time") && tag.contains("distance")) {
            //Subtract time and check distance
            float remainingTime = tag.getFloat("time") - TICK;
            float remainingDistance = tag.getFloat("distance");

            //If time over, expand
            if (remainingTime <= 0 && remainingDistance > 0) {
                if (!(holder instanceof ItemEntity)) {
                    holder.entityDropItem(stack);
                    holder.replaceItemInInventory(slot, ItemStack.EMPTY);
                }
                float expanded = tag.contains("expanded") ? tag.getFloat("expanded") : 0;
                //Expand by EXPAND_SPEED, but not more than remaining distance
                float newDistanceLeft = Math.max(remainingDistance - EXPAND_SPEED * TICK, 0);
                expanded += remainingDistance - newDistanceLeft;
                //Store new distance values
                tag.putFloat("expanded", expanded);
                tag.putFloat("distance", newDistanceLeft);
                //Finished expanding? Wait two seconds before deactivating
                if (newDistanceLeft <= 0) {
                    remainingTime = 2;
                }
                List<Entity> targets = world.getEntitiesWithinAABBExcludingEntity(holder, new AxisAlignedBB(holder.getPosX() - expanded, holder.getPosY() - expanded, holder.getPosZ() - expanded, holder.getPosX() + expanded, holder.getPosY() + expanded, holder.getPosZ() + expanded));
                for (Entity entity : targets) {
                    if (ModEntities.petrifiedByLiving.containsKey(entity.getClass())) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        IPetrificationHandler petrificationHandler = (IPetrificationHandler) ModEntities.petrifiedByLiving.get(entity.getClass());
                        petrificationHandler.getPetrified(livingEntity);
                    }
                }
            }

            tag.putFloat("time", remainingTime); //Set new time

            //If time over and fully expanded, reset
            if (remainingTime <= 0 && remainingDistance <= 0) {
                tag.remove("time");
                tag.remove("distance");
                tag.remove("expanded");
            }
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
        ItemEntity newItemEntity = new DroppedMedusaEntity(ModEntities.dropped_medusa, world);
        newItemEntity.setItem(itemstack);
        if (location.getClass() == ItemEntity.class) {
            newItemEntity.setOwnerId(((ItemEntity)location).getOwnerId());
            newItemEntity.setThrowerId(((ItemEntity)location).getThrowerId());
        }
        newItemEntity.setPosition(location.getPosX(), location.getPosY(), location.getPosZ());
        newItemEntity.setMotion(location.getMotion());
        newItemEntity.setPickupDelay(40);
        return newItemEntity;
    }
}
