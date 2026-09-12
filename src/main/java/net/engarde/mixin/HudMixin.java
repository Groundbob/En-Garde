package net.engarde.mixin;

import net.engarde.EnGarde;
import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.engarde.parry.ParryState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    /* CANCEL ATTACK INDICATOR */

    @Inject(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;", shift = At.Shift.AFTER), cancellable = true)
    private void engarde$cancelAttackIndicator(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.player instanceof ParryState parryState && parryState.engarde$isParrying()) ci.cancel();
    }

    /* HEAVY LOCK */

    @Unique
    private static final Identifier SLOT_LOCK = EnGarde.id("textures/gui/slot_lock.png");

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void engarde$heavySlotLock(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed, CallbackInfo ci) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
        if (!player.getOffhandItem().isEmpty() && itemConfig != null && itemConfig.heavyItem != null && itemConfig.heavyItem) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_LOCK, x, y, 0, 0, 16, 16, 16, 16);
        }
    }
}
