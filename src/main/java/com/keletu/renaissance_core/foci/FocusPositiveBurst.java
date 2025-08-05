package com.keletu.renaissance_core.foci;

import com.keletu.renaissance_core.entity.EntityPositiveBurstOrb;
import fr.wind_blade.isorropia.common.IsorropiaAPI;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.SoundsTC;

public class FocusPositiveBurst extends FocusEffect {

    public String getResearch() {
        return "!FocusPositiveBurst";
    }

    public String getKey() {
        return "renaissance_core.POSITIVEBURST";
    }

    public Aspect getAspect() {
        return IsorropiaAPI.LUST;
    }

    public int getComplexity() {
        return this.getSettingValue("count") * 3 + (this.getSettingValue("function") == 0 ? 0 : 15);
    }

    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
        int amount = this.getSettingValue("count");
        World world = this.getPackage().world;

        for (int i = 0; i < amount; i++) {
            double offsetX = (world.rand.nextDouble() - 0.5);
            double offsetY = (world.rand.nextDouble() - 0.5);
            double offsetZ = (world.rand.nextDouble() - 0.5);

            EntityPositiveBurstOrb orb = new EntityPositiveBurstOrb(world, target.hitVec.x + offsetX, target.hitVec.y + offsetY, target.hitVec.z + offsetZ, this);

            double velocityX = (world.rand.nextDouble() - 0.5) * 0.2;
            double velocityY = (world.rand.nextDouble() - 0.5) * 0.2;
            double velocityZ = (world.rand.nextDouble() - 0.5) * 0.2;

            orb.motionX = velocityX;
            orb.motionY = velocityY;
            orb.motionZ = velocityZ;

            world.spawnEntity(orb);
        }
        return true;
    }

    public NodeSetting[] createSettings() {
        int[] function = new int[]{0, 1, 2};
        String[] functionkDesc = new String[]{"rcfocus.positive_burst.common", "rcfocus.positive_burst.vitaminize", "rcfocus.positive_burst.fulfillment"};
        return new NodeSetting[]{new NodeSetting("count", "rcfocus.positive_burst.count", new NodeSetting.NodeSettingIntRange(5, 10)), new NodeSetting("function", "rcfocus.positive_burst.function", new NodeSetting.NodeSettingIntList(function, functionkDesc))};
    }

    public void onCast(Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundsTC.ice, SoundCategory.PLAYERS, 0.25F, 1.0F + (float) (caster.world.rand.nextGaussian() * 0.05));
    }

    @SideOnly(Side.CLIENT)
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, x, y, z, vx + world.rand.nextGaussian() * 0.01, vy + world.rand.nextGaussian() * 0.01, vz + world.rand.nextGaussian() * 0.01);
        fb.setMaxAge((int) (10.0F + 10.0F * world.rand.nextFloat()));
        fb.setRBGColorF(1.0F, 1.0F, 1.0F);
        fb.setAlphaF(0.0F, 0.7F, 0.7F, 0.0F);
        fb.setGridSize(64);
        fb.setParticles(0, 1, 1);
        fb.setScale(world.rand.nextFloat() * 2.0F, world.rand.nextFloat());
        fb.setSlowDown(0.8);
        fb.setGravity((float) (world.rand.nextGaussian() * 0.10000000149011612));
        fb.setRandomMovementScale(0.0125F, 0.0125F, 0.0125F);
        fb.setRotationSpeed((float) world.rand.nextGaussian());
        ParticleEngine.addEffectWithDelay(world, fb, world.rand.nextInt(4));
    }
}