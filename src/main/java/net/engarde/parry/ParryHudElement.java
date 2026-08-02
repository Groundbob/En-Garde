package net.engarde.parry;

import net.engarde.EnGarde;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class ParryHudElement implements HudElement {
    private static final Identifier PARRY_INDICATOR = EnGarde.id("textures/gui/parry_indicator.png");

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Player player = Minecraft.getInstance().player;
        if (player != null && !player.isSpectator()) {
            int x = graphics.guiWidth()/2 - 8, y = graphics.guiHeight()/2 + 8;

            graphics.blit(RenderPipelines.GUI_TEXTURED, PARRY_INDICATOR, x, y, 16, 16, 16, 16, 16, 16);
        }
    }
}
