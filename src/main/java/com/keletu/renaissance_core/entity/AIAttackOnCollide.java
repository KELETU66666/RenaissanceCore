package com.keletu.renaissance_core.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AIAttackOnCollide extends EntityAIBase
{
    World worldObj;
    EntityCreature attacker;
    int attackTick;
    double speedTowardsTarget;
    boolean longMemory;
    Path entityPathEntity;
    Class classTarget;

    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private static final String __OBFID = "CL_00001595";
    private int failedPathFindingPenalty;
    
    public AIAttackOnCollide(final EntityCreature p_i1635_1_, final Class p_i1635_2_, final double p_i1635_3_, final boolean p_i1635_5_) {
        this(p_i1635_1_, p_i1635_3_, p_i1635_5_);
        this.classTarget = p_i1635_2_;
    }
    
    public AIAttackOnCollide(final EntityCreature p_i1636_1_, final double p_i1636_2_, final boolean p_i1636_4_) {
        this.attacker = p_i1636_1_;
        this.worldObj = p_i1636_1_.world;
        this.speedTowardsTarget = p_i1636_2_;
        this.longMemory = p_i1636_4_;
        this.setMutexBits(3);
    }
    
    public boolean shouldExecute() {
        final EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isEntityAlive()) {
            return false;
        }
        if (this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass())) {
            return false;
        }
        if (--this.delayCounter <= 0) {
            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
            return this.entityPathEntity != null;
        }
        return true;
    }
    
    public boolean shouldContinueExecuting() {
        final EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        return entitylivingbase != null && entitylivingbase.isEntityAlive() && (this.longMemory ? this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)) : (!this.attacker.getNavigator().noPath()));
    }
    
    public void startExecuting() {
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
        this.delayCounter = 0;
    }
    
    public void resetTask() {
        this.attacker.getNavigator().clearPath();
    }
    
    public void updateTask() {
        final EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0f, 30.0f);
        final double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
        double d2 = this.attacker.width * 2.0f * this.attacker.width * 2.0f + entitylivingbase.width;
        --this.delayCounter;
        if (this.attackTick > 0) {
            --this.attackTick;
        }
        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0 && ((this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0) || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.attacker.getRNG().nextFloat() < 0.05f)) {
            this.targetX = entitylivingbase.posX;
            this.targetY = entitylivingbase.getEntityBoundingBox().minY;
            this.targetZ = entitylivingbase.posZ;
            this.delayCounter = this.failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);
            if (this.attacker.getNavigator().getPath() != null) {
                final PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
                if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1.0) {
                    this.failedPathFindingPenalty = 0;
                }
                else {
                    this.failedPathFindingPenalty += 10;
                }
            }
            else {
                this.failedPathFindingPenalty += 10;
            }
            if (d0 > 1024.0) {
                this.delayCounter += 10;
            }
            else if (d0 > 256.0) {
                this.delayCounter += 5;
            }
            if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget)) {
                this.delayCounter += 15;
            }
        }
        if (d0 <= d2 && this.attackTick <= 0) {
            this.attackTick = 10;
            if (!this.attacker.getHeldItemMainhand().isEmpty()) {
                this.attacker.swingArm(EnumHand.MAIN_HAND);
            }
            this.attacker.attackEntityAsMob(entitylivingbase);
        }
    }
}
