package com.keletu.renaissance_core.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * Author: MrCrayfish
 */
public class ModelBackpack extends ModelBiped
{
    public ModelRenderer backpack;

    public ModelBackpack()
    {
        super(0.0F);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.backpack = new ModelRenderer(this, 64, 1);
        this.backpack.setRotationPoint(0.0F, 24F, 0.0F);
        this.backpack.addBox(-7.5F, -3F, 0.0F, 15, 14, 11, -2.0F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if(entityIn.isSneaking())
        {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        if(entityIn instanceof EntityPlayer && !((EntityPlayer) entityIn).getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty()){
            GlStateManager.translate(0, 0, 0.1);
        }
        this.backpack.render(scale);
    }

    public void setupAngles(ModelPlayer model)
    {
        copyProperties(model.bipedBody, this.backpack);
    }

    private static void copyProperties(ModelRenderer source, ModelRenderer target)
    {
        target.rotateAngleX = source.rotateAngleX;
        target.rotateAngleY = source.rotateAngleY;
        target.rotateAngleZ = source.rotateAngleZ;
        target.offsetX = source.offsetX;
        target.offsetY = source.offsetY;
        target.offsetZ = source.offsetZ;
        target.rotationPointX = source.rotationPointX;
        target.rotationPointY = source.rotationPointY;
        target.rotationPointZ = source.rotationPointZ; //TODO test these as I might not need them
    }
}