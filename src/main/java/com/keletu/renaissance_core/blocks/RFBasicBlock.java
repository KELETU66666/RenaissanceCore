package com.keletu.renaissance_core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class RFBasicBlock extends Block {

    public RFBasicBlock() {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setHardness(2.0F);
        setResistance(5.0F);
        setHarvestLevel("pickaxe", 2);
    }
}
