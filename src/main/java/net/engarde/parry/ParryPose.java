package net.engarde.parry;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public enum ParryPose {
    SINGLE_HANDED("single_handed"),
    DOUBLE_HANDED("double_handed");

    public final String id;
    ParryPose(String id) {
        this.id = id;
    }

    public static ParryPose fromId(String id) {
        for (ParryPose pose : values()) {
            if (pose.id.equals(id)) return pose;
        }
        return null;
    }

    public static final StreamCodec<FriendlyByteBuf, ParryPose> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> values()[id],
            ParryPose::ordinal
    ).cast();

    public static ParryPose getParryPose(Item item) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
        if (itemConfig != null && itemConfig.poses != null && itemConfig.poses.parryPose() != null) return itemConfig.poses.parryPose();

        return SINGLE_HANDED;
    }
}