package net.engarde.util;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.engarde.parry.EnGardeDamageTypeTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EnGardeUtils {

    public static boolean parryBlocksDamage(LivingEntity entity, DamageSource source) {
        if (source.is(EnGardeDamageTypeTags.BYPASSES_PARRY)) return false;

        Vec3 sourcePosition = source.getSourcePosition();
        double angle;
        if (sourcePosition != null) {
            Vec3 viewVector = entity.calculateViewVector(0.0f, entity.getYHeadRot());
            Vec3 vectorTo = sourcePosition.subtract(entity.position());
            vectorTo = new Vec3(vectorTo.x, 0.0, vectorTo.z).normalize();
            angle = Math.acos(vectorTo.dot(viewVector));
        } else {
            angle = (float) Math.PI;
        }

        Identifier itemId = BuiltInRegistries.ITEM.getKey(entity.getMainHandItem().getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
        if (itemConfig == null || itemConfig.shield == null || itemConfig.shield.size() == null) return !(angle > (float) (Math.PI / 2));
        return !(angle > (float) (Math.PI / 180.0) * itemConfig.shield.size());
    }
}
