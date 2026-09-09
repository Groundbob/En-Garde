package net.engarde.mixin.bowrework;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.engarde.config.EnGardeConfig;
import net.engarde.reworks.bow.BowPullState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public class BowItemMixin extends Item{
    public BowItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"))
    private void engarde$initialPull(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof BowPullState pullState) {
            pullState.engarde$setPullState(5);
        }
    }

    @Inject(method = "releaseUsing", at = @At("TAIL"))
    private void engarde$resetManualPull(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof BowPullState pullState) {
            pullState.engarde$setPullState(0);
        }
    }

    @ModifyVariable(method = "releaseUsing", at = @At(value = "STORE"), name = "timeHeld")
    private int engarde$customBowPull(int timeHeld, ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {
        if (EnGardeConfig.loadConfig().enableBowRework && entity instanceof BowPullState pullState) {
            return pullState.engarde$getPullState();
        } else return timeHeld;
    }

    @ModifyExpressionValue(method = "releaseUsing" , at = @At(value = "CONSTANT", args = "doubleValue=0.1"))
    private double engarde$increaseBowThreshold(double original) {
        if (EnGardeConfig.loadConfig().enableBowRework) {
            return 0.3;
        }
        return original;
    }

    /* ACCURACY FIX */

    @Unique
    private float engarde$lastPower;

    @ModifyVariable(method = "releaseUsing", at = @At(value = "STORE"), name = "pow")
    private float engarde$capturePower(float pow) {
        this.engarde$lastPower = pow;
        return pow;
    }

    @ModifyExpressionValue(method = "releaseUsing", at = @At(value = "CONSTANT", args = "floatValue=1.0", ordinal =0))
    private float engarde$bowAccuracy(float original) {
        if (EnGardeConfig.loadConfig().enableBowRework) {
            return 2 - 2* this.engarde$lastPower * this.engarde$lastPower;
        }
        return original;
    }

    /* POWER BUFF */

    @ModifyExpressionValue(method = "releaseUsing", at = @At(value = "CONSTANT", args = "floatValue=3.0"))
    private float engarde$bowPower(float original) {
        if (EnGardeConfig.loadConfig().enableBowRework) {
            return 4.5f * engarde$lastPower;
        }
        return original;
    }

    //TODO Remove power enchantment
}
