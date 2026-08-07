package net.engarde.mixin;

import net.engarde.parry.ParryRenderState;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ArmedEntityRenderState.class)
public class ArmedEntityRenderStateMixin implements ParryRenderState {

    @Unique
    private boolean isParrying = false;

    @Override
    public boolean engarde$isParrying() {
        return this.isParrying;
    }

    @Override
    public void engarde$setParrying(boolean parrying) {
        this.isParrying = parrying;
    }
}
