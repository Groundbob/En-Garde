package net.engarde.mixin;

import net.engarde.config.EnGardeConfig;
import net.engarde.reworks.TridentSlotAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin implements TridentSlotAccess {
    /* TRIDENT LOYALTY */

    @ModifyVariable(method = "tick", at = @At("STORE"), name = "loyalty")
    private int engarde$defaultTridentLoyalty(int loyalty) {
        return (EnGardeConfig.loadConfig().defaultTridentLoyalty) ? 3 : loyalty;
    }

    @ModifyVariable(method = "tickDespawn", at = @At("STORE"), name = "loyalty")
    private int engarde$tridentLoyaltyDespawn(int loyalty) {
        return (EnGardeConfig.loadConfig().defaultTridentLoyalty) ? 3 : loyalty;
    }

    /* TRIDENT SLOT FIX */

    @Unique private int engarde$sourceSlot = -1;
    @Unique private boolean engarde$sourceOffhand = false;

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void engarde$captureSourceSlot(Level level, LivingEntity owner, ItemStack tridentItem, CallbackInfo ci) {
        if (owner instanceof Player player) {
            if (player.getUsedItemHand() == InteractionHand.OFF_HAND) {
                this.engarde$sourceOffhand = true;
            } else {
                this.engarde$sourceSlot = player.getInventory().getSelectedSlot();
            }
        }
    }

    @Override
    @Unique
    public int engarde$getSourceSlot() {
        return this.engarde$sourceSlot;
    }

    @Override
    @Unique
    public boolean engarde$isSourceOffhand() {
        return this.engarde$sourceOffhand;
    }
}
