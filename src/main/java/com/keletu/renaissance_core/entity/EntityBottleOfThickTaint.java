package com.keletu.renaissance_core.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;

import java.util.Iterator;
import java.util.List;

public class EntityBottleOfThickTaint extends EntityThrowable {
    public EntityBottleOfThickTaint(World world) {
        super(world);
    }

    public EntityBottleOfThickTaint(World world, EntityLivingBase entity) {
        super(world, entity);
    }

    public EntityBottleOfThickTaint(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    protected float getGravityVelocity() {
        return 0.05F;
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int a = 0; a < 100; ++a) {
                FXDispatcher.INSTANCE.taintsplosionFX(this);
            }

            FXDispatcher.INSTANCE.bottleTaintBreak(this.posX, this.posY, this.posZ);
        }

    }

    @Override
    protected void onImpact(RayTraceResult ray) {
        if (!this.world.isRemote) {
            List ents = this.world.getEntitiesWithinAABB(EntityLivingBase.class, (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ)).grow(5.0, 5.0, 5.0));
            if (ents.size() > 0) {
                Iterator var3 = ents.iterator();

                while (var3.hasNext()) {
                    Object ent = var3.next();
                    EntityLivingBase el = (EntityLivingBase) ent;
                    if (!(el instanceof ITaintedMob) && !el.isEntityUndead()) {
                        el.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, 100, 0, false, true));
                    }
                }
            }

            for (int a = 0; a < 10; ++a) {
                int xx = (int) ((this.rand.nextFloat() - this.rand.nextFloat()) * 5.0F);
                int zz = (int) ((this.rand.nextFloat() - this.rand.nextFloat()) * 5.0F);
                BlockPos p = this.getPosition().add(xx, 0, zz);
                if (this.world.rand.nextBoolean()) {
                    //Utils.setBiomeAt(this.world, p, TABiomes.TAINTED_LANDS);
                    if (this.world.isBlockNormalCube(p.down(), false) && this.world.getBlockState(p).getBlock().isReplaceable(this.world, p)) {
                        this.world.setBlockState(p, BlocksTC.taintFibre.getDefaultState());
                    }
                    if (this.world.isAirBlock(p.up())) {
                        ThaumcraftApi.internalMethods.addFlux(world, p.up(), 10.0F, true);
                    }
                }
            }

            this.world.setEntityState(this, (byte) 3);
            this.setDead();
        }

    }
}