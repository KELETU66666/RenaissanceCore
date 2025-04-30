package com.keletu.renaissance_core.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.SoundsTC;
import thecodex6824.thaumicaugmentation.common.network.PacketParticleEffect;
import thecodex6824.thaumicaugmentation.common.network.TANetwork;

import java.util.Iterator;
import java.util.List;

public class EntityCrimsonAnnales extends EntityItem {
    protected boolean ignoreDamage;

    public EntityCrimsonAnnales(World world) {
        super(world);
    }

    public EntityCrimsonAnnales(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityCrimsonAnnales(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    protected void breakAndDoBadThings() {
        List<EntityFluxRift> rifts = this.world.getEntitiesWithinAABB(EntityFluxRift.class, (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ)).grow(32.0));
        if (!rifts.isEmpty()) {
            Iterator<EntityFluxRift> var4 = rifts.iterator();

            while (var4.hasNext()) {
                EntityFluxRift rift = var4.next();
                AuraHelper.polluteAura(this.world, rift.getPosition(), (float) Math.sqrt(rift.getRiftSize()), true);
                rift.setDead();
            }

            EntityCrimsonPontifex pontifex = new EntityCrimsonPontifex(this.world);
            pontifex.setPositionAndRotation(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, (float) (this.world.rand.nextInt(360) - 180), 0.0F);
            pontifex.rotationYawHead = pontifex.rotationYaw;
            pontifex.renderYawOffset = pontifex.rotationYaw;
            pontifex.onInitialSpawn(this.world.getDifficultyForLocation(pontifex.getPosition()), null);
            if (this.world.spawnEntity(pontifex)) {
                pontifex.playSound(SoundsTC.craftstart, 0.75F, 1.0F);
                pontifex.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 0.75F);
                TANetwork.INSTANCE.sendToAllTracking(new PacketParticleEffect(PacketParticleEffect.ParticleEffect.EXPLOSION, pontifex.posX, pontifex.posY, pontifex.posZ), pontifex);
            }
        }

    }
    /**
     * this.getThrower() != null can't remove !!!
     * nor game will crash when use dispenser throw the item !!!
     * */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        super.attackEntityFrom(source, amount);
        if (this.getThrower() != null && ThaumcraftCapabilities.knowsResearch(world.getPlayerEntityByName(this.getThrower()), "CRIMSONPONTIFEX@0"))
            if (this.isDead && !this.ignoreDamage) {
                this.ignoreDamage = true;
                this.breakAndDoBadThings();
            }

        return false;
    }

    /**
     * this.getThrower() != null can't remove !!!
     * nor game will crash when use dispenser throw the item !!!
     * */
    @Override
    public void setDead() {
        if (this.getThrower() != null && ThaumcraftCapabilities.knowsResearch(world.getPlayerEntityByName(this.getThrower()), "CRIMSONPONTIFEX@0"))
            if (!this.isDead) {
                StackTraceElement[] trace = (new Throwable()).getStackTrace();
                if (trace.length >= 2 && trace[1].getClassName().equals("thaumcraft.common.entities.EntityFluxRift")) {
                    Iterator var3 = this.world.getEntitiesWithinAABB(EntityFluxRift.class, this.getEntityBoundingBox().grow(1.0)).iterator();

                    while (var3.hasNext()) {
                        EntityFluxRift rift = (EntityFluxRift) var3.next();
                        rift.setDead();
                    }

                    this.ignoreDamage = true;
                    this.world.createExplosion(null, this.posX, this.posY, this.posZ, 3.0F, false);
                    EntityCrimsonPontifex pontifex = new EntityCrimsonPontifex(this.world);
                    pontifex.setPositionAndRotation(this.posX, this.posY + (double) (this.height / 2.0F), this.posZ, (float) (this.world.rand.nextInt(360) - 180), 0.0F);
                    pontifex.rotationYawHead = pontifex.rotationYaw;
                    pontifex.renderYawOffset = pontifex.rotationYaw;
                    pontifex.onInitialSpawn(this.world.getDifficultyForLocation(this.getPosition()), null);
                    this.world.spawnEntity(pontifex);
                }
            }

        super.setDead();
    }
}
