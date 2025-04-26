package com.keletu.renaissance_core.capability;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.packet.PacketSyncPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class ThaumicConciliumCap implements ICapConcilium {

    public EntityPlayer player;
    public boolean lithographed;
    public boolean relieved;
    public boolean pontifexRobeToggle;
    public String[] monitored;
    public int chainedTime;
    public boolean ethereal;
    public int fleshAmount;

    public ThaumicConciliumCap(EntityPlayer player) {
        if (!(player instanceof FakePlayer)) {
            this.player = player;
        } else {
            player = null;
        }
        lithographed = false;
        relieved = false;
        chainedTime = 0;
        ethereal = false;
        fleshAmount = 0;
        pontifexRobeToggle = false;
        monitored = new String[4];
    }

    public static final ThaumicConciliumCap get(EntityPlayer player)
    {
        return (ThaumicConciliumCap) player.getCapability(RCCapabilities.CONCILIUM, null);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound properties = new NBTTagCompound();
        for (int i = 0; i < 4; i++) {
            if (monitored[i] != null && !monitored[i].isEmpty()) {
                properties.setString("monitored" + i, monitored[i]);
            } else {
                properties.setString("monitored" + i, "!!!NOTHING!!!");
            }
        }
        properties.setBoolean("lithographed", lithographed);
        properties.setBoolean("relieved", relieved);
        properties.setInteger("chainedTime", chainedTime);
        properties.setBoolean("ethereal", ethereal);
        properties.setBoolean("pontifexRobeToggle", pontifexRobeToggle);
        properties.setInteger("fleshAmount", fleshAmount);
        return properties;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        for (int i = 0; i < 4; i++) {
            monitored[i] = compound.getString("monitored" + i);
        }
        lithographed = compound.getBoolean("lithographed");
        relieved = compound.getBoolean("relieved");
        chainedTime = compound.getInteger("chainedTime");
        ethereal = compound.getBoolean("ethereal");
        pontifexRobeToggle = compound.getBoolean("pontifexRobeToggle");
        fleshAmount = compound.getInteger("fleshAmount");
    }

    @Override
    public void init(Entity entity, World world) {
        if (!(player instanceof FakePlayer)) {
            this.player = player;
        } else {
            player = null;
        }
    }

    public void sync() {
        if (this.player == null)
            return;
        sync(this.player);
    }

    public void sync(EntityPlayer player) {
        if (player == null || player instanceof FakePlayer)
            return;
        NBTTagCompound data = new NBTTagCompound();
        serializeNBT();
        data.setString("SyncName", player.getName());
        RenaissanceCore.packetInstance.sendTo(new PacketSyncPlayer(data), (EntityPlayerMP) player);
    }

}