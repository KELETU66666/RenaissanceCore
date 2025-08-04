package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.EntityPositiveBurstOrb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;

@SideOnly(Side.CLIENT)
public class RendererPositiveBurstOrb extends Render {
    public RendererPositiveBurstOrb() {
        super(Minecraft.getMinecraft().getRenderManager());
        this.shadowSize = 0.1F;
        this.shadowOpaque = 0.5F;
    }

    public void renderOrb(EntityPositiveBurstOrb orb, double par2, double par4, double par6, float par8, float par9) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);

        Minecraft.getMinecraft().renderEngine.bindTexture(ParticleEngine.particleTexture);
        int i = (int) (System.nanoTime() / 25000000L % 16L);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        float f2 = (float) i / 64f;
        float f3 = (float) (i + 1) / 64f;
        float f4 = 0.5F / 4F;
        float f5 = 0.5625F / 4F;
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.25F;
        int j = orb.getBrightnessForRender();
        int k = j % 65536;
        int l = j / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
        if (orb.vitaminize) {
            GL11.glColor4f(17, 17, 255, 128);
        } else if (orb.fulfillment) {
            GL11.glColor4f(255, 255, 0, 128);
        } else {
            GL11.glColor4f(0, 238, 0, 128);
        }
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        float f11 = 0.1F + 0.3F * ((float) (orb.orbMaxAge - orb.orbAge) / (float) orb.orbMaxAge);
        GL11.glScalef(f11, f11, f11);
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        if (orb.vitaminize) {
            builder.color(17, 17, 255, 128);
        } else if (orb.fulfillment) {
            builder.color(255, 255, 0, 128);
        } else {
            builder.color(0, 238, 0, 128);
        }
        builder.normal(0.0F, 1.0F, 0.0F);
        builder.pos(0.0F - f7, 0.0F - f8, 0.0).tex(f2, f5).endVertex();
        builder.pos(f6 - f7, 0.0F - f8, 0.0).tex(f3, f5).endVertex();
        builder.pos(f6 - f7, 1.0F - f8, 0.0).tex(f3, f4).endVertex();
        builder.pos(0.0F - f7, 1.0F - f8, 0.0).tex(f2, f4).endVertex();
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glDisable(32826);
        GlStateManager.resetColor();
        GL11.glPopMatrix();
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderOrb((EntityPositiveBurstOrb) par1Entity, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}