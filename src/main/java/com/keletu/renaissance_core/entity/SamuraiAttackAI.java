package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.packet.PacketFXLightning;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.SoundsTC;

public class SamuraiAttackAI extends EntityAIBase {
    World world;
    EntityCreature attacker;
    /**
     * An amount of decrementing ticks that allows the entity to attack once the tick reaches 0.
     */
    int attackTick;
    /**
     * The speed with which the mob will approach the target
     */
    double speedTowardsTarget;
    /**
     * When true, the mob will continue chasing its target, even if it can't find a path to them right now.
     */
    boolean longMemory;
    /**
     * The PathEntity of our entity.
     */
    Path entityPathEntity;
    Class classTarget;
    private int delayCounter;
    private double field_151497_i;
    private double field_151495_j;
    private double field_151496_k;

    private int failedPathFindingPenalty;

    public SamuraiAttackAI(EntityCreature p_i1635_1_, Class p_i1635_2_, double p_i1635_3_, boolean p_i1635_5_) {
        this(p_i1635_1_, p_i1635_3_, p_i1635_5_);
        this.classTarget = p_i1635_2_;
    }

    public SamuraiAttackAI(EntityCreature p_i1636_1_, double p_i1636_2_, boolean p_i1636_4_) {
        this.attacker = p_i1636_1_;
        this.world = p_i1636_1_.world;
        this.speedTowardsTarget = p_i1636_2_;
        this.longMemory = p_i1636_4_;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.isEntityAlive()) {
            return false;
        } else if (this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass())) {
            return false;
        } else {
            if (--this.delayCounter <= 0) {
                this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
                this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
                if (this.entityPathEntity != null) {
                    ((Samurai) this.attacker).setAnger(0);
                }
                return false;
            } else {
                ((Samurai) this.attacker).setAnger(0);
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        return entitylivingbase != null && (entitylivingbase.isEntityAlive() && (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(MathHelper.floor(entitylivingbase.posX), MathHelper.floor(entitylivingbase.posY), MathHelper.floor(entitylivingbase.posZ)))));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        if (((Samurai) this.attacker).getAnger() <= 200 && this.attacker.getHealth() > this.attacker.getMaxHealth() / 3) {
            ((Samurai) this.attacker).setAnger(MathHelper.clamp(200 + this.attacker.world.rand.nextInt(200), 0, 1000));
        }
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
        this.delayCounter = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        this.attacker.getNavigator().clearPath();
    }

    /**
     * Updates the task
     */
    public void updateTask() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
        double d1 = this.attacker.width * 2.0F * this.attacker.width * 2.0F + entitylivingbase.width;
        --this.delayCounter;

        if (this.attacker.getHealth() > this.attacker.getMaxHealth() / 2 && d0 > 64F && world.rand.nextInt(10) > 7) {
            this.attacker.addPotionEffect(new PotionEffect(MobEffects.SPEED, 10, 3));
        }
        if (this.attacker.getHealth() > this.attacker.getMaxHealth() / 3) {
            if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0 && (this.field_151497_i == 0.0D && this.field_151495_j == 0.0D && this.field_151496_k == 0.0D || entitylivingbase.getDistanceSq(this.field_151497_i, this.field_151495_j, this.field_151496_k) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F)) {
                this.field_151497_i = entitylivingbase.posX;
                this.field_151495_j = entitylivingbase.getEntityBoundingBox().minY;
                this.field_151496_k = entitylivingbase.posZ;
                this.delayCounter = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

                if (this.attacker.getNavigator().getPath() != null) {
                    PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1) {
                        failedPathFindingPenalty = 0;
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                } else {
                    failedPathFindingPenalty += 10;
                }

                if (d0 > 1024.0D) {
                    this.delayCounter += 10;
                } else if (d0 > 256.0D) {
                    this.delayCounter += 5;
                }

                if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget)) {
                    this.delayCounter += 15;
                }
            }

            this.attackTick = Math.max(this.attackTick - 1, 0);

            if (d0 <= d1 && this.attackTick <= 20) {
                this.attackTick = 20;
                this.attacker.swingArm(EnumHand.MAIN_HAND);


                this.attacker.attackEntityAsMob(entitylivingbase);
            }
        } else {
            Vec3d vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.attacker, 16, 7, new Vec3d(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ));

            if (vec3 != null && (entitylivingbase.getDistanceSq(vec3.x, vec3.y, vec3.z) > entitylivingbase.getDistanceSq(this.attacker)))
            {
                this.entityPathEntity = this.attacker.getNavigator().getPathToXYZ(vec3.x, vec3.y, vec3.z);
                this.attacker.getNavigator().tryMoveToXYZ(vec3.x, vec3.y, vec3.z, this.speedTowardsTarget);
                if (this.attacker.ticksExisted % 20 == 0 && d0 < 256F) {
                    if (!world.isRemote) {
                        if (this.attacker.getEntitySenses().canSee(entitylivingbase)) {
                            this.attacker.world.playSound(this.attacker.posX, this.attacker.posY, this.attacker.posZ, SoundsTC.shock, SoundCategory.HOSTILE, 0.25F, 1.0F, true);
                            entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(this.attacker), 2 * (((Samurai) this.attacker).getType() + 1));
                            RenaissanceCore.packetInstance.sendToAllAround(new PacketFXLightning((float) this.attacker.posX, (float) (this.attacker.posY + this.attacker.height - 0.5), (float) this.attacker.posZ, (float) entitylivingbase.posX, (float) (entitylivingbase.posY + entitylivingbase.height / 2), (float) entitylivingbase.posZ, 0x6666DD, 0.02F), new NetworkRegistry.TargetPoint(world.provider.getDimension(), this.attacker.posX, this.attacker.posY, this.attacker.posZ, 32.0));
                        }
                    }
                }
            }
        }
    }
}