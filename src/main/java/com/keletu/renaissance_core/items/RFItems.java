package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.module.bloodmagic.ItemBotanyDaggerOfSacrifice;
import com.keletu.renaissance_core.module.bloodmagic.ItemBotanySacrificialDagger;
import com.keletu.renaissance_core.module.bloodmagic.ItemThaumicDaggerOfSacrifice;
import com.keletu.renaissance_core.module.bloodmagic.ItemThaumicSacrificialDagger;
import net.minecraft.item.Item;


public class RFItems {
    public static final Item rift_feed = new Item().setRegistryName("rift_feed").setTranslationKey("rift_feed");
    public static final Item mana_slate = new Item().setRegistryName("mana_slate").setTranslationKey("mana_slate");
    public static final Item thaumic_slate = new Item().setRegistryName("thaumic_slate").setTranslationKey("thaumic_slate");
    public static final Item thaumic_sacrificial_dagger = new ItemThaumicSacrificialDagger();
    public static final Item botany_sacrificial_dagger = new ItemBotanySacrificialDagger();
    public static final Item thaumic_dagger_of_sacrifice = new ItemThaumicDaggerOfSacrifice();
    public static final Item botany_dagger_of_sacrifice = new ItemBotanyDaggerOfSacrifice();

    public static final Item etherealBloomItem = new RFItemBlocks(RFBlocks.ethereal_bloom);
    public static final Item bloodyThaumiumItem = new RFItemBlocks(RFBlocks.bloody_thaumium);
    public static final Item bloodyVoidItem = new RFItemBlocks(RFBlocks.bloody_void);
    public static final Item bloodyIchoriumItem = new RFItemBlocks(RFBlocks.bloody_ichorium);
    public static final Item thaumicRuneItem = new RFItemBlocks(RFBlocks.thaumic_rune);
    public static final Item botanyRuneItem = new RFItemBlocks(RFBlocks.mana_rune);
}
