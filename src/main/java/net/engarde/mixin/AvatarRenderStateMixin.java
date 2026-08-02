package net.engarde.mixin;

import net.engarde.accessor.ParryAccessor;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements ParryAccessor {
    @Unique
    private boolean engarde$isParrying;

    @Override
    public boolean engarde$isParrying() {
        return this.engarde$isParrying;
    }

    @Override
    public void engarde$setParrying(boolean isParrying) {
        this.engarde$isParrying = isParrying;
    }
}
