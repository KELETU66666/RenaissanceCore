package com.keletu.renaissance_core;

import com.keletu.renaissance_core.capability.IT12Capability;
import com.keletu.renaissance_core.capability.RCCapabilities;
import com.keletu.renaissance_core.capability.T12Capability;
import com.keletu.renaissance_core.container.GUIHandler;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.events.KeepDiceEvent;
import com.keletu.renaissance_core.events.ZapHandler;
import com.keletu.renaissance_core.items.RCItems;
import com.keletu.renaissance_core.module.botania.EntropinnyumTNTHandler;
import com.keletu.renaissance_core.module.botania.SubtileRegisterOverride;
import com.keletu.renaissance_core.packet.*;
import com.keletu.renaissance_core.proxy.CommonProxy;
import com.keletu.renaissance_core.tweaks.InitBotaniaRecipes;
import com.keletu.renaissance_core.util.ScanEntities;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ScanEntity;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.common.golems.client.PartModelHauler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Mod(modid = RenaissanceCore.MODID, name = RenaissanceCore.NAME, version = RenaissanceCore.VERSION, acceptedMinecraftVersions = RenaissanceCore.MC_VERSION,
        dependencies = "required-after:baubles@[1.5.2, ); required-after:thaumcraft@[6.1.BETA26]; required-after:thaumicaugmentation; required-after:mixinbooter@[4.2, ); after:thaumicwonders after:botania")
