package net.engarde.networking;

import net.engarde.EnGarde;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record BowPullPayload(int pullState) implements CustomPacketPayload {
    public static final Identifier ID = EnGarde.id("client_pull_bow");

    public static final CustomPacketPayload.Type<BowPullPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, BowPullPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BowPullPayload::pullState,
            BowPullPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
