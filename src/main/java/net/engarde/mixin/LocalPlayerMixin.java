package net.engarde.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.engarde.parry.ParryState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Inject(method = "isSlowDueToUsingItem", at = @At("RETURN"), cancellable = true)
    private void engarde$isSlowedDueToParrying (CallbackInfoReturnable<Boolean> cir) {
        if (minecraft.player instanceof ParryState parryState && parryState.engarde$isParrying()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "itemUseSpeedMultiplier", at = @At("RETURN"), cancellable = true)
    private void engarde$parrySpeedMultiplier (CallbackInfoReturnable<Float> cir) {
        if (minecraft.player instanceof ParryState parryState && parryState.engarde$isParrying()) {
            cir.setReturnValue(0.7f);
        }
    }

    @ModifyExpressionValue(method = "modifyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean engarde$isParrying(boolean original) {
        if (minecraft.player instanceof ParryState parryState && parryState.engarde$isParrying()) return true;
        return original;
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void engarde$preventDrop(boolean all, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player instanceof ParryState parryState && parryState.engarde$isParrying()) {
            cir.cancel();
        }
    }
}
