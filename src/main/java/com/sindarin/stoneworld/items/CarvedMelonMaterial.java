package com.sindarin.stoneworld.items;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

final class CarvedMelonMaterial implements IArmorMaterial {

    @Override
    public int getDurability(EquipmentSlotType equipmentSlotType) { return 1; }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType equipmentSlotType) { return 1; }

    @Override
    public int getEnchantability() { return 0; }

    @Override
    public SoundEvent getSoundEvent() { return SoundEvents.BLOCK_PUMPKIN_CARVE; }

    @Override
    public Ingredient getRepairMaterial() { return null; }

    @Override
    public String getName() { return "stoneworld:melon"; }

    @Override
    public float getToughness() { return 0; }
}

