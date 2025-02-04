package com.keletu.renaissance_core;

import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.container.GUIHandler;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.events.KeepDiceEvent;
import com.keletu.renaissance_core.events.ZapHandler;
import com.keletu.renaissance_core.items.RCItems;
import com.keletu.renaissance_core.module.botania.EntropinnyumTNTHandler;
import com.keletu.renaissance_core.module.botania.SubtileRegisterOverride;
import com.keletu.renaissance_core.packet.PacketOpenPackGui;
import com.keletu.renaissance_core.packet.PacketZap;
import com.keletu.renaissance_core.packet.PacketZapParticle;
import com.keletu.renaissance_core.proxy.CommonProxy;
import com.keletu.renaissance_core.tweaks.InitBotaniaRecipes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
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
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.golems.client.PartModelHauler;


@Mod(modid = RenaissanceCore.MODID, name = RenaissanceCore.NAME, version = RenaissanceCore.VERSION, acceptedMinecraftVersions = RenaissanceCore.MC_VERSION,
        dependencies = "required-after:baubles@[1.5.2, ); required-after:thaumcraft@[6.1.BETA26]; required-after:thaumicaugmentation; required-after:mixinbooter@[4.2, ); after:botania")
public class RenaissanceCore {
    public static final String MODID = "renaissance_core";
    public static final String NAME = "Renaissance Core";
    public static final String VERSION = "1.5.1";
    public static final String MC_VERSION = "[1.12.2]";
    public static final EnumGolemTrait GREEDY = EnumHelper.addEnum(EnumGolemTrait.class, "GREEDY", new Class[]{ResourceLocation.class}, new ResourceLocation(MODID, "textures/misc/tag_cash.png"));
    public static final EnumGolemTrait BUBBLE = EnumHelper.addEnum(EnumGolemTrait.class, "BUBBLE", new Class[]{ResourceLocation.class}, new ResourceLocation(MODID, "textures/misc/tag_bubble.png"));
    @SidedProxy(clientSide = "com.keletu.renaissance_core.proxy.ClientProxy", serverSide = "com.keletu.renaissance_core.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper packetInstance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(RenaissanceCore.MODID, new GUIHandler());

        packetInstance = NetworkRegistry.INSTANCE.newSimpleChannel("RenaissanceChannel");
        packetInstance.registerMessage(PacketZap.Handler.class, PacketZap.class, 0, Side.SERVER);
        packetInstance.registerMessage(PacketZapParticle.Handler.class, PacketZapParticle.class, 1, Side.CLIENT);
        packetInstance.registerMessage(PacketOpenPackGui.Handler.class, PacketOpenPackGui.class, 2, Side.SERVER);

        if (event.getSide().isClient()) {
            OBJLoader.INSTANCE.addDomain(MODID);
            ZapHandler.registerKeybinds();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "protection_field"), EntityProtectionField.class, "protection_field", 0, MODID, 80, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_chicken"), EntityTaintChicken.class, "TaintedChicken", 1, MODID, 64, 3, true, 10618530, 12632256);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_rabbit"), EntityTaintRabbit.class, "TaintedRabbit", 2, MODID, 64, 3, true, 10618530, 15777984);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_cow"), EntityTaintCow.class, "TaintedCow", 3, MODID, 64, 3, true, 10618530, 8272443);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_creeper"), EntityTaintCreeper.class, "TaintedCreeper", 4, MODID, 64, 3, true, 10618530, 65280);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_pig"), EntityTaintPig.class, "TaintedPig", 5, MODID, 64, 3, true, 10618530, 15702511);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_sheep"), EntityTaintSheep.class, "TaintedSheep", 6, MODID, 64, 3, true, 10618530, 8421504);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "tainted_villager"), EntityTaintVillager.class, "TaintedVillager", 7, MODID, 64, 3, true, 10618530, 65535);


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

        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("trk:ethereal_bloom"), new CrucibleRecipe("ETHEREAL_BLOOM", new ItemStack(RFBlocks.ethereal_bloom), BlocksTC.shimmerleaf, new AspectList().add(Aspect.LIGHT, 20).add(Aspect.PLANT, 40).add(Aspect.LIFE, 40).add(Aspect.FLUX, 40)));

        GolemHead.register(new GolemHead("FORAGE", new String[]{"FIRSTSTEPS"}, new ResourceLocation(MODID, "textures/models/research/r_pech.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_forage.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadNormal)}, new EnumGolemTrait[]{RenaissanceCore.GREEDY}));
        GolemHead.register(new GolemHead("STALKER", new String[]{"FIRSTSTEPS"}, new ResourceLocation(MODID, "textures/models/research/r_pech_stalker.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_stalker.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadHunter)}, new EnumGolemTrait[]{EnumGolemTrait.LIGHT}));
        GolemHead.register(new GolemHead("THAUMIUM", new String[]{"FIRSTSTEPS"}, new ResourceLocation(MODID, "textures/models/research/r_pech_thaum.png"), new PartModel(new ResourceLocation(MODID, "models/obj/pech_skull_stalker.obj"), new ResourceLocation(MODID, "textures/blocks/pech_skull_thaum.png"), PartModel.EnumAttachPoint.HEAD), new Object[]{new ItemStack(RCItems.pechHeadThaumaturge)}, new EnumGolemTrait[]{EnumGolemTrait.SMART}));

        GolemAddon.register(new GolemAddon("BUBBLE_ARMOR", new String[]{"FIRSTSTEPS"}, new ResourceLocation(MODID, "textures/models/research/bubble_wrap_item.png"), new PartModelHauler(new ResourceLocation(MODID, "models/obj/bubble_wrap.obj"), new ResourceLocation(MODID, "textures/models/entity/bubble_wrap.png"), PartModel.EnumAttachPoint.BODY), new Object[]{new ItemStack(Blocks.WOOL), new ItemStack(Items.PAPER, 6)}, new EnumGolemTrait[]{RenaissanceCore.BUBBLE}));
    }


}
