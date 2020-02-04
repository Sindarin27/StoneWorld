package com.sindarin.stoneworld.util;

import com.sindarin.stoneworld.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemTab extends ItemGroup {
    public ItemTab() {
        super("stoneworld");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.sulfur_dust);
    }
}
