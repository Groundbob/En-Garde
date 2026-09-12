package net.engarde.mixin.macerework;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.engarde.config.EnGardeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MaceItem.class)
public abstract class MaceItemMixin {
    /* FALL DAMAGE CANCEL */

    @Inject(method = "postHurtEnemy", at = @At("HEAD"), cancellable = true)
    private void engarde$cancelMaceMlg(ItemStack itemStack, LivingEntity mob, LivingEntity attacker, CallbackInfo ci) {
        if (EnGardeConfig.loadConfig().maceRework) {
            ci.cancel();
        }
    }

    @WrapOperation(method = "hurtEnemy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setIgnoreFallDamageFromCurrentImpulse(ZLnet/minecraft/world/phys/Vec3;)V"))
    private void engarde$fallDamageOverride(LivingEntity instance, boolean ignoreFallDamage, Vec3 newImpulseImpactPos, Operation<Void> original) {
        if (!EnGardeConfig.loadConfig().maceRework) {
            original.call(instance, ignoreFallDamage, newImpulseImpactPos);
        }
    }

    @WrapOperation(method = "hurtEnemy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void engarde$fallMovementOverride(LivingEntity instance, Vec3 vec3, Operation<Void> original) {
        if (!EnGardeConfig.loadConfig().maceRework) {
            original.call(instance, vec3);
        }
    }

    /* DAMAGE REWORK */

    /**
     * @author Groundbob
     * @reason Mace rework
     */
    @Overwrite
    public float getAttackDamageBonus(final Entity victim, final float ignoredDamage, final DamageSource damageSource) {
        if (damageSource.getDirectEntity() instanceof LivingEntity attacker) {
            if (attacker.isFallFlying()) {
                return 0.0F;
            }

            double fallDistance = attacker.fallDistance;
            double damage;
            if (EnGardeConfig.loadConfig().maceRework) {
                if (fallDistance <= 1.0) {
                    damage = 0.0;
                } else if (fallDistance <= 2.0) {
                    damage = 0.5;
                } else {
                    damage = 1.0;
                }
            } else {
                if (fallDistance <= 3.0) {
                    damage = 4.0 * fallDistance;
                } else if (fallDistance <= 8.0) {
                    damage = 12.0 + 2.0 * (fallDistance - 3.0);
                } else {
                    damage = 22.0 + fallDistance - 8.0;
                }
            }

            return attacker.level() instanceof ServerLevel level
                    ? (float)(damage + EnchantmentHelper.modifyFallBasedDamage(level, attacker.getWeaponItem(), victim, damageSource, 0.0F) * fallDistance)
                    : (float)damage;
        } else {
            return 0.0F;
        }
    }
}
