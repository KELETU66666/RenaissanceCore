package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.client.model.FakeRobeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;

public class RenderDissolved extends RenderBiped {

    private final ResourceLocation texture;
    private final FakeRobeModel armor = new FakeRobeModel(1.0F);
    private final ResourceLocation voidTexture = new ResourceLocation("textures/entity/end_portal.png");
    private final ShaderCallback shaderCallback;
    private ModelBiped bipedMain;

    public RenderDissolved(ModelBiped model, ResourceLocation texture, float shadowSize) {
        super(Minecraft.getMinecraft().getRenderManager(), model, shadowSize);
        this.texture = texture;
        this.bipedMain = model;
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(int shader) {
                Minecraft mc = Minecraft.getMinecraft();
                int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
                ARBShaderObjects.glUniform1fARB(x, (float)(mc.player.rotationYaw * 2.0f * 3.141592653589793 / 360.0));
                int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
                ARBShaderObjects.glUniform1fARB(z, -(float)(mc.player.rotationPitch * 2.0f * 3.141592653589793 / 360.0));
            }
        };
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float pitch) {
        super.doRender((EntityLiving) entity, x, y, z, yaw, pitch);
    }

    @Override
    protected void renderModel(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        ShaderHelper.useShader(ShaderHelper.endShader, shaderCallback);

        super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        Minecraft.getMinecraft().getTextureManager().bindTexture(voidTexture);

        armor.isRiding = mainModel.isRiding;
        armor.isSneak = bipedMain.isSneak;
        armor.swingProgress = mainModel.swingProgress;
        armor.isChild = bipedMain.isChild;

        if (!entitylivingbaseIn.isInvisible()) {
            armor.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        } else if (!entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);

            armor.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        } else {
            armor.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entitylivingbaseIn);
        }

        ShaderHelper.releaseShader();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return voidTexture;
    }

}