package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.EntityTaintRabbit;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerTaintRabbit implements LayerRenderer<EntityTaintRabbit>
{
    private static final ResourceLocation overlay;
    private final RenderTaintRabbit bindTexture;
    
    public LayerTaintRabbit(final RenderTaintRabbit render) {
        this.bindTexture = render;
    }

    @Override
    public void doRenderLayer(final EntityTaintRabbit rabbit, final float p_177148_2_, final float p_177148_3_, final float p_177148_4_, final float p_177148_5_, final float p_177148_6_, final float p_177148_7_, final float p_177148_8_) {
        if (!rabbit.isInvisible()) {
            this.bindTexture.bindTexture(LayerTaintRabbit.overlay);
            GlStateManager.translate(0.0, -0.01, 0.0);
            GlStateManager.scale(1.01, 1.01, 1.01);
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.bindTexture.getMainModel().render(rabbit, p_177148_2_, p_177148_3_, p_177148_5_, p_177148_6_, p_177148_7_, p_177148_8_);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
        }
    }
    
    public boolean shouldCombineTextures() {
        return false;
    }

    static {
        overlay = new ResourceLocation("thaumcraft", "textures/models/creature/taintrabbit.png");
    }
}
