package com.keletu.renaissance_core.entity;

import com.google.common.base.Optional;
import com.keletu.renaissance_core.RenaissanceCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.lib.potions.PotionWarpWard;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EntityConcentratedWarpCharge extends Entity implements IEntityOwnable, IEntityAdditionalSpawnData {
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityConcentratedWarpCharge.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public int power;
    public int range;
    public boolean massHysteria = false;
    public boolean selfFlagellation = false;
    public boolean byForce = false;

    public EntityConcentratedWarpCharge(World p_i1762_1_) {
        super(p_i1762_1_);

        this.setSize(1.0f, 1.0f);
    }

    protected void entityInit() {
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
    }


    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        return p_70112_1_ < 4096.0D;
    }

    public EntityConcentratedWarpCharge(double p_i1763_2_, double p_i1763_4_, double p_i1763_6_, EntityPlayer p) {
        super(p.world);
        this.setPosition(p_i1763_2_, p_i1763_4_, p_i1763_6_);
        this.power = 5;
        this.range = 1;
        this.byForce = true;
    }
    public void onUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        this.motionX *= 0.7;
        this.motionY *= 0.7;
        this.motionZ *= 0.7;
        float maxDist = byForce ? 9.0F : 49.0F;
        super.onUpdate();
        if (!byForce && (ticksExisted % 120 == 0 || ticksExisted == 0)) {
            world.playSound(null, this.getPosition(), new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":whispers")), SoundCategory.HOSTILE, 0.1F, 1.0F);
        }
        if (!this.world.isRemote) {
            if (getOwner() == null) {
                this.setDead();
            }
            if (byForce && ticksExisted > 200) {
                this.setDead();
            }
            if (getOwnerEntity() != null) {
                EntityPlayer owner = (EntityPlayer) getOwnerEntity();
                if (owner.isDead || this.dimension != owner.dimension) {
                    this.setDead();
                }
                if (this.getDistanceSq(owner) > 225.0f) {
                    this.setPosition(owner.posX, owner.posY, owner.posZ);
                }
                if (this.getDistanceSq(owner) > maxDist) {
                    EntityThaumGib.setEntityMotionFromVector(this, new Vector3(owner.posX, owner.posY, owner.posZ), 1.0f);
                }
                if (!owner.capabilities.isFlying) {
                    this.motionY -= 0.1;
                }
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

                if (this.massHysteria && this.ticksExisted % 10 == 0) {
                    Collection<PotionEffect> potions = owner.getActivePotionEffects();
                    if (!potions.isEmpty()) {
                        for (PotionEffect potion : potions) {
                            Potion id = potion.getPotion();

                            if (id.isBadEffect()) {
                                int d = 5 + 4 * this.range;
                                List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().expand(d, d, d).expand(-d, -d, -d));
                                entities.remove(owner);
                                Collections.shuffle(entities);
                                if (!entities.isEmpty()) {
                                    entities.get(0).addPotionEffect(potion);
                                    owner.removePotionEffect(id);
                                    break;
                                }
                            }
                        }
                    }
                }

                if (this.selfFlagellation && this.ticksExisted % 10 == 0) {
                    int d = 5 + 4 * this.range;
                    List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().expand(d, d, d).expand(-d, -d, -d));
                    entities.remove(owner);
                    if (!entities.isEmpty()) {
                        for (EntityPlayer p : entities) {
                            Collection<PotionEffect> potions = owner.getActivePotionEffects();
                            if (!potions.isEmpty()) {
                                for (PotionEffect potion : potions) {
                                    Potion id = potion.getPotion();

                                    if (id.isBadEffect()) {
                                        owner.addPotionEffect(potion);
                                        p.removePotionEffect(id);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                int warp = ThaumcraftCapabilities.getWarp(owner).get(IPlayerWarp.EnumWarpType.PERMANENT);
                int twarp = ThaumcraftCapabilities.getWarp(owner).get(IPlayerWarp.EnumWarpType.TEMPORARY);
                if (twarp == 0 && !byForce) {
                    //((EntityPlayer) owner).sendMessage(new TextComponentTranslation(I18n.translateToLocal("TC.no_warp")).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_PURPLE)));
                    this.setDead();
                    return;
                }
                if (!owner.isPotionActive(PotionWarpWard.instance)) {
                    List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox());
                    if (!byForce) {
                        entities.remove(owner);
                    }
                    for (EntityLivingBase entity : entities) {
                        if (entity instanceof EntityCrimsonPontifex) continue;
                        entity.attackEntityFrom(
                                EntityDamageSourceIndirect.causeIndirectMagicDamage(this, owner), warp / 10.0F * power);
                        if (!byForce) {
                            ThaumcraftApi.internalMethods.addWarpToPlayer(owner, -1, IPlayerWarp.EnumWarpType.TEMPORARY);
                        }
                    }
                }
            }
        }
        if (this.world.isRemote) {
            if (getOwner() != null) {
                if (this.ticksExisted % 3 == 0) {
                    RenaissanceCore.proxy.warpchain((EntityPlayer) getOwner(), this.posX, this.posY + 0.5, this.posZ);
                }
                if (this.massHysteria && this.world.rand.nextInt() % 8 == 0) {
                    for (int i = 0; i < 10; i++) {
                        RenaissanceCore.proxy.taintsplosion(this.world, this.posX, this.posY, this.posZ);
                    }
                }
            }
        }
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        if (this.getOwnerId() == null) {
            nbttagcompound.setUniqueId("Owner", UUID.fromString(""));
        } else {
            nbttagcompound.setUniqueId("Owner", this.getOwnerId());
        }
        nbttagcompound.setInteger("power", this.power);
        nbttagcompound.setInteger("range", this.range);
        nbttagcompound.setBoolean("massHysteria", this.massHysteria);
        nbttagcompound.setBoolean("selfFlagellation", this.selfFlagellation);
        nbttagcompound.setBoolean("byForce", this.byForce);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        UUID s = nbttagcompound.getUniqueId("Owner");
        if (s != null) {
            this.setOwner(s);
        }
        this.power = nbttagcompound.getInteger("power");
        this.range = nbttagcompound.getInteger("range");
        this.massHysteria = nbttagcompound.getBoolean("massHysteria");
        this.selfFlagellation = nbttagcompound.getBoolean("selfFlagellation");
        this.byForce = nbttagcompound.getBoolean("byForce");
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    public float getBrightness() {
        return super.getBrightness();
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return super.getBrightnessForRender();
    }

    public boolean canBeAttackedWithItem() {
        return false;
    }
    @Override
    public UUID getOwnerId()
    {
        return (this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
    }

    public void setOwner(@Nullable UUID p_184754_1_)
    {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
    }

    @Override
    public Entity getOwner() {
        return this.getOwnerEntity();
    }

    public EntityLivingBase getOwnerEntity() {
        return this.world.getPlayerEntityByUUID(this.getOwnerId());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if (this.massHysteria) {
            buffer.writeInt(1);
        } else if (this.selfFlagellation) {
            buffer.writeInt(2);
        } else if (this.byForce) {
            buffer.writeInt(3);
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        try {
            int val = additionalData.readInt();
            if (val == 1) this.massHysteria = true;
            if (val == 2) this.selfFlagellation = true;
            if (val == 3) this.byForce = true;
        } catch (Exception e) {
        }
    }

}