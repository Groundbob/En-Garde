package net.engarde.mixin;

import net.engarde.client.EnGardeAnimationUtils;
import net.engarde.client.ItemPose;
import net.engarde.parry.ParryPose;
import net.engarde.parry.ParryState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends HumanoidRenderState> {
    @Final
    @Shadow
    public ModelPart rightArm;
    @Final
    @Shadow
    public ModelPart leftArm;

    @Shadow
    public abstract ModelPart getHead();

    @Shadow
    public abstract ModelPart getArm(HumanoidArm arm);

    @Shadow
    @Final
    public ModelPart head;

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void engarde$parryRight(T state, CallbackInfo ci) {
        boolean isMainArmRight = state.mainArm.equals(HumanoidArm.RIGHT);
        if (!(state instanceof ParryState parryState)) return;
        if (!parryState.engarde$isParrying()) {
            switch (ItemPose.getItemPose(state.getMainHandItemStack())) {
                case DOUBLE_HANDED_HELD -> {
                    EnGardeAnimationUtils.animateDoubleHandHeld(this.rightArm, this.leftArm, this.head);
                    ci.cancel();
                }
                case SPEAR_HELD -> {

                    ci.cancel();
                }

                case null -> {}
            }
        } else {

            switch (ParryPose.getParryPose(state.getMainHandItemStack())) {
                case SINGLE_HANDED_PARRY -> {
                    if (isMainArmRight) {
                        this.rightArm.xRot = (float) (-Math.PI / 2) + getHead().xRot + 0.3F;
                        this.rightArm.yRot = -0.35f + getHead().yRot;
                        ci.cancel();
                    }
                }

                case DOUBLE_HANDED_PARRY -> {
                    EnGardeAnimationUtils.animateDoubleHandParry(this.rightArm, this.leftArm, this.head, isMainArmRight);
                    ci.cancel();
                }
                case null -> {
                }
            }

        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void engarde$parryLeft(T state, CallbackInfo ci) {
        boolean isMainArmRight = state.mainArm.equals(HumanoidArm.RIGHT);
        if (!(state instanceof ParryState parryState)) return;
        if (!parryState.engarde$isParrying()) {
            switch (ItemPose.getItemPose(state.getMainHandItemStack())) {
                case DOUBLE_HANDED_HELD -> {
                    EnGardeAnimationUtils.animateDoubleHandHeld(this.rightArm, this.leftArm, this.head);
                    ci.cancel();
                }
                case SPEAR_HELD -> {

                    ci.cancel();
                }

                case null -> {
                }
            }
        } else {

            switch (ParryPose.getParryPose(state.getMainHandItemStack())) {
                case SINGLE_HANDED_PARRY -> {
                    if (isMainArmRight) {
                        this.leftArm.xRot = (float) (-Math.PI / 2) + getHead().xRot + 0.3F;
                        this.leftArm.yRot = 0.35f + getHead().yRot;
                        ci.cancel();
                    }
                }
                case DOUBLE_HANDED_PARRY -> {
                    EnGardeAnimationUtils.animateDoubleHandParry(this.rightArm, this.leftArm, this.head, isMainArmRight);
                    ci.cancel();
                }
                case null -> {
                }
            }

        }

    }
}
