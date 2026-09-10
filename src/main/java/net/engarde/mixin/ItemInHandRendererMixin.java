package net.engarde.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.engarde.config.EnGardeConfig;
import net.engarde.parry.ParryState;
import net.engarde.reworks.bow.BowPullState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    /* ENGARDE PARRY */

    @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", ordinal = 1))
    private void engarde$firstPersonParryPose(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        int direction = player.getMainArm().equals(HumanoidArm.RIGHT) ? 1 : -1;
        if (hand != InteractionHand.MAIN_HAND) return;

        if (player instanceof ParryState parryState && parryState.engarde$isParrying()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(50 * direction));
            poseStack.mulPose(Axis.YP.rotationDegrees(90 * direction));
            poseStack.mulPose(Axis.XP.rotationDegrees(-15));
        }
    }

    /* BOW SHAKE */

    @ModifyArg(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 6), index = 1)
    private float engarde$shakeReduction(float y) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (EnGardeConfig.loadConfig().enableBowRework && player instanceof BowPullState pullState) {
            return (float) (y * (1.2- Math.pow((float) pullState.engarde$getPullState() /20, 6)));
        }
        return y;
    }

    /* BOW ZOOM */

    @ModifyArgs(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal =1), slice = @Slice(from = @At(value = "CONSTANT", args = "floatValue=-13.935"), to = @At(value = "CONSTANT", args = "floatValue=-55.0")))
    private void engarde$powerToPullTranslate(Args args) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (EnGardeConfig.loadConfig().enableBowRework && player instanceof BowPullState pullState) {
            float pullPower = pullState.engarde$getPullState() / 20f;
            args.set(0, pullPower * 0.0F);
            args.set(1, pullPower * 0.0F);
            args.set(2, pullPower * 0.04F);
        }
    }

    @ModifyArgs(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", ordinal =0), slice = @Slice(from = @At(value = "CONSTANT", args = "floatValue=-13.935"), to = @At(value = "CONSTANT", args = "floatValue=-55.0")))
    private void engarde$powerToPullScale(Args args) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (EnGardeConfig.loadConfig().enableBowRework && player instanceof BowPullState pullState) {
            float pullPower = pullState.engarde$getPullState() / 20f;
            args.set(2, 1.0F + pullPower * 0.2F);
        }
    }
}