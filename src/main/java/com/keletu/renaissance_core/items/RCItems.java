package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.RFBlocks;
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
    public static final Item coins = new ItemResources().setRegistryName("rc_coins").setTranslationKey("rc_coins");
    public static final Item pontifex_hood = new PontifexRobe(0, EntityEquipmentSlot.HEAD).setRegistryName("pontifex_hood").setTranslationKey("pontifex_hood").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item pontifex_robe = new PontifexRobe(0, EntityEquipmentSlot.CHEST).setRegistryName("pontifex_robe").setTranslationKey("pontifex_robe").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item pontifex_legs = new PontifexRobe(1, EntityEquipmentSlot.LEGS).setRegistryName("pontifex_legs").setTranslationKey("pontifex_legs").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item pontifex_boots = new PontifexRobe(0, EntityEquipmentSlot.FEET).setRegistryName("pontifex_boots").setTranslationKey("pontifex_boots").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item molot = new PontifexHammer().setRegistryName("molot").setTranslationKey("molot").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item crimson_annales = new ItemResources(rarityEric).setRegistryName("crimson_annales").setTranslationKey("crimson_annales").setMaxStackSize(1).setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Item research_notes_crimson = new ItemResources(rarityEric).setRegistryName("research_notes_crimson").setTranslationKey("research_notes_crimson").setMaxStackSize(1).setCreativeTab(RenaissanceCore.tabRenaissanceCore);

    public static final Item pechHeadNormal = new RCItemBlocks(RFBlocks.pechHead_normal);
    public static final Item pechHeadHunter = new RCItemBlocks(RFBlocks.pechHead_hunter);
    public static final Item pechHeadThaumaturge = new RCItemBlocks(RFBlocks.pechHead_thaumaturge);
    public static final Item quicksilverCrucible = new RCItemBlocks(RFBlocks.quicksilver_crucible);
}
