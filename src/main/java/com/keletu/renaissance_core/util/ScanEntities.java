package com.keletu.renaissance_core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.ThaumcraftApi.EntityTagsNBT;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.research.IScanThing;

import java.util.List;


public class ScanEntities implements IScanThing {

    String research;
    List<Class> entityClass;
    EntityTagsNBT[] NBTData;

    public ScanEntities(String research, List<Class> entityClass) {
        this.research = research;
        this.entityClass = entityClass;
    }

    public ScanEntities(String research, List<Class> entityClass, EntityTagsNBT... nbt) {
        this.research = research;
        this.entityClass = entityClass;
        NBTData = nbt;
    }

    @Override
    public boolean checkThing(EntityPlayer player, Object obj) {
        if (obj != null && (entityClass.contains(obj.getClass()))) {
            if (NBTData != null && NBTData.length > 0) {
                boolean b = true;
                NBTTagCompound tc = new NBTTagCompound();
                ((Entity) obj).writeToNBT(tc);
                for (EntityTagsNBT nbt : NBTData) {
                    if (!tc.hasKey(nbt.name) || !ThaumcraftApiHelper.getNBTDataFromId(tc, tc.getTagId(nbt.name), nbt.name).equals(nbt.value)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String getResearchKey(EntityPlayer player, Object object) {
        return research;
    }

}