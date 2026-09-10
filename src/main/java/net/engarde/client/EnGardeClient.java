package net.engarde.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.engarde.EnGarde;
import net.engarde.config.CustomTooltips;
import net.engarde.config.EnGardeConfig;
import net.engarde.config.ParryItemConfig;
import net.engarde.networking.BowPullSyncPayload;
import net.engarde.networking.ItemConfigSyncPayload;
import net.engarde.networking.ParryPayload;
import net.engarde.networking.ParrySyncPayload;
import net.engarde.parry.ParryHudElement;
import net.engarde.parry.ParryState;
import net.engarde.reworks.bow.BowPullState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnGardeClient implements ClientModInitializer {
    public static ClientPacketListener CLIENT_PACKET_LISTENER;
    public static final Map<Identifier, ParryItemConfig> PARRY_ITEM_CONFIGS = new HashMap<>();

    private static boolean wasScreenOpen = false;
    public static boolean wasItemOnCooldown = false;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category CATEGORY = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "custom_category")
        );

        ClientPlayConnectionEvents.JOIN.register((handler, _, _) -> {
            CLIENT_PACKET_LISTENER = handler;
            EnGardeConfig.reloadConfig();
        });

        KeyMapping parry = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.en-garde.parry",
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_R,
                        CATEGORY
                ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (EnGardeConfig.loadConfig().parryToggleable) {
                while (parry.consumeClick()) {
                    if (client.player != null) {

                        Identifier itemId = BuiltInRegistries.ITEM.getKey(client.player.getMainHandItem().getItem());
                        ParryItemConfig itemConfig = PARRY_ITEM_CONFIGS.get(itemId);
                        if (itemConfig != null && itemConfig.parryItem != null && itemConfig.parryItem) {
                            if (!client.player.getCooldowns().isOnCooldown(client.player.getMainHandItem()) && !client.player.isUsingItem()) {
                                ParryState state = (ParryState) client.player;
                                state.engarde$setParrying(!state.engarde$isParrying());

                                ClientPlayNetworking.send(new ParryPayload(true));
                            }
                        }
                    }
                }
            } else {
                boolean isParryKeyDown = parry.isDown();
                while (parry.consumeClick()) {
                    if (client.player != null) {

                        Identifier itemId = BuiltInRegistries.ITEM.getKey(client.player.getMainHandItem().getItem());
                        ParryItemConfig itemConfig = PARRY_ITEM_CONFIGS.get(itemId);
                        if (itemConfig != null && itemConfig.parryItem != null && itemConfig.parryItem) {
                            if (!client.player.getCooldowns().isOnCooldown(client.player.getMainHandItem()) && !client.player.isUsingItem()) {
                                ParryState state = (ParryState) client.player;
                                state.engarde$setParrying(true);

                                ClientPlayNetworking.send(new ParryPayload(true));
                            }
                        }
                    }
                }
                if (client.player != null) {
                    ParryState state = (ParryState) client.player;
                    if (!isParryKeyDown && state.engarde$isParrying()) {
                        state.engarde$setParrying(false);
                        ClientPlayNetworking.send(new ParryPayload(false));
                    }
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

            if (client.player != null) {
                ItemStack itemStack = client.player.getMainHandItem();
                boolean itemOnCooldown = client.player.getCooldowns().isOnCooldown(itemStack);

                if (itemOnCooldown && !wasItemOnCooldown) {
                    ParryState state = (ParryState) client.player;
                    state.engarde$setParrying(false);

                    ClientPlayNetworking.send(new ParryPayload(false));
                }
                wasItemOnCooldown = itemOnCooldown;
            }
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.CROSSHAIR, EnGarde.id("parry_indicator"), new ParryHudElement());

        ClientPlayNetworking.registerGlobalReceiver(ParrySyncPayload.TYPE, (payload, context) -> context.client().execute(() -> {
            if (context.client().level == null) return;
            var entity = Objects.requireNonNull(context.client().level).getEntity(payload.entityId());
            if (entity instanceof ParryState parryState) {
                parryState.engarde$setParrying(payload.isParrying());
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(ItemConfigSyncPayload.TYPE, ((payload, context) -> context.client().execute(() -> {
            PARRY_ITEM_CONFIGS.clear();
            PARRY_ITEM_CONFIGS.putAll(payload.itemConfigs());
        })));

        ClientPlayNetworking.registerGlobalReceiver(BowPullSyncPayload.TYPE, ((payload, context) -> context.client().execute(() -> {
            if (context.client().level == null) return;
            var entity = Objects.requireNonNull(context.client().level).getEntity(payload.entityId());
            if (entity instanceof BowPullState pullState) {
                pullState.engarde$setPullState(payload.pullState());
            }
        })));

        CustomTooltips.register();
    }
}
