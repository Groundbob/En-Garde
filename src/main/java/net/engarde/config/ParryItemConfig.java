package net.engarde.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.engarde.parry.ParryPose;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Locale;
import java.util.Optional;

public class ParryItemConfig {
    public Identifier template;
    public Boolean parryItem;
    public Boolean heavyItem;
    public Float range;
    public Float speed;
    public Float damage;
    public Float shieldSize;
    public Integer shieldCooldown;
    public ParryPose parryPose;


    private static final Codec<ParryPose> POSE_CODEC = Codec.STRING.xmap(
            s -> ParryPose.valueOf(s.toUpperCase(Locale.ROOT)),
            pose -> pose.id.toLowerCase(Locale.ROOT)
    );
    public static final Codec<ParryItemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("template").forGetter(c -> Optional.ofNullable(c.template)),
            Codec.BOOL.optionalFieldOf("parry_item").forGetter(c -> Optional.ofNullable(c.parryItem)),
            Codec.BOOL.optionalFieldOf("heavy_item").forGetter(c -> Optional.ofNullable(c.heavyItem)),
            Codec.FLOAT.optionalFieldOf("range").forGetter(c -> Optional.ofNullable(c.range)),
            Codec.FLOAT.optionalFieldOf("speed").forGetter(c -> Optional.ofNullable(c.speed)),
            Codec.FLOAT.optionalFieldOf("damage").forGetter(c -> Optional.ofNullable(c.damage)),
            Codec.FLOAT.optionalFieldOf("shield_size").forGetter(c -> Optional.ofNullable(c.shieldSize)),
            Codec.INT.optionalFieldOf("shield_cooldown").forGetter(c -> Optional.ofNullable(c.shieldCooldown)),
            POSE_CODEC.optionalFieldOf("parry_pose").forGetter(c -> Optional.ofNullable(c.parryPose))
    ).apply(instance, (template, parryItem, heavyItem, range, speed, damage, shieldSize, shieldCooldown, parryPose) ->
            new ParryItemConfig(
                    template.orElse(null),
                    parryItem.orElse(null),
                    heavyItem.orElse(null),
                    range.orElse(null),
                    speed.orElse(null),
                    damage.orElse(null),
                    shieldSize.orElse(null),
                    shieldCooldown.orElse(null),
                    parryPose.orElse(null)
            )));

    public static final StreamCodec<FriendlyByteBuf, ParryItemConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), c -> Optional.ofNullable(c.template),
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), c -> Optional.ofNullable(c.parryItem),
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), c -> Optional.ofNullable(c.heavyItem),
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), c -> Optional.ofNullable(c.range),
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), c -> Optional.ofNullable(c.speed),
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), c -> Optional.ofNullable(c.damage),
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), c -> Optional.ofNullable(c.shieldSize),
            ByteBufCodecs.optional(ByteBufCodecs.INT), c -> Optional.ofNullable(c.shieldCooldown),
            ByteBufCodecs.optional(ParryPose.STREAM_CODEC), c -> Optional.ofNullable(c.parryPose),
            (template, parryItem, heavyItem, range, speed, damage, shieldSize, shieldCooldown, parryPose) ->
                    new ParryItemConfig(
                            template.orElse(null),
                            parryItem.orElse(null),
                            heavyItem.orElse(null),
                            range.orElse(null),
                            speed.orElse(null),
                            damage.orElse(null),
                            shieldSize.orElse(null),
                            shieldCooldown.orElse(null),
                            parryPose.orElse(null)
                    )
    );

    public ParryItemConfig(Identifier template, Boolean parryItem, Boolean heavyItem, Float range, Float speed, Float damage, Float shieldSize, Integer shieldCooldown, ParryPose parryPose) {
        this.template = template;
        this.parryItem = parryItem;
        this.heavyItem = heavyItem;
        this.range = range;
        this.speed = speed;
        this.damage = damage;
        this.shieldSize = shieldSize;
        this.shieldCooldown = shieldCooldown;
        this.parryPose = parryPose;
    }

    public ParryItemConfig() {
        this(null, null, null, null, null, null, null, null, null);
    }
}
