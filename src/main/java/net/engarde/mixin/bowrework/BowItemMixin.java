package net.engarde.mixin.bowrework;

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
}
