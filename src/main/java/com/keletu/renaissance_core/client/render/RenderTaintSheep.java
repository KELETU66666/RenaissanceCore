package com.keletu.renaissance_core.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderTaintSheep extends RenderLiving {
  private static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/models/creature/sheep.png");
  
  public RenderTaintSheep(RenderManager p_i46145_1_, ModelBase p_i46145_2_, float p_i46145_3_) {
    super(p_i46145_1_, p_i46145_2_, p_i46145_3_);
    addLayer(new LayerTaintSheepWool(this));
  }
  
  protected ResourceLocation getEntityTexture(Entity entity) {
    return rl;
  }
}
