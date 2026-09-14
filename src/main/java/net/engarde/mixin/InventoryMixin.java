package net.engarde.mixin;

import net.engarde.util.EnGardeUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void engarde$heavyPickupFix(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (slot != -1) return;
        if (itemStack.isEmpty()) return;

        Inventory inventory = (Inventory) (Object) this;
        Player player = inventory.player;

        ItemStack offhand = player.getOffhandItem();
        if (offhand.isEmpty()) return;

        if (!EnGardeUtils.isHeavyItem(itemStack)) return;

        int slotException = inventory.getSelectedSlot();
        int originalCount = itemStack.getCount();

        for (int i = 0; i < 36 && !itemStack.isEmpty(); i++) {
            if (i == slotException) continue;
            ItemStack slotItemStack = inventory.getItem(i);
            if (!slotItemStack.isEmpty() && slotItemStack.isStackable() && ItemStack.isSameItemSameComponents(slotItemStack, itemStack)) {
                int room = inventory.getMaxStackSize(slotItemStack) - slotItemStack.getCount();
                if (room < 0) {
                    int moved = Math.min(room, itemStack.getCount());
                    slotItemStack.grow(moved);
                    itemStack.shrink(moved);
                }
            }
        }

        for (int i = 0; i < 36 && !itemStack.isEmpty(); i++) {
            if (i == slotException) continue;
            if (inventory.getItem(i).isEmpty()) {
                int toPlace = Math.min(inventory.getMaxStackSize(itemStack), itemStack.getCount());
                inventory.setItem(i, itemStack.split(toPlace));
            }
        }

        cir.setReturnValue(itemStack.getCount() < originalCount);
        cir.cancel();
    }
}
