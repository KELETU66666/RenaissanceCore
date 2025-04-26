package com.keletu.renaissance_core.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICapConcilium extends INBTSerializable<NBTTagCompound> {

	public static ICapConcilium get(EntityPlayer player) {
		return player.getCapability(RCCapabilities.CONCILIUM, null);
	}

	public void sync();

	public void init(Entity player, World world);

	@Override
	public NBTTagCompound serializeNBT();

	@Override
	public void deserializeNBT(NBTTagCompound nbt);

}