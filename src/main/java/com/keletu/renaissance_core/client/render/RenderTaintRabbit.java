package com.keletu.renaissance_core.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderRabbit;
import net.minecraft.util.ResourceLocation;

public class RenderTaintRabbit extends RenderRabbit {
  private static final ResourceLocation overlay = new ResourceLocation("thaumcraft", "textures/models/creature/taintrabbit.png");
  
  public RenderTaintRabbit(RenderManager manager) {
    super(manager);
    addLayer(new LayerTaintRabbit(this));
  }
}
