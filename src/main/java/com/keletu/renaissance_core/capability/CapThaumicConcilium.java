package com.keletu.renaissance_core.capability;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.events.EventHandlerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.FakePlayer;

public class CapThaumicConcilium implements ICapConcilium {
    public static final ResourceLocation CHAINED_TIME = new ResourceLocation(RenaissanceCore.MODID, "chained_time");
    public static final ResourceLocation ETHEREAL = new ResourceLocation(RenaissanceCore.MODID, "ethereal");
    public static final ResourceLocation PONTIFEX_TOGGLE = new ResourceLocation(RenaissanceCore.MODID, "pontifex_toggle");

    public EntityPlayer player;
    public boolean lithographed;
    public boolean relieved;
    private boolean pontifexRobeToggle;
    public String[] monitored;
    private int chainedTime;
    private boolean ethereal;
    public int fleshAmount;

    public CapThaumicConcilium(EntityPlayer player) {
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

    @Override
    public int getChainedTime() {
        return this.chainedTime;
    }

    @Override
    public void setChainedTime(int time) {
        if (time != this.chainedTime) {
            this.updateStat(CHAINED_TIME, chainedTime);
        }

        this.chainedTime = time;
    }

    @Override
    public boolean isEthereal() {
        return this.ethereal;
    }

    @Override
    public void setEthereal(boolean ethereal) {
        if (ethereal != this.ethereal) {
            this.updateStat(ETHEREAL, ethereal);
        }

        this.ethereal = ethereal;
    }

    @Override
    public boolean getPontifexRobeToggle() {
        return this.ethereal;
    }

    @Override
    public void setPontifexRobeToggle(boolean toggle) {
        if (toggle != this.pontifexRobeToggle) {
            this.updateStat(PONTIFEX_TOGGLE, pontifexRobeToggle);
        }

        this.pontifexRobeToggle = toggle;
    }

    @Override
    public void setLocationCorrect() {
        this.updateStat(CHAINED_TIME, chainedTime);
        this.updateStat(ETHEREAL, ethereal);
        this.updateStat(PONTIFEX_TOGGLE, pontifexRobeToggle);
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

    private void updateStat(ResourceLocation stat, boolean value) {
        if (this.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) this.player;
            player.addStat(new StatBase(stat.getNamespace(), new TextComponentString(stat.getPath())), value ? 1 : 0);
        }
    }

    private void updateStat(ResourceLocation stat, int value) {
        if (this.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) this.player;
            player.addStat(new StatBase(stat.getNamespace(), new TextComponentString(stat.getPath())), value);
        }
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
        EventHandlerEntity.syncToClientConcilium(this.player);
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final CapThaumicConcilium counter;

        public Provider(EntityPlayer player) {
            this.counter = new CapThaumicConcilium(player);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == RCCapabilities.CONCILIUM;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == RCCapabilities.CONCILIUM ? (T) this.counter : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) RCCapabilities.CONCILIUM.getStorage().writeNBT(RCCapabilities.CONCILIUM, this.counter, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            RCCapabilities.CONCILIUM.getStorage().readNBT(RCCapabilities.CONCILIUM, this.counter, null, nbt);
        }
    }
}