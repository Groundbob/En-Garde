package net.engarde.mixin;

import net.engarde.parry.ParryState;
import net.engarde.sound.CustomSounds;
import net.engarde.util.EnGardeUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin{

    @Shadow
    public abstract @Nullable LivingEntity asLivingEntity();

    @Shadow
    public abstract ItemStack getMainHandItem();

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z", shift = At.Shift.BEFORE), cancellable = true)
    private void engarde$parryBlocksCheck(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof ParryState parryState && parryState.engarde$isParrying()) {
            if (EnGardeUtils.parryBlocksDamage(this.asLivingEntity(), source)) {
                if (this.asLivingEntity() != null) {
                    onParry(level, Objects.requireNonNull(this.asLivingEntity()));
                }
                EnGardeUtils.parryCooldown(Objects.requireNonNull(this.asLivingEntity()));
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    public void onParry(final ServerLevel level, final LivingEntity user) {
        level.playSound(
                null,
                user.getX(), user.getY(), user.getZ(),
                CustomSounds.PARRY_CLANG,
                user.getSoundSource(),
                1.0F,
                0.8F + level.getRandom().nextFloat() * 0.4F);
    }
}
