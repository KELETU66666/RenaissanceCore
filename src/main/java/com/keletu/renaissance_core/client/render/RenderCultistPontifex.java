package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.CrimsonPontifex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class RenderCultistPontifex extends RenderBiped<CrimsonPontifex> {
    private static final ResourceLocation skin = new ResourceLocation("thaumcraft", "textures/entity/cultist.png");
    private static final ResourceLocation fl = new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

    public RenderCultistPontifex(RenderManager p_i46127_1_) {
        super(p_i46127_1_, new ModelBiped(), 0.5F);
        this.addLayer(new LayerHeldItem(this));
        LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
            protected void initArmor() {
                this.modelLeggings = new ModelBiped();
                this.modelArmor = new ModelBiped();
            }
        };
        this.addLayer(layerbipedarmor);
    }

    protected ResourceLocation getEntityTexture(CrimsonPontifex p_110775_1_) {
        return skin;
    }

    protected void preRenderCallback(CrimsonPontifex entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        GL11.glScalef(1.15F, 1.15F, 1.15F);
    }

    private void drawFloatyLine(double x, double y, double z, double x2, double y2, double z2, float partialTicks, int color, float speed, float distance, float width) {
        Entity player = Minecraft.getMinecraft().getRenderViewEntity();
        double iPX = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks;
        double iPY = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks;
        double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks;
        GL11.glTranslated(-iPX + x2, -iPY + y2, -iPZ + z2);
        float time = (float)(System.nanoTime() / 30000000L);
        Color co = new Color(color);
        float r = (float)co.getRed() / 255.0F;
        float g = (float)co.getGreen() / 255.0F;
        float b = (float)co.getBlue() / 255.0F;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Tessellator tessellator = Tessellator.getInstance();
        double dc1x = (double)((float)(x - x2));
        double dc1y = (double)((float)(y - y2));
        double dc1z = (double)((float)(z - z2));
        this.bindTexture(fl);
        tessellator.getBuffer().begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
        double dx2 = 0.0;
        double dy2 = 0.0;
        double dz2 = 0.0;
        double d3 = x - x2;
        double d4 = y - y2;
        double d5 = z - z2;
        float dist = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
        float blocks = (float)Math.round(dist);
        float length = blocks * 6.0F;
        float f9 = 0.0F;
        float f10 = 1.0F;

        double dz;
        float f13;
        int i;
        float f2;
        float f2a;
        float f3;
        double dx;
        double dy;
        for(i = 0; (float)i <= length * distance; ++i) {
            f2 = (float)i / length;
            f2a = (float)i * 1.5F / length;
            f2a = Math.min(0.75F, f2a);
            f3 = 1.0F - Math.abs((float)i - length / 2.0F) / (length / 2.0F);
            dx = dc1x + (double)(MathHelper.sin((float)((z % 16.0 + (double)(dist * (1.0F - f2) * 6.0F) - (double)(time % 32767.0F / 5.0F)) / 4.0)) * 0.5F * f3);
            dy = dc1y + (double)(MathHelper.sin((float)((x % 16.0 + (double)(dist * (1.0F - f2) * 6.0F) - (double)(time % 32767.0F / 5.0F)) / 3.0)) * 0.5F * f3);
            dz = dc1z + (double)(MathHelper.sin((float)((y % 16.0 + (double)(dist * (1.0F - f2) * 6.0F) - (double)(time % 32767.0F / 5.0F)) / 2.0)) * 0.5F * f3);
            f13 = (1.0F - f2) * dist - time * speed;
            tessellator.getBuffer().pos(dx * (double)f2, dy * (double)f2 - (double)width, dz * (double)f2).tex((double)f13, (double)f10).color(r, g, b, 0.8F).endVertex();
            tessellator.getBuffer().pos(dx * (double)f2, dy * (double)f2 + (double)width, dz * (double)f2).tex((double)f13, (double)f9).color(r, g, b, 0.8F).endVertex();
        }

        tessellator.draw();
        tessellator.getBuffer().begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);

        for(i = 0; (float)i <= length * distance; ++i) {
            f2 = (float)i / length;
            f2a = (float)i * 1.5F / length;
            f2a = Math.min(0.75F, f2a);
            f3 = 1.0F - Math.abs((float)i - length / 2.0F) / (length / 2.0F);
            dx = dc1x + (double)(MathHelper.sin((float)((z % 16.0 + (double)(dist * (1.0F - f2) * 6.0F) - (double)(time % 32767.0F / 5.0F)) / 4.0)) * 0.5F * f3);
            dy = dc1y + (double)(MathHelper.sin((float)((x % 16.0 + (double)(dist * (1.0F - f2) * 6.0F) - (double)(time % 32767.0F / 5.0F)) / 3.0)) * 0.5F * f3);
            dz = dc1z + (double)(MathHelper.sin((float)((y % 16.0 + (double)(dist * (1.0F - f2) * 6.0F) - (double)(time % 32767.0F / 5.0F)) / 2.0)) * 0.5F * f3);
            f13 = (1.0F - f2) * dist - time * speed;
            tessellator.getBuffer().pos(dx * (double)f2 - (double)width, dy * (double)f2, dz * (double)f2).tex((double)f13, (double)f10).color(r, g, b, 0.8F).endVertex();
            tessellator.getBuffer().pos(dx * (double)f2 + (double)width, dy * (double)f2, dz * (double)f2).tex((double)f13, (double)f9).color(r, g, b, 0.8F).endVertex();
        }

        tessellator.draw();
        GL11.glDisable(3042);
    }
}
