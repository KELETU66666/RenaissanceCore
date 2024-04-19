package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.blocks.RFBlocks;
import net.minecraft.item.Item;


public class RFItems {
    public static final Item rift_feed = new Item().setRegistryName("rift_feed").setTranslationKey("rift_feed");
    public static final Item mana_bean = new ItemManaBean().setRegistryName("mana_bean").setTranslationKey("mana_bean");

    public static final Item etherealBloomItem = new RFItemBlocks(RFBlocks.ethereal_bloom);
    public static final Item bloodyThaumiumItem = new RFItemBlocks(RFBlocks.bloody_thaumium);
    public static final Item bloodyVoidItem = new RFItemBlocks(RFBlocks.bloody_void);
    public static final Item bloodyIchoriumItem = new RFItemBlocks(RFBlocks.bloody_ichorium);
}
