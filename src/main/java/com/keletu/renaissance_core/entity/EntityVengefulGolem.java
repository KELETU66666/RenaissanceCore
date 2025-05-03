package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.packet.PacketEnslave;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;

import javax.annotation.Nullable;

public class EntityVengefulGolem extends EntityMob implements IRangedAttackMob {

    public static boolean isEnslaved = false;

    public EntityVengefulGolem(World w) {
        super(w);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 1.0, 0.5, 20, 40, 24.0F));
        this.tasks.addTask(7, new EntityAIWander(this, 0.4D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityThaumaturge.class, true));
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

    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":gnaw"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":gnaw"));
    }

    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
        int r = this.rand.nextInt(2);
        r += i;
        this.entityDropItem(new ItemStack(ItemsTC.nuggets, r, 6), 1.5F);
        super.dropFewItems(flag, i);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
        if (target instanceof EntityPlayer) {
            boolean muffed = false;
            //if(Integration.witchery){
            //    ItemStack currentArmor = target.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            //    if(currentArmor != null && currentArmor.getItem() == Integration.earmuffs){
            //        muffed = true;
            //    }
            //}
            String name = target.getName();
            if (!muffed && rand.nextInt(10) >= 2) {
                this.swingArm(EnumHand.MAIN_HAND);
                target.world.playSound(null, target.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.HOSTILE, 0.7F, 1.0F + target.world.rand.nextFloat() * 0.1F);
                if (rand.nextInt(10) > 5) {
                    int r = rand.nextInt(4);
                    target.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tc.golem.taunt." + r)).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                }
                RenaissanceCore.packetInstance.sendTo(new PacketEnslave(name, true), (EntityPlayerMP) target);
            } else {
                RenaissanceCore.packetInstance.sendTo(new PacketEnslave(name, false), (EntityPlayerMP) target);
            }
        } else if (target instanceof EntityLiving && target.world.rand.nextFloat() > 0.1F) {
            double x = target.posX + ((-0.5 + target.world.rand.nextDouble()) * 20.0);
            double y = target.posY + ((-0.5 + target.world.rand.nextDouble()) * 2.0);
            double z = target.posZ + ((-0.5 + target.world.rand.nextDouble()) * 20.0);

            ((EntityLiving) target).getNavigator().tryMoveToXYZ(x, y, z, 0.7);
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {

    }
}