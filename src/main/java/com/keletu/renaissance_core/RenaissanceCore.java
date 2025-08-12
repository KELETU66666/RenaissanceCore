package com.keletu.renaissance_core;

import com.keletu.renaissance_core.blocks.RCBlocks;
import com.keletu.renaissance_core.capability.*;
import com.keletu.renaissance_core.container.GUIHandler;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.events.KeepDiceEvent;
import com.keletu.renaissance_core.events.ZapHandler;
import com.keletu.renaissance_core.foci.FocusMediumImpulse;
import com.keletu.renaissance_core.foci.FocusPositiveBurst;
import com.keletu.renaissance_core.foci.FocusReflection;
import com.keletu.renaissance_core.items.RCItems;
import com.keletu.renaissance_core.module.botania.EntropinnyumTNTHandler;
import com.keletu.renaissance_core.module.botania.SubtileRegisterOverride;
import com.keletu.renaissance_core.packet.*;
import com.keletu.renaissance_core.proxy.CommonProxy;
import com.keletu.renaissance_core.tweaks.InitBotaniaRecipes;
import com.keletu.renaissance_core.util.ChainedRiftRecipe;
import com.keletu.renaissance_core.util.PolishRecipe;
import com.keletu.renaissance_core.util.ScanEntities;
import fr.wind_blade.isorropia.common.IsorropiaAPI;
import fr.wind_blade.isorropia.common.research.recipes.SpecieCurativeInfusionRecipe;
import keletu.forbiddenmagicre.init.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.crafting.*;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ScanEntity;
import thaumcraft.api.research.ScanItem;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.common.golems.client.PartModelHauler;
import thecodex6824.thaumicaugmentation.api.TABlocks;
import thecodex6824.thaumicaugmentation.api.TAItems;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Mod(modid = RenaissanceCore.MODID, name = RenaissanceCore.NAME, version = RenaissanceCore.VERSION, acceptedMinecraftVersions = RenaissanceCore.MC_VERSION,
        dependencies = "required-after:baubles@[1.5.2, ); required-after:thaumcraft@[6.1.BETA26]; required-after:thaumicaugmentation; required-after:fermiumbooter; after:thaumicwonders; after:isorropia; after:botania")
