package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.EntityTaintChicken;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderTaintChicken extends RenderLiving
{
    private static final ResourceLocation rl;
    
    public RenderTaintChicken(final RenderManager p_i46127_1_, final ModelBase par1ModelBase, final float par2) {
        super(p_i46127_1_, par1ModelBase, par2);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderTaintChicken.rl;
    }
    
    public void renderChicken(final EntityTaintChicken par1EntityChicken, final double par2, final double par4, final double par6, final float par8, final float par9) {
        super.doRender(par1EntityChicken, par2, par4, par6, par8, par9);
    }
    
    protected float getWingRotation(final EntityTaintChicken par1EntityChicken, final float par2) {
        final float var3 = par1EntityChicken.field_756_e + (par1EntityChicken.field_752_b - par1EntityChicken.field_756_e) * par2;
        final float var4 = par1EntityChicken.field_757_d + (par1EntityChicken.destPos - par1EntityChicken.field_757_d) * par2;
        return (MathHelper.sin(var3) + 1.0f) * var4;
    }
    
    protected float handleRotationFloat(final EntityLivingBase par1EntityLiving, final float par2) {
        return this.getWingRotation((EntityTaintChicken)par1EntityLiving, par2);
    }
    
    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderChicken((EntityTaintChicken)par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/models/creature/chicken.png");
    }
}
