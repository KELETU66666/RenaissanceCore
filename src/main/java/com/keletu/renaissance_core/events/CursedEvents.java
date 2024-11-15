package com.keletu.renaissance_core.events;

import baubles.api.BaublesApi;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.ThaumcraftApi;
import static thaumcraft.api.ThaumcraftMaterials.*;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.items.consumables.ItemSanitySoap;
import thaumcraft.common.lib.potions.PotionWarpWard;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = RenaissanceCore.MODID)
public class CursedEvents {
    static Random rand = new Random();
    public static final Map<EntityPlayer, AxisAlignedBB> CURSED_AURA = new WeakHashMap<>();

    public static boolean hasThaumiumCursed(EntityPlayer player) {
        return BaublesApi.isBaubleEquipped(player, RCItems.dice12) != -1;
    }

    public static AxisAlignedBB getBoundingBoxAroundEntity(final Entity entity, final double radius) {
        return new AxisAlignedBB(entity.posX - radius, entity.posY - radius, entity.posZ - radius, entity.posX + radius, entity.posY + radius, entity.posZ + radius);
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

    public static boolean isMaterialThaumium(Item itemTool) {
        if (itemTool instanceof ItemTool) {
            return ((ItemTool) itemTool).toolMaterial.equals(TOOLMAT_ELEMENTAL) || ((ItemTool) itemTool).toolMaterial.equals(TOOLMAT_VOID) || ((ItemTool) itemTool).toolMaterial.equals(TOOLMAT_THAUMIUM);
        } else if (itemTool instanceof ItemSword)
            return ((ItemSword) itemTool).material.equals(TOOLMAT_ELEMENTAL) || ((ItemSword) itemTool).material.equals(TOOLMAT_VOID) || ((ItemSword) itemTool).material.equals(TOOLMAT_THAUMIUM);
        else if (itemTool instanceof ItemArmor)
            return ((ItemArmor) itemTool).getArmorMaterial().equals(ARMORMAT_THAUMIUM) || ((ItemArmor) itemTool).getArmorMaterial().equals(ARMORMAT_VOID) || ((ItemArmor) itemTool).getArmorMaterial().equals(ARMORMAT_FORTRESS) || ((ItemArmor) itemTool).getArmorMaterial().equals(ARMORMAT_VOIDROBE) || ((ItemArmor) itemTool).getArmorMaterial().equals(ARMORMAT_SPECIAL);
        else
            return false;
    }

    private static Map<String, NonNullList<ItemStack>> invMaps(EntityPlayer player) {
        Map<String, NonNullList<ItemStack>> inventories = new HashMap<>();
        inventories.put("inv1", player.inventory.armorInventory);
        inventories.put("inv2", player.inventory.mainInventory);
        inventories.put("inv3", player.inventory.offHandInventory);
        return inventories;
    }

    //Config warp amount and flux amount
    @SubscribeEvent
    public static void onPlayerTick(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (hasThaumiumCursed(player)) {
            if (rand.nextInt(2) == 0)
                AuraHelper.polluteAura(player.world, player.getPosition(), 23, true);
            else
                ThaumcraftApi.internalMethods.addWarpToPlayer(player, 23, IPlayerWarp.EnumWarpType.TEMPORARY);
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

    @SubscribeEvent
    public static void onEntitySpawn(LivingSpawnEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (event.getWorld().isRemote)
            return;

        if (entity instanceof EntityBat) {
            if (CURSED_AURA.values().stream().anyMatch(entity.getEntityBoundingBox()::intersects)) {
                EntityFireBat fb = new EntityFireBat(event.getWorld());
                fb.setPosition(event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ);
                event.getWorld().spawnEntity(fb);
                event.getEntityLiving().setDead();

            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void miningBlocks(PlayerEvent.BreakSpeed event) {
        float correctedSpeed = event.getOriginalSpeed();
        float miningBoost = 1.0F;
        if (CursedEvents.hasThaumiumCursed(event.getEntityPlayer()) && !isMaterialThaumium(event.getEntityPlayer())) {
            miningBoost -= 0.5F;
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

            if (!isMaterialThaumium(player) && hasThaumiumCursed(player)) {
                event.setAmount(event.getAmount() / 2);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            if (hasThaumiumCursed(player)) {
                if (AuraHelper.getVis(player.world, player.getPosition()) / AuraHelper.getAuraBase(player.world, player.getPosition()) < 1) {
                    event.setAmount(0);
                } else
                    AuraHelper.drainVis(player.world, player.getPosition(), event.getAmount() * 10, false);
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
                if (warp.get(IPlayerWarp.EnumWarpType.PERMANENT) > amt) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, -amt, IPlayerWarp.EnumWarpType.PERMANENT);
                } else
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, -warp.get(IPlayerWarp.EnumWarpType.PERMANENT), IPlayerWarp.EnumWarpType.PERMANENT);

                if (warp.get(IPlayerWarp.EnumWarpType.NORMAL) > amt * 2) {
                    //todo config for "/2"
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, -amt * 2, IPlayerWarp.EnumWarpType.NORMAL);
                } else
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, -warp.get(IPlayerWarp.EnumWarpType.NORMAL), IPlayerWarp.EnumWarpType.NORMAL);

                if (warp.get(IPlayerWarp.EnumWarpType.TEMPORARY) > 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, -warp.get(IPlayerWarp.EnumWarpType.TEMPORARY), IPlayerWarp.EnumWarpType.TEMPORARY);
                }
            }

            event.setCanceled(true);
        }

    }
}
