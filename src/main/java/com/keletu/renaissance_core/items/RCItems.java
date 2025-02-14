package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.blocks.RFBlocks;
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

    public static final Item pechHeadNormal = new RCItemBlocks(RFBlocks.pechHead_normal);
    public static final Item pechHeadHunter = new RCItemBlocks(RFBlocks.pechHead_hunter);
    public static final Item pechHeadThaumaturge = new RCItemBlocks(RFBlocks.pechHead_thaumaturge);
}
