package com.keletu.renaissance_core.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCrystal extends ModelBase
{
    ModelRenderer Crystal;
    
    public ModelCrystal() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.Crystal = new ModelRenderer(this, 0, 0)).addBox(-16.0f, -16.0f, 0.0f, 16, 16, 16);
        this.Crystal.setRotationPoint(0.0f, 32.0f, 0.0f);
        this.Crystal.setTextureSize(64, 32);
        this.Crystal.mirror = true;
        this.setRotation(this.Crystal, 0.7071f, 0.0f, 0.7071f);
    }
    
    public void render() {
        this.Crystal.render(0.0625f);
    }
    
    public void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
