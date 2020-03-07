package com.sindarin.stoneworld.items;

import com.sindarin.stoneworld.blocks.CarvedMelonBlock;
import com.sindarin.stoneworld.blocks.ModBlocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.extensions.IForgeBlockState;

import java.util.Properties;
import java.util.function.Consumer;

public class CarvedMelonItem extends ArmorItem {

    private BlockItem blockItem;
    public CarvedMelonItem(Properties properties) {

        super(new CarvedMelonMaterial(), EquipmentSlotType.HEAD, properties);
        blockItem = new BlockItem(ModBlocks.carved_melon, new BlockItem.Properties());
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) { return 0; } //UNBREAKABLE SUIKA MELON

    @Override
    public boolean isDamageable() { return false; } //Also makes it non-repairable

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        //Attempt to use it as a block item and if not a success, use as armor item
        ActionResultType blockItemUse = blockItem.onItemUse(context);
        if (blockItemUse.isSuccessOrConsume()) {
            return blockItemUse;
        }
        else {
            return super.onItemUse(context);
        }
    }
}



