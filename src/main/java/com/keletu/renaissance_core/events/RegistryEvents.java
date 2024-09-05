package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.blocks.tile.TileEtherealBloom;
import com.keletu.renaissance_core.blocks.tile.TileManaPod;
import com.keletu.renaissance_core.items.RFItems;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.golems.EntityThaumcraftGolem;

import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber
public class RegistryEvents {
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
        event.getRegistry().registerAll(RFItems.rift_feed);
        event.getRegistry().registerAll(RFItems.arcane_lime_powder);
        event.getRegistry().registerAll(RFItems.etherealBloomItem);

        event.getRegistry().registerAll(RFItems.pechHeadNormal);
        event.getRegistry().registerAll(RFItems.pechHeadHunter);
        event.getRegistry().registerAll(RFItems.pechHeadThaumaturge);

        event.getRegistry().registerAll(RFItems.mana_bean);
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
                        addDrop(event, new ItemStack(RFItems.pechHeadThaumaturge, 1, 0));
                    else if (((EntityPech) event.getEntityLiving()).getPechType() == 2)
                        addDrop(event, new ItemStack(RFItems.pechHeadHunter, 1, 0));
                    else
                        addDrop(event, new ItemStack(RFItems.pechHeadNormal, 1, 0));

                }
            }
        }
    }

    public static void addDrop(LivingDropsEvent event, ItemStack drop) {
        EntityItem entityitem = new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, drop);
        event.getDrops().add(entityitem);
    }

}
