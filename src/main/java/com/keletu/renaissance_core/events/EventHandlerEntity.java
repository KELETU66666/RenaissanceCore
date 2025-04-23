package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.capability.IT12Capability;
import com.keletu.renaissance_core.capability.RCCapabilities;
import com.keletu.renaissance_core.capability.T12Capability;
import com.keletu.renaissance_core.entity.Dissolved;
import com.keletu.renaissance_core.entity.StrayedMirror;
import com.keletu.renaissance_core.packet.PacketSyncCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.common.lib.SoundsTC;

@Mod.EventBusSubscriber(modid = RenaissanceCore.MODID)
public class EventHandlerEntity {

    @SubscribeEvent
    public static void tickHandler(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END || event.player.world.isRemote) return;

        IT12Capability it12 = IT12Capability.get(event.player);
        if(it12 != null) {
            it12.setLocationCorrect();
            syncToClient(event.player);
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(
                    new ResourceLocation(RenaissanceCore.MODID, "canpickoff_t12"),
                    new T12Capability.Provider((EntityPlayer) event.getObject())
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.player.world.isRemote) {
            syncToClient(event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntity();
        if(player.world.isRemote) return;

        IT12Capability capability = player.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);
        if (capability != null) {
            NBTTagCompound data = capability.serializeNBT();
            player.getEntityData().setTag("CanPickOffT12", data);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer original = event.getOriginal();
        EntityPlayer player = event.getEntityPlayer();

        if(player.world.isRemote) return;

        // Handle both death and dimension change cloning
        IT12Capability oldCap = original.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);
        IT12Capability newCap = player.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);

        if (oldCap != null && newCap != null) {
            if (event.isWasDeath()) {
                NBTTagCompound data = original.getEntityData().getCompoundTag("CanPickOffT12");
                newCap.deserializeNBT(data);
            } else {
                NBTTagCompound data = oldCap.serializeNBT();
                newCap.deserializeNBT(data);
            }
            syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        RenaissanceCore.proxy.sendLocalMovementData(player);
    }

    @SubscribeEvent
    public static void onLivingAttack (LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof Dissolved) {
            if (!event.getSource().damageType.equals("outOfWorld") && !event.getSource().damageType.equals("inWall")) {
                event.setCanceled(true);
            }
        }

        if (event.getEntityLiving() instanceof StrayedMirror) {
            if (event.getSource().damageType.equals("magic")) {
                event.setCanceled(true);
            }
        }

        //if (event.entityLiving instanceof EntityPlayer) {
        //    EntityPlayer player = (EntityPlayer) event.entityLiving;
//
        //    TCPlayerCapabilities capabilities = TCPlayerCapabilities.get(player);
        //    if (capabilities != null) {
        //        if (!player.capabilities.isCreativeMode && capabilities.ethereal) {
        //            if (event.source.isMagicDamage() || event.source.damageType.equals("outOfWorld")) {
        //                capabilities.fleshAmount++;
        //                capabilities.sync();
        //            }
        //            event.setCanceled(true);
        //        }
        //    }
        //}
    }

    /*
    * Codes from deeper depths mod
    * https://github.com/SmileycorpMC/Deeper-Depths/blob/main/src/main/java/com/deeperdepths/common/DeeperDepthsEventHandler.java
    * Those codes under LGPLv2.1 License: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
    * */
    private static void deflectProjectile(Entity projectile)
    {
        double bounceStrength = -1.0D;
        projectile.motionX *= bounceStrength;
        projectile.motionY *= bounceStrength;
        projectile.motionZ *= bounceStrength;
        projectile.velocityChanged = true;

        projectile.world.playSound(null, projectile.getPosition(), SoundsTC.fly, SoundCategory.HOSTILE, 1, 1);
    }

    /*
     * Codes from deeper depths mod
     * https://github.com/SmileycorpMC/Deeper-Depths/blob/main/src/main/java/com/deeperdepths/common/DeeperDepthsEventHandler.java
     * Those codes under LGPLv2.1 License: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
     * */
    /** Events have to be used for Projectile Reflection, as `attackEntityFrom` is called within onImpact for most projectiles, which breaks this behavior! */
    @SubscribeEvent
    public static void reflectArrowEvent(ProjectileImpactEvent.Arrow event)
    {
        final EntityArrow projectile = event.getArrow();

        if (projectile.getEntityWorld().isRemote) return;
        Entity entity = event.getRayTraceResult().entityHit;

        if (event.getEntity() != null && entity instanceof StrayedMirror)
        {
            deflectProjectile(projectile);
            projectile.shootingEntity = entity;

            event.setCanceled(true);
        }
    }

    /*
     * Codes from deeper depths mod
     * https://github.com/SmileycorpMC/Deeper-Depths/blob/main/src/main/java/com/deeperdepths/common/DeeperDepthsEventHandler.java
     * Those codes under LGPLv2.1 License: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
     * */
    @SubscribeEvent
    public static void reflectFireballEvent(ProjectileImpactEvent.Fireball event)
    {
        final EntityFireball projectile = event.getFireball();

        if (projectile.getEntityWorld().isRemote) return;
        Entity entity = event.getRayTraceResult().entityHit;

        if (event.getEntity() != null && entity instanceof StrayedMirror)
        {
            deflectProjectile(projectile);
            double bounceStrength = -1.0D;
            projectile.accelerationX *= bounceStrength;
            projectile.accelerationY *= bounceStrength;
            projectile.accelerationZ *= bounceStrength;
            projectile.shootingEntity = (EntityLivingBase) entity;

            event.setCanceled(true);
        }
    }

    /*
     * Codes from deeper depths mod
     * https://github.com/SmileycorpMC/Deeper-Depths/blob/main/src/main/java/com/deeperdepths/common/DeeperDepthsEventHandler.java
     * Those codes under LGPLv2.1 License: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
     * */
    @SubscribeEvent
    public static void reflectThrowableEvent(ProjectileImpactEvent.Throwable event)
    {
        final EntityThrowable projectile = event.getThrowable();

        if (projectile.getEntityWorld().isRemote) return;
        Entity entity = event.getRayTraceResult().entityHit;

        if (event.getEntity() != null && entity instanceof StrayedMirror)
        {
            deflectProjectile(projectile);
            //projectile.thrower = entityBlocking;
            event.setCanceled(true);
        }
    }

    private static void syncToClient(EntityPlayer player) {
        if(player instanceof EntityPlayerMP) {
            IT12Capability capability = player.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);
            if(capability != null) {
                NBTTagCompound data = capability.serializeNBT();
                RenaissanceCore.packetInstance.sendTo(new PacketSyncCapability(data), (EntityPlayerMP)player);
            }
        }
    }
}