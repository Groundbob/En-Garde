package net.engarde.mixin;

import net.engarde.EnGarde;
import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.engarde.util.EnGardeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.Objects;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    /* HEAVY SLOT LOCK */

    @Shadow
    @Nullable
    protected Slot hoveredSlot;
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

    /* PRESSING OFFHAND KEYBIND */

    @Inject(method = "checkHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void engarde$heavyOffhandLock(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem() && EnGardeUtils.isHeavyItem(this.hoveredSlot.getItem()) && Minecraft.getInstance().options.keySwapOffhand.matches(event)) {
            cir.setReturnValue(false);
        }
        if (player != null) {
            Identifier itemId = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem());
            ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
            if (itemConfig != null && itemConfig.heavyItem != null && itemConfig.heavyItem) {
                cir.setReturnValue(false);
            }
        }
    }
}
