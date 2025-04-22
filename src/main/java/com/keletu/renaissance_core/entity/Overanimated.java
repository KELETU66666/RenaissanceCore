package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.packet.PacketFXBloodsplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.entities.monster.cult.EntityCultist;

import javax.annotation.Nullable;

public class Overanimated extends EntityMob {
    public Overanimated(World w) {
        super(w);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 0.6D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, Thaumaturge.class, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    protected void onDeathUpdate() {
        if (!this.world.isRemote) {
            for (int i = 0; i < 6; i++) {
                ThaumGib gib = new ThaumGib(this.world, this, i);
                this.world.spawnEntity(gib);
                gib.setType(i);
            }
            RenaissanceCore.packetInstance.sendToAllAround(new PacketFXBloodsplosion(posX, posY, posZ), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
        }
        this.setDead();
    }

    @Override
    public void onDeath(DamageSource p_70645_1_) {
    }

    @Override
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        this.swingArm(EnumHand.MAIN_HAND);
        return super.attackEntityAsMob(p_70652_1_);
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
    public void onLivingUpdate() {
        if (this.getAttackTarget() != null) {
            this.faceEntity(this.getAttackTarget(), 100.0F, 100.0F);
        }
        super.onLivingUpdate();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":gargle_many"));
    }

    @Override
    public int getTalkInterval() {
        return 65;
    }

    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
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