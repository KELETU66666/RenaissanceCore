package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.blocks.RFBlocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;

public class RCItems {
    public static final EnumRarity rarityEric = EnumHelper.addRarity("ERIC", TextFormatting.DARK_RED, "Eric");
    public static final Item arcane_lime_powder = new Item().setRegistryName("arcane_lime_powder").setTranslationKey("arcane_lime_powder");
    public static final Item dice12 = new ItemDice12().setRegistryName("dice12").setTranslationKey("dice12");
    public static final Item pech_backpack = new ItemPechBackpack().setRegistryName("pech_backpack").setTranslationKey("pech_backpack");
    public static final Item elixir = new ItemRCElixir().setRegistryName("t12_elixir").setTranslationKey("t12_elixir");
    public static final Item coins = new ItemCoins().setRegistryName("rc_coins").setTranslationKey("rc_coins");
    public static final Item pontifex_hood = new PontifexRobe(0, EntityEquipmentSlot.HEAD).setRegistryName("pontifex_hood").setTranslationKey("pontifex_hood");
    public static final Item pontifex_robe = new PontifexRobe(0, EntityEquipmentSlot.CHEST).setRegistryName("pontifex_robe").setTranslationKey("pontifex_robe");
    public static final Item pontifex_legs = new PontifexRobe(1, EntityEquipmentSlot.LEGS).setRegistryName("pontifex_legs").setTranslationKey("pontifex_legs");
    public static final Item pontifex_boots = new PontifexRobe(0, EntityEquipmentSlot.FEET).setRegistryName("pontifex_boots").setTranslationKey("pontifex_boots");
    public static final Item molot = new PontifexHammer().setRegistryName("molot").setTranslationKey("molot");

    public static final Item pechHeadNormal = new RCItemBlocks(RFBlocks.pechHead_normal);
    public static final Item pechHeadHunter = new RCItemBlocks(RFBlocks.pechHead_hunter);
    public static final Item pechHeadThaumaturge = new RCItemBlocks(RFBlocks.pechHead_thaumaturge);
    public static final Item quicksilverCrucible = new RCItemBlocks(RFBlocks.quicksilver_crucible);
}
