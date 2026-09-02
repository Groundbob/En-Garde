package net.engarde.parry;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public enum ItemPose {
    SINGLE_HANDED("single_handed"),
    DOUBLE_HANDED("double_handed"),
    SPEAR("spear");

    public final String id;
    ItemPose(String id) {
        this.id = id;
    }

    public static ItemPose fromId(String id) {
        for (ItemPose pose : values()) {
            if (pose.id.equals(id.toLowerCase())) return pose;
        }
        return null;
    }

    public static final StreamCodec<FriendlyByteBuf, ItemPose> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> values()[id],
            ItemPose::ordinal
    ).cast();

    public static ItemPose getItemPose(Item item) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);

        if (itemConfig != null && itemConfig.poses!=null && itemConfig.poses.itemPose() != null) return itemConfig.poses.itemPose();

        return null;
    }
}
