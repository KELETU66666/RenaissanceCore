package com.keletu.renaissance_core.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;
import thaumcraft.common.lib.utils.BlockUtils;

import java.util.List;

public class EntityGolemBydlo extends EntityGolem {
    static final ItemStack pick = new ItemStack(ItemsTC.primalCrusher);
    public boolean inactive;
    public int action;

    public int leftArm;
    public int rightArm;

    public EntityGolemBydlo(World p_i1686_1_) {
        super(p_i1686_1_);
        this.setSize(2.0F, 3.5F);
        this.inactive = false;
        this.action = 0;
        this.leftArm = 0;
        this.rightArm = 0;
        this.tasks.taskEntries.clear();
    }

    protected void entityInit() {
        super.entityInit();

    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(90.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) (10 + this.rand.nextInt(15)));

        if (flag) {
            entity.motionY += 0.4000000059604645D;
        }

        this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.action > 0) {
            --this.action;
        }

        if (this.leftArm > 0) {
            --this.leftArm;
        }

        if (this.rightArm > 0) {
            --this.rightArm;
        }

        int y1 = MathHelper.floor(this.posY);
        this.inactive = y1 < 0 || this.isBeingRidden();
        if (!this.world.isRemote) {

            if (this.getDistanceSq(this.getHomePosition().getX(), this.getHomePosition().getY(), this.getHomePosition().getZ()) >= 2304.0 || this.isEntityInsideOpaqueBlock()) {
                int var1 = MathHelper.floor((double) this.getHomePosition().getX());
                int var2 = MathHelper.floor((double) this.getHomePosition().getZ());
                int var3 = MathHelper.floor((double) this.getHomePosition().getY());

                for (int var0 = 1; var0 >= -1; --var0) {
                    for (int var4 = -1; var4 <= 1; ++var4) {
                        for (int var5 = -1; var5 <= 1; ++var5) {
                            if (world.getBlockState(new BlockPos(var1 + var4, var3 - 1 + var0, var2 + var5)).isTopSolid() && !this.world.isBlockNormalCube(new BlockPos(var1 + var4, var3 + var0, var2 + var5), false)) {
                                this.setLocationAndAngles((float) (var1 + var4) + 0.5F, (double) var3 + (double) var0, (float) (var2 + var5) + 0.5F, this.rotationYaw, this.rotationPitch);
                                this.getNavigator().clearPath();
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (this.getControllingPassenger() != null) {
            if (this.getControllingPassenger() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) this.getControllingPassenger();
                if (player.isSwingInProgress && this.action == 0) {
                    if (player.getHeldItemMainhand().getItem() == ItemsTC.golemBell) {
                        int fortune = 0;
                        int silktouch = 0;
                        float strength = 10;
                        RayTraceResult mop = BlockUtils.getTargetBlock(player.world, player, false, false, 5.0);
                        if (mop != null) {
                            float dur = this.world.getBlockState(mop.getBlockPos()).getBlockHardness(this.world, mop.getBlockPos()) * 100.0f;
                            dur = (float) Math.sqrt(dur);
                            this.action = 6;
                            if (!this.world.isRemote) {
                                this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
                                EnumFacing side = mop.sideHit;
                                if (ForgeHooks.isToolEffective(this.world, mop.getBlockPos(), pick)) {
                                    for (int aa = -2; aa <= 2; ++aa) {
                                        for (int bb = -2; bb <= 2; ++bb) {
                                            int xx = 0;
                                            int yy = 0;
                                            int zz = 0;
                                            if (side.getIndex() <= 1) {
                                                xx = aa;
                                                zz = bb;
                                            } else if (side.getIndex() <= 3) {
                                                xx = aa;
                                                yy = bb;
                                            } else {
                                                zz = aa;
                                                yy = bb;
                                            }

                                            if (player.world.canMineBlockBody(player, mop.getBlockPos().add(xx, yy, zz))) {
                                                IBlockState bs = player.world.getBlockState(mop.getBlockPos().add(xx, yy, zz));
                                                if (bs.getBlockHardness(this.world, mop.getBlockPos().add(xx, yy, zz)) >= 0.0F && (ForgeHooks.isToolEffective(world, mop.getBlockPos().add(xx, yy, zz), pick))) {
                                                    PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(mop.getBlockPos().getX() + 0.5, mop.getBlockPos().getY() + 0.5, mop.getBlockPos().getZ() + 0.5, new String[]{"thaumcraft.BREAK"}), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), mop.hitVec.x, mop.hitVec.y, mop.hitVec.z, 64.0));
                                                    ServerEvents.addBreaker(this.world, mop.getBlockPos().add(xx, yy, zz), this.world.getBlockState(mop.getBlockPos().add(xx, yy, zz)), player, true, silktouch > 0, fortune, strength, dur, dur, (int) (dur / strength / 3.0f), 0.25f + (silktouch > 0 ? 0.25f : 0.0f) + fortune * 0.1f, null);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (player.getHeldItemOffhand().getItem() == ItemsTC.golemBell) {
                        this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
                        this.action = 6;
                        List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(2.0, 1.0, 2.0).expand(-2.0, -1.0, -2.0));
                        list.remove(this);
                        list.remove(player);
                        for (EntityLivingBase e : list) {
                            this.attackEntityAsMob(e);
                        }
                    }
                }
            }
        }
    }

    public void travel(float strafe, float vertical, float forward) {
        if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityLivingBase) {
            this.prevRotationYaw = this.rotationYaw = this.getControllingPassenger().rotationYaw;
            this.rotationPitch = this.getControllingPassenger().rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            strafe = ((EntityLivingBase) this.getControllingPassenger()).moveStrafing * 0.5F;
            forward = ((EntityLivingBase) this.getControllingPassenger()).moveForward;


            this.stepHeight = 1.0F;
            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

            if (!this.world.isRemote) {
                this.setAIMoveSpeed((float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                super.travel(strafe, vertical, forward);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.posX - this.prevPosX;
            double d0 = this.posZ - this.prevPosZ;
            float f4 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        } else {
            this.stepHeight = 0.5F;
            this.jumpMovementFactor = 0.02F;
            super.travel(strafe, vertical, forward);
        }
    }

    @Override
    public void updateRidden() {
        super.updateRidden();
        float f = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
        float f1 = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
        float f2 = 0.5F;
        float f3 = 0.15F;

        this.getControllingPassenger().setPosition(this.posX + (double) (f2 * f), this.posY + this.getMountedYOffset() + this.getControllingPassenger().getYOffset() + (double) f3, this.posZ - (double) (f2 * f1));

        if (this.getControllingPassenger() instanceof EntityLivingBase) {
            ((EntityLivingBase) this.getControllingPassenger()).renderYawOffset = this.renderYawOffset;
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!player.isSneaking() && (player.getHeldItem(hand).isEmpty() || !(player.getHeldItem(hand).getItem() instanceof ItemNameTag))) {
            if (this.getControllingPassenger() == null) {
                if (!this.world.isRemote) {
                    player.startRiding(this);
                }
                return true;
            } else {
                return super.processInteract(player, hand);
            }
        } else {
            return false;
        }
    }


    @Override
    public boolean isAIDisabled() {
        return !this.inactive;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void despawnEntity() {
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) {
        return SoundEvents.ENTITY_IRONGOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRONGOLEM_DEATH;
    }


    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.0F, 1.0F);
    }

    public int getActionTimer() {
        // return 3 - Math.abs(this.action - 3);
        return this.action;
    }


}