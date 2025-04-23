package com.keletu.renaissance_core.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.lib.SoundsTC;

import javax.annotation.Nullable;
import java.util.Collection;

public class StrayedMirror extends EntityMob {
    public StrayedMirror(World w) {
        super(w);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 0.6D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, Thaumaturge.class, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.5D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }

    @Override
    public EntityLivingBase getAttackTarget() {
        return super.getAttackTarget();
    }

    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
        this.entityDropItem(new ItemStack(ItemsTC.mirroredGlass, 1), 1.5F);
        super.dropFewItems(flag, i);
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!source.isMagicDamage() && source.getImmediateSource() instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase) source.getImmediateSource();

            if (!source.isExplosion())
            {
                entitylivingbase.attackEntityFrom(new EntityDamageSource("magic", this).setMagicDamage(), amount);
            }
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        this.swingArm(EnumHand.MAIN_HAND);
        return super.attackEntityAsMob(p_70652_1_);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.crystal;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundsTC.jar;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_GLASS_BREAK;
    }

    protected float getSoundVolume() {
        return 2.0F;
    }

    @Override
    public int getTalkInterval() {
        return 65;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote) {
            if ((ticksExisted % 40 == 0) && getAttackTarget() != null) {
                EntityLivingBase target = getAttackTarget();

                Collection<PotionEffect> potions = this.getActivePotionEffects();
                if (!potions.isEmpty()) {
                    for (PotionEffect potion : potions) {
                        target.addPotionEffect(potion);
                        this.removePotionEffect(potion.getPotion());
                    }
                }

                if(this.isBurning()){
                    target.setFire(this.fire);
                    this.extinguish();
                }
            }
        }
    }
}