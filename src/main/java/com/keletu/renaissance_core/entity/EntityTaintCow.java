package com.keletu.renaissance_core.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;

public class EntityTaintCow extends EntityMob implements ITaintedMob {
    public EntityTaintCow(final World par1World) {
        super(par1World);
        this.setSize(0.9f, 1.3f);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityVillager.class, 1.0, true));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.tasks.addTask(9, new AIAttackOnCollide(this, EntityAnimal.class, 1.0, false));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false));
        this.targetTasks.addTask(8, new EntityAINearestAttackableTarget<>(this, EntityAnimal.class, false));
    }

    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }

    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27);
    }

    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_COW_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_COW_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COW_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
    }

    protected float getSoundPitch() {
        return 0.7f;
    }

    protected float getSoundVolume() {
        return 0.4f;
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        //if (this.world.isRemote && this.ticksExisted < 5) {
        //    for (int a = 0; a < Thaumcraft.proxy.getFX().particleCount(10); ++a) {
        //        Thaumcraft.proxy.getFX().splooshFX((Entity)this);
        //    }
        //}
    }

    protected Item getDropItem() {
        return ThaumcraftApiHelper.makeCrystal(Aspect.FLUX).getItem();
    }

    protected void dropFewItems(final boolean flag, final int i) {
        if (this.world.rand.nextInt(3) == 0) {
            if (this.world.rand.nextBoolean()) {
                this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
            } else {
                this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
            }
        }
    }


    public boolean attackEntityAsMob(final Entity victim) {
        if (super.attackEntityAsMob(victim)) {
            if (victim instanceof EntityLivingBase) {
                byte b0 = 0;
                if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 3;
                } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 6;
                }
                if (b0 > 0 && this.rand.nextInt(b0 + 1) > 2) {
                    ((EntityLivingBase) victim).addPotionEffect(new PotionEffect(PotionFluxTaint.instance, b0 * 20, 0));
                }
            }
            return true;
        }
        return false;
    }
}
