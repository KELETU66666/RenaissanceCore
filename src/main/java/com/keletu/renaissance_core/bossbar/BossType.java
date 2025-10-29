package com.keletu.renaissance_core.bossbar;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.util.ResourceLocation;

/*
 * From mod EnderModpackTweaks by MasterEnderman
 * Link to it's repo: https://github.com/Ender-Development/EnderModpackTweaks
 * Those codes under MIT License.
 * Modified by keletu.
 *
 * */
public class BossType {
    private final String entity;
    private final ResourceLocation overlay;
    private final ResourceLocation bar;
    private final int barOffsetY;
    private final int barOffsetX;
    private final int barWidth;
    private final int overlayWidth;
    private final int overlayHeight;
    private final ResourceLocation background;
    private final int hX;
    private final int hY;
    private final int healthbar;

    public BossType(String entity, String overlay, String bar, int barOffsetY, int barOffsetX, int barWidth, int overlayWidth, int overlayHeight, int hX, int hY, int healthbar) {
        this.entity = entity;
        this.overlay = new ResourceLocation(RenaissanceCore.MODID, String.format("textures/gui/boss_bars/%s.png", overlay));
        this.bar = new ResourceLocation(RenaissanceCore.MODID, String.format("textures/gui/bars/%s.png", bar));
        this.barOffsetY = barOffsetY;
        this.barOffsetX = barOffsetX;
        this.barWidth = barWidth;
        this.overlayWidth = overlayWidth;
        this.overlayHeight = overlayHeight;
        this.background = new ResourceLocation(RenaissanceCore.MODID, "textures/gui/bars/background.png");
        this.hX = hX;
        this.hY = hY;
        this.healthbar = healthbar;
    }

    public BossType(String entity, String overlay, String bar, int barOffsetY, int barOffsetX, int barWidth, int overlayWidth, int overlayHeight, String background, int hX, int hY, int healthbar) {
        this.entity = entity;
        this.overlay = new ResourceLocation(RenaissanceCore.MODID, String.format("textures/gui/boss_bars/%s.png", overlay));
        this.bar = new ResourceLocation(RenaissanceCore.MODID, String.format("textures/gui/bars/%s.png", bar));
        this.barOffsetY = barOffsetY;
        this.barOffsetX = barOffsetX;
        this.barWidth = barWidth;
        this.overlayWidth = overlayWidth;
        this.overlayHeight = overlayHeight;
        this.background = new ResourceLocation(RenaissanceCore.MODID, String.format("textures/gui/bars/%s.png", background));
        this.hX = hX;
        this.hY = hY;
        this.healthbar = healthbar;
    }

    public String getEntity() {
        return entity;
    }

    public ResourceLocation getOverlay() {
        return overlay;
    }

    public ResourceLocation getBar() {
        return bar;
    }

    public int getBarOffsetY() {
        return barOffsetY;
    }

    public int getBarOffsetX() {
        return barOffsetX;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public int getOverlayWidth() {
        return overlayWidth;
    }

    public int getOverlayHeight() {
        return overlayHeight;
    }

    public ResourceLocation getBackground() {
        return background;
    }

    public int getHealthBarOffset() {
        return healthbar;
    }

    public int getHealthBarLength(){
        return hX;
    }

    public int getHealthBarWidth(){
        return hY;
    }
}