package net.engarde.mixin;

import net.engarde.EnGarde;
import net.engarde.parry.AttackStrengthAccessor;
import net.engarde.parry.ParryState;
import net.engarde.reworks.bow.BowPullState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements ParryState, AttackStrengthAccessor, BowPullState {

    @Shadow
    public abstract float getAttackStrengthScale(float a);

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
                    EnGarde.broadcastParryState(serverPlayer, false);
                }
            }
        }

        this.engarde$lastSlot = inventory.getSelectedSlot();
    }

    @Unique
    private float engarde$cachedAttackStrength;

    @Override
    public float engarde$getLastAttackStrength() {
        return this.engarde$cachedAttackStrength;
    }

    @Override
    public void engarde$setLastAttackStrength(float strength) {
        this.engarde$cachedAttackStrength = strength;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F", shift = At.Shift.AFTER))
    private void engarde$deflectAttackCheck(Entity entity, CallbackInfo ci) {
        this.engarde$setLastAttackStrength(getAttackStrengthScale(0.5f));
    }

    @Unique
    private int engarde$BowPullState = 0;

    @Override
    public int engarde$getPullState() {
        return engarde$BowPullState;
    }

    @Override
    public void engarde$setPullState(int pullState) {
        engarde$BowPullState = pullState;
    }
}
