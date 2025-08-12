package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.TileHexOfPredictability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;

public class RendererTileHexOfPredictability extends TileEntitySpecialRenderer<TileHexOfPredictability> {
    private IModelCustom modelCaps, model1, model2, model3;
    private final ResourceLocation hexCapRL = new ResourceLocation(RenaissanceCore.MODID + ":models/hex_caps.obj");
    private final ResourceLocation hex1RL = new ResourceLocation(RenaissanceCore.MODID + ":models/hex1.obj");
    private final ResourceLocation hex2RL = new ResourceLocation(RenaissanceCore.MODID + ":models/hex2.obj");
    private final ResourceLocation hex3RL = new ResourceLocation(RenaissanceCore.MODID + ":models/hex3.obj");

    private final ResourceLocation texture = new ResourceLocation(RenaissanceCore.MODID + ":textures/models/hex_runes.png");
    private final ResourceLocation textureCaps = new ResourceLocation(RenaissanceCore.MODID + ":textures/models/hex_caps.png");

    private final ShaderCallback shaderCallback;

    private int progress;
    private boolean flag;

    public RendererTileHexOfPredictability() {
        modelCaps = AdvancedModelLoader.loadModel(hexCapRL);
        model1 = AdvancedModelLoader.loadModel(hex1RL);
        model2 = AdvancedModelLoader.loadModel(hex2RL);
        model3 = AdvancedModelLoader.loadModel(hex3RL);
        progress = 0;
        flag = true;
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(int shader) {
            }
        };

    }

    @Override
    public void render(TileHexOfPredictability te, double x, double y, double z, float f, int i, float f2) {
        if (!te.isMaster && te.hasWorld()) return;
        if (Minecraft.getMinecraft().getRenderViewEntity() == null) return;
        float ticks = (float) Minecraft.getMinecraft().getRenderViewEntity().ticksExisted;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
        Minecraft.getMinecraft().renderEngine.bindTexture(textureCaps);
        ShaderHelper.useShader(ShaderHelper.hexRunesShader, shaderCallback);
        int progLoc = ARBShaderObjects.glGetUniformLocationARB(ShaderHelper.hexRunesShader, "progress");
        ARBShaderObjects.glUniform1iARB(progLoc, progress);

        modelCaps.renderAll();
        if (te.hasRift) {
            progress += flag ? 1 : -1;
            if (progress == 60 || progress == 0) {
                flag = !flag;
            }
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        GL11.glPushMatrix();
        if (te.hasRift) {
            GL11.glRotatef((ticks % 360.0F), 0.0F, 1.0F, 0.0F);
        }
        model1.renderAll();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        if (te.hasRift) {
            GL11.glRotatef((ticks % 360.0F), 0.0F, -1.0F, 0.0F);
        }
        model2.renderAll();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        if (te.hasRift) {
            GL11.glRotatef((ticks % 360.0F), 0.0F, 1.0F, 0.0F);
        }
        model3.renderAll();
        GL11.glPopMatrix();

        ShaderHelper.releaseShader();

        GL11.glPopMatrix();
    }

}