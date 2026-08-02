package net.engarde.parry;

import net.engarde.EnGarde;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public class ParryHudElement implements HudElement {
    private static final Identifier PARRY_INDICATOR = EnGarde.id("hud/parry_indicator");
    private static final Identifier PARRY_CROSS = EnGarde.id("hud/parry_cross");

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = Minecraft.getInstance().player;
        Options options = minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            if (player != null && !player.isSpectator()) {
                if (((ParryState) player).engarde$isParrying()) {
                    int x = graphics.guiWidth() / 2 - 8, y = graphics.guiHeight() / 2 + 6;

                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PARRY_INDICATOR, x, y, 16, 16, 0xAAffffff);
                    graphics.blitSprite(RenderPipelines.CROSSHAIR, PARRY_CROSS, x, y, 16, 16);
                }
            }
        }
    }
}
