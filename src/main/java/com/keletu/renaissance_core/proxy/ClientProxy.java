package com.keletu.renaissance_core.proxy;

import com.keletu.renaissance_core.ConfigsRC;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.client.model.VengefulGolemModel;
import com.keletu.renaissance_core.client.render.*;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.module.botania.PageArcaneWorkbenchRecipe;
import com.keletu.renaissance_core.module.botania.PageCrucibleRecipe;
import com.keletu.renaissance_core.module.botania.PageInfusionRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{

    @Override
    public void regRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityProtectionField.class, RenderEnderCrystal::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintChicken.class, new RenderTaintChicken(0.3F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintRabbit.class, new RenderTaintRabbit());
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintCow.class, new RenderTaintCow(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintCreeper.class, new RenderTaintCreeper());
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintPig.class, new RenderTaintPig(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintSheep.class, new RenderTaintSheep(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintVillager.class, new RenderTaintVillager());
        RenderingRegistry.registerEntityRenderingHandler(EntityVengefulGolem.class, new VengefulGolemRenderer(new VengefulGolemModel(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/vengeful_golem.png"), 0.5f));
        if (ConfigsRC.CHANGE_BOTANIA_RECIPE && Loader.isModLoaded("botania")) {
            PageArcaneWorkbenchRecipe.init();
            PageCrucibleRecipe.init();
            PageInfusionRecipe.init();
        }
    }

    @Override
    public void addRenderLayers() {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();

        addLayersToSkin(skinMap.get("default"));
        addLayersToSkin(skinMap.get("slim"));
    }

    private static void addLayersToSkin(RenderPlayer renderPlayer) {
        renderPlayer.addLayer(new LayerBackpack(renderPlayer));
    }

    public void sendLocalMovementData(EntityLivingBase ent) {
        if (ent == Minecraft.getMinecraft().player) {
            if (ent.world.isRemote) {
                if (EntityVengefulGolem.isEnslaved) {
                    EntityPlayerSP player = (EntityPlayerSP) ent;
                    AxisAlignedBB aabb = player.getEntityBoundingBox().expand(32.0, 32.0, 32.0);
                    List<EntityVengefulGolem> list = player.world.getEntitiesWithinAABB(EntityVengefulGolem.class, aabb);

                    if ((list.isEmpty()) && new Random().nextInt(10) > 5) {
                        EntityVengefulGolem.isEnslaved = false;
                    }

                    if (player.ticksExisted % 60 == 0) {
                        player.rotationYaw = 360.0F * (-0.5F + player.world.rand.nextFloat());
                    }

                    float strafe = -0.5F + player.world.rand.nextFloat();
                    float forward = 1.0F;
                    player.travel(strafe, 0.0F, forward);

                    player.movementInput.jump = false;
                    player.movementInput.sneak = false;
                    player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, MathHelper.cos((float)player.ticksExisted), player.onGround));
                    player.connection.sendPacket(new CPacketInput(0.0F, player.moveForward, false, false));
                }
            }
        }
    }
}
