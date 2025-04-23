package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.cult.EntityCultist;

public class Samurai extends EntityMob {

    private static final DataParameter<Byte> TYPE = EntityDataManager.createKey(Samurai.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> ANGER = EntityDataManager.createKey(Samurai.class, DataSerializers.VARINT);

    public Samurai(World w) {
        super(w);
        this.tasks.addTask(0, new EntityAISwimming(this));
        //this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 0.6D, false));
        this.tasks.addTask(2, new SamuraiAttackAI(this, EntityLivingBase.class, 1D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1D));
        this.tasks.addTask(7, new EntityAIWander(this, 1D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, Thaumaturge.class, true));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, true));

        this.setSize(1.0F, 2.0F);

    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(15.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(TYPE, (byte) rand.nextInt(3));
        this.dataManager.register(ANGER, 0);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        //super.addRandomArmor();
    }

    @Override
    public int getTotalArmorValue() {
        return 20;
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean isOnSameTeam(Entity el) {
        return el instanceof Samurai;
    }

    @Override
    protected void damageEntity(DamageSource source, float damage) {
        if (!this.isEntityInvulnerable(source)) {
            damage = ForgeHooks.onLivingHurt(this, source, damage);
            if (damage <= 0) return;
            int type = getType();
            damage /= type == 2 ? 25.0F : type == 1 ? 20.0F : 15.0F;
            damage = this.applyPotionDamageCalculations(source, damage);
            float f1 = damage;
            damage = Math.max(damage - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f1 - damage));

            if (damage != 0.0F) {
                float f2 = this.getHealth();
                this.setHealth(f2 - damage);
                this.getCombatTracker().trackDamage(source, f2, damage);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - damage);
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int i = 0;
        int type = getType();
        f += type == 2 ? 15.0F : type == 1 ? 10.0F : 7.0F;

        if (target instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) target).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag) {
            if (i > 0) {
                target.addVelocity(-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F, 0.1D, MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F);
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0) {
                target.setFire(j * 4);
            }

            if (target instanceof EntityLivingBase) {
                EnchantmentHelper.applyThornEnchantments((EntityLivingBase) target, this);
            }

            EnchantmentHelper.applyArthropodEnchantments(this, target);
        }

        return flag;
    }

    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData par1EntityLivingData) {
        par1EntityLivingData = super.onInitialSpawn(diff, par1EntityLivingData);
        this.setType(this.world.rand.nextInt(3));
        return super.onInitialSpawn(diff, par1EntityLivingData);
    }

    public byte getType() {
        return this.dataManager.get(TYPE);
    }

    public void setType(int par1) {
        this.dataManager.set(TYPE, (byte) par1);
    }

    public int getAnger() {
        return this.dataManager.get(ANGER);
    }

    public void setAnger(int par1) {
        this.dataManager.set(ANGER, par1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":speech"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":uagh"));
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
        int r = this.rand.nextInt(3);
        int a = this.rand.nextInt(10);
        if (a % 3 == 0) {
            this.entityDropItem(new ItemStack(ItemsTC.baubles, 1, r), 1.5F);
        }
        r = this.rand.nextInt(10) + 4;
        switch (getType()) {
            case 0: {
                this.entityDropItem(new ItemStack(ItemsTC.nuggets, r, 6), 1.5F);
                break;
            }
            case 1: {
                this.entityDropItem(new ItemStack(ItemsTC.nuggets, r, 7), 1.5F);
                break;
            }
            case 2: {
                this.entityDropItem(new ItemStack(ItemsTC.nuggets, r, 7), 1.5F);
                break;
            }
        }
        super.dropFewItems(flag, i);
    }

    @Override
    public void onUpdate() {
        if (getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
        }
        super.onUpdate();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Anger", getAnger());
        nbt.setByte("Type", getType());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setAnger(nbt.getInteger("Anger"));
        setType(nbt.getByte("Type"));
    }
}