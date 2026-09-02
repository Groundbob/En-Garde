package net.engarde.parry;

import net.engarde.EnGarde;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface EnGardeDamageTypeTags {
    TagKey<DamageType> BYPASSES_PARRY = create("bypasses_parry");

    private static TagKey<DamageType> create(final String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, EnGarde.id(name));
    }
}

