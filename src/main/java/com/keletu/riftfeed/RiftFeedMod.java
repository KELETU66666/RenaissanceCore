package com.keletu.riftfeed;

import com.keletu.riftfeed.blocks.RFBlocks;
import com.keletu.riftfeed.blocks.tile.TileEtherealBloom;
import com.keletu.riftfeed.client.TileEtherealBloomRenderer;
import com.keletu.riftfeed.items.RFItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import vazkii.botania.api.BotaniaAPI;


@Mod(modid = RiftFeedMod.MODID, name = RiftFeedMod.NAME, version = RiftFeedMod.VERSION, acceptedMinecraftVersions = RiftFeedMod.MC_VERSION,
dependencies="required-after:baubles@[1.5.2, ); required-after:thaumcraft@[6.1.BETA26]; required-after:mixinbooter@[4.2, )")
public class RiftFeedMod
{
    public static final String MODID = "riftfeed";
    public static final String NAME = "Renaissance Core";
    public static final String VERSION = "1.0.0";
    public static final String MC_VERSION = "[1.12.2]";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(MODID, "research/research.json"));
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("ftr:rift_feed"),
                new ShapedArcaneRecipe(new ResourceLocation(""), "RIFT_FEED",
                        75,
                        new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1),
                        new ItemStack(RFItems.rift_feed, 4),
                        "VWV",
                        "WCW",
                        "VWV",
                        'V', new ItemStack(ItemsTC.voidSeed),
                        'W', ThaumcraftApiHelper.makeCrystal(Aspect.FLUX),
                        'C', new ItemStack(ItemsTC.nuggets, 1, 10)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("ftr:thaumic_rune"),
                new InfusionRecipe(/*"RUNE"*/"", new ItemStack(RFBlocks.thaumic_rune, 1), 2, (new AspectList()).add(Aspect.MAGIC, 10).add(Aspect.LIFE, 10).add(Aspect.ORDER, 10), new ItemStack(Blocks.STONE), "ingotThaumium", new ItemStack(RFItems.thaumic_slate), "ingotThaumium", new ItemStack(RFItems.thaumic_slate)));

        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("ftr:thaumic_slate"),
                new CrucibleRecipe(/*"RUNE"*/"", new ItemStack(RFItems.thaumic_slate), Blocks.STONE, new AspectList().add(Aspect.MAGIC, 5).add(Aspect.EARTH, 5)));

        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("ftr:ethereal_bloom"),
                new CrucibleRecipe("ETHEREAL_BLOOM", new ItemStack(RFBlocks.ethereal_bloom), BlocksTC.shimmerleaf, new AspectList().add(Aspect.LIGHT, 20).add(Aspect.PLANT, 40).add(Aspect.LIFE, 40).add(Aspect.FLUX, 40)));

        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(RFItems.mana_slate, 1), new ItemStack(Blocks.STONE), 3000);
    }
}
