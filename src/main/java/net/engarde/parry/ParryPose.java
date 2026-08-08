package net.engarde.parry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum ParryPose {
    SINGLE_HANDED("single_handed"),
    DOUBLE_HANDED("double_handed");

    private final String id;
    ParryPose(String id) {
        this.id = id;
    }

    public static ParryPose getParryPose(ItemStack itemStack) {
        if (itemStack.is(Items.DIAMOND_SWORD)) return SINGLE_HANDED;
        if (itemStack.is(Items.MACE)) return DOUBLE_HANDED;

        return SINGLE_HANDED;
    }
}