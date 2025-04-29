package com.keletu.renaissance_core.events;

import baubles.api.BaublesApi;
import com.keletu.renaissance_core.ConfigsRC;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import static thaumcraft.api.ThaumcraftMaterials.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.items.consumables.ItemSanitySoap;
import thaumcraft.common.lib.potions.PotionWarpWard;
import thaumcraft.common.lib.research.ResearchManager;
import thecodex6824.thaumicaugmentation.common.entity.EntityItemImportant;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = RenaissanceCore.MODID)
public class CursedEvents {
    static Random rand = new Random();
    private static EntityPlayer cursedPlayer;
    public static final Map<EntityPlayer, AxisAlignedBB> CURSED_AURA = new WeakHashMap<>();

    public static boolean hasThaumiumCursed(EntityPlayer player) {
        return BaublesApi.isBaubleEquipped(player, RCItems.dice12) != -1;
    }

    public static AxisAlignedBB getBoundingBoxAroundEntity(final Entity entity, final double radius) {
        return new AxisAlignedBB(entity.posX - radius, entity.posY - radius, entity.posZ - radius, entity.posX + radius, entity.posY + radius, entity.posZ + radius);
    }

    public static boolean isVanillaMaterial(EntityPlayer player) {
        Item itemTool = player.getHeldItemMainhand().getItem();
        if (itemTool instanceof ItemTool) {
            return ((ItemTool) itemTool).toolMaterial.equals(Item.ToolMaterial.DIAMOND) || ((ItemTool) itemTool).toolMaterial.equals(Item.ToolMaterial.IRON) || ((ItemTool) itemTool).toolMaterial.equals(Item.ToolMaterial.STONE) || ((ItemTool) itemTool).toolMaterial.equals(Item.ToolMaterial.GOLD) || ((ItemTool) itemTool).toolMaterial.equals(Item.ToolMaterial.WOOD);
        } else if (itemTool instanceof ItemSword)
            return ((ItemSword) itemTool).material.equals(Item.ToolMaterial.DIAMOND) || ((ItemSword) itemTool).material.equals(Item.ToolMaterial.IRON) || ((ItemSword) itemTool).material.equals(Item.ToolMaterial.STONE) || ((ItemSword) itemTool).material.equals(Item.ToolMaterial.GOLD) || ((ItemSword) itemTool).material.equals(Item.ToolMaterial.WOOD);
        else
            return false;
    }

    public static boolean isMaterialThaumium(EntityPlayer player) {
        Item itemTool = player.getHeldItemMainhand().getItem();
        if (itemTool instanceof ItemTool) {
            return ((ItemTool) itemTool).toolMaterial.equals(TOOLMAT_ELEMENTAL) || ((ItemTool) itemTool).toolMaterial.equals(TOOLMAT_VOID) || ((ItemTool) itemTool).toolMaterial.equals(TOOLMAT_THAUMIUM);
        } else if (itemTool instanceof ItemSword)
            return ((ItemSword) itemTool).material.equals(TOOLMAT_ELEMENTAL) || ((ItemSword) itemTool).material.equals(TOOLMAT_VOID) || ((ItemSword) itemTool).material.equals(TOOLMAT_THAUMIUM);
        else
            return false;
    }

