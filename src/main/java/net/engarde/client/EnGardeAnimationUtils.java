package net.engarde.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;

@Environment(EnvType.CLIENT)
public class EnGardeAnimationUtils {
    public static void animateDoubleHandHeld(final ModelPart rightArm, final ModelPart leftArm, final ModelPart head) {
        rightArm.xRot = -1.2f + head.xRot/2;
        rightArm.yRot = -0.6f + head.yRot / 4;
        leftArm.xRot = -1.2f + head.xRot/2;
        leftArm.yRot = 0.6f + head.yRot/4;
    }

    public static void animateDoubleHandParry(final ModelPart rightArm, final ModelPart leftArm, final ModelPart head, final boolean mainHandRight) {
        ModelPart mainHand = mainHandRight ? rightArm : leftArm;
        ModelPart offhand = mainHandRight ? leftArm : rightArm;
        mainHand.xRot = -1.5f + (mainHandRight ? head.yRot : -head.yRot) / 4;
        mainHand.yRot = (mainHandRight ? -0.3f : 0.3f);
        mainHand.zRot = (mainHandRight ? 1.5f : -1.5f);
        offhand.xRot = -1.3f;
        offhand.yRot = head.yRot / 2;
    }
}
