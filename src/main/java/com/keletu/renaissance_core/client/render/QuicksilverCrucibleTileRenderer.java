package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.blocks.RCBlocks;
import com.keletu.renaissance_core.blocks.TileQuicksilverCrucible;
import com.verdantartifice.thaumicwonders.common.blocks.BlocksTW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

public class QuicksilverCrucibleTileRenderer extends TileEntitySpecialRenderer<TileQuicksilverCrucible> {
    public QuicksilverCrucibleTileRenderer() {
    }

    public void renderEntityAt(TileQuicksilverCrucible cr, double x, double y, double z, float fq) {
        this.renderFluid(cr, x, y, z);
    }

    public void renderFluid(TileQuicksilverCrucible cr, double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + (0.3 + (double) cr.aspects.getAmount(Aspect.EXCHANGE) / 100.0), z + 1.0);
        GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);

        TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(BlocksTW.FLUID_QUICKSILVER.getDefaultState());
        float n = (float) cr.aspects.visSize();
        float recolor = n / 500.0f;
        if (recolor > 0.0f) {
            recolor = 0.5f + recolor / 2.0f;
        }
        if (recolor > 1.0f) {
            recolor = 1.0f;
        }
        if (cr.aspects.getAmount(Aspect.EXCHANGE) > 0) {
            UtilsFX.renderQuadFromIcon(icon, 1.0f, 1.0f - recolor / 3.0f, 1.0f - recolor, 1.0f - recolor / 2.0f, RCBlocks.quicksilver_crucible.getPackedLightmapCoords(cr.getWorld().getBlockState(cr.getPos()), cr.getWorld(), cr.getPos()), 771, 1.0f);
        }
        GL11.glPopMatrix();
    }

    @Override
    public void render(TileQuicksilverCrucible te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderEntityAt(te, x, y, z, partialTicks);
    }
}