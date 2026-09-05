package net.engarde.mixin.durability;

import net.engarde.config.EnGardeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "processDurabilityChange", at = @At("HEAD"), cancellable = true)
    private void engarde$disableDurability(int amount, ServerLevel level, ServerPlayer player, CallbackInfoReturnable<Integer> cir) {
        if (EnGardeConfig.loadConfig().disableDurability) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    private void engarde$disableDurability(CallbackInfoReturnable<Boolean> cir) {
        if (EnGardeConfig.loadConfig().disableDurability) {
            cir.setReturnValue(false);
        }
    }
}
