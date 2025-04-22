package com.keletu.renaissance_core.client.render.layer;

import com.keletu.renaissance_core.client.model.ModelBackpack;
import com.keletu.renaissance_core.container.GUIHandler;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.Thaumcraft;

/**
 * Author: MrCrayfish
 */
public class LayerBackpack implements LayerRenderer<EntityPlayer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Thaumcraft.MODID, "textures/entity/pech_forage.png");

    private RenderPlayer renderer;
    private ModelBackpack model = new ModelBackpack();

    public LayerBackpack(RenderPlayer renderer)
    {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack backpackStack = GUIHandler.getBaubleStack(entity);
        if(!backpackStack.isEmpty())
        {
            this.renderer.bindTexture(TEXTURE);
            this.model.setModelAttributes(this.renderer.getMainModel());
            this.model.setupAngles(this.renderer.getMainModel());
            this.model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}