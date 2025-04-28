package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.capability.ICapConcilium;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityEtherealShackles extends EntityThrowable {
    public EntityEtherealShackles(World world) {
        super(world);
    }

    public EntityEtherealShackles(World world, EntityLivingBase entity) {
        super(world, entity);
    }

    public EntityEtherealShackles(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    protected float getGravityVelocity() {
        return 0.0F;
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 100) {
            this.setDead();
        }

    }

    @Override
    protected void onImpact(RayTraceResult mop) {
        if (mop.entityHit instanceof EntityPlayer) {
            if (!this.world.isRemote) {
                ICapConcilium capabilities = ICapConcilium.get((EntityPlayer) mop.entityHit);
                capabilities.setChainedTime(100);
                capabilities.sync();
                world.playSound(null, mop.entityHit.getPosition(), new SoundEvent(new ResourceLocation(RenaissanceCore.MODID+":shackles")), SoundCategory.HOSTILE, 0.9F, 0.9F);
            }
        } else if (mop.entityHit instanceof EntityLiving){
            ((EntityLivingBase) mop.entityHit).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 4));
        }
        this.setDead();
    }
}