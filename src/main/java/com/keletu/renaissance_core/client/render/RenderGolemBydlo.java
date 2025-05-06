package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.client.model.ModelGolemBydlo;
import com.keletu.renaissance_core.entity.EntityGolemBydlo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderGolemBydlo extends RenderLiving {
    ModelBase damage;
    private static final ResourceLocation thaumium = new ResourceLocation(RenaissanceCore.MODID, "textures/models/entity/golem_bydlo.png");

    public RenderGolemBydlo(ModelBase par1ModelBase) {
        super(Minecraft.getMinecraft().getRenderManager(), par1ModelBase, 1.5F);
        ModelGolemBydlo mg = new ModelGolemBydlo(false);
        mg.pass = 2;
        this.damage = mg;
    }

    public void render(EntityGolemBydlo e, double par2, double par4, double par6, float par8, float par9) {
        super.doRender(e, par2, par4, par6, par8, par9);
    }


    public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        this.render((EntityGolemBydlo)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.render((EntityGolemBydlo)par1Entity, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return thaumium;
    }
}