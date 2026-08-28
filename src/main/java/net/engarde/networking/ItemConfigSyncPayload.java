package net.engarde.networking;

import net.engarde.EnGarde;
import net.engarde.config.ParryItemConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public record ItemConfigSyncPayload(Map<Identifier, ParryItemConfig> itemConfigs) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "item_config_sync_payload");

    public static final CustomPacketPayload.Type<ItemConfigSyncPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemConfigSyncPayload> CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, Identifier, ParryItemConfig, Map<Identifier, ParryItemConfig>>map(
            HashMap::new,
            Identifier.STREAM_CODEC,
            ParryItemConfig.STREAM_CODEC
    ).map(ItemConfigSyncPayload::new, ItemConfigSyncPayload::itemConfigs);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
