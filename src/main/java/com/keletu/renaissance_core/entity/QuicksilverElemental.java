package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.ConfigsRC;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.QuicksilverCrucibleTile;
import com.keletu.renaissance_core.blocks.RFBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.crafting.TileCrucible;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class QuicksilverElemental extends EntityMob {
    public QuicksilverElemental(World w) {
        super(w);
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 0.6D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, Thaumaturge.class, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
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
        int r = this.rand.nextInt(8) + 4;
        r += i;
        this.entityDropItem(new ItemStack(ItemsTC.nuggets, r, 5), 1.5F);
        super.dropFewItems(flag, i);
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float f) {
        if (!source.isFireDamage()) {
            if (ConfigsRC.quicksilverImmortality) {
                return false;
            } else {
                f = 0.1F;
            }
        }
        BlockPos pos = new BlockPos(MathHelper.floor(posX),
                MathHelper.floor(posY),
                MathHelper.floor(posZ));
        TileEntity tile = this.world.getTileEntity(pos);
        if (tile instanceof TileCrucible) {
            world.setBlockToAir(pos);
            world.removeTileEntity(pos);
            QuicksilverCrucibleTile crucible = new QuicksilverCrucibleTile();
            world.setBlockState(pos, RFBlocks.quicksilver_crucible.getDefaultState());
            world.setTileEntity(pos, crucible);
            QuicksilverCrucibleTile placed = (QuicksilverCrucibleTile) world.getTileEntity(pos);
            placed.aspects.add(Aspect.EXCHANGE, 20);
            placed.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().expand(16.0, 16.0, 16.0));
            if (!players.isEmpty()) {
                for (EntityPlayer player : players) {
                    if (!ThaumcraftCapabilities.knowsResearch(player, "QUICKSILVERCRUCIBLE")) {
                        ThaumcraftApi.internalMethods.completeResearch(player, "QUICKSILVERCRUCIBLE");
                        player.world.playSound(null, player.getPosition(), SoundsTC.heartbeat, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }

            this.setDead();
        }
        return super.attackEntityFrom(source, f);
    }

    @Override
    public boolean attackEntityAsMob(Entity e) {
        if (e instanceof EntityLivingBase) {
            if (((EntityLivingBase) e).getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
                this.swingArm(EnumHand.MAIN_HAND);
                float f = (float) ((EntityLivingBase) e).getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                return e.attackEntityFrom(DamageSource.ANVIL, f);
            } else {
                this.swingArm(EnumHand.MAIN_HAND);
                return e.attackEntityFrom(DamageSource.ANVIL, (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
            }
        }
        this.swingArm(EnumHand.MAIN_HAND);
        return super.attackEntityAsMob(e);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.setSize(0.6F * (getHealth() / 30.0F), 1.8F * (getHealth() / 30.0F));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":melted"));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ITEM_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":melted"));
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
            if ((ticksExisted % 40 == 0) && (getAttackTarget() != null)) {
                EntityLivingBase target = getAttackTarget();
                PotionEffect effect = new PotionEffect(PotionVisExhaust.instance, 1000, 20);
                effect.getCurativeItems().clear();
                target.addPotionEffect(effect);

                Collection<PotionEffect> potions = target.getActivePotionEffects();
                if (!potions.isEmpty()) {

                    for (PotionEffect potion : potions) {
                        boolean badEffect = potion.getPotion().isBadEffect();
                        if (!badEffect) {
                            target.removePotionEffect(potion.getPotion());
                            break;
                        }
                    }
                }
            }
        }
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