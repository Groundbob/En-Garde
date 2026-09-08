package net.engarde.networking;

import net.engarde.EnGarde;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record ParryPayload(boolean isParrying) implements CustomPacketPayload {
    public static final Identifier ID = EnGarde.id("client_parry");

    public static final CustomPacketPayload.Type<ParryPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ParryPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ParryPayload::isParrying,
            ParryPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
