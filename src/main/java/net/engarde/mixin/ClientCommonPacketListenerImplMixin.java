package net.engarde.mixin;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.engarde.parry.ParryState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.Identifier;
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

        Identifier itemId = BuiltInRegistries.ITEM.getKey(client.player.getMainHandItem().getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
        if (client.player instanceof ParryState parryState && parryState.engarde$isParrying() || itemConfig != null && itemConfig.heavyItem != null && itemConfig.heavyItem) {
            ci.cancel();
        }
    }
}
