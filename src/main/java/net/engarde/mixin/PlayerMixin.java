package net.engarde.mixin;

import net.engarde.EnGarde;
import net.engarde.parry.ParryState;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements ParryState {

    @Unique
    private boolean engarde$parrying = false;

    @Unique
    private int engarde$lastSlot = -1;

    @Override
    public boolean engarde$isParrying() {
        return this.engarde$parrying;
    }

    @Override
    public void engarde$setParrying(boolean parrying) {
        this.engarde$parrying = parrying;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void engarde$onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        Inventory inventory = player.getInventory();

        if (this.engarde$parrying) {
            if (this.engarde$lastSlot != -1 && this.engarde$lastSlot != inventory.getSelectedSlot()) {
                this.engarde$parrying = false;
                if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    player.sendSystemMessage(Component.literal("Parry Cancelled (switched slots)"));
                    EnGarde.broadcastParryState(serverPlayer, false);
                }
            }
        }

        this.engarde$lastSlot = inventory.getSelectedSlot();
    }
}
