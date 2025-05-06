package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;

public class RenderProjectileEtherealShackles extends Render {
    public static IModelCustom shacklesModel;
    public static IModelCustom chainModel;
    final ResourceLocation shackles = new ResourceLocation(RenaissanceCore.MODID+":models/shackles.obj");
    final ResourceLocation chain = new ResourceLocation(RenaissanceCore.MODID+":models/chain.obj");
    public static ResourceLocation shacklesTexture = new ResourceLocation(RenaissanceCore.MODID+":textures/models/shackles.png");
    public static ResourceLocation chainTexture = new ResourceLocation(RenaissanceCore.MODID+":textures/models/Chain.png");

    public RenderProjectileEtherealShackles(){
        super(Minecraft.getMinecraft().getRenderManager());
        shacklesModel = AdvancedModelLoader.loadModel(shackles);
        chainModel = AdvancedModelLoader.loadModel(chain);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y + 0.5F, (float)z);
        GL11.glRotatef(entity.ticksExisted * 10.0F, 0.0F, 1.0F, 0.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(shacklesTexture);
        shacklesModel.renderAll();
        Minecraft.getMinecraft().renderEngine.bindTexture(chainTexture);
        chainModel.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return shacklesTexture;
    }

}