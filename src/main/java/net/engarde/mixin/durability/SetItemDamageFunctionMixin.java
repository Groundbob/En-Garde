package net.engarde.mixin.durability;

import net.engarde.config.EnGardeConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SetItemDamageFunction.class)
public class SetItemDamageFunctionMixin {
    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void engarde$disableDurability(ItemStack itemStack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (EnGardeConfig.loadConfig().disableDurability) {
            cir.cancel();
        }
    }
}
