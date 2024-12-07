package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.EntityTaintCreeper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class RenderTaintCreeper extends RenderLiving
{
    private final ModelBase model;
    private static final ResourceLocation rl;
    private static final ResourceLocation armorTexture;
    
    public RenderTaintCreeper() {
        super(Minecraft.getMinecraft().getRenderManager(), new ModelCreeper(), 0.5f);
        this.model = new ModelCreeper(2.0f);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderTaintCreeper.rl;
    }
    
    protected void updateCreeperScale(final EntityTaintCreeper par1EntityCreeper, final float par2) {
        float var4 = par1EntityCreeper.getCreeperFlashIntensity(par2);
        final float var5 = 1.0f + MathHelper.sin(var4 * 100.0f) * var4 * 0.01f;
        if (var4 < 0.0f) {
            var4 = 0.0f;
        }
        if (var4 > 1.0f) {
            var4 = 1.0f;
        }
        var4 *= var4;
        var4 *= var4;
        final float var6 = (1.0f + var4 * 0.4f) * var5;
        final float var7 = (1.0f + var4 * 0.1f) / var5;
        GL11.glScalef(var6, var7, var6);
    }
    
    protected int updateCreeperColorMultiplier(final EntityTaintCreeper par1EntityCreeper, final float par2, final float par3) {
        final float var5 = par1EntityCreeper.getCreeperFlashIntensity(par3);
        if ((int)(var5 * 10.0f) % 2 == 0) {
            return 0;
        }
        int var6 = (int)(var5 * 0.2f * 255.0f);
        if (var6 < 0) {
            var6 = 0;
        }
        if (var6 > 255) {
            var6 = 255;
        }
        final short var7 = 255;
        final short var8 = 255;
        final short var9 = 255;
        return var6 << 24 | var7 << 16 | var8 << 8 | var9;
    }

    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        this.updateCreeperScale((EntityTaintCreeper)par1EntityLiving, par2);
    }
    
    protected int getColorMultiplier(final EntityLivingBase par1EntityLiving, final float par2, final float par3) {
        return this.updateCreeperColorMultiplier((EntityTaintCreeper)par1EntityLiving, par2, par3);
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/models/creature/creeper.png");
        armorTexture = new ResourceLocation("thaumcraft", "textures/entity/creeper/creeper_armor.png");
    }
}
