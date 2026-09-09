package net.engarde.networking;

import net.engarde.EnGarde;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record BowPullSyncPayload(int entityId, int pullState) implements CustomPacketPayload {
    public static final Identifier ID = EnGarde.id("sync_bow");
    public static final CustomPacketPayload.Type<BowPullSyncPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, BowPullSyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BowPullSyncPayload::entityId,
            ByteBufCodecs.INT, BowPullSyncPayload::pullState,
            BowPullSyncPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