public class RenaissanceCore {
    public static final String MODID = "renaissance_core";
    public static final String NAME = "Renaissance Core";
    public static final String VERSION = "1.12.1";
    public static final String MC_VERSION = "[1.12.2]";
    public static final EnumGolemTrait GREEDY = EnumHelper.addEnum(EnumGolemTrait.class, "GREEDY", new Class[]{ResourceLocation.class}, new ResourceLocation(MODID, "textures/misc/tag_cash.png"));
    public static final EnumGolemTrait BUBBLE = EnumHelper.addEnum(EnumGolemTrait.class, "BUBBLE", new Class[]{ResourceLocation.class}, new ResourceLocation(MODID, "textures/misc/tag_bubble.png"));
    @SidedProxy(clientSide = "com.keletu.renaissance_core.proxy.ClientProxy", serverSide = "com.keletu.renaissance_core.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper packetInstance;
    public static CreativeTabs tabRenaissanceCore = new CreativeTabs("tabRenaissanceCore") {
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(RCItems.dice12);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(RenaissanceCore.MODID, new GUIHandler());

        CapabilityManager.INSTANCE.register(IT12Capability.class, new RCCapabilities.CapabilityCanPickoffT12(), () -> new T12Capability(null));
        CapabilityManager.INSTANCE.register(ICapConcilium.class, new RCCapabilities.CapThaumicConcilium(), () -> new CapThaumicConcilium(null));

        packetInstance = NetworkRegistry.INSTANCE.newSimpleChannel("RenaissanceChannel");
        packetInstance.registerMessage(PacketZap.Handler.class, PacketZap.class, 0, Side.SERVER);
        packetInstance.registerMessage(PacketZapParticle.Handler.class, PacketZapParticle.class, 1, Side.CLIENT);
        packetInstance.registerMessage(PacketOpenPackGui.Handler.class, PacketOpenPackGui.class, 2, Side.SERVER);
        packetInstance.registerMessage(PacketSyncCapability.Handler.class, PacketSyncCapability.class, 3, Side.CLIENT);
        packetInstance.registerMessage(PacketEnslave.class, PacketEnslave.class, 4, Side.CLIENT);
        packetInstance.registerMessage(PacketMakeHole.class, PacketMakeHole.class, 5, Side.CLIENT);
        packetInstance.registerMessage(PacketFXBloodsplosion.class, PacketFXBloodsplosion.class, 6, Side.CLIENT);
        packetInstance.registerMessage(PacketFXLightning.class, PacketFXLightning.class, 7, Side.CLIENT);
        packetInstance.registerMessage(PacketTogglePontifexRobe.class, PacketTogglePontifexRobe.class, 8, Side.SERVER);

        if (event.getSide().isClient()) {
            OBJLoader.INSTANCE.addDomain(MODID);
            ZapHandler.registerKeybinds();
        }

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        int id = 0;
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_chicken"), EntityTaintChicken.class, "TaintedChicken", id++, MODID, 64, 3, true, 10618530, 12632256);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_rabbit"), EntityTaintRabbit.class, "TaintedRabbit", id++, MODID, 64, 3, true, 10618530, 15777984);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_cow"), EntityTaintCow.class, "TaintedCow", id++, MODID, 64, 3, true, 10618530, 8272443);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_creeper"), EntityTaintCreeper.class, "TaintedCreeper", id++, MODID, 64, 3, true, 10618530, 65280);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_pig"), EntityTaintPig.class, "TaintedPig", id++, MODID, 64, 3, true, 10618530, 15702511);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_sheep"), EntityTaintSheep.class, "TaintedSheep", id++, MODID, 64, 3, true, 10618530, 8421504);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_villager"), EntityTaintVillager.class, "TaintedVillager", id++, MODID, 64, 3, true, 10618530, 65535);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "vengeful_golem"), EntityVengefulGolem.class, "VengefulGolem", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "dissolved"), EntityDissolved.class, "Dissolved", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "upcoming_hole"), EntityUpcomingHole.class, "UpcomingHoleEntity", id++, MODID, 64, 1, false);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "quicksilver_elemental"), EntityQuicksilverElemental.class, "QuicksilverElemental", id++, MODID, 64, 1, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "overanimated"), EntityOveranimated.class, "Overanimated", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "thaum_gib"), EntityThaumGib.class, "ThaumGib", id++, MODID, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "thaumaturge"), EntityThaumaturge.class, "Thaumaturge", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "strayed_mirror"), EntityStrayedMirror.class, "StrayedMirror", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "samurai"), EntitySamurai.class, "Samurai", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "crimson_pontifex"), EntityCrimsonPontifex.class, "CrimsonPontifex", id++, MODID, 64, 3, true, 0x00FFFF, 0x111111);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "concentrated_warp_charge"), EntityConcentratedWarpCharge.class, "ConcentratedWarpChargeEntity", id++, MODID, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "crimson_paladin"), EntityCrimsonPaladin.class, "CrimsonPaladin", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "ethereal_shackles"), EntityEtherealShackles.class, "EtherealShacklesEntity", id++, MODID, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "mad_thaumaturge"), EntityMadThaumaturge.class, "MadThaumaturge", id++, MODID, 64, 1, true, 0x00FFFF, 0x111111);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "golem_bydlo"), EntityGolemBydlo.class, "GolemBydlo", id++, MODID, 64, 3, true, 0x00FFFF, 0x555555);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "bottle_thick_taint"), EntityBottleOfThickTaint.class, "BottleOfThickTaintEntity", id++, MODID, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "positive_burst_orb"), EntityPositiveBurstOrb.class, "EntityPositiveBurstOrb", id++, MODID, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "compressed_blast"), EntityCompressedBlast.class, "CompressedBlastEntity", id++, MODID, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "hex_rift"), EntityHexRift.class, "EntityHexRift", id++, MODID, 64, 1, true);

        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".MadThaumaturge", new AspectList().add(Aspect.MAN, 4).add(Aspect.MIND, 4).add(Aspect.ELDRITCH, 8));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".CrimsonPaladin", new AspectList().add(Aspect.MAN, 4).add(Aspect.LIFE, 4).add(Aspect.ELDRITCH, 4).add(Aspect.MAGIC, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".CrimsonPontifex", new AspectList().add(Aspect.SOUL, 16).add(Aspect.LIFE, 16).add(Aspect.MAGIC, 16));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".Thaumaturge", new AspectList().add(Aspect.MAN, 4).add(Aspect.MAGIC, 4).add(Aspect.AURA, 4).add(Aspect.ORDER, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".ThaumGib", new AspectList().add(Aspect.MAN, 4).add(Aspect.MAGIC, 4).add(Aspect.LIFE, 4).add(Aspect.ENTROPY, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".Overanimated", new AspectList().add(Aspect.MAN, 4).add(Aspect.MAGIC, 4).add(Aspect.LIFE, 4).add(Aspect.ELDRITCH, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".QuicksilverElemental", new AspectList().add(Aspect.MAN, 4).add(Aspect.LIFE, 4).add(Aspect.METAL, 4).add(Aspect.EXCHANGE, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".VengefulGolem", new AspectList().add(Aspect.MAN, 4).add(Aspect.CRAFT, 4).add(IsorropiaAPI.PRIDE, 4).add(Aspect.MOTION, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".Dissolved", new AspectList().add(Aspect.MAN, 4).add(Aspect.VOID, 4).add(Aspect.ELDRITCH, 4).add(Aspect.ALCHEMY, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".StrayedMirror", new AspectList().add(Aspect.MAN, 4).add(Aspect.EXCHANGE, 4)/*.add(Aspect.SPATIO, 4)*/.add(Aspect.MOTION, 4));

        FocusEngine.registerElement(FocusPositiveBurst.class, new ResourceLocation(MODID, "textures/foci_icons/positive_burst.png"), 10854849);
        FocusEngine.registerElement(FocusMediumImpulse.class, new ResourceLocation(MODID, "textures/foci_icons/impulse.png"), 0xFF3333);
        FocusEngine.registerElement(FocusReflection.class, new ResourceLocation(MODID, "textures/foci_icons/reflection.png"), 0x000000);

        ScanningManager.addScannableThing(new ScanEntities("!SpecialCreatures", Arrays.asList(EntityOveranimated.class, EntityDissolved.class, EntityVengefulGolem.class, EntityQuicksilverElemental.class, EntityStrayedMirror.class, EntitySamurai.class)));
        ScanningManager.addScannableThing(new ScanEntity("!Thaumaturge", EntityThaumaturge.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!MadThaumaturge", EntityMadThaumaturge.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!OverAnimated", EntityOveranimated.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!CrimsonPaladin", EntityCrimsonPaladin.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Dissolved", EntityDissolved.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!VengefulGolem", EntityVengefulGolem.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!QuicksilverElemental", EntityQuicksilverElemental.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!StrayedMirror", EntityStrayedMirror.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Samurai", EntitySamurai.class, true));
        ScanningManager.addScannableThing(new ScanItem("f_crimsonnotes", new ItemStack(RCItems.research_notes_crimson)));

        if (Loader.isModLoaded("botania")) {
            MinecraftForge.EVENT_BUS.register(new EntropinnyumTNTHandler());
            SubtileRegisterOverride override = new SubtileRegisterOverride();
            if (override.successInject)
                override.reRegisterSubtile();
            if (ConfigsRC.CHANGE_BOTANIA_RECIPE) {
                ResearchCategories.registerCategory("BOTANY", "HEDGEALCHEMY", null, new ResourceLocation("botania", "textures/items/grassseeds0.png"), new ResourceLocation(RenaissanceCore.MODID, "textures/misc/tab_botany.jpg"));
                ThaumcraftApi.registerResearchLocation(new ResourceLocation(MODID, "research/botany.json"));
            }
        }
        ResearchCategories.registerCategory("RENAISSANCE_CONCILIUM", "!SpecialCreatures", null, new ResourceLocation("renaissance_core", "textures/research/r_thaumicconcilium.png"), new ResourceLocation(RenaissanceCore.MODID, "textures/misc/tab_renaissance.jpg"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(MODID, "research/research.json"));

        MinecraftForge.EVENT_BUS.register(new KeepDiceEvent());

        proxy.regRenderer();
        proxy.addRenderLayers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (ConfigsRC.CHANGE_BOTANIA_RECIPE && Loader.isModLoaded("botania"))
            InitBotaniaRecipes.replaceWithVanillaRecipes();

        List<Biome> biomes = BiomeProvider.allowedBiomes;
        Iterator<Biome> i$ = biomes.iterator();

        while (i$.hasNext()) {
            Biome bgb = i$.next();
            if (!bgb.getSpawnableList(EnumCreatureType.MONSTER).isEmpty() & bgb.getSpawnableList(EnumCreatureType.MONSTER).size() > 0) {
                EntityRegistry.addSpawn(EntityDissolved.class, ConfigsRC.dissolvedSpawnChance, 1, 2, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(EntityQuicksilverElemental.class, ConfigsRC.quicksilverElementalSpawnChance, 1, 2, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(EntitySamurai.class, ConfigsRC.paranoidWarriorSpawnChance, 3, 5, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(EntityVengefulGolem.class, ConfigsRC.vengefulGolemSpawnChance, 1, 2, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(EntityOveranimated.class, ConfigsRC.overanimatedSpawnChance, 2, 3, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(EntityStrayedMirror.class, ConfigsRC.strayedMirrorSpawnChance, 1, 2, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(EntityMadThaumaturge.class, ConfigsRC.madThaumaturgeSpawnChance, 2, 3, EnumCreatureType.MONSTER, bgb);
            }
            if (!bgb.getSpawnableList(EnumCreatureType.CREATURE).isEmpty() & bgb.getSpawnableList(EnumCreatureType.CREATURE).size() > 0) {
                EntityRegistry.addSpawn(EntityThaumaturge.class, ConfigsRC.thaumaturgeSpawnChance, 1, 3, EnumCreatureType.CREATURE, bgb);
            }

        }

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("trk:lime_powder"),
                new ShapelessArcaneRecipe(new ResourceLocation(""), "ARCANE_LIME_POWDER",
                        10,
                        new AspectList(),
                        new ItemStack(RCItems.arcane_lime_powder, 4),
                        new Object[]{
                                new ItemStack(ItemsTC.quicksilver),
                                new ItemStack(ItemsTC.salisMundus),
                                new ItemStack(Items.CLAY_BALL),
                                new ItemStack(ItemsTC.tallow)}));

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("trk:dest_crystal"), new ShapelessArcaneRecipe(new ResourceLocation(""), "!DestCrystal", 200,
                new AspectList().add(Aspect.ORDER, 2).add(Aspect.ENTROPY, 2).add(Aspect.FIRE, 2).add(Aspect.WATER, 2).add(Aspect.AIR, 2).add(Aspect.EARTH, 2),
                new ItemStack(RCItems.destabilizedCrystal),
                new Object[]{new ItemStack(ItemsTC.crystalEssence),
                        new ItemStack(ItemsTC.crystalEssence),
                        new ItemStack(ItemsTC.crystalEssence),
                        new ItemStack(ItemsTC.crystalEssence),
                        new ItemStack(ItemsTC.crystalEssence),
                        new ItemStack(ItemsTC.crystalEssence),
                        new ItemStack(ItemsTC.crystalEssence),
                        new ItemStack(ItemsTC.crystalEssence),
                        new ItemStack(ItemsTC.crystalEssence)}));


        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("trk:crimson_hammer"),
                new InfusionRecipe("PONTIFEXHAMMER",
                        new ItemStack(RCItems.molot),
                        7,
                        new AspectList().add(IsorropiaAPI.HUNGER, 30).add(Aspect.METAL, 250).add(Aspect.EXCHANGE, 30).add(IsorropiaAPI.PRIDE, 125).add(Aspect.AVERSION, 200).add(Aspect.LIFE, 200),
                        new ItemStack(ItemsTC.crimsonBlade),
                        new ItemStack(TAItems.FOCUS_ANCIENT),
                        "blockGold",
                        "ingotVoid",
                        "plateVoid",
                        new ItemStack(ItemsTC.causalityCollapser),
                        new ItemStack(BlocksTC.logGreatwood),
                        new ItemStack(ItemsTC.causalityCollapser),
                        "plateVoid",
                        "ingotVoid",
                        "blockGold"));

        ItemStack windingchest = new ItemStack(RCItems.runic_chestplate);
        windingchest.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 100));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("trk:runic_winding_chest"), new InfusionRecipe("!RunicWindings",
                windingchest,
                10, new AspectList().add(Aspect.ELDRITCH, 100).add(Aspect.PROTECT, 200).add(Aspect.MAGIC, 75).add(Aspect.ENERGY, 100).add(Aspect.MIND, 200),
                new ItemStack(Items.PAPER),
                new ItemStack(ItemsTC.visResonator),
                "nitor",
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ModItems.CRYSTAL_WELL),
                new ItemStack(ItemsTC.salisMundus),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ItemsTC.morphicResonator)));

        ItemStack windinglegs = new ItemStack(RCItems.runic_leggings);
        windinglegs.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 100));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("trk:runic_winding_legs"), new InfusionRecipe("!RunicWindings",
                windinglegs, 10, new AspectList().add(Aspect.ELDRITCH, 100).add(Aspect.PROTECT, 200).add(Aspect.MAGIC, 75).add(Aspect.ENERGY, 100).add(Aspect.MIND, 200),
                new ItemStack(Items.PAPER),
                new ItemStack(ItemsTC.visResonator),
                "nitor",
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ModItems.CRYSTAL_WELL),
                new ItemStack(ItemsTC.salisMundus),
                new ItemStack(Items.DIAMOND),
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ItemsTC.celestialNotes, 1, 32767),
                new ItemStack(ItemsTC.morphicResonator)));

        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("trk:bottle_thick_taint"), new CrucibleRecipe("!BottleThickTaint",
                new ItemStack(RCItems.bottle_of_thick_taint),
                new ItemStack(ItemsTC.bottleTaint),
                new AspectList().add(Aspect.FLUX, 50).add(Aspect.ALCHEMY, 50).add(Aspect.CRYSTAL, 50)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("trk:vis_conductor"), new InfusionRecipe("!VisConductor",
                new ItemStack(RCItems.vis_conductor), 20,
                new AspectList().add(Aspect.EXCHANGE, 100).add(Aspect.ENERGY, 100).add(Aspect.MAGIC, 200).add(Aspect.MAN, 300).add(Aspect.TOOL, 50),
                new ItemStack(ItemsTC.resonator),
                new ItemStack(BlocksTC.infusionMatrix),
                new ItemStack(Items.QUARTZ),
                new ItemStack(ItemsTC.visResonator),
                new ItemStack(BlocksTC.rechargePedestal),
                new ItemStack(BlocksTC.infusionMatrix),
                new ItemStack(Items.QUARTZ),
                new ItemStack(ItemsTC.visResonator),
                new ItemStack(BlocksTC.rechargePedestal)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("trk:dump_jackboots"), new InfusionRecipe("DUMPJACKBOOTS", new ItemStack(RCItems.dump_jackboots), 5,
                new AspectList().add(Aspect.EARTH, 100).add(Aspect.PROTECT, 30).add(IsorropiaAPI.SLOTH, 100).add(Aspect.MECHANISM, 50),
                new ItemStack(ItemsTC.travellerBoots, 1, 32767),
                new ItemStack(Blocks.PISTON),
                new ItemStack(ItemsTC.thaumiumShovel, 1, 32767),
                new ItemStack(ModItems.ResourceNS, 1, 4),
                new ItemStack(BlocksTC.crystalEarth),
                new ItemStack(ModItems.ResourceNS, 1, 4),
                new ItemStack(ItemsTC.thaumiumShovel, 1, 32767),
                new ItemStack(TABlocks.ITEM_GRATE),
                new ItemStack(ItemsTC.thaumiumShovel, 1, 32767),
                new ItemStack(ModItems.ResourceNS, 1, 4),
                new ItemStack(BlocksTC.crystalAir),
                new ItemStack(ModItems.ResourceNS, 1, 4),
                new ItemStack(ItemsTC.thaumiumShovel, 1, 32767)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("trk:tight_belt"), new InfusionRecipe("TIGHTBELT",
                new ItemStack(RCItems.tight_belt), 8,
                new AspectList().add(Aspect.FLUX, 200).add(Aspect.PROTECT, 200).add(Aspect.ALCHEMY, 300).add(Aspect.MAN, 30),
                new ItemStack(TAItems.THAUMOSTATIC_HARNESS_AUGMENT, 1, 1),
                new ItemStack(RCItems.bottle_of_thick_taint),
                new ItemStack(RCItems.bottle_of_thick_taint),
                ThaumcraftApiHelper.makeCrystal(Aspect.FLUX),
                new ItemStack(ItemsTC.causalityCollapser),
                ThaumcraftApiHelper.makeCrystal(Aspect.FLUX),
                new ItemStack(RCItems.bottle_of_thick_taint),
                new ItemStack(RCItems.bottle_of_thick_taint),
                new ItemStack(RCItems.bottle_of_thick_taint),
                ThaumcraftApiHelper.makeCrystal(Aspect.FLUX),
                new ItemStack(ItemsTC.causalityCollapser),
                ThaumcraftApiHelper.makeCrystal(Aspect.FLUX),
                new ItemStack(RCItems.bottle_of_thick_taint)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("trk:burdening_amulet"), new InfusionRecipe("BURDENINGAMULET",
                new ItemStack(RCItems.burdening_amulet), 8,
                new AspectList().add(Aspect.PROTECT, 200).add(Aspect.SENSES, 100).add(Aspect.DEATH, 200).add(Aspect.ELDRITCH, 300).add(IsorropiaAPI.ENVY, 200),
                new ItemStack(ItemsTC.amuletVis, 1, 0),
                new ItemStack(TAItems.FRACTURE_LOCATOR),
                new ItemStack(ModItems.ResourceNS, 1, 1),
                new ItemStack(ModItems.ResourceNS, 1, 1),
                new ItemStack(Blocks.GOLD_BLOCK),
                new ItemStack(ModItems.ResourceNS, 1, 1),
                new ItemStack(ModItems.ResourceNS, 1, 1),
                new ItemStack(Items.ENDER_EYE),
                new ItemStack(ModItems.ResourceNS, 1, 1),
                new ItemStack(ModItems.ResourceNS, 1, 1),
                new ItemStack(Blocks.OBSIDIAN),
                new ItemStack(ModItems.ResourceNS, 1, 1),
                new ItemStack(ModItems.ResourceNS, 1, 1)));

        IsorropiaAPI.registerCreatureInfusionRecipe(new ResourceLocation("renaissance_core", "golem_bydlo"),
                ((SpecieCurativeInfusionRecipe.Builder) new SpecieCurativeInfusionRecipe.Builder()
                        .withAspects(new AspectList().add(Aspect.METAL, 100).add(Aspect.ENTROPY, 100).add(Aspect.ENERGY, 50).add(IsorropiaAPI.PRIDE, 50))
                        .withComponents(
                                Ingredient.fromStacks(new ItemStack(ItemsTC.mind, 1, 0)),
                                Ingredient.fromItem(ItemsTC.salisMundus),
                                Ingredient.fromStacks(OreDictionary.getOres("blockThaumium").get(0)),
                                Ingredient.fromItem(ItemsTC.tallow),
                                Ingredient.fromStacks(OreDictionary.getOres("blockThaumium").get(0)),
                                Ingredient.fromStacks(new ItemStack(ItemsTC.mind, 1, 0)),
                                Ingredient.fromItem(ItemsTC.mechanismComplex),
                                Ingredient.fromStacks(new ItemStack(ItemsTC.mind, 1, 0)),
                                Ingredient.fromStacks(OreDictionary.getOres("blockThaumium").get(0)),
                                Ingredient.fromItem(ItemsTC.tallow),
                                Ingredient.fromStacks(OreDictionary.getOres("blockThaumium").get(0)),
                                Ingredient.fromItem(ItemsTC.salisMundus))
                        .withInstability(8)
                        .withKnowledgeRequirement("GOLEMBYDLO"))
                        .withResult(EntityGolemBydlo.class)
                        .withPredicate(entity -> entity.getClass() == EntityMadThaumaturge.class)
                        .withFakeIngredients(Ingredient.fromStacks(new ItemStack(RCItems.item_icon, 1, 1)), new ItemStack(RCItems.item_icon, 1, 2))
                        .build());

        GolemHead.register(new GolemHead("FORAGE", new String[]{"PECHGOLEM"}, new ResourceLocation(MODID, "textures/research/r_pech.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_forage.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadNormal)}, new EnumGolemTrait[]{RenaissanceCore.GREEDY}));
        GolemHead.register(new GolemHead("STALKER", new String[]{"PECHGOLEM"}, new ResourceLocation(MODID, "textures/research/r_pech_stalker.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_stalker.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadHunter)}, new EnumGolemTrait[]{EnumGolemTrait.DEFT}));
        GolemHead.register(new GolemHead("THAUMIUM", new String[]{"PECHGOLEM"}, new ResourceLocation(MODID, "textures/research/r_pech_thaum.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_thaum.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadThaumaturge)}, new EnumGolemTrait[]{EnumGolemTrait.SMART}));

        GolemAddon.register(new GolemAddon("BUBBLE_ARMOR", new String[]{"GOLEMWRAP"}, new ResourceLocation(MODID, "textures/research/bubble_wrap_item.png"), new PartModelHauler(new ResourceLocation(MODID, "models/obj/bubble_wrap.obj"), new ResourceLocation(MODID, "textures/models/entity/bubble_wrap.png"), PartModel.EnumAttachPoint.BODY), new Object[]{new ItemStack(Blocks.WOOL), new ItemStack(Items.PAPER, 6)}, new EnumGolemTrait[]{RenaissanceCore.BUBBLE}));

        PolishRecipe.addPolishmentRecipe(new ItemStack(RCItems.dump_jackboots), new AspectList().add(Aspect.MOTION, 100));
        PolishRecipe.addPolishmentRecipe(new ItemStack(RCItems.burdening_amulet), new AspectList().add(Aspect.SENSES, 50));
        PolishRecipe.addPolishmentRecipe(new ItemStack(RCItems.tight_belt), new AspectList().add(Aspect.FLIGHT, 100));

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("trk", "vis_condenser"), new ShapedArcaneRecipe(new ResourceLocation(""),
                "VISCONDENSER", 200,
                new AspectList().add(Aspect.ENTROPY, 5).add(Aspect.AIR, 3).add(Aspect.FIRE, 3).add(Aspect.ORDER, 1),
                RCBlocks.vis_condenser,
                "ISI",
                "BFB",
                "BCB",
                'I', "ingotGold",
                'B', "blockGold",
                'S', new ItemStack(BlocksTC.stoneArcane),
                'F', RCItems.vis_conductor,
                'C', new ItemStack(BlocksTC.rechargePedestal)));

        ItemStack hood = new ItemStack(RCItems.pontifex_hood);
        hood.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 5));
        ChainedRiftRecipe.addRiftRecipe(new ResourceLocation("trk:pontifex_hood"), ChainedRiftRecipe.addChainedRiftRecipe("CRIMSONPONTIFEX", hood, new ItemStack(ItemsTC.crimsonRobeHelm), new AspectList().add(IsorropiaAPI.FLESH, 100).add(Aspect.EXCHANGE, 200).add(IsorropiaAPI.PRIDE, 200).add(Aspect.PROTECT, 300).add(Aspect.VOID, 100).add(Aspect.LIFE, 100)));

        ItemStack hood1 = new ItemStack(RCItems.pontifex_robe);
        hood1.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 5));
        ChainedRiftRecipe.addRiftRecipe(new ResourceLocation("trk:pontifex_robe"), ChainedRiftRecipe.addChainedRiftRecipe("CRIMSONPONTIFEX", hood1, new ItemStack(ItemsTC.crimsonRobeChest), new AspectList().add(IsorropiaAPI.FLESH, 100).add(Aspect.EXCHANGE, 200).add(IsorropiaAPI.PRIDE, 200).add(Aspect.PROTECT, 300).add(Aspect.VOID, 100).add(Aspect.LIFE, 100)));

        ItemStack hood2 = new ItemStack(RCItems.pontifex_legs);
        hood2.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 5));
        ChainedRiftRecipe.addRiftRecipe(new ResourceLocation("trk:pontifex_legs"), ChainedRiftRecipe.addChainedRiftRecipe("CRIMSONPONTIFEX", hood2, new ItemStack(ItemsTC.crimsonRobeLegs), new AspectList().add(IsorropiaAPI.FLESH, 100).add(Aspect.EXCHANGE, 200).add(IsorropiaAPI.PRIDE, 200).add(Aspect.PROTECT, 300).add(Aspect.VOID, 100).add(Aspect.LIFE, 100)));

        ItemStack hood3 = new ItemStack(RCItems.pontifex_boots);
        hood3.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 5));
        ChainedRiftRecipe.addRiftRecipe(new ResourceLocation("trk:pontifex_boots"), ChainedRiftRecipe.addChainedRiftRecipe("CRIMSONPONTIFEX", hood3, new ItemStack(ItemsTC.crimsonBoots), new AspectList().add(IsorropiaAPI.FLESH, 100).add(Aspect.EXCHANGE, 200).add(IsorropiaAPI.PRIDE, 200).add(Aspect.PROTECT, 300).add(Aspect.VOID, 100).add(Aspect.LIFE, 100)));

        Part N = new Part(Blocks.BEDROCK, RCBlocks.hex_of_predictability);
        Part O = new Part(BlocksTC.stoneAncient, Blocks.AIR);
        Part[][][] HexOfPredictabilityPlaceHolder = {
                {
                        {O, O, O},
                        {O, N, O},
                        {O, O, O}
                }
        };

        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("trk", "hex_of_predictability"), new ThaumcraftApi.BluePrint("HEXOFPREDICTABILITY", new ItemStack(RCItems.hexOfPredictor), HexOfPredictabilityPlaceHolder, new ItemStack(Item.getItemFromBlock(Blocks.BEDROCK)), new ItemStack(Item.getItemFromBlock(BlocksTC.stoneAncient), 8)));

    }
}
