package net.engarde.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
    private void engarde$rotateHeavyWeaponsForParry(ArmedEntityRenderState state, ItemStackRenderState item, ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        if (engarde$shouldRotate(state, itemStack)) {
            poseStack.translate((arm == HumanoidArm.RIGHT)?-0.1f:0.1f,0.0f,-0.07f);

            float rotation = (arm == HumanoidArm.RIGHT) ? 75f : -75f;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            poseStack.mulPose(Axis.XP.rotationDegrees(10f));
        }
    }

    @Unique
    private static boolean engarde$shouldRotate(ArmedEntityRenderState state, ItemStack itemStack) {
        if (!((ParryRenderState) state).engarde$isParrying()) {
            return false;
        }
        return ParryPose.getParryPose(itemStack) == ParryPose.DOUBLE_HANDED;
    }
}
