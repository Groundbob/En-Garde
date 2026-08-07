package net.engarde.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.engarde.parry.ParryPose;
import net.engarde.parry.ParryState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {

    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    private void engarde$rotateHeavyWeaponsThirdPerson(ArmedEntityRenderState state, ItemStackRenderState item, ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        //TODO: Make this check for whether you are parrying (somehow)
        //TODO: Make the pose actually work
        if (ParryPose.getParryPose(itemStack) == ParryPose.DOUBLE_HANDED_PARRY) {
            //poseStack.translate(0.5f,0.5f,0.5f);

            //float rotation = (arm == HumanoidArm.RIGHT) ? 90f : -90f;
            //poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            //poseStack.translate(-0.5f,-0.5f,-0.5f);
        }
    }

    @Unique
    private static boolean engarde$shouldRotate(LivingEntity livingEntity, ItemStack itemStack) {
        if (!(livingEntity instanceof ParryState parryState) || !parryState.engarde$isParrying()) {
            return false;
        }
        return ParryPose.getParryPose(itemStack) == ParryPose.DOUBLE_HANDED_PARRY;
    }
}
