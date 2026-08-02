package net.engarde.mixin;

import net.engarde.accessor.ParryAccessor;
import net.engarde.parry.ParryPose;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends HumanoidRenderState> {
    @Final
    @Shadow public ModelPart rightArm;
    @Final
    @Shadow public ModelPart leftArm;

    @Inject(method = "poseRightArm", at = @At("HEAD"),cancellable = true)
    private void engarde$parryRight(T state, CallbackInfo ci) {
        if (!(state instanceof ParryAccessor parryAccessor) || !parryAccessor.engarde$isParrying()) return;
        
        switch (ParryPose.getParryPose(state.getMainHandItemStack())) {
            case SINGLE_HANDED_PARRY -> {
                if (state.mainArm.equals(HumanoidArm.RIGHT)) {
                    this.rightArm.xRot = -1.2f;
                    this.rightArm.yRot = -0.5f;
                }
            }
            case DOUBLE_HANDED_PARRY -> {
                if (state.mainArm.equals(HumanoidArm.RIGHT)) {
                    this.rightArm.xRot = -1.2f;
                    this.rightArm.yRot = -0.6f;
                } else {
                    this.rightArm.xRot = -1.2f;
                    this.rightArm.yRot = -0.6f;
                }
            }
            case null -> {}
        }

        ci.cancel();
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void engarde$parryLeft(T state, CallbackInfo ci) {
        if (!(state instanceof ParryAccessor parryAccessor) || !parryAccessor.engarde$isParrying()) return;

        switch (ParryPose.getParryPose(state.getMainHandItemStack())) {
            case SINGLE_HANDED_PARRY -> {
                if (state.mainArm.equals(HumanoidArm.LEFT)) {
                    this.leftArm.xRot = -1.2f;
                    this.leftArm.yRot = 0.5f;
                }
            }
            case DOUBLE_HANDED_PARRY -> {
                if (state.mainArm.equals(HumanoidArm.LEFT)) {
                    this.leftArm.xRot = -1.2f;
                    this.leftArm.yRot = 0.6f;
                } else {
                    this.leftArm.xRot = -1.2f;
                    this.leftArm.yRot = 0.6f;
                }
            }
            case null -> {}
        }

        ci.cancel();
    }

}
