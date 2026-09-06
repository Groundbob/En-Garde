package net.engarde.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.engarde.parry.ItemPose;
import net.engarde.parry.ParryPose;
import net.engarde.parry.ParryRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {

    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    private void engarde$rotateHeavyWeapons(ArmedEntityRenderState state, ItemStackRenderState item, ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        int armDirection = arm == HumanoidArm.RIGHT ? 1 : -1;
        if (engarde$shouldRotateParry(state, itemStack)) {
            poseStack.translate(-0.1 * armDirection,0.0f,-0.07f);

            poseStack.mulPose(Axis.YP.rotationDegrees(75 * armDirection));
            poseStack.mulPose(Axis.XP.rotationDegrees(10));
        }
        if (engarde$shouldRotateHeld(state, itemStack)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-25 * armDirection));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-10));
        }
    }

    @Unique
    private static boolean engarde$shouldRotateParry(ArmedEntityRenderState state, ItemStack itemStack) {
        if (!((ParryRenderState) state).engarde$isParrying()) {
            return false;
        }
        return ParryPose.getParryPose(itemStack.getItem()) == ParryPose.DOUBLE_HANDED;
    }

    @Unique
    private static boolean engarde$shouldRotateHeld(ArmedEntityRenderState state, ItemStack itemStack) {
        if (((ParryRenderState) state).engarde$isParrying()) return false;
        return ItemPose.getItemPose(itemStack.getItem()) == ItemPose.DOUBLE_HANDED;
    }
}
