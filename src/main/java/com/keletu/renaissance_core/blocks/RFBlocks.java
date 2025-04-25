package com.keletu.renaissance_core.blocks;

import net.minecraft.block.Block;
import thaumcraft.common.config.ConfigItems;

public class RFBlocks {
    public static final Block pechHead_normal = new BlockPechhead().setRegistryName("pech_skull_forage").setTranslationKey("pech_skull_forage").setCreativeTab(ConfigItems.TABTC);
    public static final Block pechHead_hunter = new BlockPechhead().setRegistryName("pech_skull_stalker").setTranslationKey("pech_skull_stalker").setCreativeTab(ConfigItems.TABTC);
    public static final Block pechHead_thaumaturge = new BlockPechhead().setRegistryName("pech_skull_thaum").setTranslationKey("pech_skull_thaum").setCreativeTab(ConfigItems.TABTC);
    public static final Block full_crucible = new BlockFullCrucible().setRegistryName("full_crucible").setTranslationKey("full_crucible").setCreativeTab(ConfigItems.TABTC);
    public static final Block quicksilver_crucible = new QuicksilverCrucibleBlock().setRegistryName("quicksilver_crucible").setTranslationKey("quicksilver_crucible").setCreativeTab(ConfigItems.TABTC);

}
