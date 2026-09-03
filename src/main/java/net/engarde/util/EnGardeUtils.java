package net.engarde.util;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.engarde.data.EnGardeDamageTypeTags;
import net.engarde.data.EnGardeItemTagProvider;
import net.engarde.parry.AttackStrengthAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public static void parryCooldown(LivingEntity entity) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(entity.getMainHandItem().getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);

        int parryCooldownInTicks = (itemConfig != null && itemConfig.shield != null && itemConfig.shield.cooldown() != null) ? itemConfig.shield.cooldown() : 100;
        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(entity.getMainHandItem(), parryCooldownInTicks);
        }
    }

    public static boolean attackDisablesParry(DamageSource source) {
        if (!(source.getEntity() instanceof Player player)) return true;

        ItemStack attackerItemStack = player.getMainHandItem();
        if (!attackerItemStack.is(EnGardeItemTagProvider.DEFLECTING_WEAPON)) {
            return false;
        } else {
            float strength = ((AttackStrengthAccessor) player).engarde$getLastAttackStrength();
            return strength > 0.9f;
        }
    }
}
