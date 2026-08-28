package net.engarde.parry;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public enum ParryPose {
    SINGLE_HANDED("single_handed"),
    DOUBLE_HANDED("double_handed");

    public final String id;
    ParryPose(String id) {
        this.id = id;
    }

    public static final StreamCodec<FriendlyByteBuf, ParryPose> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> values()[id],
            ParryPose::ordinal
    ).cast();

    public static ParryPose getParryPose(ItemStack itemStack) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
        if (itemConfig != null && itemConfig.parryPose != null) return itemConfig.parryPose;

        return SINGLE_HANDED;
    }
}