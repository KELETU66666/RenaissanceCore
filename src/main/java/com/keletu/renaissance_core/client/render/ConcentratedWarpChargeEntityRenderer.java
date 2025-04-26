package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.client.model.ConcentratedWarpChargeModel;
import com.keletu.renaissance_core.entity.ConcentratedWarpChargeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ConcentratedWarpChargeEntityRenderer extends Render {

    ConcentratedWarpChargeModel model = new ConcentratedWarpChargeModel();
    private static final ResourceLocation rl = new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/warp_cube.png");

    public ConcentratedWarpChargeEntityRenderer() {
        super(Minecraft.getMinecraft().getRenderManager());
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        ConcentratedWarpChargeEntity charge = (ConcentratedWarpChargeEntity) entity;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslatef((float) x, (float) y + 0.5f, (float) z);
        float pulse = 0.025F * MathHelper.sin((float) entity.ticksExisted * 0.075F);
        float dark = charge.selfFlagellation ? MathHelper.sin((float) entity.ticksExisted * 0.075F) : 0.0f;
        GL11.glScalef(1F - pulse, 1F - pulse, 1F - pulse);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1f - dark, 1f - dark, 1f - dark, 0.5f + (pulse * 10.0f));
        Minecraft.getMinecraft().renderEngine.bindTexture(getEntityTexture(charge));
        model.render(charge);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return rl;
    }

}