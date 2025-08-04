package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.Utils;

public class EntityCompressedBlast extends EntityThrowable implements IEntityAdditionalSpawnData {
    private int fireworkAge;
    /**
     * The lifetime of the firework in ticks. When the age reaches the lifetime the firework explodes.
     */
    public int lifetime;
    FocusPackage focusPackage;

    public EntityCompressedBlast(World p_i1762_1_) {
        super(p_i1762_1_);
        this.setSize(0.25F, 0.25F);
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        return p_70112_1_ < 4096.0D;
    }

    public EntityCompressedBlast(World p_i1763_1_, FocusPackage pack, Trajectory trajectory) {
        super(p_i1763_1_);
        this.fireworkAge = 0;
        this.setSize(0.25F, 0.25F);
        focusPackage = pack;
        this.setPosition(trajectory.source.x + trajectory.direction.x * pack.getCaster().width * 2.1, trajectory.source.y + trajectory.direction.y * pack.getCaster().width * 2.1, trajectory.source.z + trajectory.direction.z * pack.getCaster().width * 2.1);
        this.shoot(trajectory.direction.x, trajectory.direction.y, trajectory.direction.z, 2.1F, 0.0f);
        int i = 1;

        /*
        this.motionX = this.rand.nextGaussian() * 0.001D;
        this.motionZ = this.rand.nextGaussian() * 0.001D;
        this.motionY = this.rand.nextGaussian() * 0.001D;;
         */
        this.lifetime = 5 * i + this.rand.nextInt(20);
        ignoreEntity = pack.getCaster();
        thrower = pack.getCaster();
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @SideOnly(Side.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        this.motionX = p_70016_1_;
        this.motionY = p_70016_3_;
        this.motionZ = p_70016_5_;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(p_70016_1_, p_70016_5_) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(p_70016_3_, f) * 180.0D / Math.PI);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();
        /*this.motionX *= 0.5D;
        this.motionZ *= 0.5D;
        this.motionY *= 0.5D;

         */
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float) (Math.atan2(this.motionY, f) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

        /*if (this.fireworkAge == 0) {
            this.world.playSoundAtEntity(this, "fireworks.launch", 3.0F, 1.0F);
        }
         */

        ++this.fireworkAge;

        if (this.world.isRemote && this.fireworkAge % 2 < 2) {
            this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, this.rand.nextGaussian() * 0.05D, -this.motionY * 0.5D, this.rand.nextGaussian() * 0.05D);
        }

        if (this.world.isRemote && this.fireworkAge % 5 == 0) {
            RenaissanceCore.proxy.smeltFX(this.posX, this.posY, this.posZ, this.world, 1);
        }

        if (this.fireworkAge % 5 == 0) {
            this.world.playSound(null, this.getPosition(), new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":thump")), SoundCategory.PLAYERS, 0.9F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
        }

        if (!this.world.isRemote && this.fireworkAge > this.lifetime) {
            this.world.setEntityState(this, (byte) 17);
            this.setDead();
        }
    }

    @Override
    protected void onImpact(RayTraceResult mop) {
        if (mop != null) {
            if (!world.isRemote) {
                if (mop.entityHit != null) {
                    mop.hitVec = getPositionVector();
                }
                Vec3d pv = new Vec3d(prevPosX, prevPosY, prevPosZ);
                Vec3d vf = new Vec3d(motionX, motionY, motionZ);
                ServerEvents.addRunnableServer(getEntityWorld(), new Runnable() {
                    @Override
                    public void run() {
                        FocusEngine.runFocusPackage(focusPackage, new Trajectory[]{new Trajectory(pv, vf.normalize())}, new RayTraceResult[]{mop});
                    }
                }, 0);
                setDead();
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Life", this.fireworkAge);
        nbt.setInteger("LifeTime", this.lifetime);
        nbt.setTag("pack", focusPackage.serialize());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.fireworkAge = nbt.getInteger("Life");
        this.lifetime = nbt.getInteger("LifeTime");
        try {
            (focusPackage = new FocusPackage()).deserialize(nbt.getCompoundTag("pack"));
        } catch (Exception ignore) {
        }
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness() {
        return super.getBrightness();
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return super.getBrightnessForRender();
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        Utils.writeNBTTagCompoundToBuffer(data, focusPackage.serialize());
    }

    public void readSpawnData(ByteBuf data) {
        try {
            (focusPackage = new FocusPackage()).deserialize(Utils.readNBTTagCompoundFromBuffer(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote) {
            if(this.ticksExisted % 2 == 0){
                this.world.spawnParticle("fireworksSpark", this.posX, this.posY - 0.3D, this.posZ, this.rand.nextGaussian() * 0.05D, -this.motionY * 0.5D, this.rand.nextGaussian() * 0.05D);
            }
            if (this.ticksExisted % 15 == 0){
                ThaumicConcilium.proxy.smeltFX(this.posX, this.posY, this.posZ, this.world, 5);
            }
            if(this.ticksExisted >= 100){
                this.setDead();
            }
        } else {
            if (this.ticksExisted % 15 == 0) {
                this.world.playSoundAtEntity(this,  ThaumicConcilium.MODID+":thump", 0.9F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
            }
        }

    }

    protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
        if (!this.world.isRemote) {
            if (firework == null) {
                boolean var2 = this.world.getGameRules().getGameRuleBooleanValue("mobGriefing");
                this.world.createExplosion((Entity) null, this.posX, this.posY, this.posZ, 2.0F, var2);
            }
            this.setDead();
        }
        if(this.world.isRemote) {
            if (firework != null) {
                if (firework.hasTagCompound()) {
                    NBTTagCompound nbttagcompound = null;
                    nbttagcompound = firework.getTagCompound().getCompoundTag("Fireworks");
                    this.world.makeFireworks(this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, nbttagcompound);
                }
            }
        }

    }

    public float getShadowSize() {
        return 0.1F;
    }
     */
}