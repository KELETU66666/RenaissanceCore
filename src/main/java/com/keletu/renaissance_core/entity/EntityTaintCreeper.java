package com.keletu.renaissance_core.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;

import java.util.List;

public class EntityTaintCreeper extends EntityMob implements ITaintedMob {
    private static final DataParameter<Integer> STATE = EntityDataManager.createKey(EntityTaintCreeper.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(EntityTaintCreeper.class, DataSerializers.BOOLEAN);
    private int lastActiveTime;
    private int timeSinceIgnited;
    private int fuseTime;
    private int explosionRadius;

    public EntityTaintCreeper(final World par1World) {
        super(par1World);
        this.fuseTime = 30;
        this.explosionRadius = 3;
        this.tasks.addTask(1, new EntityAISwimming(this));
        //this.tasks.addTask(2, new EntityAICreeperSwell(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityOcelot.class, 6.0f, 1.0, 1.2));
        this.tasks.addTask(4, new AIAttackOnCollide(this, 1.0, false));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }

    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }

    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }

    public void fall(final float distance, final float damageMultiplier) {
        super.fall(distance, damageMultiplier);
        this.timeSinceIgnited += (int) (distance * 1.5f);
        if (this.timeSinceIgnited > this.fuseTime - 5) {
            this.timeSinceIgnited = this.fuseTime - 5;
        }
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (this.dataManager.get(POWERED)) {
            compound.setBoolean("powered", true);
        }

        compound.setShort("Fuse", (short) this.fuseTime);
        compound.setByte("ExplosionRadius", (byte) this.explosionRadius);
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.dataManager.set(POWERED, compound.getBoolean("powered"));

        if (compound.hasKey("Fuse", 99)) {
            this.fuseTime = compound.getShort("Fuse");
        }

        if (compound.hasKey("ExplosionRadius", 99)) {
            this.explosionRadius = compound.getByte("ExplosionRadius");
        }
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(STATE, -1);
        this.dataManager.register(POWERED, Boolean.FALSE);
    }

    protected Item getDropItem() {
        return ThaumcraftApiHelper.makeCrystal(Aspect.FLUX).getItem();
    }

    protected void dropFewItems(final boolean flag, final int i) {
        if (this.world.rand.nextBoolean()) {
            this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
        } else {
            this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
        }
    }

    public void onUpdate() {
        if (this.isEntityAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            final int var1 = this.getCreeperState();
            if (var1 > 0 && this.timeSinceIgnited == 0) {
                this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
            }
            this.timeSinceIgnited += var1;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }
            if (this.timeSinceIgnited >= 30) {
                this.timeSinceIgnited = 30;
                if (!this.world.isRemote) {
                    this.world.createExplosion(this, this.posX, this.posY + this.height / 2.0f, this.posZ, 1.5f, false);
                    final List<EntityLivingBase> ents = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ).expand(6.0, 6.0, 6.0).expand(-6.0, -6.0, -6.0));
                    if (ents.size() > 0) {
                        for (final EntityLivingBase ent : ents) {
                            if (!(ent instanceof ITaintedMob) && !ent.isEntityUndead()) {
                                ent.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 100, 0, false, true));
                            }
                        }
                    }
                    final int x = (int) this.posX;
                    final int y = (int) this.posY;
                    final int z = (int) this.posZ;
                    for (int a = 0; a < 10; ++a) {
                        final int xx = x + (int) ((this.rand.nextFloat() - this.rand.nextFloat()) * 5.0f);
                        final int zz = z + (int) ((this.rand.nextFloat() - this.rand.nextFloat()) * 5.0f);
                        if (this.world.rand.nextBoolean()) {
                            final BlockPos bp = new BlockPos(xx, y, zz);
                            final IBlockState bs = this.world.getBlockState(bp);
                            if (this.world.isBlockNormalCube(bp.down(), false) && bs.getBlock().isReplaceable(this.world, bp)) {
                                this.world.setBlockState(bp, BlocksTC.taintFibre.getDefaultState());
                            }
                        }
                    }
                    this.setDead();
                } else {
                    //for (int a2 = 0; a2 < Thaumcraft.proxy.getFX().particleCount(100); ++a2) {
                    //    Thaumcraft.proxy.getFX().taintsplosionFX((Entity) this);
                    //}
                }
            }
        }
        super.onUpdate();
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        //if (this.world.isRemote && this.ticksExisted < 5) {
        //    for (int a = 0; a < Thaumcraft.proxy.getFX().particleCount(10); ++a) {
        //        Thaumcraft.proxy.getFX().splooshFX((Entity) this);
        //    }
        //}
    }

    public float getCreeperFlashIntensity(final float par1) {
        return (this.lastActiveTime + (this.timeSinceIgnited - this.lastActiveTime) * par1) / 28.0f;
    }

    protected String getHurtSound() {
        return "mob.creeper.say";
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CREEPER_DEATH;
    }

    protected float getSoundPitch() {
        return 0.7f;
    }

    public boolean attackEntityAsMob(final Entity par1Entity) {
        return true;
    }

    public int getCreeperState() {
        return this.dataManager.get(STATE);
    }

    public void setCreeperState(int state) {
        this.dataManager.set(STATE, state);
    }
}
