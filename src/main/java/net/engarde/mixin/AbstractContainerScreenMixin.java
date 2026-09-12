package net.engarde.mixin;

import net.engarde.EnGarde;
import net.engarde.util.EnGardeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Objects;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Unique
    private static final Identifier SLOT_LOCK = EnGarde.id("textures/gui/slot_lock.png");

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void engarde$heavyWeaponLockColor(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if ((EnGardeUtils.disablesOffhand() && (player != null && (!player.isCreative() && slot.getContainerSlot() == 40) || (Objects.requireNonNull(player).isCreative() && slot.getContainerSlot() == 45)) || EnGardeUtils.isHeavyItemSlot(slot))) {
            graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, new Color(64, 64, 64, 100).getRGB());
        }
    }

    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void engarde$heavyWeaponLock(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if ((EnGardeUtils.disablesOffhand() && (player != null && (!player.isCreative() && slot.getContainerSlot() == 40) || (Objects.requireNonNull(player).isCreative() && slot.getContainerSlot() == 45)) || EnGardeUtils.isHeavyItemSlot(slot))) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_LOCK, slot.x, slot.y, 0, 0, 16, 16, 16, 16);
        }
    }
}
