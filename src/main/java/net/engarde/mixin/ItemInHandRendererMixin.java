package net.engarde.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.engarde.parry.ParryState;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
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
    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemModelResolver;updateForTopItem(Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/ItemOwner;I)V"))
    private void engarde$rotateForParry(LivingEntity mob, ItemStack itemStack, ItemDisplayContext type, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        if (engarde$shouldRotateParry(mob, itemStack)) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(mob.getMainArm() == HumanoidArm.RIGHT ? 50f : -50f));
            poseStack.mulPose(Axis.YP.rotationDegrees(mob.getMainArm() == HumanoidArm.RIGHT ? 90f : -90f));
            poseStack.mulPose(Axis.XP.rotationDegrees(-15f));
        }
    }

    @Unique
    private static boolean engarde$shouldRotateParry(LivingEntity livingEntity, ItemStack itemStack) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);

        if (!(livingEntity instanceof ParryState parryState) || !parryState.engarde$isParrying()) {
            return false;
        }
        if (itemConfig == null || itemConfig.parryItem == null) return false;
        return itemConfig.parryItem;
    }
}