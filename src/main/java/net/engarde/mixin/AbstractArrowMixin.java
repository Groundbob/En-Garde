package net.engarde.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.engarde.config.EnGardeConfig;
import net.engarde.reworks.TridentSlotAccess;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {
    @WrapOperation(method = "tryPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean engarde$tridentSlotPickup(Inventory instance, ItemStack itemStack, Operation<Boolean> original) {
        if (!EnGardeConfig.loadConfig().defaultTridentLoyalty || !(itemStack.is(Items.TRIDENT)) || !(this instanceof TridentSlotAccess thrownTridentAccess)) {
            return original.call(instance, itemStack);
        }

        Player player = instance.player;

        if (thrownTridentAccess.engarde$isSourceOffhand()) {
            if (player.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
                player.setItemSlot(EquipmentSlot.OFFHAND, itemStack);
                return true;
            }
        } else {
            int slot = thrownTridentAccess.engarde$getSourceSlot();
            if (slot >= 0 && slot < 9 && instance.getItem(slot).isEmpty()) {
                instance.setItem(slot, itemStack);
                return true;
            }
        }
        return original.call(instance, itemStack);
    }
}
