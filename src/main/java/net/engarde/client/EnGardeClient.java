package net.engarde.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.engarde.EnGarde;
import net.engarde.networking.ParryPayload;
import net.engarde.networking.ParrySyncPayload;
import net.engarde.parry.ParryHudElement;
import net.engarde.parry.ParryState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.resources.Identifier;

public class EnGardeClient implements ClientModInitializer {
    private static boolean wasScreenOpen = false;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category CATEGORY = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "custom_category")
        );

        KeyMapping parry = KeyMappingHelper.registerKeyMapping(
                new ToggleKeyMapping(
                        "key.en-garde.parry",
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_R,
                        CATEGORY,
                        () -> true,
                        false
                ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (parry.consumeClick()) {
                if (client.player != null) {

                    ParryState state = (ParryState) client.player;
                    state.engarde$setParrying(!state.engarde$isParrying());

                    ClientPlayNetworking.send(new ParryPayload(true));

                }
            }

            boolean isScreenOpen = client.gui.screen() != null;

            if (isScreenOpen && !wasScreenOpen) {
                if (client.player != null) {
                    ParryState state = (ParryState) client.player;
                    state.engarde$setParrying(false);

                    ClientPlayNetworking.send(new ParryPayload(false));
                }
            }
            wasScreenOpen = isScreenOpen;
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.CROSSHAIR, EnGarde.id("parry_indicator"), new ParryHudElement());

        ClientPlayNetworking.registerGlobalReceiver(ParrySyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().level == null) return;
                var entity = context.client().level.getEntity(payload.entityId());
                if (entity instanceof ParryState parryState) {
                    parryState.engarde$setParrying(payload.isParrying());
                }
            });
        });

    }
}
