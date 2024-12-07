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

public class EntityTaintChicken extends EntityMob implements ITaintedMob {
    public boolean field_753_a;
    public float field_752_b;
    public float destPos;
    public float field_757_d;
    public float field_756_e;
    public float field_755_h;

    public EntityTaintChicken(final World par1World) {
        super(par1World);
        this.field_753_a = false;
        this.field_752_b = 0.0f;
        this.destPos = 0.0f;
        this.field_755_h = 1.0f;
        this.setSize(0.5f, 0.8f);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.3f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityVillager.class, 1.0, true));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityAnimal.class, 1.0, true));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(5, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityAnimal.class, false));
    }

    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }

    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4);
    }

    public int getTotalArmorValue() {
        return 2;
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.field_756_e = this.field_752_b;
        this.field_757_d = this.destPos;
        this.destPos += (float) ((this.onGround ? -1 : 4) * 0.3);
        if (this.destPos < 0.0f) {
            this.destPos = 0.0f;
        }
        if (this.destPos > 1.0f) {
            this.destPos = 1.0f;
        }
        if (!this.onGround && this.field_755_h < 1.0f) {
            this.field_755_h = 1.0f;
        }
        this.field_755_h *= (float) 0.9;
        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.9;
        }
        this.field_752_b += this.field_755_h * 2.0f;
        //if (this.world.isRemote && this.ticksExisted < 5) {
        //    for (int a = 0; a < FXDispatcher.INSTANCE.particleCount(10); ++a) {
        //        Thaumcraft.proxy.getFX().splooshFX((Entity)this);
        //    }
        //}
    }

    @Override
    public void fall(final float distance, final float damageMultiplier) {
    }

    @Override
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_CHICKEN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_CHICKEN_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_CHICKEN_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
    }

    protected float getSoundPitch() {
        return 0.7f;
    }

    protected Item getDropItem() {
        return ThaumcraftApiHelper.makeCrystal(Aspect.FLUX).getItem();
    }

    protected void dropFewItems(final boolean flag, final int i) {
        if (this.world.rand.nextInt(4) == 0) {
            this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
        } else {
            this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
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
 