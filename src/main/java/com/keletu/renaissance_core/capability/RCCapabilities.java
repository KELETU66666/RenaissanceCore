package com.keletu.renaissance_core.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class RCCapabilities {
    @CapabilityInject(IT12Capability.class)
    public static Capability<IT12Capability> PICK_OFF_T12_CAP;

    public static class CapabilityCanPickoffT12 implements Capability.IStorage<IT12Capability> {

        @Override
        public NBTBase writeNBT(Capability<IT12Capability> capability, IT12Capability instance, EnumFacing side) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("canPickOff", instance.getCanTakeOffT12());
            return tag;
        }

        @Override
        public void readNBT(Capability<IT12Capability> capability, IT12Capability instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound tag = (NBTTagCompound) nbt;
            instance.setCanPickOffT12(tag.getBoolean("canPickOff"));
        }
    }
}
