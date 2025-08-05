package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.RCBlocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;

public class RCItems {
    public static final EnumRarity rarityEric = EnumHelper.addRarity("ERIC", TextFormatting.DARK_RED, "Eric");
    public static final Item arcane_lime_powder = new Item().setRegistryName("arcane_lime_powder").setTranslationKey("arcane_lime_powder").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item dice12 = new ItemDice12().setRegistryName("dice12").setTranslationKey("dice12").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item pech_backpack = new ItemPechBackpack().setRegistryName("pech_backpack").setTranslationKey("pech_backpack").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item elixir = new ItemRCElixir().setRegistryName("t12_elixir").setTranslationKey("t12_elixir").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item pontifex_hood = new ItemPontifexRobe(0, EntityEquipmentSlot.HEAD).setRegistryName("pontifex_hood").setTranslationKey("pontifex_hood").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item pontifex_robe = new ItemPontifexRobe(0, EntityEquipmentSlot.CHEST).setRegistryName("pontifex_robe").setTranslationKey("pontifex_robe").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item pontifex_legs = new ItemPontifexRobe(1, EntityEquipmentSlot.LEGS).setRegistryName("pontifex_legs").setTranslationKey("pontifex_legs").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item pontifex_boots = new ItemPontifexRobe(0, EntityEquipmentSlot.FEET).setRegistryName("pontifex_boots").setTranslationKey("pontifex_boots").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item molot = new ItemPontifexHammer().setRegistryName("molot").setTranslationKey("molot").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item crimson_annales = new ItemCrimsonAnnales().setRegistryName("crimson_annales").setTranslationKey("crimson_annales").setMaxStackSize(1).setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item research_notes_crimson = new ItemResources(rarityEric).setRegistryName("research_notes_crimson").setTranslationKey("research_notes_crimson").setMaxStackSize(1).setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item runic_chestplate = new ItemRunicWindings(0, EntityEquipmentSlot.CHEST).setRegistryName("runic_chestplate").setTranslationKey("runic_chestplate").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item runic_leggings = new ItemRunicWindings(1, EntityEquipmentSlot.LEGS).setRegistryName("runic_leggings").setTranslationKey("runic_leggings").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item bottle_of_thick_taint = new ItemBottleOfThickTaint().setRegistryName("bottle_thick_taint").setTranslationKey("bottle_thick_taint").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item research_page = new ItemResearchPage().setRegistryName("research_page").setTranslationKey(RenaissanceCore.MODID + "." + "research_page").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item item_icon = new ItemIcon().setRegistryName("icon").setTranslationKey("icon");

    public static final Item pechHeadNormal = new RCItemBlocks(RCBlocks.pechHead_normal);
    public static final Item pechHeadHunter = new RCItemBlocks(RCBlocks.pechHead_hunter);
    public static final Item pechHeadThaumaturge = new RCItemBlocks(RCBlocks.pechHead_thaumaturge);
    public static final Item quicksilverCrucible = new RCItemBlocks(RCBlocks.quicksilver_crucible);
}
