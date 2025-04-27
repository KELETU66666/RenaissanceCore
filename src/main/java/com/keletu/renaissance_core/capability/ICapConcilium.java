package com.keletu.renaissance_core.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICapConcilium extends INBTSerializable<NBTTagCompound> {

	static ICapConcilium get(EntityPlayer player) {
		return player.getCapability(RCCapabilities.CONCILIUM, null);
	}

	void sync();

	void init(Entity player, World world);

	@Override
	NBTTagCompound serializeNBT();

	@Override
	void deserializeNBT(NBTTagCompound nbt);

	int getChainedTime();

	void setChainedTime(int time);

	boolean isEthereal();

	void setEthereal(boolean ethereal);

	boolean getPontifexRobeToggle();

	void setPontifexRobeToggle(boolean toggle);
	public void setLocationCorrect();
}