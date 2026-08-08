package net.engarde.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HumanoidModel.class)
public interface HumanoidModelAccessor<T extends HumanoidRenderState> {
    @Invoker("poseRightArm")
    void engarde$poseRightArm(T state);

    @Invoker("poseLeftArm")
    void engarde$poseLeftArm(T state);
}
