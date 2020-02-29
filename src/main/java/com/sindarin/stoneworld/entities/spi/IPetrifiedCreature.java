package com.sindarin.stoneworld.entities.spi;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ToolType;

public interface IPetrifiedCreature<T extends LivingEntity> {
    float damageMultiplier = 2.0f; //How much stronger attacks with a pickaxe are against a statue, and how much weaker attacks without a pickaxe

    IPetrificationHandler<T> getPetrificationHandler(); //Method to get an object's petrification handler, needed so we can (de)petrify them

    //Helper method to calculate damage received from an attack
    static float calculateDamage(DamageSource source, float damage) {
        if (source.isFireDamage()
                || source.isMagicDamage()
                || source.getDamageType() == "sting"
                || source.getDamageType() == "arrow"
        ) return 0F; //Receive no damage from fire, magic, bees or arrows
        if (source.isDamageAbsolute()) return damage; //Absolute damage should be handled as-is
        if (source.getTrueSource() instanceof MobEntity) {
            MobEntity damager = (MobEntity)source.getTrueSource();
            return getDamageMultiplied(damage, damager.getHeldItemMainhand());
        }
        else if (source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity damager = (PlayerEntity)source.getTrueSource();
            return getDamageMultiplied(damage, damager.getHeldItemMainhand());
        }
        return damage;
    }
    //Helper method to check for damage multiplication
    static float getDamageMultiplied(float damage, ItemStack mainHandItem) {
        if (mainHandItem.getToolTypes().contains(ToolType.PICKAXE)) {
            damage *= damageMultiplier;
        } else {
            damage /= damageMultiplier;
        }
        return damage;
    }

}
