package com.keletu.renaissance_core.blocks;

import net.minecraft.block.Block;
import thaumcraft.common.config.ConfigItems;

public class RFBlocks {
    public static final Block ethereal_bloom = new BlockEtherealBloom().setRegistryName("ethereal_bloom").setTranslationKey("ethereal_bloom");
    public static final Block mana_pod = new BlockManaPod().setRegistryName("mana_pod").setTranslationKey("mana_pod");
    public static final Block pechHead_normal = new BlockPechhead().setRegistryName("pech_skull_forage").setTranslationKey("pech_skull_forage").setCreativeTab(ConfigItems.TABTC);
    public static final Block pechHead_hunter = new BlockPechhead().setRegistryName("pech_skull_stalker").setTranslationKey("pech_skull_stalker").setCreativeTab(ConfigItems.TABTC);
    public static final Block pechHead_thaumaturge = new BlockPechhead().setRegistryName("pech_skull_thaum").setTranslationKey("pech_skull_thaum").setCreativeTab(ConfigItems.TABTC);

}
