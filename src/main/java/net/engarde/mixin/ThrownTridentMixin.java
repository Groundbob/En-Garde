package net.engarde.mixin;

import net.engarde.config.EnGardeConfig;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrownTrident.class)
public class ThrownTridentMixin {
    @ModifyVariable(method = "tick", at = @At("STORE"), name = "loyalty")
    private int engarde$defaultTridentLoyalty(int loyalty) {
        return (EnGardeConfig.loadConfig().defaultTridentLoyalty) ? 3 : 0;
    }
}
