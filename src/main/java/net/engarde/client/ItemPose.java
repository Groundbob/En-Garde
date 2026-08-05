package net.engarde.client;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum ItemPose {
    DOUBLE_HANDED_HELD,
    SPEAR_HELD;

    public static ItemPose getItemPose(ItemStack itemStack) {
        if (itemStack.is(Items.MACE)) return DOUBLE_HANDED_HELD;
        if (itemStack.is(Items.DIAMOND_SPEAR)) return SPEAR_HELD;

        return null;
    }
}
