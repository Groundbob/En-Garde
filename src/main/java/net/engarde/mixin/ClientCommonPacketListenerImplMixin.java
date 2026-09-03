package net.engarde.mixin;

import net.engarde.parry.ParryState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {
    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void engarde$stopOffhand(Packet<?> packet, CallbackInfo ci) {
        if (!(packet instanceof ServerboundPlayerActionPacket actionPacket)) return;
        if (actionPacket.getAction() != ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) return;
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) return;

        if (client.player instanceof ParryState parryState && parryState.engarde$isParrying()) {
            ci.cancel();
        }
    }
}
