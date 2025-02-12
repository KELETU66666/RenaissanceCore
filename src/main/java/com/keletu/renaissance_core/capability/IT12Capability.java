package com.keletu.renaissance_core.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IT12Capability extends INBTSerializable<NBTTagCompound> {
    public static IT12Capability get(EntityPlayer player) {
        return player.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);
    }

    public void canTakeOffT12(boolean can);

    public boolean getCanTakeOffT12();

    public void setCanPickOffT12(boolean bool);

    @Override
    public NBTTagCompound serializeNBT();

    @Override
    public void deserializeNBT(NBTTagCompound nbt);

    public void setLocationCorrect();

    public void matchStats();
}
