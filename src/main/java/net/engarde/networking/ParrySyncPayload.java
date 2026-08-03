package net.engarde.networking;

import net.engarde.EnGarde;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ParrySyncPayload(int entityId, boolean isParrying) implements CustomPacketPayload {
    public static final Identifier PARRY_SYNC_ID = Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "sync_parry");
    public static final CustomPacketPayload.Type<ParrySyncPayload> TYPE = new CustomPacketPayload.Type<>(PARRY_SYNC_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ParrySyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ParrySyncPayload::entityId,
            ByteBufCodecs.BOOL, ParrySyncPayload::isParrying,
            ParrySyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}