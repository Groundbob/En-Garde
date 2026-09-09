package net.engarde.mixin.bowrework;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.engarde.config.EnGardeConfig;
import net.engarde.reworks.bow.BowPullState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    public AbstractClientPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @ModifyExpressionValue(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getTicksUsingItem()I"))
    private int engarde$customBowUsingTicks(int original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (EnGardeConfig.loadConfig().enableBowRework && player instanceof BowPullState pullState) {
            return pullState.engarde$getPullState();
        }
        return original;
    }
}
