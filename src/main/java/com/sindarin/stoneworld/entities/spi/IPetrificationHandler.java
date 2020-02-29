package com.sindarin.stoneworld.entities.spi;

import net.minecraft.entity.LivingEntity;

public interface IPetrificationHandler<T extends LivingEntity> {
    IPetrifiedCreature<T> getPetrified(T entity);
    T getRevived(IPetrifiedCreature<T> petrified);
}
