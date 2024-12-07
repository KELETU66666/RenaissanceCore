package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.client.model.ModelTaintSheep2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderTaintSheep extends RenderLiving {
  private static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/models/creature/sheep.png");
  
  public RenderTaintSheep(float p_i46145_3_) {
    super(Minecraft.getMinecraft().getRenderManager(), new ModelTaintSheep2(), p_i46145_3_);
    addLayer(new LayerTaintSheepWool(this));
  }
  
  protected ResourceLocation getEntityTexture(Entity entity) {
    return rl;
  }
}
