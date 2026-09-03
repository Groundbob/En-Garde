package net.engarde.mixin;

import net.engarde.parry.ParryState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void engarde$parryDisablesAttack(CallbackInfoReturnable<Boolean> cir) {
        if (this.player instanceof ParryState parryState && parryState.engarde$isParrying()) {
            cir.cancel();
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void engarde$parryDisablesUse(CallbackInfo ci) {
        if (this.player instanceof ParryState parryState && parryState.engarde$isParrying()) {
            ci.cancel();
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void engarde$parryDisablesMine(boolean down, CallbackInfo ci) {
        if (this.player instanceof ParryState parryState && parryState.engarde$isParrying()) {
            ci.cancel();
        }
    }
}
