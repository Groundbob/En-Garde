package net.engarde.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.engarde.parry.ParryState;
import net.engarde.util.EnGardeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
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

    /* DISABLE INTERACTIONS DURING PARRY */

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

    /* DISABLE HOTBAR KEYBINDS */

    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"))
    private void engarde$cancelHotbarKeybind(Inventory instance, int selected, Operation<Void> original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!(player != null && !player.getOffhandItem().isEmpty() && EnGardeUtils.isHeavyItem(instance.getItem(selected)))) {
            original.call(instance, selected);
        }
    }
}
