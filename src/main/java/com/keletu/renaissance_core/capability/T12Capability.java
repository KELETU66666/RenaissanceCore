package com.keletu.renaissance_core.capability;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class T12Capability implements IT12Capability {
	public static final ResourceLocation CAN_PICK_OFF_T12 = new ResourceLocation(RenaissanceCore.MODID, "can_pick_off_t12");

	private final EntityPlayer player;
	private boolean bool = true;

	public T12Capability(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public boolean getCanTakeOffT12() {
		return this.bool;
	}

	@Override
	public void canTakeOffT12(boolean bool) {
		this.bool = bool;
	}

	@Override
	public void setCanPickOffT12(boolean bool) {
		if (bool != this.bool) {
			this.updateStat(CAN_PICK_OFF_T12, bool);
		}

		this.bool = bool;
	}

	@Override
	public void setLocationCorrect() {
		this.updateStat(CAN_PICK_OFF_T12, bool);
	}

	@Override
	public void matchStats() {
		this.updateStat(CAN_PICK_OFF_T12, this.bool);
	}

	private void updateStat(ResourceLocation stat, boolean value) {
		if (this.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) this.player;
			player.addStat(new StatBase(stat.getNamespace(), new TextComponentString(stat.getPath())), value ? 1 : 0);
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("canPickOff", this.bool);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.bool = tag.getBoolean("canPickOff");
	}

	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
		private final T12Capability counter;

		public Provider(EntityPlayer player) {
			this.counter = new T12Capability(player);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == RCCapabilities.PICK_OFF_T12_CAP;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return capability == RCCapabilities.PICK_OFF_T12_CAP ? (T) this.counter : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) RCCapabilities.PICK_OFF_T12_CAP.getStorage().writeNBT(RCCapabilities.PICK_OFF_T12_CAP, this.counter, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			RCCapabilities.PICK_OFF_T12_CAP.getStorage().readNBT(RCCapabilities.PICK_OFF_T12_CAP, this.counter, null, nbt);
		}
	}

}