    public static void addDrop(LivingDropsEvent event, ItemStack drop) {
        if (drop == null || drop.getItem() == null)
            return;

        EntityItem entityitem = new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, drop);
        entityitem.setPickupDelay(10);
        event.getDrops().add(entityitem);
    }

    public static void addDropWithChance(LivingDropsEvent event, ItemStack drop, int chance) {
        if (new Random().nextInt(100) < chance) {
            addDrop(event, drop);
        }
    }

    public static ItemStack getRandomSizeStack(Item item, int minAmount, int maxAmount) {
        return new ItemStack(item, minAmount + new Random().nextInt(maxAmount - minAmount + 1));
    }

    public static ItemStack getRandomSizeStack(Item item, int minAmount, int maxAmount, int meta) {
        return new ItemStack(item, minAmount + new Random().nextInt(maxAmount - minAmount + 1), meta);
    }

    public static void addOneOf(LivingDropsEvent event, ItemStack... itemStacks) {
        int chosenStack = new Random().nextInt(itemStacks.length);
        addDrop(event, itemStacks[chosenStack]);
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityDragon && !event.getEntity().world.isRemote) {
            EntityDragon dragon = (EntityDragon) event.getEntity();
            // final burst of XP/actual death is at 200 ticks
            if (dragon.deathTicks == 199 && cursedPlayer != null) {

                Vec3d center = new Vec3d(dragon.posX, dragon.posY, dragon.posZ);
                EntityItemImportant heart = new EntityItemImportant(event.getEntity().world, center.x, center.y, center.z, new ItemStack(ItemsTC.primordialPearl, 1, 7));

                event.getEntity().world.spawnEntity(heart);

                cursedPlayer = null;

            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.isRecentlyHit() && event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityLivingBase killed = event.getEntityLiving();
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

            if (hasThaumiumCursed(player)) {


                //if (!ELConfigs.enableSpecialDrops)
                //    return;

                //if (killed instanceof EntityDragon) {
                //    if (SuperpositionHandler.isTheWorthyOne(player)) {
                //        int heartsGained = SuperpositionHandler.getPersistentInteger(player, "AbyssalHeartsGained", 0);
//
                //        if (heartsGained < 5) { // Only as many as there are unique items from them, +1
                //            abyssalHeartOwner = player;
                //        }
                //    }
                //}

                if (killed instanceof EntityDragon) {
                    cursedPlayer = player;
                }

                if (killed.getClass() == EntityZombie.class || killed.getClass() == EntityHusk.class) {
                    addDropWithChance(event, getRandomSizeStack(Items.IRON_NUGGET, 1, 18), 35);
                } else if (killed.getClass() == EntitySkeleton.class || killed.getClass() == EntityStray.class) {
                    addDropWithChance(event, ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY, rand.nextInt(10) + 1), 20);
                } else if (killed.getClass() == EntitySpider.class || killed.getClass() == EntityCaveSpider.class) {
                    addDropWithChance(event, getRandomSizeStack(ItemsTC.nuggets, 1, 18, 5), 10);
                } else if (killed.getClass() == EntityCreeper.class) {
                    addDropWithChance(event, ThaumcraftApiHelper.makeCrystal(Aspect.EARTH, rand.nextInt(10) + 1), 20);
                } else if (killed.getClass() == EntityWitch.class && ResearchManager.completeResearch(player, "FIRSTSTEPS")) {
                    addDropWithChance(event, getRandomSizeStack(ItemsTC.salisMundus, 2, 3), 30);
                } else if (killed.getClass() == EntityPigZombie.class) {
                    addDropWithChance(event, getRandomSizeStack(ItemsTC.tallow, 1, 5), 50);
                } else if (killed.getClass() == EntitySilverfish.class) {
                    addDropWithChance(event, getRandomSizeStack(ItemsTC.amber, 1, 2), 30);
                } else if (killed.getClass() == EntityEnderman.class) {
                    addDropWithChance(event, new ItemStack(ItemsTC.voidSeed), 5);
                } else if (killed.getClass() == EntityBlaze.class || killed.getClass() == EntityMagmaCube.class) {
                    addDropWithChance(event, ThaumcraftApiHelper.makeCrystal(Aspect.FIRE, rand.nextInt(10) + 1), 20);
                } else if (killed.getClass() == EntityGhast.class) {
                    addDropWithChance(event, getRandomSizeStack(ItemsTC.quicksilver, 1, 4), 50);
                } else if (killed.getClass() == EntitySlime.class) {
                    addDropWithChance(event, ThaumcraftApiHelper.makeCrystal(Aspect.WATER, rand.nextInt(10) + 1), 20);
                } else if (killed.getClass() == EntityPig.class) {
                    addDrop(event, getRandomSizeStack(ItemsTC.chunks, 1, 10, 2));
                } else if (killed.getClass() == EntityCow.class) {
                    addDrop(event, getRandomSizeStack(ItemsTC.chunks, 1, 10, 0));
                } else if (killed.getClass() == EntityChicken.class) {
                    addDrop(event, getRandomSizeStack(ItemsTC.chunks, 1, 10, 1));
                } else if (killed.getClass() == EntityRabbit.class) {
                    addDrop(event, getRandomSizeStack(ItemsTC.chunks, 1, 10, 4));
                } else if (killed.getClass() == EntitySheep.class) {
                    addDrop(event, getRandomSizeStack(ItemsTC.chunks, 1, 10, 5));
                } else if (killed.getClass() == EntityVillager.class) {
                    addDrop(event, getRandomSizeStack(ItemsTC.chunks, 1, 2));
                } else if (killed.getClass() == EntityPech.class) {
                    addDropWithChance(event, new ItemStack(RCItems.pech_backpack), 50);
                }

            }
        }
    }

    //Config warp amount and flux amount
    @SubscribeEvent
    public static void onPlayerTick(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (hasThaumiumCursed(player)) {
            if (rand.nextInt(2) == 0)
                AuraHelper.polluteAura(player.world, player.getPosition(), ConfigsRC.cursedSleepPollution, true);
            else
                ThaumcraftApi.internalMethods.addWarpToPlayer(player, ConfigsRC.cursedSleepWarpPoint, IPlayerWarp.EnumWarpType.TEMPORARY);
        }
    }

    @SubscribeEvent
    public static void tickHandler(TickEvent.PlayerTickEvent event) {
        if (event.player.world.isRemote)
            return;

        EntityPlayer player = event.player;

        if (hasThaumiumCursed(player)) {
            CURSED_AURA.put(player, getBoundingBoxAroundEntity(player, 128));
        } else {
            CURSED_AURA.remove(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void miningBlocks(PlayerEvent.BreakSpeed event) {
        float correctedSpeed = event.getOriginalSpeed();
        float miningBoost = 1.0F;
        if (CursedEvents.hasThaumiumCursed(event.getEntityPlayer())) {
            if (isVanillaMaterial(event.getEntityPlayer()))
                miningBoost -= 0.5F;
            if (isMaterialThaumium(event.getEntityPlayer()))
                miningBoost += 0.25F;
        }

        correctedSpeed = correctedSpeed * miningBoost;
        correctedSpeed -= event.getOriginalSpeed();

        event.setNewSpeed(event.getNewSpeed() + correctedSpeed);
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (event.getAmount() >= Float.MAX_VALUE)
            return;

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

            if (hasThaumiumCursed(player)) {
                if (isVanillaMaterial(player))
                    event.setAmount(event.getAmount() / 2);
                if (isMaterialThaumium(player))
                    event.setAmount(event.getAmount() * 1.25F);
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (event.getSource() == DamageSourceThaumcraft.taint && hasThaumiumCursed(player)) {
                event.setAmount(event.getAmount() * 2F);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            if (hasThaumiumCursed(player)) {
                if (AuraHelper.getVis(player.world, player.getPosition()) / AuraHelper.getAuraBase(player.world, player.getPosition()) <= ConfigsRC.cursedPlayerRegenHealthAura) {
                    event.setAmount(0);
                } else
                    AuraHelper.drainVis(player.world, player.getPosition(), event.getAmount() * ConfigsRC.cursedPlayerRegenHealthVis, false);
            }
        }

    }


    @SubscribeEvent
    public static void onEatFoods(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntity().world.isRemote) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            ItemStack food = event.getItem();

            if (hasThaumiumCursed(player)) {
                if (OreDictionary.containsMatch(false, OreDictionary.getOres("listAllHarmfulFoods"), food)) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 3, IPlayerWarp.EnumWarpType.TEMPORARY);
                    player.addPotionEffect(new PotionEffect(MobEffects.POISON, 300, 0));
                    player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
                } else if (OreDictionary.containsMatch(false, OreDictionary.getOres("listAllRawMeats"), food)) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.TEMPORARY);
                    player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 150, 0));
                }
            }
        }
    }


    @SubscribeEvent
    public static void onUseSoap(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            if (player.world.isRemote)
                return;

            int qq = event.getItem().getMaxItemUseDuration() - event.getDuration();
            if (hasThaumiumCursed(player) && qq > 95 && event.getItem().getItem() instanceof ItemSanitySoap) {
                IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
                int amt = 1;
                if (player.isPotionActive(PotionWarpWard.instance)) {
                    ++amt;
                }
                int i = MathHelper.floor(player.posX);
                int j = MathHelper.floor(player.posY);
                int k = MathHelper.floor(player.posZ);
                if (player.world.getBlockState(new BlockPos(i, j, k)).getBlock() == BlocksTC.purifyingFluid) {
                    ++amt;
                }
                if (ConfigsRC.canRemovePermanentWarp) {
                    if (warp.get(IPlayerWarp.EnumWarpType.PERMANENT) > amt) {
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -amt, IPlayerWarp.EnumWarpType.PERMANENT);
                    } else
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -warp.get(IPlayerWarp.EnumWarpType.PERMANENT), IPlayerWarp.EnumWarpType.PERMANENT);
                }
                if (warp.get(IPlayerWarp.EnumWarpType.NORMAL) > amt * 2) {
                    //todo config for "/2"
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, -amt * 2, IPlayerWarp.EnumWarpType.NORMAL);
                } else
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, -warp.get(IPlayerWarp.EnumWarpType.NORMAL), IPlayerWarp.EnumWarpType.NORMAL);

                if (warp.get(IPlayerWarp.EnumWarpType.TEMPORARY) > 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, -warp.get(IPlayerWarp.EnumWarpType.TEMPORARY), IPlayerWarp.EnumWarpType.TEMPORARY);
                }
                event.getItem().shrink(1);

                event.setCanceled(true);
            }
        }

    }
}
