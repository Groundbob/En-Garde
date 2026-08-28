package net.engarde.config;

import com.mojang.serialization.Codec;
import net.engarde.EnGarde;
import net.engarde.networking.ItemConfigSyncPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ParryItemManager extends SimpleJsonResourceReloadListener<ParryItemConfig> {
    public ParryItemManager(Codec<ParryItemConfig> codec, FileToIdConverter lister) {
        super(codec, lister);
    }


    @Override
    protected void apply(@NonNull Map<Identifier, ParryItemConfig> preparations, @NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        EnGarde.PARRY_ITEM_CONFIGS.clear();
        preparations.keySet().forEach((id) -> EnGarde.PARRY_ITEM_CONFIGS.put(id, getItemConfig(id, preparations)));
        if (EnGarde.SERVER == null) return;
        PlayerLookup.all(EnGarde.SERVER).forEach(player -> ServerPlayNetworking.send(player, new ItemConfigSyncPayload(EnGarde.PARRY_ITEM_CONFIGS)));
    }

    private ParryItemConfig getItemConfig (Identifier id, @NonNull Map<Identifier, ParryItemConfig> preparations) {
        ParryItemConfig itemConfig = new ParryItemConfig();
        ParryItemConfig loaded = preparations.get(id);
        if (loaded==null) return itemConfig;
        if (loaded.template!=null) {
            if (loaded.template.equals(id)) {
                EnGarde.LOGGER.warn("Parry Item : " + id + " called itself as a template!");
            } else {
                itemConfig = getItemConfig(loaded.template, preparations);
            }
        }
        if (loaded.parryItem != null) itemConfig.parryItem = loaded.parryItem;
        if (loaded.heavyItem != null) itemConfig.heavyItem = loaded.heavyItem;
        if (loaded.combat != null) {
            Float range;
            Float speed;
            Float damage;
            if (itemConfig.combat==null) itemConfig.combat = new ParryItemConfig.CombatStats(null, null, null);
            range = itemConfig.combat.range();
            speed = itemConfig.combat.speed();
            damage = itemConfig.combat.damage();
            if (loaded.combat.range() != null) range = loaded.combat.range();
            if (loaded.combat.speed() != null) speed = loaded.combat.speed();
            if (loaded.combat.damage() != null) damage = loaded.combat.damage();
            itemConfig.combat = new ParryItemConfig.CombatStats(range, speed, damage);
        }
        if (loaded.shield != null) {
            Float size;
            Integer cooldown;
            if (itemConfig.shield==null) itemConfig.shield = new ParryItemConfig.ShieldStats(null, null);
            size = itemConfig.shield.size();
            cooldown = itemConfig.shield.cooldown();
            if (loaded.shield.size() != null) size = loaded.shield.size();
            if (loaded.shield.cooldown() != null) cooldown = loaded.shield.cooldown();
            itemConfig.shield = new ParryItemConfig.ShieldStats(size, cooldown);
        }
        if (loaded.parryPose != null) itemConfig.parryPose = loaded.parryPose;
        if (loaded.itemPose != null) itemConfig.itemPose = loaded.itemPose;

        return itemConfig;
    }
}
