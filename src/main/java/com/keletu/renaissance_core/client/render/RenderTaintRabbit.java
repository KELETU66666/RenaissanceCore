package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.client.render.layer.LayerTaintRabbit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderRabbit;
import net.minecraft.util.ResourceLocation;

public class RenderTaintRabbit extends RenderRabbit {
  private static final ResourceLocation overlay = new ResourceLocation("thaumcraft", "textures/models/creature/taintrabbit.png");
  
  public RenderTaintRabbit() {
    super(Minecraft.getMinecraft().getRenderManager());
    addLayer(new LayerTaintRabbit(this));
  }
}
