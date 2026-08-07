package net.engarde.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;

@Environment(EnvType.CLIENT)
public class EnGardeAnimationUtils {
    public static void animateDoubleHandHeld(final ModelPart rightArm, final ModelPart leftArm) {
        rightArm.xRot = -1.2f;
        rightArm.yRot = -0.6f;
        leftArm.xRot = -1.2f;
        leftArm.yRot = 0.6f;
    }

    public static void animateSingleHandParry(final ModelPart rightArm, final ModelPart leftArm, final ModelPart head, final boolean mainHandRight) {
        ModelPart mainHand = mainHandRight ? rightArm : leftArm;
        mainHand.xRot = (float) (-Math.PI/2) + head.xRot + 0.3f;
        mainHand.yRot = (mainHandRight ? -0.35f : 0.35f) + head.yRot;
    }

    //TODO: Fix bug where when you parry with mace, your offhand doesn't always touch the mace.
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
