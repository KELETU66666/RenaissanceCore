package com.keletu.renaissance_core;

import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.events.ChampionEvents;
import com.keletu.renaissance_core.items.ItemManaBean;
import com.keletu.renaissance_core.items.RFItems;
import com.keletu.renaissance_core.module.botania.EntropinnyumTNTHandler;
import com.keletu.renaissance_core.module.botania.SubtileRegisterOverride;
import com.keletu.renaissance_core.village.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
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


@Mod(modid = RenaissanceCore.MODID, name = RenaissanceCore.NAME, version = RenaissanceCore.VERSION, acceptedMinecraftVersions = RenaissanceCore.MC_VERSION,
dependencies="required-after:baubles@[1.5.2, ); required-after:thaumcraft@[6.1.BETA26]; required-after:mixinbooter@[4.2, )")
public class RenaissanceCore
{
    public static final String MODID = "renaissance_core";
    public static final String NAME = "Renaissance Core";
    public static final String VERSION = "1.0.0";
    public static final String MC_VERSION = "[1.12.2]";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        ChampionEvents.infernalMobList();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(MODID, "research/research.json"));

        VillageWizardManager.registerUselessVillager();
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageWizardManager());
        MapGenStructureIO.registerStructureComponent(ComponentWizardTower.class, "TEWizTower");

        VillageBankerManager.registerUselessVillager();
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageBankerManager());
        MapGenStructureIO.registerStructureComponent(ComponentBankerHome.class, "TEBank");
        MinecraftForge.EVENT_BUS.register(new LootHandler());
        MinecraftForge.EVENT_BUS.register(new EntropinnyumTNTHandler());

        if(event.getSide().isClient())
            registerItemColourHandlers();

        SubtileRegisterOverride override = new SubtileRegisterOverride();
        if (override.successInject)
            override.reRegisterSubtile();
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

        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("ftr:ethereal_bloom"),
                new CrucibleRecipe("ETHEREAL_BLOOM", new ItemStack(RFBlocks.ethereal_bloom), BlocksTC.shimmerleaf, new AspectList().add(Aspect.LIGHT, 20).add(Aspect.PLANT, 40).add(Aspect.LIFE, 40).add(Aspect.FLUX, 40)));
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemColourHandlers() {
        IItemColor itemCrystalPlanterColourHandler = (stack, tintIndex) -> {
            Item item = stack.getItem();
            if (item == RFItems.mana_bean) {
                return ((ItemManaBean) item).getColor(stack, tintIndex);
            }
            return 16777215;
        };

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemCrystalPlanterColourHandler, RFItems.mana_bean);

    }
}
