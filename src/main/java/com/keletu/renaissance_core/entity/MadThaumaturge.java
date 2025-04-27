package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.items.PontifexRobe;
import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.lib.SoundsTC;

public class MadThaumaturge extends EntityMob {

    private static final DataParameter<Byte> RIDING = EntityDataManager.createKey(MadThaumaturge.class, DataSerializers.BYTE);
    private int attackTime = 0;

    public MadThaumaturge(World w) {
        super(w);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, Thaumaturge.class, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(RIDING, (byte) 0);
    }

    public double getYOffset() {
        return this.isRiding() ? -1.5 : 0.0;
    }

    public int getTotalArmorValue() {
        int i = super.getTotalArmorValue() + 2;

        if (i > 20) {
            i = 20;
        }

        return i;
    }

    public boolean canPickUpLoot() {
        return false;
    }

    public boolean isAIDisabled() {
        return false;
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":sad"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":sad"));
    }

    @Override
    public int getTalkInterval() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.3F;
    }

    public void addArmor(DifficultyInstance diff) {
        this.setEquipmentBasedOnDifficulty(diff);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance diff) {
        if (this.world.rand.nextBoolean()) {
            this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ItemsTC.voidHoe));
        } else {
            this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ItemsTC.voidShovel));
        }
        this.inventoryArmorDropChances[0] = 0.05F;
    }

    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData par1EntityLivingData) {
        par1EntityLivingData = super.onInitialSpawn(diff, par1EntityLivingData);
        this.setEquipmentBasedOnDifficulty(diff);
        return super.onInitialSpawn(diff, par1EntityLivingData);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            if (player.getHeldItemMainhand().isEmpty()) return true;
            if (PontifexRobe.isFullSet(player) && player.getHeldItemMainhand().getItem() == RCItems.crimson_annales) {
                if (!player.world.isRemote) {
                    this.setDead();
                    EntityCultist cultist = null;
                    int rand = this.rand.nextInt(3);
                    switch (rand) {
                        case 0: {
                            cultist = new EntityCultistKnight(this.world);
                            break;
                        }
                        case 1: {
                            cultist = new EntityCultistCleric(this.world);
                            break;
                        }
                        case 2: {
                            cultist = new CrimsonPaladin(this.world);
                            break;
                        }
                    }
                    cultist.setPositionAndRotation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                    cultist.onInitialSpawn(world.getDifficultyForLocation(this.getPosition()), null);
                    player.world.spawnEntity(cultist);
                    cultist.playSound(SoundsTC.craftfail, 1.0F, 1.0F);
                } else {
                    for (int i = 0; i < 4; i++) {
                        RenaissanceCore.proxy.bloodinitiation(player, this);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return true;
    }

    public int getRiding() {
        return this.getDataManager().get(RIDING);
    }

    public void setRiding(int s) {
        this.getDataManager().set(RIDING, (byte) s);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        --this.attackTime;

        if (!this.world.isRemote) {
            if (this.getRidingEntity() == null && this.getAttackTarget() != null && this.getAttackTarget().isBeingRidden() && !this.getAttackTarget().isDead && this.getDistanceSq(this.getAttackTarget()) < 4.0 && this.world.rand.nextInt(100) > 90) {
                this.startRiding(this.getAttackTarget());
                this.setRiding(this.getAttackTarget().getEntityId());
            }
            if (this.getRidingEntity() != null && !this.isDead && this.attackTime <= 0) {
                this.attackTime = 10 + this.rand.nextInt(10);
                this.attackEntityAsMob(this.getRidingEntity());
                if ((double) this.rand.nextFloat() < 0.2) {
                    this.dismountRidingEntity();
                    this.setRiding(-1);
                }
            }

            if (this.getRidingEntity() == null && this.getRiding() != -1) {
                this.setRiding(-1);
            }
        } else if (this.getRidingEntity() == null && this.getRiding() != -1) {
            Entity e = this.world.getEntityByID(this.getRiding());
            if (e != null) {
                this.startRiding(e);
            }
        } else if (this.getRidingEntity() != null && this.getRiding() == -1) {
            this.dismountRidingEntity();
        }
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
        int d = this.rand.nextInt(10);
        if (d > 7) {
            int r = this.rand.nextInt(10);
            if (r <= 3)
                this.entityDropItem(new ItemStack(ItemsTC.curio, 1, 5), 1.5F);

        }
        super.dropFewItems(flag, i);
    }
}