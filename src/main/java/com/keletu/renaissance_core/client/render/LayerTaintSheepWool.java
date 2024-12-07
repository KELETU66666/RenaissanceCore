package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.client.model.ModelTaintSheep1;
import com.keletu.renaissance_core.entity.EntityTaintSheep;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class LayerTaintSheepWool implements LayerRenderer
{
    private static final ResourceLocation TEXTURE;
    private final RenderTaintSheep sheepRenderer;
    private final ModelTaintSheep1 sheepModel;
    
    public LayerTaintSheepWool(final RenderTaintSheep p_i46112_1_) {
        this.sheepModel = new ModelTaintSheep1();
        this.sheepRenderer = p_i46112_1_;
    }
    
    public void doRenderLayer(final EntityTaintSheep entity, final float p_177162_2_, final float p_177162_3_, final float p_177162_4_, final float p_177162_5_, final float p_177162_6_, final float p_177162_7_, final float p_177162_8_) {
        if (!entity.getSheared() && !entity.isInvisible()) {
            this.sheepRenderer.bindTexture(LayerTaintSheepWool.TEXTURE);
            final float[] afloat = EntitySheep.getDyeRgb(EnumDyeColor.PURPLE);
            GlStateManager.color(afloat[0], afloat[1], afloat[2]);
            this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
            this.sheepModel.setLivingAnimations(entity, p_177162_2_, p_177162_3_, p_177162_4_);
            this.sheepModel.render(entity, p_177162_2_, p_177162_3_, p_177162_5_, p_177162_6_, p_177162_7_, p_177162_8_);
        }
    }
    
    public boolean shouldCombineTextures() {
        return true;
    }
    
    public void doRenderLayer(final EntityLivingBase p_177141_1_, final float p_177141_2_, final float p_177141_3_, final float p_177141_4_, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float p_177141_8_) {
        this.doRenderLayer((EntityTaintSheep)p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
    }
    
    static {
        TEXTURE = new ResourceLocation("thaumcraft", "textures/models/creature/sheep_fur.png");
    }
}
