package com.keletu.renaissance_core.proxy;

import com.keletu.renaissance_core.ConfigsRC;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.TileQuicksilverCrucible;
import com.keletu.renaissance_core.client.model.VengefulGolemModel;
import com.keletu.renaissance_core.client.render.*;
import com.keletu.renaissance_core.client.render.layer.LayerBackpack;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.module.botania.PageArcaneWorkbenchRecipe;
import com.keletu.renaissance_core.module.botania.PageCrucibleRecipe;
import com.keletu.renaissance_core.module.botania.PageInfusionRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.other.FXEssentiaStream;
import thaumcraft.client.fx.particles.FXBreakingFade;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        ShaderHelper.initShaders();
    }

    @Override
    public void regRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintChicken.class, new RenderTaintChicken(0.3F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintRabbit.class, new RenderTaintRabbit());
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintCow.class, new RenderTaintCow(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintCreeper.class, new RenderTaintCreeper());
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintPig.class, new RenderTaintPig(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintSheep.class, new RenderTaintSheep(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintVillager.class, new RenderTaintVillager());
        RenderingRegistry.registerEntityRenderingHandler(EntityVengefulGolem.class, new VengefulGolemRenderer(new VengefulGolemModel(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/vengeful_golem.png"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(EntityDissolved.class, new DissolvedRenderer(new ModelBiped(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/thaumaturge.png"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(EntityUpcomingHole.class, new UpcomingHoleRender());
        RenderingRegistry.registerEntityRenderingHandler(EntityQuicksilverElemental.class, new QuicksilverElementalRenderer(new ModelBiped(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/quicksilver_elemental.png"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(EntityOveranimated.class, new ThaumaturgeRenderer(new ModelBiped(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/overanimated.png"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(EntityThaumGib.class, new ThaumGibRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityThaumaturge.class, new ThaumaturgeRenderer(new ModelBiped(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/thaumaturge.png"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(EntityStrayedMirror.class, new StrayedMirrorRenderer(new ModelBiped(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/thaumaturge.png"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(EntitySamurai.class, new SamuraiRenderer(new ModelBiped(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/thaumaturge.png"), 0.15f));
        RenderingRegistry.registerEntityRenderingHandler(EntityCrimsonPontifex.class, new RenderCultistPontifex(Minecraft.getMinecraft().getRenderManager()));
        RenderingRegistry.registerEntityRenderingHandler(EntityConcentratedWarpCharge.class, new ConcentratedWarpChargeEntityRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityCrimsonPaladin.class, new CrimsonPaladinRenderer(new ModelBiped(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/crimson_paladin.png"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(EntityEtherealShackles.class, new EtherealShacklesEntityRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityMadThaumaturge.class, new ThaumaturgeRenderer(new ModelBiped(), new ResourceLocation(RenaissanceCore.MODID + ":textures/models/entity/mad_thaumaturge.png"), 0.5f));

        ClientRegistry.bindTileEntitySpecialRenderer(TileQuicksilverCrucible.class, new QuicksilverCrucibleTileRenderer());

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

    public void bloodsplosion(World world, double x, double y, double z) {
        FXBreakingFade fx = new FXBreakingFade(world, x, y + (double) (world.rand.nextFloat() * 2), z, Items.SLIME_BALL);
        if (world.rand.nextBoolean()) {
            fx.setRBGColorF(0.8F, 0.0F, 0.0F);
            fx.setAlphaF(0.4F);
        } else {
            fx.setRBGColorF(0.6F, 0.0F, 0.0F);
            fx.setAlphaF(0.6F);
        }

        fx.motionX = (float) (Math.random() * 2.0 - 1.0);
        fx.motionY = (float) (Math.random() * 2.0 - 1.0);
        fx.motionZ = (float) (Math.random() * 2.0 - 1.0);
        float f = (float) (Math.random() + Math.random() + 1.0) * 0.15F;
        float f1 = MathHelper.sqrt(fx.motionX * fx.motionX + fx.motionY * fx.motionY + fx.motionZ * fx.motionZ);
        fx.motionX = fx.motionX / (double) f1 * (double) f * 0.9640000000596046;
        fx.motionY = fx.motionY / (double) f1 * (double) f * 0.9640000000596046 + 0.10000000149011612;
        fx.motionZ = fx.motionZ / (double) f1 * (double) f * 0.9640000000596046;
        fx.setParticleMaxAge((int) (66.0F / (world.rand.nextFloat() * 0.9F + 0.1F)));
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
    }

    public void sendLocalMovementData(EntityLivingBase ent) {
        if (ent == Minecraft.getMinecraft().player) {
            if (ent.world.isRemote) {
                if (EntityVengefulGolem.isEnslaved) {
                    EntityPlayerSP player = (EntityPlayerSP) ent;
                    AxisAlignedBB aabb = player.getEntityBoundingBox().expand(32.0, 32.0, 32.0).expand(-32.0, -32.0, -32.0);
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
                    player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, MathHelper.cos((float) player.ticksExisted), player.onGround));
                    player.connection.sendPacket(new CPacketInput(0.0F, player.moveForward, false, false));
                }
            }
        }
    }

    public void taintsplosion(World world, double x, double y, double z) {
        FXBreakingFade fx = new FXBreakingFade(world, x, y + (double) (world.rand.nextFloat()), z, Items.SLIME_BALL);
        if (world.rand.nextBoolean()) {
            fx.setRBGColorF(0.6F, 0.0F, 0.3F);
            fx.setAlphaF(0.4F);
        } else {
            fx.setRBGColorF(0.3F, 0.0F, 0.3F);
            fx.setAlphaF(0.6F);
        }

        fx.motionX = (float) (Math.random() * 2.0 - 1.0);
        fx.motionY = (float) (Math.random() * 2.0 - 1.0);
        fx.motionZ = (float) (Math.random() * 2.0 - 1.0);
        float f = (float) (Math.random() + Math.random() + 1.0) * 0.15F;
        float f1 = MathHelper.sqrt(fx.motionX * fx.motionX + fx.motionY * fx.motionY + fx.motionZ * fx.motionZ);
        fx.motionX = fx.motionX / (double) f1 * (double) f * 0.9640000000596046;
        fx.motionY = fx.motionY / (double) f1 * (double) f * 0.9640000000596046 + 0.10000000149011612;
        fx.motionZ = fx.motionZ / (double) f1 * (double) f * 0.9640000000596046;
        fx.setParticleMaxAge((int) (66.0F / (world.rand.nextFloat() * 0.9F + 0.1F)));
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
    }

    @Override
    public void warpchain(EntityPlayer player, double tx, double ty, double tz) {
        ParticleEngine.addEffect(player.world, new FXEssentiaStream(player.world, player.posX, player.posY + (new Random().nextDouble() - 1), player.posZ, tx, ty, tz, 1, 0x9929BD, 0.1F, 0, 0.2F));
    }

    @Override
    public void lifedrain(Entity player, double tx, double ty, double tz) {
        ParticleEngine.addEffect(player.world, new FXEssentiaStream(player.world, tx, ty, tz, player.posX, player.posY + 0.5, player.posZ, 1, 0x7A1A1A, 0.1F, 0, 0.2F));
    }

    @Override
    public void quicksilverFlow(World w, double x, double y, double z, double tx, double ty, double tz) {
        for (int i = 0; i < 5; i++) {
            ParticleEngine.addEffect(w, new FXEssentiaStream(w, tx + (new Random().nextDouble()), ty, tz + (new Random().nextDouble()), x + 0.5, y + 0.5, z + 0.5, 3, 0xAAAAAA, 0.1F, 0, 0.2F));
        }
    }

    @Override
    public void bloodinitiation(Entity player, Entity madman) {
        ParticleEngine.addEffect(player.world, new FXEssentiaStream(player.world, player.posX, player.posY + 0.5, player.posZ, madman.posX, madman.posY + 1.8, madman.posZ, 5, 0x7A1A1A, 0.1F, 0, 0.2F));
    }
}
