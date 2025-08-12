package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.TileHexOfPredictability;
import com.keletu.renaissance_core.packet.PacketFXLightning;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeedPrime;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import thaumcraft.common.items.casters.foci.FocusMediumCloud;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.RandomItemChooser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EntityHexRift extends EntityFluxRift implements IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> SEED = EntityDataManager.createKey(EntityHexRift.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(EntityHexRift.class, DataSerializers.VARINT);
    private static final DataParameter<Float> STABILITY = EntityDataManager.createKey(EntityHexRift.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> COLLAPSE = EntityDataManager.createKey(EntityHexRift.class, DataSerializers.BOOLEAN);
    int maxSize = 0;
    int lastSize = -1;
    static ArrayList<RandomItemChooser.Item> events;
    public int hexX, hexY, hexZ;
    public boolean chained;

    public EntityHexRift(World par1World) {
        super(par1World);
        hexX = hexY = hexZ = 0;
        chained = false;
    }

    protected void entityInit() {
        this.getDataManager().register(SEED, 0);
        this.getDataManager().register(SIZE, 5);
        this.getDataManager().register(STABILITY, 0.0F);
        this.getDataManager().register(COLLAPSE, false);
    }

    public int getRiftSeed() {
        return this.getDataManager().get(SEED);
    }

    public void setRiftSeed(int s) {
        this.getDataManager().set(SEED, s);
    }


    public boolean getCollapse() {
        return this.getDataManager().get(COLLAPSE);
    }

    public void setCollapse(boolean b) {
        if (b) {
            this.maxSize = this.getRiftSize();
        }

        this.getDataManager().set(COLLAPSE, b);
    }

    public float getRiftStability() {
        return this.getDataManager().get(STABILITY);
    }

    public void setRiftStability(float s) {
        if (s > 100.0F) {
            s = 100.0F;
        }

        if (s < -100.0F) {
            s = -100.0F;
        }

        this.getDataManager().set(STABILITY, s);
    }

    public int getRiftSize() {
        return this.getDataManager().get(SIZE);
    }

    public void setRiftSize(int s) {
        this.getDataManager().set(SIZE, s);
        this.setSize();
    }


    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Chained", chained);
        nbttagcompound.setIntArray("Hex", new int[]{hexX, hexY, hexZ});
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        chained = nbttagcompound.getBoolean("Chained");
        int[] hex = nbttagcompound.getIntArray("Hex");
        hexX = hex[0];
        hexY = hex[1];
        hexZ = hex[2];
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        //buffer.writeBoolean(this.interdimensional);
        buffer.writeBoolean(this.chained);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        try {
            //this.interdimensional = additionalData.readBoolean();
            this.chained = additionalData.readBoolean();
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpdate() {
        if (!points.isEmpty() && chained) {
            int pi = rand.nextInt(points.size() - 1);
            Vec3d copy = points.get(pi);
            Vec3d v1 = new Vec3d(copy.x, copy.y, copy.z).add(posX, posY + height / 2.0, posZ);
            TileEntity tile = world.getTileEntity(new BlockPos(hexX, hexY, hexZ));
            if (!world.isRemote && !(tile instanceof TileHexOfPredictability)) {
                setDead();
            }
            if (tile instanceof TileHexOfPredictability) {
                List<EntityItem> items = EntityUtils.getEntitiesInRange(this.world, v1.x, v1.y, v1.z, this, EntityItem.class, 1.4);
                if (!world.isRemote && !items.isEmpty()) {
                    for (EntityItem item : items) {
                        if (item.getItem().getItem().equals(ItemsTC.curio) && item.getItem().getItemDamage() == 6) {
                            if (item.getThrower() != null && ThaumcraftCapabilities.knowsResearch(world.getPlayerEntityByName(item.getThrower()), "CRIMSONPONTIFEX@0")) {
                                this.world.createExplosion(null, this.posX, this.posY, this.posZ, 3.0F, false);
                                EntityCrimsonPontifex pontifex = new EntityCrimsonPontifex(this.world);
                                pontifex.setPositionAndRotation(this.posX, this.posY + (double) (this.height / 2.0F), this.posZ, (float) (this.world.rand.nextInt(360) - 180), 0.0F);
                                pontifex.rotationYawHead = pontifex.rotationYaw;
                                pontifex.renderYawOffset = pontifex.rotationYaw;
                                pontifex.onInitialSpawn(this.world.getDifficultyForLocation(this.getPosition()), null);
                                this.world.spawnEntity(pontifex);
                            }
                            item.setDead();
                            this.setDead();
                        }
                    }

                    if (((TileHexOfPredictability) tile).heat > 0 && ((TileHexOfPredictability) tile).essentia.size() > 0) {
                        world.playSound(null, this.getPosition(), SoundsTC.jacobs, SoundCategory.NEUTRAL, 0.5F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
                        List<Entity> shrinked = items.stream().limit(6).collect(Collectors.toList());
                        for (Entity i : shrinked) {
                            Aspect[] aspects = ((TileHexOfPredictability) tile).essentia.getAspects();
                            int rgb = aspects[world.rand.nextInt(aspects.length)].getColor();
                            RenaissanceCore.packetInstance.sendToAllAround(new PacketFXLightning((float) v1.x, (float) v1.y, (float) v1.z, (float) i.posX, (float) i.posY, (float) i.posZ, rgb, (((TileHexOfPredictability) tile).heat / 2400F) * 2), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0));
                        }
                    }
                }
            }

        } else
            super.onUpdate();
    }

    public static void constructRift(World world, int x, int y, int z, int size) {
        Vec3d p2 = new Vec3d(x, y, z);
        EntityHexRift rift = new EntityHexRift(world);
        rift.setRiftSeed(world.rand.nextInt());
        rift.setLocationAndAngles(p2.x, p2.y, p2.z, (float) world.rand.nextInt(360), 0.0f);
        size = MathHelper.clamp(size, 5, 150);
        rift.chained = true;
        rift.hexX = x;
        rift.hexY = y - 2;
        rift.hexZ = z;
        if (world.spawnEntity(rift)) {
            rift.posX = p2.x + 0.5;
            rift.posY = p2.y;
            rift.posZ = p2.z + 0.5;
            rift.setRiftSize(5);
            rift.setRiftSize(size);
            rift.setCollapse(false);
        }
    }

    private void executeRiftEvent() {
        RandomItemChooser ric = new RandomItemChooser();
        FluxEventEntry ei = (FluxEventEntry) ric.chooseOnWeight(events);
        if (ei != null) {
            if (ei.nearTaintAllowed || !TaintHelper.isNearTaintSeed(this.world, this.getPosition())) {
                boolean didit = false;
                switch (ei.event) {
                    case 0:
                        EntityWisp wisp = new EntityWisp(this.world);
                        wisp.setLocationAndAngles(this.posX + this.rand.nextGaussian() * 5.0, this.posY + this.rand.nextGaussian() * 5.0, this.posZ + this.rand.nextGaussian() * 5.0, 0.0F, 0.0F);
                        if (this.world.rand.nextInt(5) == 0) {
                            wisp.setType(Aspect.FLUX.getTag());
                        }

                        if (wisp.getCanSpawnHere() && this.world.spawnEntity(wisp)) {
                            didit = true;
                        }
                        break;
                    case 1:
                        EntityTaintSeedPrime seed = new EntityTaintSeedPrime(this.world);
                        seed.setLocationAndAngles((double) ((int) (this.posX + this.rand.nextGaussian() * 5.0)) + 0.5, (int) (this.posY + this.rand.nextGaussian() * 5.0), (double) ((int) (this.posZ + this.rand.nextGaussian() * 5.0)) + 0.5, (float) this.world.rand.nextInt(360), 0.0F);
                        if (seed.getCanSpawnHere() && this.world.spawnEntity(seed)) {
                            didit = true;
                            seed.boost = this.getRiftSize();
                            AuraHelper.polluteAura(this.getEntityWorld(), this.getPosition(), (float) (this.getRiftSize() / 2), true);
                            this.setDead();
                        }
                        break;
                    case 2:
                        List<EntityLivingBase> targets2 = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(16.0, 16.0, 16.0));
                        if (targets2 != null && targets2.size() > 0) {
                            Iterator var12 = targets2.iterator();

                            while (var12.hasNext()) {
                                EntityLivingBase target = (EntityLivingBase) var12.next();
                                didit = true;
                                if (target instanceof EntityPlayer) {
                                    ((EntityPlayer) target).sendStatusMessage(new TextComponentString("§5§o" + I18n.translateToLocal("tc.fluxevent.2")), true);
                                }

                                PotionEffect pe = new PotionEffect(PotionInfectiousVisExhaust.instance, 3000, 2);
                                pe.getCurativeItems().clear();

                                try {
                                    target.addPotionEffect(pe);
                                } catch (Exception var11) {
                                }
                            }
                        }
                        break;
                    case 3:
                        EntityPlayer target = this.world.getClosestPlayerToEntity(this, 16.0);
                        if (target != null) {
                            FocusPackage p = new FocusPackage(target);
                            FocusMediumRoot root = new FocusMediumRoot();
                            root.setupFromCasterToTarget(target, target, 0.5);
                            p.addNode(root);
                            FocusMediumCloud fp = new FocusMediumCloud();
                            fp.initialize();
                            fp.getSetting("radius").setValue(MathHelper.getInt(this.rand, 1, 3));
                            fp.getSetting("duration").setValue(MathHelper.getInt(this.rand, Math.min(this.getRiftSize() / 2, 30), Math.min(this.getRiftSize(), 120)));
                            p.addNode(fp);
                            p.addNode(new FocusEffectFlux());
                            FocusEngine.castFocusPackage(target, p, true);
                        }
                        break;
                    case 4:
                        this.setCollapse(true);
                }

                if (didit) {
                    this.setRiftStability(this.getRiftStability() + (float) ei.cost);
                }

            }
        }
    }

    private void calcSteps(ArrayList<Vec3d> pp, ArrayList<Float> ww, Random rr) {
        pp.clear();
        ww.clear();
        Vec3d right = (new Vec3d(rr.nextGaussian(), rr.nextGaussian(), rr.nextGaussian())).normalize();
        Vec3d left = right.scale(-1.0);
        Vec3d lr = new Vec3d(0.0, 0.0, 0.0);
        Vec3d ll = new Vec3d(0.0, 0.0, 0.0);
        int steps = MathHelper.ceil((float) this.getRiftSize() / 3.0F);
        float girth = (float) this.getRiftSize() / 300.0F;
        double angle = 0.33;
        float dec = girth / (float) steps;

        for (int a = 0; a < steps; ++a) {
            girth -= dec;
            right = right.rotatePitch((float) (rr.nextGaussian() * angle));
            right = right.rotateYaw((float) (rr.nextGaussian() * angle));
            lr = lr.add(right.scale(0.2));
            pp.add(new Vec3d(lr.x, lr.y, lr.z));
            ww.add(girth);
            left = left.rotatePitch((float) (rr.nextGaussian() * angle));
            left = left.rotateYaw((float) (rr.nextGaussian() * angle));
            ll = ll.add(left.scale(0.2));
            pp.add(0, new Vec3d(ll.x, ll.y, ll.z));
            ww.add(0, girth);
        }

        lr = lr.add(right.scale(0.1));
        pp.add(new Vec3d(lr.x, lr.y, lr.z));
        ww.add(0.0F);
        ll = ll.add(left.scale(0.1));
        pp.add(0, new Vec3d(ll.x, ll.y, ll.z));
        ww.add(0, 0.0F);
    }

    private void completeCollapse() {
        int qq = (int) Math.sqrt(this.maxSize);
        if (this.rand.nextInt(100) < qq) {
            this.entityDropItem(new ItemStack(ItemsTC.primordialPearl, 1, 4 + this.rand.nextInt(4)), 0.0F);
        }

        for (int a = 0; a < qq; ++a) {
            this.entityDropItem(new ItemStack(ItemsTC.voidSeed), 0.0F);
        }

        label64:
        {
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockBamf(this.posX, this.posY, this.posZ, 0, true, true, null), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 64.0));
            List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityLivingBase.class, 32.0);
            Iterator var3;
            EntityLivingBase p;
            int w;
            switch (this.getStability()) {
                case VERY_UNSTABLE:
                    var3 = list.iterator();

                    while (var3.hasNext()) {
                        p = (EntityLivingBase) var3.next();
                        w = (int) ((1.0 - p.getDistanceSq(this) / 32.0) * 120.0);
                        if (w > 0) {
                            p.addPotionEffect(new PotionEffect(PotionFluxTaint.instance, w * 20, 0));
                        }
                    }
                case UNSTABLE:
                    var3 = list.iterator();

                    while (var3.hasNext()) {
                        p = (EntityLivingBase) var3.next();
                        w = (int) ((1.0 - p.getDistanceSq(this) / 32.0) * 300.0);
                        if (w > 0) {
                            p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, w * 20, 0));
                        }
                    }
                case STABLE:
                    break;
                default:
                    break label64;
            }

            var3 = list.iterator();

            while (var3.hasNext()) {
                p = (EntityLivingBase) var3.next();
                if (p instanceof EntityPlayer) {
                    w = (int) ((1.0 - p.getDistanceSq(this) / 32.0) * 25.0);
                    if (w > 0) {
                        ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer) p, w, IPlayerWarp.EnumWarpType.NORMAL);
                        ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer) p, w, IPlayerWarp.EnumWarpType.TEMPORARY);
                    }
                }
            }
        }

        this.setDead();
    }

    static {
        events = new ArrayList();
        events.add(new FluxEventEntry(0, 50, 5, true));
        events.add(new FluxEventEntry(1, 10, 0, false));
        events.add(new FluxEventEntry(2, 20, 10, true));
        events.add(new FluxEventEntry(3, 20, 10, true));
        events.add(new FluxEventEntry(4, 1, 0, true));
    }

    static class FluxEventEntry implements RandomItemChooser.Item {
        int weight;
        int event;
        int cost;
        boolean nearTaintAllowed;

        protected FluxEventEntry(int event, int weight, int cost, boolean nearTaintAllowed) {
            this.weight = weight;
            this.event = event;
            this.cost = cost;
            this.nearTaintAllowed = nearTaintAllowed;
        }

        public double getWeight() {
            return this.weight;
        }
    }
}
