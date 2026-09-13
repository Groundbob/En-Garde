package net.engarde.mixin;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow
    @Final
    public Container container;

    @Shadow
    public abstract int getContainerSlot();

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void engarde$preventHeavyOffhand(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = Minecraft.getInstance().player;
        Identifier itemIdMouse = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfigMouse = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemIdMouse);

        if (this.container instanceof Inventory && this.getContainerSlot() == 40 && itemConfigMouse != null && itemConfigMouse.heavyItem != null && itemConfigMouse.heavyItem) {
            cir.setReturnValue(false);
        }
        if (player != null) {
            Identifier itemIdHand = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem());
            ParryItemConfig itemConfigHand = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemIdHand);
            if ((this.container instanceof Inventory && this.getContainerSlot() == 40 && itemConfigHand != null && itemConfigHand.heavyItem != null && itemConfigHand.heavyItem)
                    || (this.container instanceof Inventory && this.getContainerSlot() == player.getInventory().getSelectedSlot() && itemConfigMouse != null && itemConfigMouse.heavyItem != null && itemConfigMouse.heavyItem && !player.getOffhandItem().isEmpty())
            ) {
                cir.setReturnValue(false);
            }
        }
    }
}
