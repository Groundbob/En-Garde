package net.engarde;

import net.engarde.networking.ParryPayload;
import net.engarde.parry.ParryState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EnGarde implements ModInitializer {
	public static final String MOD_ID = "en-garde";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Initializing En Garde!");

		PayloadTypeRegistry.serverboundPlay().register(ParryPayload.TYPE, ParryPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ParryPayload.TYPE, (payload, context) -> {
			context.server().execute(() -> {
				if (context.player() instanceof ParryState parryStatePlayer) {

					boolean isCurrentlyParrying = parryStatePlayer.engarde$isParrying();

					if (payload.isParrying()) {
						boolean newParryState = !isCurrentlyParrying;
						parryStatePlayer.engarde$setParrying(newParryState);

						if (newParryState) {
							context.player().sendSystemMessage(Component.literal("Parry Stance: ON"));
						} else {
							context.player().sendSystemMessage(Component.literal("Parry Stance: OFF"));
						}
					} else {
						if (isCurrentlyParrying) {
							parryStatePlayer.engarde$setParrying(false);
							context.player().sendSystemMessage(Component.literal("Parry Cancelled (Menu opened)"));
						}

					}
				}
			});
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
