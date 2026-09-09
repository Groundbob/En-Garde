package net.engarde.mixin.bowrework;

import net.engarde.config.EnGardeConfig;
import net.engarde.networking.BowPullPayload;
import net.engarde.reworks.bow.BowPullState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin{
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void engarde$bowScrollDetect(long handle, double xoffset, double yoffset, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (EnGardeConfig.loadConfig().enableBowRework) {
            if (player != null && player.isUsingItem() && player.getActiveItem().getItem() instanceof BowItem && player instanceof BowPullState pullState) {
                pullState.engarde$setPullState(pullState.engarde$getPullState() - (int) yoffset);
                int newPullState = pullState.engarde$getPullState();
                ClientPlayNetworking.send(new BowPullPayload(newPullState));
                ci.cancel();
            }
        }
    }
}
