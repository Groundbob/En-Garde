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
        preparations.keySet().forEach((id) -> {
            EnGarde.PARRY_ITEM_CONFIGS.put(id, getItemConfig(id, preparations));
        });
        if (EnGarde.SERVER == null) return;
        PlayerLookup.all(EnGarde.SERVER).forEach(player -> {
            ServerPlayNetworking.send(player, new ItemConfigSyncPayload(EnGarde.PARRY_ITEM_CONFIGS));
        });
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
        if (loaded.range != null) itemConfig.range = loaded.range;
        if (loaded.speed != null) itemConfig.speed = loaded.speed;
        if (loaded.damage != null) itemConfig.damage = loaded.damage;
        if (loaded.shieldSize != null) itemConfig.shieldSize = loaded.shieldSize;
        if (loaded.shieldCooldown != null) itemConfig.shieldCooldown = loaded.shieldCooldown;
        if (loaded.parryPose != null) itemConfig.parryPose = loaded.parryPose;


        return itemConfig;
    }
}