public class RenaissanceCore {
    public static final String MODID = "renaissance_core";
    public static final String NAME = "Renaissance Core";
    public static final String VERSION = "1.8.0";
    public static final String MC_VERSION = "[1.12.2]";
    public static final EnumGolemTrait GREEDY = EnumHelper.addEnum(EnumGolemTrait.class, "GREEDY", new Class[]{ResourceLocation.class}, new ResourceLocation(MODID, "textures/misc/tag_cash.png"));
    public static final EnumGolemTrait BUBBLE = EnumHelper.addEnum(EnumGolemTrait.class, "BUBBLE", new Class[]{ResourceLocation.class}, new ResourceLocation(MODID, "textures/misc/tag_bubble.png"));
    @SidedProxy(clientSide = "com.keletu.renaissance_core.proxy.ClientProxy", serverSide = "com.keletu.renaissance_core.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper packetInstance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(RenaissanceCore.MODID, new GUIHandler());

        CapabilityManager.INSTANCE.register(IT12Capability.class, new RCCapabilities.CapabilityCanPickoffT12(), () -> new T12Capability(null));

        packetInstance = NetworkRegistry.INSTANCE.newSimpleChannel("RenaissanceChannel");
        packetInstance.registerMessage(PacketZap.Handler.class, PacketZap.class, 0, Side.SERVER);
        packetInstance.registerMessage(PacketZapParticle.Handler.class, PacketZapParticle.class, 1, Side.CLIENT);
        packetInstance.registerMessage(PacketOpenPackGui.Handler.class, PacketOpenPackGui.class, 2, Side.SERVER);
        packetInstance.registerMessage(PacketSyncCapability.Handler.class, PacketSyncCapability.class, 3, Side.CLIENT);
        packetInstance.registerMessage(PacketEnslave.class, PacketEnslave.class, 4, Side.CLIENT);
        packetInstance.registerMessage(PacketMakeHole.class, PacketMakeHole.class, 5, Side.CLIENT);
        packetInstance.registerMessage(PacketFXBloodsplosion.class, PacketFXBloodsplosion.class, 6, Side.CLIENT);
        packetInstance.registerMessage(PacketFXLightning.class, PacketFXLightning.class, 7, Side.CLIENT);

        if (event.getSide().isClient()) {
            OBJLoader.INSTANCE.addDomain(MODID);
            ZapHandler.registerKeybinds();
        }
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
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "dissolved"), Dissolved.class, "Dissolved", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "upcoming_hole"), UpcomingHoleEntity.class, "UpcomingHoleEntity", id++, MODID, 64, 1, false);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "quicksilver_elemental"), QuicksilverElemental.class, "QuicksilverElemental", id++, MODID, 64, 1, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "overanimated"), Overanimated.class, "Overanimated", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "thaum_gib"), ThaumGib.class, "ThaumGib", id++, MODID, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "thaumaturge"), Thaumaturge.class, "Thaumaturge", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "strayed_mirror"), StrayedMirror.class, "StrayedMirror", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "samurai"), Samurai.class, "Samurai", id++, MODID, 64, 3, true, 0x00FFFF, 0x00008B);

        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".Thaumaturge", new AspectList().add(Aspect.MAN, 4).add(Aspect.MAGIC, 4).add(Aspect.AURA, 4).add(Aspect.ORDER, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".ThaumGib", new AspectList().add(Aspect.MAN, 4).add(Aspect.MAGIC, 4).add(Aspect.LIFE, 4).add(Aspect.ENTROPY, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".Overanimated", new AspectList().add(Aspect.MAN, 4).add(Aspect.MAGIC, 4).add(Aspect.LIFE, 4).add(Aspect.ELDRITCH, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".QuicksilverElemental", new AspectList().add(Aspect.MAN, 4).add(Aspect.LIFE, 4).add(Aspect.METAL, 4).add(Aspect.EXCHANGE, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".VengefulGolem", new AspectList().add(Aspect.MAN, 4).add(Aspect.CRAFT, 4)/*.add(DarkAspects.PRIDE, 4)*/.add(Aspect.MOTION, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".Dissolved", new AspectList().add(Aspect.MAN, 4).add(Aspect.VOID, 4).add(Aspect.ELDRITCH, 4).add(Aspect.ALCHEMY, 4));
        ThaumcraftApi.registerEntityTag(RenaissanceCore.MODID + ".StrayedMirror", new AspectList().add(Aspect.MAN, 4).add(Aspect.EXCHANGE, 4)/*.add(Aspect.SPATIO, 4)*/.add(Aspect.MOTION, 4));

        ScanningManager.addScannableThing(new ScanEntities("SPECIAL_CREATURES", Arrays.asList(Overanimated.class, Dissolved.class, EntityVengefulGolem.class, QuicksilverElemental.class, StrayedMirror.class, Samurai.class)));
        ScanningManager.addScannableThing(new ScanEntity("!Thaumaturge", Thaumaturge.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!OverAnimated", Overanimated.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Dissolved", Dissolved.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!VengefulGolem", EntityVengefulGolem.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!QuicksilverElemental", QuicksilverElemental.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!StrayedMirror", StrayedMirror.class, true));
        ScanningManager.addScannableThing(new ScanEntity("!Samurai", Samurai.class, true));

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
                EntityRegistry.addSpawn(Dissolved.class, ConfigsRC.dissolvedSpawnChance, 1, 2, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(QuicksilverElemental.class, ConfigsRC.quicksilverElementalSpawnChance, 1, 2, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(Samurai.class, ConfigsRC.paranoidWarriorSpawnChance, 3, 5, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(EntityVengefulGolem.class, ConfigsRC.vengefulGolemSpawnChance, 1, 2, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(Overanimated.class, ConfigsRC.overanimatedSpawnChance, 2, 3, EnumCreatureType.MONSTER, bgb);
                EntityRegistry.addSpawn(StrayedMirror.class, ConfigsRC.strayedMirrorSpawnChance, 1, 2, EnumCreatureType.MONSTER, bgb);
                //EntityRegistry.addSpawn(MadThaumaturge.class, TCConfig.madThaumaturgeSpawnChance, 2, 3, EnumCreatureType.MONSTER, new Biome[]{bgb});
            }
            if (!bgb.getSpawnableList(EnumCreatureType.CREATURE).isEmpty() & bgb.getSpawnableList(EnumCreatureType.CREATURE).size() > 0) {
                EntityRegistry.addSpawn(Thaumaturge.class, ConfigsRC.thaumaturgeSpawnChance, 1, 3, EnumCreatureType.CREATURE, bgb);
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

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("trk:coin_to_stacks"),
                new ShapedArcaneRecipe(new ResourceLocation(""), "",
                        5,
                        new AspectList(),
                        new ItemStack(RCItems.coins, 1, 1),
                        "ccc",
                        "ccc",
                        "ccc",
                        'c', new ItemStack(RCItems.coins)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("trk:stacks_to_enchanted"),
                new InfusionRecipe("",
                        new ItemStack(RCItems.coins, 1, 2),
                        4,
                        new AspectList().add(Aspect.DESIRE, 6).add(Aspect.EXCHANGE, 6).add(Aspect.MAGIC, 6),
                        new ItemStack(RCItems.coins, 1, 1),
                        new ItemStack(RCItems.coins, 1, 1),
                        new ItemStack(RCItems.coins, 1, 1),
                        new ItemStack(RCItems.coins, 1, 1),
                        new ItemStack(RCItems.coins, 1, 1),
                        new ItemStack(RCItems.coins, 1, 1),
                        new ItemStack(RCItems.coins, 1, 1),
                        new ItemStack(RCItems.coins, 1, 1),
                        new ItemStack(RCItems.coins, 1, 1)));

        GolemHead.register(new GolemHead("FORAGE", new String[]{"FIRSTSTEPS"}, new ResourceLocation(MODID, "textures/research/r_pech.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_forage.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadNormal)}, new EnumGolemTrait[]{RenaissanceCore.GREEDY}));
        GolemHead.register(new GolemHead("STALKER", new String[]{"FIRSTSTEPS"}, new ResourceLocation(MODID, "textures/research/r_pech_stalker.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_stalker.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadHunter)}, new EnumGolemTrait[]{EnumGolemTrait.LIGHT}));
        GolemHead.register(new GolemHead("THAUMIUM", new String[]{"FIRSTSTEPS"}, new ResourceLocation(MODID, "textures/research/r_pech_thaum.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_thaum.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadThaumaturge)}, new EnumGolemTrait[]{EnumGolemTrait.SMART}));

        GolemAddon.register(new GolemAddon("BUBBLE_ARMOR", new String[]{"FIRSTSTEPS"}, new ResourceLocation(MODID, "textures/research/bubble_wrap_item.png"), new PartModelHauler(new ResourceLocation(MODID, "models/obj/bubble_wrap.obj"), new ResourceLocation(MODID, "textures/models/entity/bubble_wrap.png"), PartModel.EnumAttachPoint.BODY), new Object[]{new ItemStack(Blocks.WOOL), new ItemStack(Items.PAPER, 6)}, new EnumGolemTrait[]{RenaissanceCore.BUBBLE}));
    }
}
