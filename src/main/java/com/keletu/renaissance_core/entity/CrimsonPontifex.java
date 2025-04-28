package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.items.RCItems;
import com.keletu.renaissance_core.packet.PacketEnslave;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CrimsonPontifex extends EntityThaumcraftBoss implements IRangedAttackMob {

    private static final DataParameter<Byte> TITLE = EntityDataManager.createKey(CrimsonPontifex.class, DataSerializers.BYTE);
    static String[] titles = new String[]{"Ivius", "Ufarihm", "Ihith", "Pemonar", "Shagron", "Ugimaex", "Qroleus", "Oxon", "Rheforn", "Zubras"};
    private int attackCounter = 0;
    private int aggroCooldown = 0;
    private int cultists;
    private Entity targetedEntity = null;
    public boolean continuousAttack = false;


    public CrimsonPontifex(World p_i1738_1_) {
        super(p_i1738_1_);
        this.setSize(0.75F, 2.25F);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 0.7, false));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
        this.experienceValue = 40;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(25.0);
    }

    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(TITLE, (byte) 0);
    }

    protected static String generateName(Random rng) {
        return titles[rng.nextInt(titles.length)];
    }

    @Override
    public void generateName() {
        int mod = (int) getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
        if (mod >= 0) {
            setCustomNameTag(new TextComponentTranslation("text.entity.CrimsonPontifex.name",
                    ChampionModifier.mods[mod].getModNameLocalized(), generateName(rand)).getFormattedText());
        }
    }

    private String getTitle() {
        return this.titles[this.getDataManager().get(TITLE)];
    }

    private void setTitle(int title) {
        this.getDataManager().set(TITLE, (byte) title);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("title", getDataManager().get(TITLE));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setTitle(nbt.getByte("title"));

        bossInfo.setName(getDisplayName());
    }

    public boolean isOnSameTeam(Entity el) {
        return el instanceof EntityCultist || el instanceof EntityCultistLeader || el instanceof CrimsonPontifex;
    }

    public boolean canPickUpLoot() {
        return false;
    }

    public boolean canAttackClass(Class clazz) {
        return clazz != EntityCultistCleric.class && clazz != EntityCultistLeader.class && clazz != EntityCultistKnight.class && super.canAttackClass(clazz);
    }

    protected Item getDropItem() {
        return Item.getItemById(0);
    }

    protected void dropFewItems(boolean flag, int fortune) {
        EntityUtils.entityDropSpecialItem(this, new ItemStack(RCItems.research_notes_crimson), height / 2.0f);
        entityDropItem(new ItemStack(ItemsTC.lootBag, 1, 2), 1.5f);
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        bossInfo.setName(getDisplayName());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":chant"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":growl"));
    }

    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    public int getTalkInterval() {
        return 450;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance diff) {
        this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(RCItems.molot));
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(RCItems.pontifex_hood));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(RCItems.pontifex_robe));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(RCItems.pontifex_legs));
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(RCItems.pontifex_boots));

    }

    @Override
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance diff) {
        float f = diff.getClampedAdditionalDifficulty();
        if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.5F * f) {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int) (7.0F + f * (float) this.rand.nextInt(22)), false);
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData data) {
        setEquipmentBasedOnDifficulty(diff);
        setEnchantmentBasedOnDifficulty(diff);
        bossInfo.setName(getDisplayName());
        EntityUtils.makeChampion(this, true);
        return super.onInitialSpawn(diff, data);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (damagesource.getImmediateSource() instanceof EntityLivingBase) {
            this.targetedEntity = damagesource.getImmediateSource();
            this.aggroCooldown = 200;
        }

        if (damagesource.getTrueSource() instanceof EntityLivingBase) {
            this.targetedEntity = damagesource.getTrueSource();
            this.aggroCooldown = 200;
        }
        i /= (cultists + 3);
        return super.attackEntityFrom(damagesource, i);
    }


    protected void updateAITasks() {
        super.updateAITasks();
        List<Entity> list = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityCultist.class, 8.0);
        if (!list.isEmpty()) {
            cultists = list.size();
        }
        Iterator<Entity> i$ = list.iterator();

        while (i$.hasNext()) {
            Entity e = i$.next();

            try {
                if (e instanceof EntityCultist && !((EntityCultist) e).isPotionActive(MobEffects.REGENERATION)) {
                    ((EntityCultist) e).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 1));
                }
            } catch (Exception ignore) {
            }
        }
        double attackrange = 16.0;
        if (this.targetedEntity != null && this.targetedEntity.isDead) {
            this.targetedEntity = null;
        }

        --this.aggroCooldown;
        if (this.world.rand.nextInt(10) > 7 && (this.targetedEntity == null || this.aggroCooldown-- <= 0)) {
            this.targetedEntity = this.world.getClosestPlayerToEntity(this, 32.0);
            if (this.targetedEntity != null) {
                this.aggroCooldown = 50;
            }
        }

        if (this.targetedEntity != null && this.targetedEntity.getDistanceSq(this) < attackrange * attackrange) {
            if (this.canEntityBeSeen(this.targetedEntity)) {
                ++this.attackCounter;
                if (continuousAttack && attackCounter < 10) {
                } else if (attackCounter > 10) {
                    if (!world.isRemote) {
                        if (targetedEntity instanceof EntityPlayer) {
                            RenaissanceCore.packetInstance.sendTo(new PacketEnslave(targetedEntity.getName(), false), (EntityPlayerMP) targetedEntity);
                        }
                    }
                    continuousAttack = false;
                }
                if (this.attackCounter == 20) {
                    if (targetedEntity instanceof EntityPlayer) {
                        if (!world.isRemote) {
                            int rand = world.rand.nextInt(4);
                            switch (rand) {
                                case 0: {
                                    boolean found = false;
                                    List<ConcentratedWarpChargeEntity> charges = world.getEntitiesWithinAABB(ConcentratedWarpChargeEntity.class, targetedEntity.getEntityBoundingBox().expand(7.0, 7.0, 7.0).expand(-7.0, -7.0, -7.0));
                                    if (!charges.isEmpty()) {
                                        for (ConcentratedWarpChargeEntity e : charges) {

                                            if (e.getOwner().getName().equals(targetedEntity.getName())) {
                                                found = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!found) {
                                        ConcentratedWarpChargeEntity charge = new ConcentratedWarpChargeEntity(targetedEntity.posX + (-0.5 + world.rand.nextFloat()) * 4.0F, targetedEntity.posY, targetedEntity.posZ + (-0.5 + world.rand.nextFloat()) * 4.0F, (EntityPlayer) targetedEntity);
                                        charge.setOwner(targetedEntity.getUniqueID());
                                        world.spawnEntity(charge);
                                    }

                                    break;
                                }
                                case 1: {
                                    if (targetedEntity instanceof EntityPlayer) {
                                        String name = targetedEntity.getName();
                                        RenaissanceCore.packetInstance.sendTo(new PacketEnslave(name, true), (EntityPlayerMP) targetedEntity);
                                        continuousAttack = true;
                                    }
                                    break;
                                }
                                case 2: {
                                    List<EntityCultistPortalLesser> portals = world.getEntitiesWithinAABB(EntityCultistPortalLesser.class, this.getEntityBoundingBox().expand(64.0, 64.0, 64.0).expand(-64.0, -64.0, -64.0));
                                    if (portals.size() < 3) {
                                        if (!world.isRemote) {
                                            EntityCultistPortalLesser portal = new EntityCultistPortalLesser(world);
                                            int a = (int) this.posX + this.rand.nextInt(5) - this.rand.nextInt(5);
                                            int b = (int) this.posZ + this.rand.nextInt(5) - this.rand.nextInt(5);
                                            if (a != (int) this.posX && b != (int) this.posZ && this.world.isAirBlock(new BlockPos(a, (int) this.posY, b))) {
                                                portal.setLocationAndAngles(a, posY, b, world.rand.nextFloat(), world.rand.nextFloat());
                                                world.spawnEntity(portal);
                                                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(new BlockPos(a, (int) this.posY, b), this, 0, 0, 0), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
                                                this.playSound(SoundsTC.wandfail, 1.0F, 1.0F);
                                            }
                                        }
                                    }
                                    break;
                                }
                                case 3: {
                                    List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(6.0, 6.0, 6.0).expand(-6.0, -6.0, -6.0));
                                    list.remove(this);
                                    if (!list.isEmpty()) {
                                        for (EntityLivingBase e : entities) {
                                            if (!e.isDead && !(e instanceof EntityCultist)) {
                                                double x = e.posX;
                                                double y = e.posY;
                                                double z = e.posZ;
                                                if (!world.isRemote) {
                                                    e.attackEntityFrom(DamageSource.MAGIC, (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                                                    this.heal(10.0F);
                                                } else {
                                                    for (int i = 0; i < 3; i++) {
                                                        RenaissanceCore.proxy.lifedrain(this, x, y, z);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    this.attackCounter = -20 + this.world.rand.nextInt(20);
                }
            } else if (this.attackCounter > 0) {
                --this.attackCounter;
            }
        }

    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (this.canEntityBeSeen(target)) {
            this.swingArm(this.getActiveHand());
            EtherealShacklesEntity shackles = new EtherealShacklesEntity(target.world, this);
            double d0 = target.posX + target.motionX - this.posX;
            double d1 = target.posY - this.posY;
            double d2 = target.posZ + target.motionZ - this.posZ;
            shackles.shoot(d0, d1, d2, 2.0F, 2.0F);
            shackles.playSound(new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":shackles_throw")), 0.5F, 1.0F);
            this.world.spawnEntity(shackles);
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {

    }
}