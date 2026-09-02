package net.engarde.mixin;

import net.engarde.parry.ParryState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin{

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z", shift = At.Shift.BEFORE), cancellable = true)
    private void engarde$parryBlocksCheck(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof ParryState parryState && parryState.engarde$isParrying()) {
            if (!source.is(DamageTypeTags.BYPASSES_SHIELD)) {
                cir.setReturnValue(false);
            }
        }
    }
}
