package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.blocks.RFBlocks;
import net.minecraft.item.Item;


public class RCItems {
    public static final Item arcane_lime_powder = new Item().setRegistryName("arcane_lime_powder").setTranslationKey("arcane_lime_powder");
    public static final Item dice12 = new ItemDice12().setRegistryName("dice12").setTranslationKey("dice12");
    public static final Item pech_backpack = new ItemPechBackpack().setRegistryName("pech_backpack").setTranslationKey("pech_backpack");

    public static final Item pechHeadNormal = new RCItemBlocks(RFBlocks.pechHead_normal);
    public static final Item pechHeadHunter = new RCItemBlocks(RFBlocks.pechHead_hunter);
    public static final Item pechHeadThaumaturge = new RCItemBlocks(RFBlocks.pechHead_thaumaturge);
}
