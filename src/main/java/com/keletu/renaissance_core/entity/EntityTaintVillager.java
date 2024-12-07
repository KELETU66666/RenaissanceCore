package com.keletu.renaissance_core.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;

public class EntityTaintVillager extends EntityMob implements ITaintedMob {
    private int randomTickDivider;
    Village villageObj;

    public EntityTaintVillager(final World par1World) {
        super(par1World);
        this.randomTickDivider = 0;
        this.villageObj = null;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(2, new AIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0f, 1.0f));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityVillager.class, 5.0f, 0.02f));
        this.tasks.addTask(9, new EntityAIWander(this, 1.0));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0f));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }

    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        //if (this.world.isRemote && this.ticksExisted < 5) {
        //    for (int a = 0; a < Thaumcraft.proxy.getFX().particleCount(10); ++a) {
        //        Thaumcraft.proxy.getFX().splooshFX((Entity) this);
        //    }
        //}
    }

    protected void handleJumpWater() {
        final int randomTickDivider = this.randomTickDivider - 1;
        this.randomTickDivider = randomTickDivider;
        if (randomTickDivider <= 0) {
            final BlockPos blockpos = new BlockPos(this);
            this.world.villageCollection.addToVillagerPositionList(blockpos);
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.villageObj = this.world.villageCollection.getNearestVillage(blockpos, 32);
            if (this.villageObj == null) {
                this.detachHome();
            } else {
                final BlockPos blockpos2 = this.villageObj.getCenter();
                this.setHomePosAndDistance(blockpos2, (int) (this.villageObj.getVillageRadius() * 1.0f));
            }
        }
        super.handleJumpWater();
    }

    protected Item getDropItem() {
        return ThaumcraftApiHelper.makeCrystal(Aspect.FLUX).getItem();
    }

    protected void dropFewItems(final boolean flag, final int i) {
        if (this.world.rand.nextInt(3) == 0) {
            this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
        } else {
            this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
        }
        if (this.world.rand.nextInt(8) < 1 + i) {
            this.entityDropItem(new ItemStack(Items.GOLD_NUGGET), 1.5f);
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    protected float getSoundPitch() {
        return 0.7f;
    }

    public void setRevengeTarget(final EntityLivingBase par1EntityLiving) {
        super.setRevengeTarget(par1EntityLiving);
        if (this.villageObj != null && par1EntityLiving != null) {
            this.villageObj.addOrRenewAgressor(par1EntityLiving);
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
