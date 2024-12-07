package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.EntityTaintPig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderTaintPig extends RenderLiving {
  public RenderTaintPig(float par3) {
    super(Minecraft.getMinecraft().getRenderManager(), new ModelPig(), par3);
  }
  
  private static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/models/creature/pig.png");
  
  protected ResourceLocation getEntityTexture(Entity entity) {
    return rl;
  }
  
  public void func_40286_a(EntityTaintPig par1EntityPig, double par2, double par4, double par6, float par8, float par9) {
    super.doRender(par1EntityPig, par2, par4, par6, par8, par9);
  }
  
  public void doRender(EntityLiving par1Entity, double par2, double par4, double par6, float par8, float par9) {
    func_40286_a((EntityTaintPig)par1Entity, par2, par4, par6, par8, par9);
  }
}
