package com.keletu.renaissance_core.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.casters.FocusEffect;

import java.util.Iterator;
import java.util.List;

public class EntityPositiveBurstOrb extends Entity implements IEntityAdditionalSpawnData {
    public int orbAge = 0;
    public int orbMaxAge = 150;
    public int orbCooldown;
    private int orbHealth = 5;
    public boolean vitaminize;
    public boolean fulfillment;
    private EntityPlayer closestPlayer;

    public boolean isInRangeToRenderDist(double par1) {
        double d1 = 0.5;
        d1 *= 64.0 * this.renderDistanceWeight;
        return par1 < d1 * d1;
    }

    public EntityPositiveBurstOrb(World world, double x, double y, double z, FocusEffect effect) {
        super(world);
        this.setSize(0.125F, 0.125F);
        this.setPosition(x, y, z);
        this.rotationYaw = (float) (Math.random() * 360.0);
        if (effect != null) {
            this.vitaminize = effect.getSettingValue("function") == 1;
            this.fulfillment = effect.getSettingValue("function") == 2;
        }

    }

    public double getYOffset() {
        return this.height / 2.0F;
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    public EntityPositiveBurstOrb(World par1World) {
        super(par1World);
        this.setSize(0.125F, 0.125F);
    }

    protected void entityInit() {
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        float f1 = 0.5F;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        int i = super.getBrightnessForRender();
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int) (f1 * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.orbCooldown > 0) {
            --this.orbCooldown;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.029999999329447746;
        if (this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ))).getMaterial() == Material.LAVA) {
            this.motionY = 0.20000000298023224;
            this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0, this.posZ);
        double d0 = 8.0;
        if (this.ticksExisted % 5 == 0 && this.closestPlayer == null) {
            List<Entity> targets = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ).grow(d0, d0, d0));
            if (targets.size() > 0) {
                double distance = Double.MAX_VALUE;
                Iterator i$ = targets.iterator();

                while (i$.hasNext()) {
                    Entity t = (Entity) i$.next();

                    double d = t.getDistance(this);
                    if (d < distance) {
                        distance = d;
                        this.closestPlayer = (EntityPlayer) t;
                    }
                }
            }
        }

        if (this.closestPlayer != null) {
            double d1 = (this.closestPlayer.posX - this.posX) / d0;
            double d2 = (this.closestPlayer.posY + (double) this.closestPlayer.getEyeHeight() - this.posY) / d0;
            double d3 = (this.closestPlayer.posZ - this.posZ) / d0;
            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
            double d5 = 1.0 - d4;
            if (d5 > 0.0) {
                d5 *= d5;
                this.motionX += d1 / d4 * d5 * 0.1;
                this.motionY += d2 / d4 * d5 * 0.1;
                this.motionZ += d3 / d4 * d5 * 0.1;
            }
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        float f = 0.98F;
        if (this.onGround) {
            f = 0.58800006F;
            IBlockState i = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ)));
            if (!i.getBlock().isAir(i, this.world, new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ)))) {
                f = i.getBlock().slipperiness * 0.98F;
            }
        }

        this.motionX *= f;
        this.motionY *= 0.9800000190734863;
        this.motionZ *= f;
        if (this.onGround) {
            this.motionY *= -0.8999999761581421;
        }

        ++this.orbAge;
        if (this.orbAge >= this.orbMaxAge) {
            this.setDead();
        }

    }

    public boolean handleWaterMovement() {
        return this.world.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this);
    }

    protected void dealFireDamage(int par1) {
        this.attackEntityFrom(DamageSource.IN_FIRE, (float) par1);
    }

    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if (this.world.isRemote || this.isDead) return false; //Forge: Fixes MC-53850
        if (this.isEntityInvulnerable(par1DamageSource)) {
            return false;
        } else {
            this.markVelocityChanged();
            this.orbHealth = (int) ((float) this.orbHealth - par2);
            if (this.orbHealth <= 0) {
                this.setDead();
            }

            return false;
        }
    }

    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setShort("Health", (byte) this.orbHealth);
        par1NBTTagCompound.setShort("Age", (short) this.orbAge);
        par1NBTTagCompound.setBoolean("Fulfillment", this.fulfillment);
        par1NBTTagCompound.setBoolean("Vitaminize", this.vitaminize);
    }

    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        this.orbHealth = par1NBTTagCompound.getShort("Health") & 255;
        this.orbAge = par1NBTTagCompound.getShort("Age");
        this.fulfillment = par1NBTTagCompound.getBoolean("Fulfillment");
        this.vitaminize = par1NBTTagCompound.getBoolean("Vitaminize");
    }

    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        if (!this.world.isRemote) {
            //if (!par1EntityPlayer.equals(caster)) {
            if (this.orbCooldown == 0 && par1EntityPlayer.xpCooldown == 0 && !par1EntityPlayer.getIsInvulnerable()) {
                if (this.fulfillment) {
                    if (par1EntityPlayer.getAbsorptionAmount() < 20.0f) {
                        par1EntityPlayer.setAbsorptionAmount((par1EntityPlayer.getAbsorptionAmount() + 1));
                    }
                } else if (this.vitaminize) {
                    switch (this.rand.nextInt(3)) {
                        case 0:
                            par1EntityPlayer.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 1));
                            break;
                        case 1:
                            par1EntityPlayer.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 1));
                            break;
                        case 2:
                            par1EntityPlayer.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 1));
                    }

                } else {
                    par1EntityPlayer.heal(0.5f);
                }
                par1EntityPlayer.xpCooldown = 2;
                this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                this.setDead();
            }
            // }
        }

    }

    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if (this.fulfillment) {
            buffer.writeChar('f');
        } else if (this.vitaminize) {
            buffer.writeChar('v');
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        try {
            char c = additionalData.readChar();
            if (c == 'f') {
                this.fulfillment = true;
            } else if (c == 'v') {
                this.vitaminize = true;
            }
        } catch (Exception e) {
        }
    }
}