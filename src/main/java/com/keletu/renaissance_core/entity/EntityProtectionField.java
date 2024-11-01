package com.keletu.renaissance_core.entity;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityProtectionField extends EntityEnderCrystal {

    public EntityProtectionField(World worldIn) {
        super(worldIn);
    }

    public boolean attackEntityFrom(DamageSource s, float f) {
        if (s.getTrueSource() instanceof EntityPlayer && ((EntityPlayer) s.getTrueSource()).isCreative()) {
            {
                this.setDead();
                return true;
            }
        } else
            return false;
    }
}
