package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.blocks.TileDestabilizedCrystal;
import com.keletu.renaissance_core.client.model.ModelCrystal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;

import java.awt.*;
import java.util.Random;

public class RendererTileDestabilizedCrystal extends TileEntitySpecialRenderer {
    private final ModelCrystal model = new ModelCrystal();

    public RendererTileDestabilizedCrystal() {
    }

    private void translateFromOrientation(float x, float y, float z, int orientation) {
        if (orientation == 0) {
            GL11.glTranslatef(x + 0.5F, y + 1.15F, z + 0.5F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 1) {
            GL11.glTranslatef(x + 0.5F, y - 0.1F, z + 0.5F);
        } else if (orientation == 2) {
            GL11.glTranslatef(x + 0.5F, y + 0.5F, z + 1.1F);
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 3) {
            GL11.glTranslatef(x + 0.5F, y + 0.5F, z - 0.1F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 4) {
            GL11.glTranslatef(x + 1.1F, y + 0.5F, z + 0.5F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        } else if (orientation == 5) {
            GL11.glTranslatef(x - 0.1F, y + 0.5F, z + 0.5F);
            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
        }

    }

    private void drawCrystal(int ori, float x, float y, float z, float a1, float a2, Random rand, int color, float size) {
        EntityPlayer p = Minecraft.getMinecraft().player;
        float shade = MathHelper.sin((float) (p.ticksExisted + rand.nextInt(10)) / (5.0F + rand.nextFloat())) * 0.075F + 0.925F;
        Color c = new Color(color);
        float r = (float) c.getRed() / 220.0F;
        float g = (float) c.getGreen() / 220.0F;
        float b = (float) c.getBlue() / 220.0F;

        GL11.glPushMatrix();
        GL11.glEnable(2977);
        GL11.glEnable(3042);
        GL11.glEnable(32826);
        GL11.glBlendFunc(770, 771);
        this.translateFromOrientation(x, y, z, ori);
        GL11.glRotatef(a1, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(a2, 1.0F, 0.0F, 0.0F);
        GL11.glScalef((0.15F + rand.nextFloat() * 0.075F) * size, (0.5F + rand.nextFloat() * 0.1F) * size, (0.15F + rand.nextFloat() * 0.05F) * size);
        int var19 = (int) (210.0F * shade);
        int var20 = var19 % 65536;
        int var21 = var19 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var20, (float) var21);
        GL11.glColor4f(r, g, b, 1.0F);

        this.model.render();
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glDisable(32826);
        GL11.glDisable(3042);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float f, int i, float f2) {
        GL11.glPushMatrix();
        TileDestabilizedCrystal tco = (TileDestabilizedCrystal) te;
        int color = tco != null && tco.aspect != null ? Aspect.aspects.get(tco.aspect).getColor() : 0xFFFFFF;
        float scaleFactor = Math.max(0.6f, Math.min(1.6f, tco == null ? 1.0F : 0.6f + tco.capacity / 512.0f));
        boolean draining = tco == null ? false : tco.draining;
        int orientation = tco == null ? EnumFacing.UP.getIndex() : tco.orientation;
        Random rand = tco == null ? new Random(1) : new Random(tco.getBlockMetadata() + tco.getPos().getX() + (long) tco.getPos().getY() * tco.getPos().getZ());

        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/models/crystal.png"));
        if (draining) {
            GL11.glPushMatrix();
            float bob = MathHelper.sin((float) Minecraft.getMinecraft().player.ticksExisted / 14.0F) * 0.08F + 0.03F;
            GL11.glTranslatef(0.0F, bob, 0.0F);
        }
        this.drawCrystal(orientation, (float) x, (float) y, (float) z, (rand.nextFloat() - rand.nextFloat()) * 5.0F, (rand.nextFloat() - rand.nextFloat()) * 5.0F, rand, color, scaleFactor);

        for (int a = 0; a < 6; a++) {
            int angle1 = rand.nextInt(36) + 72 * a;
            int angle2 = 15 + rand.nextInt(15);
            this.drawCrystal(orientation, (float) x, (float) y, (float) z, (float) angle1, (float) angle2, rand, color, scaleFactor - 0.3f);
        }
        if (draining) {
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
        GL11.glDisable(3042);
    }
}