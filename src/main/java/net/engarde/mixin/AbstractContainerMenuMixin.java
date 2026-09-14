package net.engarde.mixin;

import net.engarde.util.EnGardeUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Shadow
    public abstract Slot getSlot(int index);

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void engarde$heavySwapCancel(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        if (containerInput != ContainerInput.SWAP) return;
        if (buttonNum < 0 || buttonNum >= 9) return;
        if (buttonNum != player.getInventory().getSelectedSlot()) return;
        if (player.getOffhandItem().isEmpty()) return;
        if (slotIndex < 0) return;

        ItemStack incoming = this.getSlot(slotIndex).getItem();
        if (incoming.isEmpty()) return;

        if (EnGardeUtils.isHeavyItem(incoming)) {
            ci.cancel();
        }
    }
}
