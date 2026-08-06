package net.engarde.mixin;

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

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void engarde$parryRight(T state, CallbackInfo ci) {
        if (!(state instanceof ParryState parryState)) return;
        if (!parryState.engarde$isParrying()) {
            switch (ItemPose.getItemPose(state.getMainHandItemStack())) {
                case DOUBLE_HANDED_HELD -> {
                    if (state.mainArm.equals(HumanoidArm.RIGHT)) {
                        this.rightArm.xRot = -1.2f + getHead().xRot / 2;
                        this.rightArm.yRot = -0.6f + getHead().yRot / 4;
                    } else {
                        this.rightArm.xRot = -1.2f + getHead().xRot / 2;
                        this.rightArm.yRot = -0.6f + getHead().yRot / 4;
                    }
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
                    if (state.mainArm.equals(HumanoidArm.RIGHT)) {
                        this.rightArm.xRot = (float) (-Math.PI / 2) + getHead().xRot + 0.3F;
                        this.rightArm.yRot = -0.35f + getHead().yRot;
                        ci.cancel();
                    }
                }

                case DOUBLE_HANDED_PARRY -> {
                    if (state.mainArm.equals(HumanoidArm.RIGHT)) {
                        this.rightArm.xRot = -1.5f + getHead().yRot / 4;
                        this.rightArm.yRot = -0.3f;
                        this.rightArm.zRot = 1.5f;
                        ci.cancel();
                    } else {
                        this.rightArm.xRot = -1.3f;
                        this.rightArm.yRot = -0.2f + getHead().yRot / 2;
                        ci.cancel();
                    }
                }
                case null -> {
                }
            }

        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void engarde$parryLeft(T state, CallbackInfo ci) {
        if (!(state instanceof ParryState parryState)) return;
        if (!parryState.engarde$isParrying()) {
            switch (ItemPose.getItemPose(state.getMainHandItemStack())) {
                case DOUBLE_HANDED_HELD -> {
                    if (state.mainArm.equals(HumanoidArm.LEFT)) {
                        this.leftArm.xRot = -1.2f + getHead().xRot / 2;
                        this.leftArm.yRot = 0.6f + getHead().yRot / 4;
                    } else {
                        this.leftArm.xRot = -1.2f + getHead().xRot / 2;
                        this.leftArm.yRot = 0.6f + getHead().yRot / 4;
                    }
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
                    if (state.mainArm.equals(HumanoidArm.LEFT)) {
                        this.leftArm.xRot = (float) (-Math.PI / 2) + getHead().xRot + 0.3F;
                        this.leftArm.yRot = 0.35f + getHead().yRot;
                        ci.cancel();
                    }
                }
                case DOUBLE_HANDED_PARRY -> {
                    if (state.mainArm.equals(HumanoidArm.LEFT)) {
                        this.leftArm.xRot = -1.5f - getHead().yRot / 4;
                        this.leftArm.yRot = 0.3f;
                        this.leftArm.zRot = -1.5f;
                        ci.cancel();
                    } else {
                        this.leftArm.xRot = -1.3f;
                        this.leftArm.yRot = 0.2f + getHead().yRot / 2;
                        ci.cancel();
                    }
                }
                case null -> {
                }
            }

        }

    }
}
