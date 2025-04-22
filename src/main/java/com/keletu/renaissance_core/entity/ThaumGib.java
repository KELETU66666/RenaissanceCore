package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.entities.monster.cult.EntityCultist;

public class ThaumGib extends EntityMob {

    private static final DataParameter<Byte> TYPE = EntityDataManager.createKey(ThaumGib.class, DataSerializers.BYTE);

    public ThaumGib(World world) {
        super(world);
        this.setSize(0.6F, 0.5F);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.63F));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 0.6D, false));
        this.tasks.addTask(7, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, Thaumaturge.class, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.85D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(TYPE, (byte) 0);
    }

    public ThaumGib(World world, EntityLivingBase parent, int gibType) {
        this(world);
        setType(gibType);
        float f = 0.0f;
        switch (gibType) {
            case 0: {
                f = 1.5f;
                break;
            }
            case 1:
            case 2:
            case 3: {
                f = 1.0f;
                break;
            }

        }
        setLocationAndAngles(parent.posX, parent.getEntityBoundingBox().minY + f, parent.posZ, parent.rotationYaw, parent.rotationPitch);
        setEntityMotionFromVector(this, new Vector3(this.posX + (-0.5 + world.rand.nextGaussian()) * 2.0, this.posY, this.posZ + (-0.5 + world.rand.nextGaussian()) * 2.0), 0.5f);
    }

    public static void setEntityMotionFromVector(Entity entity, Vector3 originalPosVector, float modifier) {
        Vector3 entityVector = Vector3.fromEntityCenter(entity);
        Vector3 finalVector = originalPosVector.copy().subtract(entityVector);
        if (finalVector.mag() > 1.0) {
            finalVector.normalize();
        }

        entity.motionX = finalVector.x * (double)modifier;
        entity.motionY = finalVector.y * (double)modifier;
        entity.motionZ = finalVector.z * (double)modifier;
    }

    public byte getType() {
        return this.dataManager.get(TYPE);
    }

    public void setType(int par1) {
        this.dataManager.set(TYPE, (byte) par1);
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float p_70785_2_) {
        if (p_70785_2_ > 2.0F && p_70785_2_ < 6.0F && this.rand.nextInt(10) == 0) {
            if (this.onGround && source.getTrueSource() != null) {
                double d0 = source.getTrueSource().posX - this.posX;
                double d1 = source.getTrueSource().posZ - this.posZ;
                float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1);
                this.motionX = d0 / (double) f2 * 0.5D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                this.motionZ = d1 / (double) f2 * 0.5D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                this.motionY = 0.4000000059604645D;
            }
        }

        return super.attackEntityFrom(source, p_70785_2_);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":gargle_one"));
    }

    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
        int r = this.rand.nextInt(10);
        int a = this.rand.nextInt(2);
        if (r <= 3) {
            this.entityDropItem(new ItemStack(ItemsTC.chunks, a + i), 1.5F);
        } else if (r <= 6) {
            this.entityDropItem(new ItemStack(ItemsTC.chunks, a + i), 1.5F);
        } else {
            this.entityDropItem(new ItemStack(ItemsTC.chunks, a + i), 1.5F);
        }

        super.dropFewItems(flag, i);
    }

    @Override
    public int getTalkInterval() {
        return 65;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":gargle_pain"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setByte("Type", (byte) getType());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        setType(p_70037_1_.getByte("Type"));
    }
}