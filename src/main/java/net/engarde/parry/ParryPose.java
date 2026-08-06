package net.engarde.parry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum ParryPose {
    SINGLE_HANDED_PARRY,
    DOUBLE_HANDED_PARRY;

    public static ParryPose getParryPose(ItemStack itemStack) {
        if (itemStack.is(Items.DIAMOND_SWORD)) return SINGLE_HANDED_PARRY;
        if (itemStack.is(Items.MACE)) return DOUBLE_HANDED_PARRY;

        return SINGLE_HANDED_PARRY;
    }
}