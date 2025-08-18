package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.capability.IT12Capability;
import com.keletu.renaissance_core.capability.RCCapabilities;
import com.keletu.renaissance_core.capability.T12Capability;
import com.keletu.renaissance_core.packet.PacketSyncCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = RenaissanceCore.MODID)
public class EventHandlerEntity {

    @SubscribeEvent
    public static void tickHandler(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.player.world.isRemote) return;

        IT12Capability it12 = IT12Capability.get(event.player);
        if (it12 != null) {
            it12.setLocationCorrect();
            syncToClientT12(event.player);
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
        if (!event.player.world.isRemote) {
            syncToClientT12(event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntity();
        if (player.world.isRemote) return;

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

        if (player.world.isRemote) return;

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
            syncToClientT12(player);
        }
    }

    private static void syncToClientT12(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            IT12Capability capability = player.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);
            if (capability != null) {
                NBTTagCompound data = capability.serializeNBT();
                RenaissanceCore.packetInstance.sendTo(new PacketSyncCapability(data, 0), (EntityPlayerMP) player);
            }
        }
    }
}