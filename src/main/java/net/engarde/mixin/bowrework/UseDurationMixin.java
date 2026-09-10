package net.engarde.mixin.bowrework;

import net.engarde.reworks.bow.BowPullState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UseDuration.class)
public abstract class UseDurationMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void engarde$overrideDuration(ItemStack itemStack, ClientLevel level, ItemOwner owner, int seed, CallbackInfoReturnable<Float> cir) {
        if (itemStack.getItem() instanceof BowItem && owner instanceof BowPullState pullState) {
            float pull = pullState.engarde$getPullState();
            cir.setReturnValue(pull);
        }
    }
}
