package net.engarde.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.engarde.parry.ParryPose;
import net.engarde.parry.ParryState;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemModelResolver;updateForTopItem(Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/ItemOwner;I)V", shift = At.Shift.AFTER))
    private void engarde$rotateHeavyItems(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext type, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        if (engarde$shouldRotate(livingEntity, itemStack)) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
        }
    }

    @Unique
    private static boolean engarde$shouldRotate(LivingEntity livingEntity, ItemStack itemStack) {
        if (!(livingEntity instanceof ParryState parryState) || !parryState.engarde$isParrying()) {
            return false;
        }
        return ParryPose.getParryPose(itemStack) == ParryPose.DOUBLE_HANDED;
    }
}