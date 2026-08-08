package net.engarde.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.engarde.parry.ParryPose;

import java.util.Locale;

public class ParryItemConfig {
    public boolean parryItem;
    public boolean heavyItem;
    public float rangeModifier;
    public float speedModifier;
    public float shieldSize;
    public int shieldCooldown;
    public ParryPose parryPose;


    private static final Codec<ParryPose> POSE_CODEC = Codec.STRING.xmap(
            s -> ParryPose.valueOf(s.toUpperCase(Locale.ROOT)),
            pose -> pose.name().toLowerCase(Locale.ROOT)
    );
    public static final Codec<ParryItemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("parry_item", false).forGetter(c -> c.parryItem),
            Codec.BOOL.optionalFieldOf("heavy_item", false).forGetter(c -> c.heavyItem),
            Codec.FLOAT.optionalFieldOf("range_modifier", 0.0f).forGetter(c -> c.rangeModifier),
            Codec.FLOAT.optionalFieldOf("speed_modifier", 0.0f).forGetter(c -> c.speedModifier),
            Codec.FLOAT.optionalFieldOf("shield_size", 1.0f).forGetter(c -> c.shieldSize),
            Codec.INT.optionalFieldOf("shield_cooldown", 10).forGetter(c -> c.shieldCooldown),
            POSE_CODEC.optionalFieldOf("parry_pose", ParryPose.SINGLE_HANDED).forGetter(c -> c.parryPose)
    ).apply(instance, ParryItemConfig::new));

    public ParryItemConfig(boolean parryItem, boolean heavyItem, float rangeModifier, float speedModifier, float shieldSize, int shieldCooldown, ParryPose parryPose) {
        this.parryItem = parryItem;
        this.heavyItem = heavyItem;
        this.rangeModifier = rangeModifier;
        this.speedModifier = speedModifier;
        this.shieldSize = shieldSize;
        this.shieldCooldown = shieldCooldown;
        this.parryPose = parryPose;
    }

    public ParryItemConfig() {
        this(false, false, 0.0f, 0.0f, 1.0f, 10, ParryPose.SINGLE_HANDED);
    }
}
