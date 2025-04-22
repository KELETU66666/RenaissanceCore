package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.*;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.foci.FocusEffectFire;
import thaumcraft.common.items.casters.foci.FocusMediumProjectile;

import java.util.*;

public class Thaumaturge extends EntityMob implements IRangedAttackMob {
    public boolean trading = false;
    private static final DataParameter<Integer> ANGER = EntityDataManager.createKey(Thaumaturge.class, DataSerializers.VARINT);
    public boolean updateAINextTick = false;
    private final EntityAIAttackRanged aiBlastAttack = new EntityAIAttackRanged(this, 1.0, 20, 40, 15.0F);
    private final AIAttackOnCollide aiMeleeAttack = new AIAttackOnCollide(this, EntityLivingBase.class, 0.6, false);
    static HashMap<Integer, Integer> valuedItems = new HashMap();
    public static ArrayList<List> tradeInventory = new ArrayList<>();

    public Thaumaturge(World world) {
        super(world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AILongRangeAttack(this, 3.0, 1.0, 20, 40, 24.0F));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.1, false));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.5));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
        this.tasks.addTask(9, new EntityAIWander(this, 0.6));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.tasks.addTask(11, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(12, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new Thaumaturge.AIThaumaturgeTarget<>(this, EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntitySkeleton.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityBrainyZombie.class, true));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(
                this,
                EntityLivingBase.class,
                10,
                true,
                false,
                input -> input != null && input instanceof IEldritchMob
        ));

        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(
                this,
                EntityLivingBase.class,
                10,
                true,
                false,
                input -> input != null && input instanceof ITaintedMob
        ));

        if (world != null && !world.isRemote) {
            this.setCombatTask();
        }
    }

    public Thaumaturge(World world, double x, double y, double z) {
        this(world);
        setPosition(x, y, z);
        //this.faceEntity();
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ANGER, 0);
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }

    @Override
    public String getName() {
        if (this.hasCustomName()) {
            return this.getCustomNameTag();
        } else {
            return I18n.translateToLocal("entity.ThaumicConcilium.Thaumaturge.name");
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    public void onUpdate() {
        if (this.getAnger() > 0) {
            this.setAnger(this.getAnger() - 1);
        }

        double d0;
        double d1;
        double d2;
        if (this.world.isRemote && this.rand.nextInt(15) == 0 && this.getAnger() > 0) {
            d0 = this.rand.nextGaussian() * 0.02;
            d1 = this.rand.nextGaussian() * 0.02;
            d2 = this.rand.nextGaussian() * 0.02;
            this.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + 0.5 + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
        }
        super.onUpdate();
    }

    @Override
    protected void dropFewItems(boolean flag, int i) {
        int r = this.rand.nextInt(2);
        r += i;
        this.entityDropItem(new ItemStack(ItemsTC.nuggets, r, 6), 1.5F);
        super.dropFewItems(flag, i);
    }

    public int getAnger() {
        return this.dataManager.get(ANGER);
    }

    public void setAnger(int par1) {
        this.dataManager.set(ANGER, par1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":think"));
    }

    @Override
    public int getTalkInterval() {
        return 100;
    }

    private void becomeAngryAt(Entity par1Entity) {
        this.setAttackTarget((EntityLivingBase) par1Entity);
        if (this.getAnger() <= 0) {
            //this.world.setEntityState(this, (byte)19);
            //this.playSound("thaumcraft:pech_charge", this.getSoundVolume(), this.getSoundPitch());
        }

        this.setAttackTarget((EntityLivingBase) par1Entity);
        this.setAnger(400 + this.rand.nextInt(400));
        this.updateAINextTick = true;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4);
    }

    public int getTotalArmorValue() {
        int i = super.getTotalArmorValue() + 2;
        if (i > 20) {
            i = 20;
        }

        return i;
    }

    public boolean getCanSpawnHere() {
        return true;
    }

    public boolean attackEntityFrom(DamageSource damSource, float par2) {
        if (this.isEntityInvulnerable(damSource)) {
            return false;
        } else {
            Entity entity = damSource.getTrueSource();
            if (entity instanceof EntityPlayer) {
                List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(32.0, 16.0, 32.0));

                for (int i = 0; i < list.size(); ++i) {
                    Entity entity1 = (Entity) list.get(i);
                    if (entity1 instanceof Thaumaturge) {
                        Thaumaturge thaumaturge = (Thaumaturge) entity1;
                        thaumaturge.becomeAngryAt(entity);
                    }
                }
                this.becomeAngryAt(entity);
            }
            return super.attackEntityFrom(damSource, par2);
        }
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        //super.addRandomArmor();
        ItemStack wand = new ItemStack(ItemsTC.casterBasic);
        ItemStack focus = getFireFocus();
        ((ItemCaster) wand.getItem()).setFocus(wand, focus);
        this.setHeldItem(EnumHand.MAIN_HAND, wand);
        this.inventoryHandsDropChances[0] = 0.1F;
    }

    public static ItemStack getFireFocus() {
        ItemStack focus = new ItemStack(ItemsTC.focus1);
        FocusPackage core = new FocusPackage();
        FocusNode root = new FocusMediumRoot();
        core.addNode(root);
        FocusNode medium = new FocusMediumProjectile();
        medium.initialize();
        medium.getSetting("speed").setValue(3);
        medium.setParent(root);
        core.addNode(medium);
        FocusNode effect = new FocusEffectFire();
        effect.setParent(medium);
        core.addNode(effect);

        int complexity = 0;

        Object2IntOpenHashMap<String> nodeCounts = new Object2IntOpenHashMap<>(core.nodes.size());
        Iterator<IFocusElement> var12 = core.nodes.iterator();

        while (var12.hasNext()) {
            IFocusElement node = var12.next();
            if (node instanceof FocusNode) {
                int count = nodeCounts.getOrDefault(node.getKey(), 0) + 2;
                double complexityMultiplier = 0.5 * (double) count;
                nodeCounts.addTo(node.getKey(), 1);
                complexity = (int) ((double) complexity + (double) ((FocusNode) node).getComplexity() * complexityMultiplier);
            }
        }

        core.setComplexity(complexity);

        ItemFocus.setPackage(focus, core);
        return focus;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!player.isSneaking() && (player.getHeldItem(hand).isEmpty() || !(player.getHeldItem(hand).getItem() instanceof ItemNameTag))) {
            if (!this.world.isRemote && this.getAnger() == 0 && hand == EnumHand.MAIN_HAND) {
                if (ThaumcraftCapabilities.knowsResearch(player, "BASEAUROMANCY")) {
                    // player.openGui(RenaissanceCore.MODID, 1, this.world, this.getEntityId(), 0, 0);
                    player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tc.thaumaturge.taunt.WIP")).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                } else {
                    int r = rand.nextInt(4);
                    player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tc.thaumaturge.taunt." + r)).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                }
                return true;
            } else {
                return super.processInteract(player, hand);
            }
        } else {
            return false;
        }
    }

    public void updateAITasks() {
        if (this.updateAINextTick) {
            this.updateAINextTick = false;
            this.setCombatTask();
        }

        super.updateAITasks();
        if (this.ticksExisted % 40 == 0) {
            this.heal(1.0F);
        }

    }

    public void setCombatTask() {
        this.tasks.removeTask(this.aiBlastAttack);
        this.tasks.removeTask(this.aiMeleeAttack);
        ItemStack itemstack = this.getHeldItem(EnumHand.MAIN_HAND);
        if (!itemstack.isEmpty() && itemstack.getItem() == ItemsTC.casterBasic) {
            this.tasks.addTask(1, this.aiBlastAttack);
        } else {
            this.tasks.addTask(2, this.aiMeleeAttack);
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_) {
        FocusPackage p = new FocusPackage(this);
        FocusMediumRoot root = new FocusMediumRoot();
        double off = this.getDistance(target) / 6.0F;
        root.setupFromCasterToTarget(this, target, off);
        p.addNode(root);
        FocusMediumProjectile fp = new FocusMediumProjectile();
        fp.initialize();
        fp.getSetting("speed").setValue(3);
        p.addNode(fp);
        p.addNode(new FocusEffectFire());
        FocusEngine.castFocusPackage(this, p, true);
        this.swingArm(this.getActiveHand());
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {

    }

    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, IEntityLivingData par1EntityLivingData) {
        par1EntityLivingData = super.onInitialSpawn(diff, par1EntityLivingData);
        this.setEquipmentBasedOnDifficulty(diff);
        this.tasks.addTask(1, this.aiBlastAttack);
        return super.onInitialSpawn(diff, par1EntityLivingData);
    }

    public int getValue(ItemStack item) {
        if (item == null) {
            return 0;
        } else {
            int value = valuedItems.containsKey(Item.getIdFromItem(item.getItem())) ? valuedItems.get(Item.getIdFromItem(item.getItem())) : 0;
            return value;
        }
    }

    public boolean isValued(ItemStack item) {
        if (item == null) {
            return false;
        } else {
            boolean value = valuedItems.containsKey(Item.getIdFromItem(item.getItem()));
            return value;
        }
    }

    static {
        //valuedItems.put(Item.getIdFromItem(ItemsTC.itemManaBean), 1);
        valuedItems.put(Item.getIdFromItem(Items.SKULL), 3);
        valuedItems.put(Item.getIdFromItem(ItemsTC.casterBasic), 3);
        valuedItems.put(Item.getIdFromItem(Items.EXPERIENCE_BOTTLE), 3);
        valuedItems.put(Item.getIdFromItem(Items.ENCHANTED_BOOK), 5);
        valuedItems.put(Item.getIdFromItem(ItemsTC.curio), -1);

        //tradeInventory.add(Arrays.asList(9, new ItemStack(ItemsTC.itemManaBean)));
        for (int a = 0; a < 6; ++a) {
            tradeInventory.add(Arrays.asList(10, new ItemStack(ItemsTC.curio, 1, a)));
        }
        tradeInventory.add(Arrays.asList(7, new ItemStack(ItemsTC.nuggets, 1, 6)));
        tradeInventory.add(Arrays.asList(7, new ItemStack(ItemsTC.nuggets, 1, 6)));
        for (int a = 0; a < 7; ++a) {
            tradeInventory.add(Arrays.asList(2, new ItemStack(ItemsTC.salisMundus, 1, a)));
        }
        tradeInventory.add(Arrays.asList(1, new ItemStack(ItemsTC.focusPouch)));
        tradeInventory.add(Arrays.asList(1, getFireFocus()));
        tradeInventory.add(Arrays.asList(4, new ItemStack(ItemsTC.amuletVis, 1, 0)));
        //tradeInventory.add(Arrays.asList(1, new ItemStack(ForbiddenItems.wandCore, 1, 5)));
    }

    static class AIThaumaturgeTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
        Thaumaturge thaumaturge;

        public AIThaumaturgeTarget(Thaumaturge thaumaturge, Class<T> classTarget) {
            super(thaumaturge, classTarget, true);
            this.thaumaturge = thaumaturge;
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            return thaumaturge.getAnger() > 0 && super.shouldExecute();
        }


        public boolean shouldContinueExecuting() {
            return thaumaturge.getAnger() > 0 && thaumaturge.getAttackTarget() == null;
        }
    }
}