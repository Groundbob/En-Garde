package net.engarde.parry;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public enum ItemPose {
    DOUBLE_HANDED("double_handed"),
    SPEAR("spear");

    public final String id;
    ItemPose(String id) {
        this.id = id;
    }

    public static final StreamCodec<FriendlyByteBuf, ItemPose> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> values()[id],
            ItemPose::ordinal
    ).cast();

    public static ItemPose getItemPose(ItemStack itemStack) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);

        if (itemConfig != null && itemConfig.itemPose != null) return itemConfig.itemPose;

        return null;
    }
}
