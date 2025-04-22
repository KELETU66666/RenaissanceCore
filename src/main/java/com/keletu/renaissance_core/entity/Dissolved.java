package com.keletu.renaissance_core.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.lib.SoundsTC;

import javax.annotation.Nullable;

public class Dissolved extends EntityMob {
    public Dissolved(World w) {
        super(w);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 0.6D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.4D));
        this.tasks.addTask(7, new EntityAIWander(this, 0.4D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        //this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, MadThaumaturge.class, true));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, Thaumaturge.class, true));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, EntityCultist.class, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);

    }


    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {

        if (player.getHeldItem(hand).isEmpty()) return false;
        ItemStack stack = player.getHeldItem(hand);
        if (!(stack.getItem() instanceof ICaster)) return false;
        ItemStack focus = ((ICaster) stack.getItem()).getFocusStack(stack);
        if (focus == null) return false;
        if (focus.hasTagCompound()) {
            this.attackEntityFrom(DamageSource.OUT_OF_WORLD, 9000F);
            return true;
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource p_70645_1_) {
        for (int i = 0; i < 15; i++) {
            FXDispatcher.INSTANCE.sparkle((float) (this.posX + (-0.5 + this.world.rand.nextFloat())), (float) (this.posY + (this.world.rand.nextFloat() * 2)), (float) ((float) this.posZ + (-0.5 + this.world.rand.nextFloat())), 2, 0, 0);
        }

        super.onDeath(p_70645_1_);
    }


    @Override
    protected void entityInit() {
        super.entityInit();
    }

    public boolean canPickUpLoot() {
        return false;
    }


    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }


    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    protected SoundEvent getAmbientSound() {
        return SoundsTC.egidle;
    }

    protected SoundEvent getDeathSound() {
        return SoundsTC.egdeath;
    }

    public int getTalkInterval() {
        return 100;
    }

    @Override
    public void onLivingUpdate() {
        if ((ticksExisted % 200 == 0) && (getAttackTarget() != null)) {
            EntityLivingBase target = getAttackTarget();
            UpcomingHoleEntity hole = new UpcomingHoleEntity(world);
            hole.setPositionAndRotation(target.posX, target.posY, target.posZ, world.rand.nextFloat(), world.rand.nextFloat());
            world.spawnEntity(hole);
        }
        super.onLivingUpdate();

    }

    @Override
    public boolean attackEntityAsMob(Entity p_70652_1_) {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        this.swingArm(EnumHand.MAIN_HAND);
        return p_70652_1_.attackEntityFrom(DamageSource.OUT_OF_WORLD, f);
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
        int r = this.rand.nextInt(6);
        r += i;
        this.entityDropItem(new ItemStack(ItemsTC.voidSeed, r), 1.5F);
        super.dropFewItems(flag, i);
    }

    @Override
    public void performHurtAnimation() {
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