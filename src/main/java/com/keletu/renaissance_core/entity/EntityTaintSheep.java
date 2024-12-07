package com.keletu.renaissance_core.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;

import java.util.ArrayList;

public class EntityTaintSheep extends EntityMob implements IShearable, ITaintedMob
{
    private static final DataParameter<Byte> DYE_COLOR = EntityDataManager.createKey(EntityTaintSheep.class, DataSerializers.BYTE);

    /**
     * Internal crafting inventory used to check the result of mixing dyes corresponding to the fleece color when
     * breeding sheep.
     */
    private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container()
    {
        public boolean canInteractWith(EntityPlayer playerIn)
        {
            return false;
        }
    }, 2, 1);

    /**
     * Used to control movement as well as wool regrowth. Set to 40 on handleHealthUpdate and counts down with each
     * tick.
     */
    private int sheepTimer;
    private EntityAIEatGrass entityAIEatGrass;

    public boolean canAttackClass(final Class clazz) {
        return !ITaintedMob.class.isAssignableFrom(clazz);
    }

    public boolean isOnSameTeam(Entity otherEntity) {
        return otherEntity instanceof ITaintedMob || super.isOnSameTeam(otherEntity);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }

    protected Item getDropItem() {
        return ThaumcraftApiHelper.makeCrystal(Aspect.FLUX).getItem();
    }

    protected void dropFewItems(final boolean flag, final int i) {
        if (this.world.rand.nextInt(3) == 0) {
            if (this.world.rand.nextBoolean()) {
                this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
            }
            else {
                this.entityDropItem(ThaumcraftApiHelper.makeCrystal(Aspect.FLUX), this.height / 2.0f);
            }
        }
    }

    public int getTotalArmorValue() {
        return 1;
    }

    protected float getSoundPitch() {
        return 0.7f;
    }

    public boolean attackEntityAsMob(final Entity victim) {
        if (super.attackEntityAsMob(victim)) {
            if (victim instanceof EntityLivingBase) {
                byte b0 = 0;
                if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 3;
                }
                else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 6;
                }
                if (b0 > 0 && this.rand.nextInt(b0 + 1) > 2) {
                    ((EntityLivingBase)victim).addPotionEffect(new PotionEffect(PotionFluxTaint.instance, b0 * 20, 0));
                }
            }
            return true;
        }
        return false;
    }
    public EntityTaintSheep(World worldIn)
    {
        super(worldIn);
        this.setSize(0.9F, 1.3F);
        this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.DYE));
        this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.DYE));
    }

    protected void initEntityAI()
    {
        this.entityAIEatGrass = new EntityAIEatGrass(this);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, this.entityAIEatGrass);
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityVillager.class, 1.0, true));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false));
    }

    protected void updateAITasks()
    {
        this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
        super.updateAITasks();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.world.isRemote)
        {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }

        super.onLivingUpdate();
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DYE_COLOR, (byte) 0);
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        if (id == 10)
        {
            this.sheepTimer = 40;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    public boolean isShearable(final ItemStack item, final IBlockAccess world, final BlockPos pos) {
        return !this.getSheared();
    }

    public ArrayList<ItemStack> onSheared(final ItemStack item, final IBlockAccess world, final BlockPos pos, final int fortune) {
        final ArrayList<ItemStack> ret = new ArrayList<>();
        this.setSheared(true);
        for (int i = 1 + this.rand.nextInt(3), j = 0; j < i; ++j) {
            ret.add(new ItemStack(Blocks.WOOL, 1, 10));
        }
        return ret;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Sheared", this.getSheared());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setSheared(compound.getBoolean("Sheared"));
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SHEEP_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SHEEP_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SHEEP_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    /**
     * returns true if a sheeps wool has been sheared
     */
    public boolean getSheared()
    {
        return (this.dataManager.get(DYE_COLOR) & 16) != 0;
    }

    /**
     * make a sheep sheared if set to true
     */
    public void setSheared(boolean sheared)
    {
        byte b0 = this.dataManager.get(DYE_COLOR);

        if (sheared)
        {
            this.dataManager.set(DYE_COLOR, (byte) (b0 | 16));
        }
        else
        {
            this.dataManager.set(DYE_COLOR, (byte) (b0 & -17));
        }
    }

    @SideOnly(Side.CLIENT)
    public float getHeadRotationPointY(float p_70894_1_)
    {
        if (this.sheepTimer <= 0)
        {
            return 0.0F;
        }
        else if (this.sheepTimer >= 4 && this.sheepTimer <= 36)
        {
            return 1.0F;
        }
        else
        {
            return this.sheepTimer < 4 ? ((float)this.sheepTimer - p_70894_1_) / 4.0F : -((float)(this.sheepTimer - 40) - p_70894_1_) / 4.0F;
        }
    }

    @SideOnly(Side.CLIENT)
    public float getHeadRotationAngleX(float p_70890_1_)
    {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36)
        {
            float f = ((float)(this.sheepTimer - 4) - p_70890_1_) / 32.0F;
            return ((float)Math.PI / 5F) + ((float)Math.PI * 7F / 100F) * MathHelper.sin(f * 28.7F);
        }
        else
        {
            return this.sheepTimer > 0 ? ((float)Math.PI / 5F) : this.rotationPitch * 0.017453292F;
        }
    }

    /**
     * This function applies the benefits of growing back wool and faster growing up to the acting entity. (This
     * function is used in the AIEatGrass)
     */
    public void eatGrassBonus()
    {
        this.setSheared(false);
    }

    public float getEyeHeight()
    {
        return 0.95F * this.height;
    }
}