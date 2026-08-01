package net.engarde;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EnGarde implements ModInitializer {
	public static final String MOD_ID = "en-garde";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing En Garde!");

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
					client.player.sendSystemMessage(Component.literal("You have parried!"));
				}
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
