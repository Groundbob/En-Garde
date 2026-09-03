package net.engarde.data;

import net.engarde.EnGarde;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface EnGardeItemTagProvider {
    TagKey<Item> DEFLECTING_WEAPON = create();

    private static TagKey<Item> create() {
        return TagKey.create(Registries.ITEM, EnGarde.id("deflecting_weapon"));
    }
}
