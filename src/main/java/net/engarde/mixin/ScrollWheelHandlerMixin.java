package net.engarde.mixin;

import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScrollWheelHandler.class)
public class ScrollWheelHandlerMixin {
    /* HEAVY ITEM SCROLL SKIP */

    @Inject(method = "getNextScrollWheelSelection", at = @At("RETURN"), cancellable = true)
    private static void engarde$skipHeavySlot(double wheel, int currentSelected, int limit, CallbackInfoReturnable<Integer> cir) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.getOffhandItem().isEmpty()) return;

        int direction = wheel > 0 ? -1 : 1;
        int slotCandidate = cir.getReturnValue();
        int safety = 0;

        while (engarde$isHeavy(player, slotCandidate) && safety < limit) {
            slotCandidate = Math.floorMod(slotCandidate + direction, limit);
            safety ++;
        }

        if (safety < limit) {
            cir.setReturnValue(slotCandidate);
        }
    }

    @Unique
    private static boolean engarde$isHeavy(LocalPlayer player, int slot) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(player.getInventory().getItem(slot).getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
        return itemConfig != null && itemConfig.heavyItem != null && itemConfig.heavyItem;
    }
}
