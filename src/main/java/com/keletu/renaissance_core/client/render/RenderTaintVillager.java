package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.EntityTaintVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderTaintVillager extends RenderLiving {
    private static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/models/creature/villager.png");

    public RenderTaintVillager() {
        super(Minecraft.getMinecraft().getRenderManager(), new ModelVillager(0.0F), 0.5F);
        addLayer(new LayerCustomHead(((ModelVillager) super.getMainModel()).villagerHead));
    }

    public ModelBase getMainModel() {
        return super.getMainModel();
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return rl;
    }

    protected void preRenderCallback(EntityTaintVillager p_77041_1_, float p_77041_2_) {
        float f1 = 0.9375F;
        this.shadowSize = 0.5F;
        GlStateManager.scale(f1, f1, f1);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_) {
        preRenderCallback((EntityTaintVillager) p_77041_1_, p_77041_2_);
    }
}
