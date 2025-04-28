package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.projectile.EntityGolemOrb;
import thecodex6824.thaumicaugmentation.api.TAItems;

public class EntityCrimsonPaladin extends EntityCultist implements IRangedAttackMob {
    public static ItemStack staff = new ItemStack(TAItems.GAUNTLET, 1, 0);

    public EntityCrimsonPaladin(World w) {
        super(w);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 8.0, 1.0, 20, 40, 24.0F));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityThaumaturge.class, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    public void addArmor(DifficultyInstance diff) {
        this.setEquipmentBasedOnDifficulty(diff);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance diff) {
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonRobeHelm));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPlateChest));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonRobeLegs));
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        this.setHeldItem(EnumHand.MAIN_HAND, staff.copy());
    }

    public boolean isAIDisabled() {
        return false;
    }


    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData p_110161_1_) {
        this.setEquipmentBasedOnDifficulty(diff);
        return super.onInitialSpawn(diff, p_110161_1_);
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        this.swingArm(EnumHand.MAIN_HAND);
        return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), f);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f) {
        if (!this.world.isRemote) {
            world.playSound(null, this.getPosition(), new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":spell")), SoundCategory.HOSTILE, 1.0F, 1.0F);
            int rand = world.rand.nextInt(2);
            if (rand == 0) {
                if (entitylivingbase instanceof EntityPlayer) {
                    EntityEtherealShackles shackles = new EntityEtherealShackles(entitylivingbase.world, this);
                    double d0 = entitylivingbase.posX + entitylivingbase.motionX - this.posX;
                    double d1 = entitylivingbase.posY - this.posY;
                    double d2 = entitylivingbase.posZ + entitylivingbase.motionZ - this.posZ;
                    shackles.shoot(d0, d1, d2, 2.0F, 2.0F);
                    this.world.spawnEntity(shackles);
                    shackles.playSound(new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":shackles_throw")), 0.5F, 1.0F);
                    this.swingArm(EnumHand.MAIN_HAND);
                }
            } else {
                this.faceEntity(entitylivingbase, 100.0F, 100.0F);
                EntityGolemOrb orb = new EntityGolemOrb(this.world, this, entitylivingbase, true);
                Vec3d look = this.getLook(1.0F);
                orb.setPosition(orb.posX + look.x, orb.posY + look.y, orb.posZ + look.z);
                orb.shoot(look.x, look.y, look.z, 0.66F, 3.0F);
                this.world.spawnEntity(orb);
                this.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":chant"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":growl"));
    }

    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    public int getTalkInterval() {
        return 450;
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
/*        int r = this.rand.nextInt(10);
        if (r == 0) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 9), 1.5F);
        } else if (r <= 1) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 17), 1.5F);
        } else if (r <= 3 + i) {
            this.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 18), 1.5F);
        }*/

        super.dropFewItems(flag, i);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
    }
}