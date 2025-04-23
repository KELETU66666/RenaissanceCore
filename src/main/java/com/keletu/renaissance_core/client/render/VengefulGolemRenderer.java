package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.client.render.layer.LayerGolemBell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.items.ItemsTC;

public class VengefulGolemRenderer extends RenderBiped<EntityLiving> {

    private final ResourceLocation texture;

    public VengefulGolemRenderer(ModelBiped model, ResourceLocation texture, float shadow) {
        super(Minecraft.getMinecraft().getRenderManager(), model, shadow);
        this.texture = texture;
        this.addLayer(new LayerGolemBell(this, new ItemStack(ItemsTC.golemBell)));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return texture;
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}