package net.engarde.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.engarde.parry.ItemPose;
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
    public CombatStats combat;
    public ShieldStats shield;
    public ParryPose parryPose;
    public ItemPose itemPose;

    public record CombatStats(Float range, Float speed, Float damage) {
        public static final Codec<CombatStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("range").forGetter(c -> Optional.ofNullable(c.range)),
                Codec.FLOAT.optionalFieldOf("speed").forGetter(c -> Optional.ofNullable(c.speed)),
                Codec.FLOAT.optionalFieldOf("damage").forGetter(c -> Optional.ofNullable(c.damage))
        ).apply(instance, (range, speed, damage) ->
                new CombatStats(
                        range.orElse(null),
                        speed.orElse(null),
                        damage.orElse(null)
                )));

        public static final StreamCodec<FriendlyByteBuf, CombatStats> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), c-> Optional.ofNullable(c.range),
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), c-> Optional.ofNullable(c.speed),
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), c-> Optional.ofNullable(c.damage),
                (range, speed, damage) ->
                        new CombatStats(
                                range.orElse(null),
                                speed.orElse(null),
                                damage.orElse(null)
                        )
        );
    }

    public record ShieldStats(Float size, Integer cooldown) {
        public static final Codec<ShieldStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("size").forGetter(c -> Optional.ofNullable(c.size)),
                Codec.INT.optionalFieldOf("cooldown").forGetter(c -> Optional.ofNullable(c.cooldown))
        ).apply(instance, (size, cooldown) ->
                new ShieldStats(
                        size.orElse(null),
                        cooldown.orElse(null)
                )));

        public static final StreamCodec<FriendlyByteBuf, ShieldStats> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.FLOAT), c-> Optional.ofNullable(c.size),
                ByteBufCodecs.optional(ByteBufCodecs.INT), c-> Optional.ofNullable(c.cooldown),
                (size, cooldown) ->
                        new ShieldStats(
                                size.orElse(null),
                                cooldown.orElse(null)
                        )
        );
    }

    private static final Codec<ParryPose> PARRY_POSE_CODEC = Codec.STRING.xmap(
            s -> ParryPose.valueOf(s.toUpperCase(Locale.ROOT)),
            pose -> pose.id.toLowerCase(Locale.ROOT)
    );

    private static final Codec<ItemPose> ITEM_POSE_CODEC = Codec.STRING.xmap(
            s -> ItemPose.valueOf(s.toUpperCase(Locale.ROOT)),
            pose -> pose.id.toLowerCase(Locale.ROOT)
    );

    public static final Codec<ParryItemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("template").forGetter(c -> Optional.ofNullable(c.template)),
            Codec.BOOL.optionalFieldOf("parry_item").forGetter(c -> Optional.ofNullable(c.parryItem)),
            Codec.BOOL.optionalFieldOf("heavy_item").forGetter(c -> Optional.ofNullable(c.heavyItem)),
            CombatStats.CODEC.optionalFieldOf("combat").forGetter(c -> Optional.ofNullable(c.combat)),
            ShieldStats.CODEC.optionalFieldOf("shield").forGetter(c -> Optional.ofNullable(c.shield)),
            PARRY_POSE_CODEC.optionalFieldOf("parry_pose").forGetter(c -> Optional.ofNullable(c.parryPose)),
            ITEM_POSE_CODEC.optionalFieldOf("item_pose").forGetter(c -> Optional.ofNullable(c.itemPose))
    ).apply(instance, (template, parryItem, heavyItem, combat, shield, parryPose, itemPose) ->
            new ParryItemConfig(
                    template.orElse(null),
                    parryItem.orElse(null),
                    heavyItem.orElse(null),
                    combat.orElse(null),
                    shield.orElse(null),
                    parryPose.orElse(null),
                    itemPose.orElse(null)
            )));

    public static final StreamCodec<FriendlyByteBuf, ParryItemConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), c -> Optional.ofNullable(c.template),
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), c -> Optional.ofNullable(c.parryItem),
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), c -> Optional.ofNullable(c.heavyItem),
            ByteBufCodecs.optional(CombatStats.STREAM_CODEC), c -> Optional.ofNullable(c.combat),
            ByteBufCodecs.optional(ShieldStats.STREAM_CODEC), c -> Optional.ofNullable(c.shield),
            ByteBufCodecs.optional(ParryPose.STREAM_CODEC), c -> Optional.ofNullable(c.parryPose),
            ByteBufCodecs.optional(ItemPose.STREAM_CODEC), c -> Optional.ofNullable(c.itemPose),
            (template, parryItem, heavyItem, combat, shield, parryPose, itemPose) ->
                    new ParryItemConfig(
                            template.orElse(null),
                            parryItem.orElse(null),
                            heavyItem.orElse(null),
                            combat.orElse(null),
                            shield.orElse(null),
                            parryPose.orElse(null),
                            itemPose.orElse(null)
                    )
    );

    public ParryItemConfig(Identifier template, Boolean parryItem, Boolean heavyItem, CombatStats combat, ShieldStats shield, ParryPose parryPose, ItemPose itemPose) {
        this.template = template;
        this.parryItem = parryItem;
        this.heavyItem = heavyItem;
        this.combat = combat;
        this.shield = shield;
        this.parryPose = parryPose;
        this.itemPose = itemPose;
    }

    public ParryItemConfig() {
        this(null, null, null, null, null, null, null);
    }
}
