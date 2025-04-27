package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.capability.ICapConcilium;
import static com.keletu.renaissance_core.client.render.EtherealShacklesEntityRenderer.*;
import com.keletu.renaissance_core.client.render.ShaderHelper;
import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.Project;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ModelEvents {
    @SubscribeEvent
    public static void regModels(ModelRegistryEvent event) {
        defaultModel(RCItems.arcane_lime_powder);
        defaultModel(RCItems.dice12);
        defaultModel(RCItems.pech_backpack);
        defaultModel(RCItems.elixir);
        defaultModel(RCItems.coins);
        defaultModel(RCItems.coins, 1);
        defaultModel(RCItems.coins, 2);
        defaultModel(RCItems.pontifex_hood);
        defaultModel(RCItems.pontifex_robe);
        defaultModel(RCItems.pontifex_legs);
        defaultModel(RCItems.pontifex_boots);
        defaultModel(RCItems.molot);
        defaultModel(RCItems.crimson_annales);

        defaultModel(RCItems.pechHeadNormal);
        defaultModel(RCItems.pechHeadHunter);
        defaultModel(RCItems.pechHeadThaumaturge);
        defaultModel(RCItems.quicksilverCrucible);
    }

    static void defaultModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    static void defaultModel(Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderChains(RenderHandEvent event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        ICapConcilium capabilities = ICapConcilium.get(player);
        if (capabilities.getChainedTime() != 0){
            event.setCanceled(true);
            renderArm(event.getRenderPass(), event.getPartialTicks(), Minecraft.getMinecraft(), capabilities.getChainedTime());
        }
        //event.setCanceled(true);
        //renderArm(event.renderPass, event.partialTicks, Minecraft.getMinecraft());
    }

    public static void renderArm(int renderPass, float partialTicks, Minecraft mc, int chainedTime) {
        GL11.glClear(256);
        float farPlaneDistance = (float)(mc.gameSettings.renderDistanceChunks * 16);
        double cameraZoom = 1.0D;
        double cameraYaw = 0.0D;
        double cameraPitch = 0.0D;
        if(mc.entityRenderer.debugViewDirection <= 0) {
            GlStateManager.pushMatrix();
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            float f1 = 0.07F;
            if(mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float)(-(renderPass * 2 - 1)) * f1, 0.0F, 0.0F);
            }

            if(cameraZoom != 1.0D) {
                GL11.glTranslatef((float)cameraYaw, (float)(-cameraPitch), 0.0F);
                GL11.glScaled(cameraZoom, cameraZoom, 1.0D);
            }
            Project.gluPerspective(getFOVModifier(partialTicks, mc.entityRenderer, mc), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, farPlaneDistance * 2.0F);
            //if(mc.playerController.enableEverythingIsScrewedUpMode()) {
            //    float f2 = 0.6666667F;
            //    GL11.glScalef(1.0F, f2, 1.0F);
            //}

            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            if(mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float)(renderPass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GL11.glPushMatrix();
            hurtCameraEffect(partialTicks, mc);
            if(mc.gameSettings.viewBobbing) {
                setupViewBobbing(partialTicks, mc);
            }

            if(mc.gameSettings.thirdPersonView == 0 && !((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping() && !mc.gameSettings.hideGUI /*&& !mc.playerController.enableEverythingIsScrewedUpMode()*/) {
                mc.entityRenderer.enableLightmap();
                renderEmptyHand(mc, partialTicks, chainedTime);
                mc.entityRenderer.disableLightmap();
            }

            GL11.glPopMatrix();
            if(mc.gameSettings.thirdPersonView == 0 && !((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping()) {
                mc.entityRenderer.itemRenderer.renderOverlays(partialTicks);
                hurtCameraEffect(partialTicks, mc);
            }

            if(mc.gameSettings.viewBobbing) {
                setupViewBobbing(partialTicks, mc);
            }
            GlStateManager.popMatrix();
        }

    }

    private static void renderEmptyHand(Minecraft mc, float p_78440_1_, int chainedTime) {
        float f1 = 1.0F;
        EntityPlayerSP entityclientplayermp = mc.player;
        float f2 = entityclientplayermp.prevRotationPitch + (entityclientplayermp.rotationPitch - entityclientplayermp.prevRotationPitch) * p_78440_1_;
        GL11.glPushMatrix();
        GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(entityclientplayermp.prevRotationYaw + (entityclientplayermp.rotationYaw - entityclientplayermp.prevRotationYaw) * p_78440_1_, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        float f3 = entityclientplayermp.prevRenderArmPitch + (entityclientplayermp.renderArmPitch - entityclientplayermp.prevRenderArmPitch) * p_78440_1_;
        float f4 = entityclientplayermp.prevRenderArmYaw + (entityclientplayermp.renderArmYaw - entityclientplayermp.prevRenderArmYaw) * p_78440_1_;
        GL11.glRotatef((entityclientplayermp.rotationPitch - f3) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((entityclientplayermp.rotationYaw - f4) * 0.1F, 0.0F, 1.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(!entityclientplayermp.isInvisible()) {
            Render render1 = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(mc.player);
            RenderPlayer renderplayer = (RenderPlayer)render1;
            mc.getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            float f10 = 1.0F;
            GL11.glPushMatrix();
            GL11.glRotatef(1.0F, 0.0F, 0.0F, 1.0F);

            GL11.glPushMatrix();
            GL11.glTranslatef(0.53F, -0.8F, -0.8F);
            GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-5.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0.5F, 0.0F, 0.1F);
            GL11.glScalef(f10, f10, f10);
            renderplayer.renderRightArm(mc.player);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(-0.05F, -0.8F, -0.8F);
            GL11.glRotatef(-5.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(f10, f10, f10);
            renderplayer.renderRightArm(mc.player);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, -1.2F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            float k = 0F;
            if (chainedTime < 10){
                k = ((float) (10 - chainedTime) / 30);
            }
            GL11.glScalef(1.05F + k, 1.05F, 1.05F + k);
            ShaderHelper.useShader(ShaderHelper.etherealShader);
            //int progLoc = ARBShaderObjects.glGetUniformLocationARB(ShaderHelper.etherealShader, "progress");
//
            //if (progLoc != -1) {
            //    ARBShaderObjects.glUniform1iARB(progLoc, 100 - chainedTime);
            //} else {
            //    System.err.println("Uniform variable 'progress' not found in shader!");
            //}
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
            mc.getTextureManager().bindTexture(shacklesTexture);
            shacklesModel.renderAll();
            mc.getTextureManager().bindTexture(chainTexture);
            chainModel.renderAll();
            GL11.glDisable(GL11.GL_BLEND);
            ShaderHelper.releaseShader();
            GL11.glPopMatrix();

            GL11.glPopMatrix();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
    }

    private static void hurtCameraEffect(float p_78482_1_, Minecraft mc) {
        EntityLivingBase entitylivingbase = (EntityLivingBase) mc.getRenderViewEntity();
        float f1 = (float)entitylivingbase.hurtTime - p_78482_1_;
        float f2;
        if(entitylivingbase.getHealth() <= 0.0F) {
            f2 = (float)entitylivingbase.deathTime + p_78482_1_;
            GL11.glRotatef(40.0F - 8000.0F / (f2 + 200.0F), 0.0F, 0.0F, 1.0F);
        }

        if(f1 >= 0.0F) {
            f1 /= (float)entitylivingbase.maxHurtTime;
            f1 = MathHelper.sin(f1 * f1 * f1 * f1 * 3.1415927F);
            f2 = entitylivingbase.attackedAtYaw;
            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f1 * 14.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
        }

    }

    private static void setupViewBobbing(float p_78475_1_, Minecraft mc) {
        if(mc.getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
            float f1 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f2 = -(entityplayer.distanceWalkedModified + f1 * p_78475_1_);
            float f3 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * p_78475_1_;
            float f4 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * p_78475_1_;
            GL11.glTranslatef(MathHelper.sin(f2 * 3.1415927F) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * 3.1415927F) * f3), 0.0F);
            GL11.glRotatef(MathHelper.sin(f2 * 3.1415927F) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(Math.abs(MathHelper.cos(f2 * 3.1415927F - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
        }

    }

    private static float getFOVModifier(float partialTicks, EntityRenderer er, Minecraft mc) {
        if(er.debugViewDirection > 0) {
            return 90.0F;
        } else {
            EntityLivingBase entityplayer = (EntityLivingBase) mc.getRenderViewEntity();
            float f1 = 70.0F;
            if(entityplayer.getHealth() <= 0.0F) {
                float block = (float)entityplayer.deathTime + partialTicks;
                f1 /= (1.0F - 500.0F / (block + 500.0F)) * 2.0F + 1.0F;
            }

            IBlockState block1 = ActiveRenderInfo.getBlockStateAtEntityViewpoint(mc.world, entityplayer, partialTicks);
            if(block1.getMaterial() == Material.WATER) {
                f1 = f1 * 60.0F / 70.0F;
            }

            return f1;
        }
    }
}
