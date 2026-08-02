package net.engarde.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.engarde.EnGarde;
import net.engarde.networking.ParryPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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

                    ParryPayload payload = new ParryPayload(true);
                    ClientPlayNetworking.send(payload);

                }
            }

            boolean isScreenOpen = client.gui.screen() != null;

            if (isScreenOpen && !wasScreenOpen) {
                if (client.player != null) {
                    ClientPlayNetworking.send(new ParryPayload(false));
                }
            }
            wasScreenOpen = isScreenOpen;
        });


    }
}
