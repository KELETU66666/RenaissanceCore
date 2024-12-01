package com.keletu.renaissance_core.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class RCItemBlocks extends ItemBlock {
    public RCItemBlocks(Block block) {
        super(block);
        this.setTranslationKey(block.getTranslationKey());
        this.setRegistryName(block.getRegistryName());
    }
}
