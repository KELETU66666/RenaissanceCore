package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class StrayedMirrorRenderer extends RenderBiped {

    private final ResourceLocation texture;
    private ModelBiped bipedModel;

    private final ModelBiped armor = new ModelBiped(0.5F);

    public StrayedMirrorRenderer(ModelBiped model, ResourceLocation texture, float shadowSize) {
        super(Minecraft.getMinecraft().getRenderManager(), model, shadowSize);
        this.texture = texture;
        this.bipedModel = model;
    }

    @Override
    public void doRender(final EntityLiving par1EntityChicken, final double par2, final double par4, final double par6, final float par8, final float par9) {
        super.doRender(par1EntityChicken, par2, par4, par6, par8, par9);
        bipedModel.bipedHeadwear.isHidden = true;
    }

    @Override
    protected void renderModel(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(RenaissanceCore.MODID, "textures/models/entity/strayed_mirror.png"));
        armor.isRiding = mainModel.isRiding;
        armor.isSneak = bipedModel.isSneak;
        armor.swingProgress = mainModel.swingProgress;
        armor.isChild = bipedModel.isChild;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        if (!entity.isInvisible()) {
            armor.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        } else if (!entity.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
            armor.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glPopMatrix();
            GL11.glDepthMask(true);
        } else {
            armor.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        }
        GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return texture;
    }
}