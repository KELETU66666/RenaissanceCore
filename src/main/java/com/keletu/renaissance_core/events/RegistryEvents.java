package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.RCBlocks;
import com.keletu.renaissance_core.blocks.TileDestabilizedCrystal;
import com.keletu.renaissance_core.blocks.TileQuicksilverCrucible;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.items.ItemVisConductor;
import com.keletu.renaissance_core.items.RCItems;
import com.keletu.renaissance_core.packet.PacketFXLightning;
import com.keletu.renaissance_core.util.PolishRecipe;
import fr.wind_blade.isorropia.common.IsorropiaAPI;
import net.minecraft.block.Block;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.crafting.TilePedestal;

import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber
public class RegistryEvents {
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

    @SubscribeEvent
    public static void regBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(RCBlocks.pechHead_normal);
        event.getRegistry().registerAll(RCBlocks.pechHead_hunter);
        event.getRegistry().registerAll(RCBlocks.pechHead_thaumaturge);
        event.getRegistry().registerAll(RCBlocks.full_crucible);
        event.getRegistry().registerAll(RCBlocks.quicksilver_crucible);
        event.getRegistry().registerAll(RCBlocks.destabilized_crystal);

        GameRegistry.registerTileEntity(TileQuicksilverCrucible.class, new ResourceLocation(RenaissanceCore.MODID, "quicksilver_crucible"));
        GameRegistry.registerTileEntity(TileDestabilizedCrystal.class, new ResourceLocation(RenaissanceCore.MODID, "destabilized_crystal"));
    }

    @SubscribeEvent
    public static void regItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(RCItems.arcane_lime_powder);
        event.getRegistry().registerAll(RCItems.dice12);
        event.getRegistry().registerAll(RCItems.pech_backpack);
        event.getRegistry().registerAll(RCItems.elixir);
        event.getRegistry().registerAll(RCItems.pontifex_hood);
        event.getRegistry().registerAll(RCItems.pontifex_robe);
        event.getRegistry().registerAll(RCItems.pontifex_legs);
        event.getRegistry().registerAll(RCItems.pontifex_boots);
        event.getRegistry().registerAll(RCItems.molot);
        event.getRegistry().registerAll(RCItems.crimson_annales);
        event.getRegistry().registerAll(RCItems.research_notes_crimson);
        event.getRegistry().registerAll(RCItems.runic_chestplate);
        event.getRegistry().registerAll(RCItems.runic_leggings);
        event.getRegistry().registerAll(RCItems.bottle_of_thick_taint);
        event.getRegistry().registerAll(RCItems.research_page);
        event.getRegistry().registerAll(RCItems.vis_conductor);
        event.getRegistry().registerAll(RCItems.dump_jackboots);
        event.getRegistry().registerAll(RCItems.tight_belt);
        event.getRegistry().registerAll(RCItems.burdening_amulet);

        event.getRegistry().registerAll(RCItems.item_icon);

        event.getRegistry().registerAll(RCItems.pechHeadNormal);
        event.getRegistry().registerAll(RCItems.pechHeadHunter);
        event.getRegistry().registerAll(RCItems.pechHeadThaumaturge);
        event.getRegistry().registerAll(RCItems.quicksilverCrucible);
        event.getRegistry().registerAll(RCItems.destabilizedCrystal);
    }

    @SubscribeEvent
    public static void registerAspects(AspectRegistryEvent event) {
        AspectEventProxy proxy = event.register;
        proxy.registerComplexObjectTag(new ItemStack(RCItems.pechHeadNormal, 1, 0), new AspectList().add(Aspect.MAN, 20).add(Aspect.DESIRE, 20).add(Aspect.TOOL, 20));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.pechHeadHunter, 1, 0), new AspectList().add(Aspect.MAN, 20).add(Aspect.DESIRE, 20).add(Aspect.AVERSION, 20));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.pechHeadThaumaturge, 1, 0), new AspectList().add(Aspect.MAN, 20).add(Aspect.DESIRE, 20).add(Aspect.MAGIC, 20));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.dice12, 1, 0), new AspectList().add(Aspect.AIR, 66).add(Aspect.WATER, 66).add(Aspect.FIRE, 66).add(Aspect.EARTH, 66).add(Aspect.ENTROPY, 66).add(Aspect.ORDER, 66));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.crimson_annales, 1, 0), new AspectList().add(Aspect.MIND, 30).add(IsorropiaAPI.HUNGER, 30).add(Aspect.ELDRITCH, 30).add(Aspect.MAN, 30));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.research_notes_crimson, 1, 0), new AspectList().add(Aspect.MIND, 66).add(Aspect.ELDRITCH, 66).add(IsorropiaAPI.WRATH, 66));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.quicksilverCrucible, 1, 0), new AspectList().add(Aspect.METAL, 100).add(Aspect.MAGIC, 100).add(Aspect.FIRE, 30).add(IsorropiaAPI.FLESH, 10));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.research_page, 1, 0), new AspectList().add(Aspect.MIND, 30));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.research_page, 1, 1), new AspectList().add(Aspect.MIND, 30));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.research_page, 1, 2), new AspectList().add(Aspect.MIND, 30));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.research_page, 1, 3), new AspectList().add(Aspect.MIND, 30));
        proxy.registerComplexObjectTag(new ItemStack(RCItems.research_page, 1, 4), new AspectList().add(Aspect.MIND, 30));

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
                if (!CursedEvents.hasThaumiumCursed((EntityPlayer) event.getSource().getTrueSource()))
                    addDropWithChance(event, new ItemStack(RCItems.pech_backpack), 3);
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

    @SubscribeEvent
    public static void onInfusion(final PlayerEvent.ItemCraftedEvent event) {
        /*if (event.crafting.getItem() instanceof ICaster && !(event.player instanceof FakePlayer)) {
            NBTTagCompound tag = event.crafting.getTagCompound();
            if (tag != null) {
                String xyl = tag.getString("Xylography");
                if (xyl != null && xyl.equals(" ")) {
                    tag.setString("Xylography", event.player.getName());
                    event.crafting.setTagCompound(tag);
                }
            }
        }*/

        AspectList aspectList = PolishRecipe.getPolishmentRecipe(event.crafting);
        if (aspectList != null) {
            EntityPlayer player = event.player;
            double iX = event.player.posX;
            double iY = event.player.posY + 1;
            double iZ = event.player.posZ;
            boolean found = false;
            for (int yy = -16; yy <= 16; yy++)
                for (int zz = -16; zz <= 16; zz++)
                    for (int xx = -16; xx <= 16; xx++)
                        if (event.player.world.getTileEntity(new BlockPos((int) event.player.posX + xx, (int) event.player.posY + yy, (int) event.player.posZ + zz)) instanceof TileInfusionMatrix) {
                            iX = event.player.posX + xx;
                            iY = event.player.posY + yy;
                            iZ = event.player.posZ + zz;
                            found = true;
                        }
            if (player.getHeldItem(player.getActiveHand()) != null && found) {
                if (player.getHeldItem(player.getActiveHand()).getItem() instanceof ItemVisConductor) {
                    ItemStack wand = player.getHeldItem(player.getActiveHand());
                    NBTTagCompound fociTag = wand.getTagCompound();
                    if (fociTag != null) {
                        if (fociTag.hasKey("blockX")) {
                            int x = fociTag.getInteger("blockX");
                            int y = fociTag.getInteger("blockY");
                            int z = fociTag.getInteger("blockZ");
                            BlockPos pos = new BlockPos(x, y, z);

                            if (player.world.getTileEntity(pos) instanceof TileDestabilizedCrystal) {
                                TileDestabilizedCrystal crystal = (TileDestabilizedCrystal) player.world.getTileEntity(pos);
                                int amount = MathHelper.clamp(fociTag.getInteger("amount") - crystal.capacity, 0, Integer.MAX_VALUE);
                                if (amount >= aspectList.getAmount(aspectList.getAspects()[0]) && crystal.aspect.equalsIgnoreCase(aspectList.getAspects()[0].getTag())) {
                                    if (!player.world.isRemote) {
                                        player.world.setBlockToAir(pos);
                                        player.world.removeTileEntity(pos);
                                        player.world.playSound(null, player.getPosition(), SoundsTC.shock, SoundCategory.BLOCKS, 0.8F, player.world.rand.nextFloat() * 0.1F + 0.9F);
                                        player.stopActiveHand();
                                        int rgb = Aspect.aspects.get(crystal.aspect).getColor();
                                        RenaissanceCore.packetInstance.sendToAllAround(new PacketFXLightning((float) player.posX, (float) (player.posY + 1F), (float) player.posZ, (float) iX, (float) iY, (float) iZ, rgb, 1.0F), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 32.0));
                                        return;
                                    } else {
                                       FXDispatcher.INSTANCE.burst(x, y, z, 2.0F);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!event.player.world.isRemote) {
                event.crafting.shrink(1);

                TileEntity te = event.player.world.getTileEntity(new BlockPos(iX,  iY - 2,  iZ));
                if (te instanceof TilePedestal) {
                    ((TilePedestal) te).setInventorySlotContents(0, ItemStack.EMPTY);
                    player.stopActiveHand();
                }
            }
        }
    }

    public static void addDrop(LivingDropsEvent event, ItemStack drop) {
        EntityItem entityitem = new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, drop);
        event.getDrops().add(entityitem);
    }

    public static void addDropWithChance(LivingDropsEvent event, ItemStack drop, int chance) {
        if (new Random().nextInt(100) < chance) {
            addDrop(event, drop);
        }
    }
}
