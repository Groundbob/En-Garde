package net.engarde;

import net.engarde.networking.ParryPayload;
import net.engarde.networking.ParrySyncPayload;
import net.engarde.parry.ParryState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EnGarde implements ModInitializer {
	public static final String MOD_ID = "en-garde";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Initializing En Garde!");

		PayloadTypeRegistry.serverboundPlay().register(ParryPayload.TYPE, ParryPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ParrySyncPayload.TYPE, ParrySyncPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ParryPayload.TYPE, (payload, context) -> {
			context.server().execute(() -> {
				if (context.player() instanceof ParryState parryStatePlayer) {
					ServerPlayer serverPlayer = context.player();
					boolean before = parryStatePlayer.engarde$isParrying();
					boolean after = before;

					if (payload.isParrying()) {
						after = !before;
					} else if (before) {
							after = false;
					}

					if (after != before) {
						parryStatePlayer.engarde$setParrying(after);
						serverPlayer.sendSystemMessage(Component.literal(after ? "Parry Stance: ON" : "Parry Stance: OFF"));
						broadcastParryState(serverPlayer, after);
					}
				}
			});
		});

		EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
			if (entity instanceof ParryState parryState && parryState.engarde$isParrying()) {
				ServerPlayNetworking.send(player, new ParrySyncPayload(entity.getId(), true));
			}
		});
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
