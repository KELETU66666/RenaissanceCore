package com.keletu.renaissance_core.client.render;

import com.keletu.renaissance_core.client.model.FakeModelFortressArmor;
import com.keletu.renaissance_core.client.render.layer.LayerGolemBell;
import com.keletu.renaissance_core.entity.Samurai;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.items.ItemsTC;
import thecodex6824.thaumicaugmentation.api.TAItems;

public class SamuraiRenderer extends RenderBiped {

    private final ResourceLocation texture;
    private ModelBiped bipedModel;
    private final ResourceLocation voidFortress = new ResourceLocation("taintedmagic", "textures/models/ModelVoidFortressArmor.png");
    private final ResourceLocation shadowFortress = new ResourceLocation("taintedmagic", "textures/models/ModelShadowFortressArmor.png");

    private final FakeModelFortressArmor armor = new FakeModelFortressArmor(1.0F);
    private final ResourceLocation thaumTexture = new ResourceLocation("taintedmagic", "textures/models/ModelKatanaThaumium.png");
    private final ResourceLocation voidTexture = new ResourceLocation("taintedmagic", "textures/models/ModelKatanaVoidmetal.png");
    private final ResourceLocation shadowTexture = new ResourceLocation("taintedmagic", "textures/models/ModelKatanaShadowmetal.png");


    private final ModelSaya saya = new ModelSaya();
    private final ModelKatana katana = new ModelKatana();
    private final ItemStack swordItem = new ItemStack(ItemsTC.thaumiumSword);
    private final ItemStack wand = new ItemStack(TAItems.GAUNTLET, 1, 0);

    public SamuraiRenderer(ModelBiped model, ResourceLocation texture, float shadowSize) {
        super(Minecraft.getMinecraft().getRenderManager(), model, shadowSize);
        this.bipedModel = model;
        this.texture = texture;
        this.addLayer(new LayerGolemBell(this, swordItem));
    }

    @Override
    protected void renderModel(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        byte type = ((Samurai) entity).getType();
        switch (type) {
            case 0:
                Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thaumcraft","textures/entity/armor/fortress_armor.png"));
                break;
            case 1:
                Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/entity/armor/fortress_armor.png"));
                break;
            case 2:
                Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/entity/armor/fortress_armor.png"));
                break;
        }
        armor.isRiding = mainModel.isRiding;
        armor.isSneak = bipedModel.isSneak;
        armor.swingProgress = mainModel.swingProgress;
        armor.isChild = bipedModel.isChild;
        if (!entity.isInvisible()) {
            armor.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        } else if (!entity.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
            armor.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glPopMatrix();
            GL11.glDepthMask(true);
        } else {
            armor.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        }

        /*GL11.glPushMatrix();

        final int light = entity.getBrightnessForRender();
        final int lightmapX = light % 65536;
        final int lightmapY = light / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPushMatrix();

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(55, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);

        GL11.glTranslatef(-0.6F, 2.25F, 1.25F);
        //switch (type) {
        //    case 0:
        //        Minecraft.getMinecraft().renderEngine.bindTexture(thaumTexture);
        //        break;
        //    case 1:
        //        Minecraft.getMinecraft().renderEngine.bindTexture(voidTexture);
        //        break;
        //    case 2:
        //        Minecraft.getMinecraft().renderEngine.bindTexture(shadowTexture);
        //        break;
        //}
        saya.render(0.0625F);

        GL11.glPopMatrix();

        //System.out.println("AGG " + ((Samurai)entity).getAnger());
        if (((Samurai) entity).getAnger() == 0) {
            GL11.glPushMatrix();

            GL11.glScalef(0.5F, 0.5F, 0.5F);
            GL11.glRotatef(55, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);

            GL11.glTranslatef(-0.6F, 2.25F, 1.25F);

            //Minecraft.getMinecraft().renderEngine.bindTexture(getTexture(stack));
            katana.render(0.0625F);

            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();*/

    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return texture;
    }

    class ModelSaya extends ModelBase {
        public ModelRenderer saya;

        public ModelSaya() {
            textureWidth = 32;
            textureHeight = 64;

            saya = new ModelRenderer(this, 10, 0);
            saya.setRotationPoint(0.0F, -40.0F, 0.0F);
            saya.addBox(-1.0F, 0.5F, -2.0F, 2, 48, 4, 0.0F);
        }

        public void render(final float size) {
            saya.render(size);
        }

        /**
         * This is a helper function from Tabula to set the rotation of model parts
         */
        public void setRotation(final ModelRenderer m, final float x, final float y, final float z) {
            m.rotateAngleX = x;
            m.rotateAngleY = y;
            m.rotateAngleZ = z;
        }
    }

    class ModelKatana extends ModelBase {
        public ModelRenderer blade;
        public ModelRenderer grip1;
        public ModelRenderer grip2;

        public ModelKatana() {
            textureWidth = 32;
            textureHeight = 64;

            grip2 = new ModelRenderer(this, 22, 0);
            grip2.setRotationPoint(0.0F, 0.0F, 0.0F);
            grip2.addBox(-1.0F, -12.0F, -1.5F, 2, 12, 3, 0.0F);

            blade = new ModelRenderer(this, 0, 0);
            blade.setRotationPoint(0.0F, -40.0F, 0.0F);
            blade.addBox(-0.5F, 0.0F, -2.0F, 1, 48, 4, -0.75F);

            grip1 = new ModelRenderer(this, 0, 52);
            grip1.setRotationPoint(0.0F, -40.0F, 0.0F);
            grip1.addBox(-2.5F, 0.0F, -3.5F, 5, 1, 7, 0.0F);
            grip1.addChild(grip2);
        }

        public void render(final float size) {
            blade.render(size);
            grip1.render(size);
        }
    }

}