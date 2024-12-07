package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.EntityTaintCow;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderTaintCow extends RenderLiving {
  public RenderTaintCow(RenderManager rm, ModelBase par1ModelBase, float par2) {
    super(rm, par1ModelBase, par2);
  }
  
  private static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/models/creature/cow.png");
  
  protected ResourceLocation getEntityTexture(Entity entity) {
    return rl;
  }
  
  public void renderCow(EntityTaintCow par1EntityCow, double par2, double par4, double par6, float par8, float par9) {
    super.doRender(par1EntityCow, par2, par4, par6, par8, par9);
  }
  
  public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
    renderCow((EntityTaintCow)par1EntityLiving, par2, par4, par6, par8, par9);
  }
}
