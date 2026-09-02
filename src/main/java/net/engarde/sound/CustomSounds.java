package net.engarde.sound;

import net.engarde.EnGarde;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class CustomSounds {
    private CustomSounds() {

    }

    public static final SoundEvent PARRY_CLANG = registerSound ("parry_clang");
    public static final SoundEvent HEAVY_PARRY_CLANG = registerSound("heavy_parry_clang");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = EnGarde.id(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    public static void initialize() {
        EnGarde.LOGGER.info("Registering " + EnGarde.MOD_ID + " Sounds");
    }
}
