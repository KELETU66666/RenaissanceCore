package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.ConfigsRC;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.blocks.tile.TileEtherealBloom;
import com.keletu.renaissance_core.blocks.tile.TileManaPod;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.items.RCItems;
import com.keletu.renaissance_core.recipes.VerdantCharmToRing;
import com.keletu.renaissance_core.recipes.VerdantRingToCharm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.golems.EntityThaumcraftGolem;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber
public class RegistryEvents {

    // ==================================================
    //                 Break Block Event
    // ==================================================
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getState() == null || event.getWorld() == null || event.isCanceled() || event.getWorld().isRemote) {
            return;
        }

        if (event.getPlayer() != null && !event.getPlayer().isCreative()) {
            List<EntityProtectionField> list = event.getWorld().getEntitiesWithinAABB(EntityProtectionField.class, event.getPlayer().getEntityBoundingBox().grow(ConfigsRC.protectionRange));
            if (!(event.getState().getBlock() instanceof BlockFire) && list.size() > 0) {
                event.setCanceled(true);
                event.setResult(Event.Result.DENY);
                event.getPlayer().sendStatusMessage(new TextComponentString(I18n.translateToLocal("rc.protection.break")), true);
            }
        }
    }

    @SubscribeEvent
    public static void livingDeath(final LivingDeathEvent event) {
        if (!event.getEntityLiving().world.isRemote && !(event.getEntityLiving() instanceof EntityOwnedConstruct) && !(event.getEntityLiving() instanceof EntityGolem) && !(event.getEntityLiving() instanceof ITaintedMob) && event.getEntityLiving().isPotionActive(PotionFluxTaint.instance)) {
            Entity entity = null;
            if (event.getEntityLiving() instanceof EntityCreeper) {
                entity = new EntityTaintCreeper(event.getEntityLiving().world);
            } else if (event.getEntityLiving() instanceof EntitySheep) {
                entity = new EntityTaintSheep(event.getEntityLiving().world);
            } else if (event.getEntityLiving() instanceof EntityCow) {
                entity = new EntityTaintCow(event.getEntityLiving().world);
            } else if (event.getEntityLiving() instanceof EntityPig) {
                entity = new EntityTaintPig(event.getEntityLiving().world);
            } else if (event.getEntityLiving() instanceof EntityChicken) {
                entity = new EntityTaintChicken(event.getEntityLiving().world);
            } else if (event.getEntityLiving() instanceof EntityVillager) {
                entity = new EntityTaintVillager(event.getEntityLiving().world);
            } else if (event.getEntityLiving().getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD || event.getEntityLiving() instanceof EntityAnimal) {
                for (int n = (int) Math.max(1.0, Math.sqrt(event.getEntityLiving().getMaxHealth() + 2.0f)), a = 0; a < n; ++a) {
                    final Entity e = new EntityTaintCrawler(event.getEntityLiving().world);
                    e.setLocationAndAngles(event.getEntityLiving().posX + (event.getEntityLiving().world.rand.nextFloat() - event.getEntityLiving().world.rand.nextFloat()) * event.getEntityLiving().width, event.getEntityLiving().posY + event.getEntityLiving().world.rand.nextFloat() * event.getEntityLiving().height, event.getEntityLiving().posZ + (event.getEntityLiving().world.rand.nextFloat() - event.getEntityLiving().world.rand.nextFloat()) * event.getEntityLiving().width, (float) event.getEntityLiving().world.rand.nextInt(360), 0.0f);
                    event.getEntityLiving().world.spawnEntity(e);
                }
                event.getEntityLiving().setDead();
                event.setCanceled(true);
            } else if (event.getEntityLiving() instanceof EntityRabbit) {
                entity = new EntityTaintRabbit(event.getEntityLiving().world);
                ((EntityRabbit) entity).setRabbitType(((EntityRabbit) event.getEntityLiving()).getRabbitType());
            } else {
                entity = new EntityThaumicSlime(event.getEntityLiving().world);
                ((EntityThaumicSlime) entity).setSlimeSize((int) (1.0f + Math.min(event.getEntityLiving().getMaxHealth() / 10.0f, 6.0f)), false);
            }
            if (entity != null) {
                entity.setLocationAndAngles(event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, event.getEntityLiving().rotationYaw, 0.0f);
                event.getEntityLiving().world.spawnEntity(entity);
                if (!(event.getEntityLiving() instanceof EntityPlayer)) {
                    event.getEntityLiving().setDead();
                    event.setCanceled(true);
                }
            }
        }
    }

    // ==================================================
    //                 Block Place Event
    // ==================================================

    /**
     * This uses the block place events to update Block Spawn Triggers.
     **/
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.getState() == null || event.getWorld() == null || event.isCanceled()) {
            return;
        }

        if (event.getPlayer() != null && !event.getPlayer().isCreative()) {
            List<EntityProtectionField> list = event.getWorld().getEntitiesWithinAABB(EntityProtectionField.class, event.getPlayer().getEntityBoundingBox().grow(ConfigsRC.protectionRange));
            if (list.size() > 0) {
                event.setCanceled(true);
                event.setResult(Event.Result.DENY);
                event.getPlayer().sendStatusMessage(new TextComponentString(I18n.translateToLocal("rc.protection.place")), true);
            }
        }
    }


    @SubscribeEvent
    public static void regBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(RFBlocks.ethereal_bloom);
        event.getRegistry().registerAll(RFBlocks.mana_pod);
        event.getRegistry().registerAll(RFBlocks.pechHead_normal);
        event.getRegistry().registerAll(RFBlocks.pechHead_hunter);
        event.getRegistry().registerAll(RFBlocks.pechHead_thaumaturge);
        event.getRegistry().registerAll(RFBlocks.full_crucible);

        GameRegistry.registerTileEntity(TileEtherealBloom.class, new ResourceLocation(RenaissanceCore.MODID, "ethereal_bloom"));
        GameRegistry.registerTileEntity(TileManaPod.class, new ResourceLocation(RenaissanceCore.MODID, "mana_pod"));
    }

    @SubscribeEvent
    public static void regItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(RCItems.arcane_lime_powder);
        event.getRegistry().registerAll(RCItems.etherealBloomItem);
        event.getRegistry().registerAll(RCItems.dice12);
        event.getRegistry().registerAll(RCItems.verdantRing);

        event.getRegistry().registerAll(RCItems.pechHeadNormal);
        event.getRegistry().registerAll(RCItems.pechHeadHunter);
        event.getRegistry().registerAll(RCItems.pechHeadThaumaturge);

        event.getRegistry().registerAll(RCItems.mana_bean);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new VerdantCharmToRing().setRegistryName(RenaissanceCore.MODID, "verdant_charm_to_ring"));
        event.getRegistry().register(new VerdantRingToCharm().setRegistryName(RenaissanceCore.MODID, "verdant_ring_to_charm"));
    }

    @SubscribeEvent
    public static void onGolemInWall(LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityThaumcraftGolem) {
            EntityThaumcraftGolem golem = (EntityThaumcraftGolem) event.getEntity();
            if (golem.getProperties().hasTrait(RenaissanceCore.BUBBLE) && (Objects.equals(event.getSource().damageType, DamageSource.IN_WALL.damageType) || Objects.equals(event.getSource().damageType, DamageSource.FALLING_BLOCK.damageType))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Random rand = new Random();
        if (event.getEntityLiving() instanceof EntityPech && event.isRecentlyHit() && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer && !(event.getSource().getTrueSource() instanceof FakePlayer)) {
            if (event.getEntityLiving().getClass() == EntityPech.class && event.isRecentlyHit() && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
                ItemStack weap = ((EntityPlayer) event.getSource().getTrueSource()).getHeldItem(EnumHand.MAIN_HAND);
                if (!weap.isEmpty() && weap.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation("forbiddenmagicre", "skull_axe")) && rand.nextInt(26) <= (3 + EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, weap))) {
                    if (((EntityPech) event.getEntityLiving()).getPechType() == 1)
                        addDrop(event, new ItemStack(RCItems.pechHeadThaumaturge, 1, 0));
                    else if (((EntityPech) event.getEntityLiving()).getPechType() == 2)
                        addDrop(event, new ItemStack(RCItems.pechHeadHunter, 1, 0));
                    else
                        addDrop(event, new ItemStack(RCItems.pechHeadNormal, 1, 0));

                }
            }
        }
    }

    public static void addDrop(LivingDropsEvent event, ItemStack drop) {
        EntityItem entityitem = new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, drop);
        event.getDrops().add(entityitem);
    }

}
