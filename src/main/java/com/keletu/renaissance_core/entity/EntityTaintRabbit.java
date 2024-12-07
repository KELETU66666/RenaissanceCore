package com.keletu.renaissance_core.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;

public class EntityTaintRabbit extends EntityRabbit implements ITaintedMob
{

    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;
    
    public EntityTaintRabbit(final World worldIn) {
        super(worldIn);
        this.jumpTicks = 0;
        this.jumpDuration = 0;
        this.wasOnGround = false;
        this.currentMoveTypeDuration = 0;
        this.tasks.taskEntries.clear();
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new AIAttackOnCollide(this, EntityPlayer.class, 1.4, false));
        this.tasks.addTask(5, new EntityAIWander(this, 0.6));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
        this.tasks.addTask(8, new AIAttackOnCollide(this, EntityAnimal.class, 1.4, false));
        this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0f));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(8, new EntityAINearestAttackableTarget<>(this, EntityAnimal.class, false));
        this.setMovementSpeed(0.0);
    }
    
    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }
    
    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }
    
    public int getTotalArmorValue() {
        return (this.getRabbitType() == 99) ? 8 : 3;
    }
    
    public EntityRabbit createChild(final EntityAgeable ageable) {
        return null;
    }
    
    protected void dropFewItems(final boolean p_70628_1_, final int p_70628_2_) {
        for (int j = this.rand.nextInt(2) + this.rand.nextInt(1 + p_70628_2_), k = 0; k < j; ++k) {
            this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), 1);
        }
    }
    
    public boolean isBreedingItem(final ItemStack stack) {
        return false;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }
    
    public boolean attackEntityAsMob(final Entity entity) {
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
        if (this.getRabbitType() == 99) {
            f *= 2.0f;
        }
        int i = 0;
        if (entity instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entity).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }
        final boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);
        if (flag) {
            if (entity instanceof EntityLivingBase) {
                byte b0 = 0;
                if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 3;
                }
                else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 6;
                }
                if (b0 > 0 && this.rand.nextInt(b0 + 1) > 2) {
                    ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(PotionFluxTaint.instance, b0 * 20, 0));
                }
            }
            if (i > 0) {
                entity.addVelocity(-MathHelper.sin(this.rotationYaw * 3.1415927f / 180.0f) * i * 0.5f, 0.1, MathHelper.cos(this.rotationYaw * 3.1415927f / 180.0f) * i * 0.5f);
                this.motionX *= 0.6;
                this.motionZ *= 0.6;
            }
            final int j = EnchantmentHelper.getFireAspectModifier(this);
            if (j > 0) {
                entity.setFire(j * 4);
            }
            this.applyEnchantments(this, entity);
        }
        return flag;
    }
}
