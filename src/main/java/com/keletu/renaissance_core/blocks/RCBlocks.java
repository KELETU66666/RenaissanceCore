package com.keletu.renaissance_core.blocks;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.block.Block;

public class RCBlocks {
    public static final Block pechHead_normal = new BlockPechhead().setRegistryName("pech_skull_forage").setTranslationKey("pech_skull_forage").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Block pechHead_hunter = new BlockPechhead().setRegistryName("pech_skull_stalker").setTranslationKey("pech_skull_stalker").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Block pechHead_thaumaturge = new BlockPechhead().setRegistryName("pech_skull_thaum").setTranslationKey("pech_skull_thaum").setCreativeTab(RenaissanceCore.tabRenaissanceCore);
    public static final Block full_crucible = new BlockFullCrucible().setRegistryName("full_crucible").setTranslationKey("full_crucible");

    public static final Block quicksilver_crucible = new BlockQuicksilverCrucible().setRegistryName("quicksilver_crucible").setTranslationKey("quicksilver_crucible").setCreativeTab(RenaissanceCore.tabRenaissanceCore);

}
