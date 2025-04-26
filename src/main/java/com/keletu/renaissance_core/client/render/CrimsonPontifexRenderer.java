package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.CrimsonPontifex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CrimsonPontifexRenderer extends RenderBiped<CrimsonPontifex> {

    private final ResourceLocation texture;

    public CrimsonPontifexRenderer(ModelBiped model, ResourceLocation texture, float shadowSize) {
        super(Minecraft.getMinecraft().getRenderManager(), model, shadowSize);
        this.texture = texture;
    }

    protected void preRenderCallback(CrimsonPontifex entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        GL11.glScalef(1.15f, 1.15f, 1.15f);
    }

    @Override
    protected ResourceLocation getEntityTexture(CrimsonPontifex entity) {
        return texture;
    }
}