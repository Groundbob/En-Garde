package net.engarde;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.engarde.config.ParryItemManager;
import net.engarde.networking.ItemConfigSyncPayload;
import net.engarde.networking.ParryPayload;
import net.engarde.networking.ParrySyncPayload;
import net.engarde.parry.ParryState;
import net.engarde.sound.CustomSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class EnGarde implements ModInitializer {
	public static final String MOD_ID = "en-garde";
	public static MinecraftServer SERVER;
	public static final Map<Identifier, ParryItemConfig> PARRY_ITEM_CONFIGS = new HashMap<>();

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Initializing En Garde!");

		ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);

		ServerPlayerEvents.JOIN.register(player -> ServerPlayNetworking.send(player, new ItemConfigSyncPayload(PARRY_ITEM_CONFIGS)));

		PayloadTypeRegistry.serverboundPlay().register(ParryPayload.TYPE, ParryPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ParrySyncPayload.TYPE, ParrySyncPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ItemConfigSyncPayload.TYPE, ItemConfigSyncPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ParryPayload.TYPE, (payload, context) -> context.server().execute(() -> {
            if (context.player() instanceof ParryState parryStatePlayer) {
                ServerPlayer serverPlayer = context.player();

                Item mainHandItem = serverPlayer.getMainHandItem().getItem();
                Identifier itemId = BuiltInRegistries.ITEM.getKey(mainHandItem);
                ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);

                if ((itemConfig == null || !itemConfig.parryItem)&&payload.isParrying()) return;

                boolean before = parryStatePlayer.engarde$isParrying();
                boolean after = before;

                if (payload.isParrying()) {
                    after = !before;
                } else if (before) {
                        after = false;
                }

                if (after != before) {
                    parryStatePlayer.engarde$setParrying(after);
                    broadcastParryState(serverPlayer, after);
                }
            }
        }));

		EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
			if (entity instanceof ParryState parryState && parryState.engarde$isParrying()) {
				ServerPlayNetworking.send(player, new ParrySyncPayload(entity.getId(), true));
			}
		});

		ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id("parry_items"), new ParryItemManager(ParryItemConfig.CODEC, new FileToIdConverter("parry_items", ".json")));

		CustomSounds.initialize();
	}

	public static void broadcastParryState(ServerPlayer player, boolean isParrying) {
		ParrySyncPayload sync = new ParrySyncPayload(player.getId(), isParrying);
		for (ServerPlayer tracker : PlayerLookup.tracking(player)) {
			ServerPlayNetworking.send(tracker, sync);
		}
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
