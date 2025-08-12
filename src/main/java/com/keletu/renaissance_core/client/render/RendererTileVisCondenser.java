package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.TileVisCondenser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;

public class RendererTileVisCondenser extends TileEntitySpecialRenderer<TileVisCondenser> {
    private final IModelCustom model;
    private final ResourceLocation modelRL = new ResourceLocation(RenaissanceCore.MODID + ":models/obj/vis_condenser.obj");
    private final ResourceLocation texture = new ResourceLocation(RenaissanceCore.MODID + ":textures/models/vis_condenser.png");

    public RendererTileVisCondenser() {
        model = AdvancedModelLoader.loadModel(modelRL);

    }

    @Override
    public void render(TileVisCondenser tile, double x, double y, double z, float f, int i, float f1) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        model.renderAll();
        GL11.glPopMatrix();
    }

}