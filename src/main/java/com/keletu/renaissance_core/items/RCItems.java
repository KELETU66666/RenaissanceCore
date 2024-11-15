package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.blocks.RFBlocks;
import net.minecraft.item.Item;


public class RCItems {
    public static final Item rift_feed = new Item().setRegistryName("rift_feed").setTranslationKey("rift_feed");
    public static final Item mana_bean = new ItemManaBean().setRegistryName("mana_bean").setTranslationKey("mana_bean");
    public static final Item arcane_lime_powder = new Item().setRegistryName("arcane_lime_powder").setTranslationKey("arcane_lime_powder");
    public static final Item dice12 = new ItemDice12().setRegistryName("dice12").setTranslationKey("dice12");

    public static final Item etherealBloomItem = new RFItemBlocks(RFBlocks.ethereal_bloom);
    public static final Item pechHeadNormal = new RFItemBlocks(RFBlocks.pechHead_normal);
    public static final Item pechHeadHunter = new RFItemBlocks(RFBlocks.pechHead_hunter);
    public static final Item pechHeadThaumaturge = new RFItemBlocks(RFBlocks.pechHead_thaumaturge);
}
