package net.engarde.mixin;

import net.engarde.accessor.ParryAccessor;
import net.engarde.parry.ParryState;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin <AvatarlikeEntity extends Avatar & ClientAvatarEntity> {


    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void engarde$attachParry(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        if (state instanceof ParryAccessor parryAccessor) {
            boolean parrying = entity instanceof ParryState parryState && parryState.engarde$isParrying();
            parryAccessor.engarde$setParrying(parrying);
        }
    }

}
