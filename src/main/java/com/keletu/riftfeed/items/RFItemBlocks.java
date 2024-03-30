package com.keletu.riftfeed.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class RFItemBlocks extends ItemBlock {
    public RFItemBlocks(Block block) {
        super(block);
        this.setTranslationKey(block.getTranslationKey());
        this.setRegistryName(block.getRegistryName());
    }
}
