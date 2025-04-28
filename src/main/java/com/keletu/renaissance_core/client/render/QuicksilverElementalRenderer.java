package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.entity.EntityQuicksilverElemental;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class QuicksilverElementalRenderer extends RenderBiped {

    private static final ResourceLocation SHADOW_TEXTURES = new ResourceLocation("textures/misc/shadow.png");
    private final ResourceLocation texture;
    private final ShaderCallback shaderCallback;

    public QuicksilverElementalRenderer(ModelBiped model, ResourceLocation texture, float shadowSize) {
        super(Minecraft.getMinecraft().getRenderManager(), model, shadowSize);
        this.texture = texture;
        shaderCallback = new ShaderCallback() {
            @Override
            public void call(int shader) {
            }
        };
    }

    /**
     * Renders the entities shadow.
     */
    @Override
    public void renderShadow(Entity entityIn, double x, double y, double z, float shadowAlpha, float partialTicks)
    {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.renderManager.renderEngine.bindTexture(SHADOW_TEXTURES);
        World world = this.getWorldFromRenderManager();
        GlStateManager.depthMask(false);
        float f = ((EntityQuicksilverElemental)entityIn).getHealth() / 30;

        if (entityIn instanceof EntityLiving)
        {
            EntityLiving entityliving = (EntityLiving)entityIn;
            f *= entityliving.getRenderSizeModifier();

            if (entityliving.isChild())
            {
                f *= 0.5F;
            }
        }

        double d5 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        double d0 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        double d1 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        int i = MathHelper.floor(d5 - (double)f);
        int j = MathHelper.floor(d5 + (double)f);
        int k = MathHelper.floor(d0 - (double)f);
        int l = MathHelper.floor(d0);
        int i1 = MathHelper.floor(d1 - (double)f);
        int j1 = MathHelper.floor(d1 + (double)f);
        double d2 = x - d5;
        double d3 = y - d0;
        double d4 = z - d1;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k, i1), new BlockPos(j, l, j1)))
        {
            IBlockState iblockstate = world.getBlockState(blockpos.down());

            if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE && world.getLightFromNeighbors(blockpos) > 3)
            {
                this.renderShadowSingle(iblockstate, x, y, z, blockpos, shadowAlpha, f, d2, d3, d4);
            }
        }

        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase e, float p_77041_2_) {
        float s = e.getHealth() / 30.0F;
        GL11.glScalef(s, s, s);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float pitch) {
        super.doRender((EntityLiving) entity, x, y, z, yaw, pitch);
    }

    @Override
    protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        ShaderHelper.useShader(ShaderHelper.wiggleShader, shaderCallback);
        super.renderModel(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        ShaderHelper.releaseShader();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return texture;
    }

}