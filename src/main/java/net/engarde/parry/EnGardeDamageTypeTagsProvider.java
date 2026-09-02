package net.engarde.parry;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class EnGardeDamageTypeTagsProvider extends TagsProvider<DamageType> {
    public EnGardeDamageTypeTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        this.tag(EnGardeDamageTypeTags.BYPASSES_PARRY)
                .addTag(DamageTypeTags.BYPASSES_ARMOR)
                .addTag(DamageTypeTags.BYPASSES_SHIELD)
                .add(
                        DamageTypes.ARROW,
                        DamageTypes.EXPLOSION,
                        DamageTypes.PLAYER_EXPLOSION,
                        DamageTypes.SPIT,
                        DamageTypes.FIREBALL,
                        DamageTypes.THROWN,
                        DamageTypes.TRIDENT,
                        DamageTypes.WIND_CHARGE,
                        DamageTypes.WITHER_SKULL
                );
    }
}
