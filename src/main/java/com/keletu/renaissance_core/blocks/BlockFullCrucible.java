package com.keletu.renaissance_core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.SoundsTC;

import java.util.Random;

public class BlockFullCrucible extends Block {

    public BlockFullCrucible() {
        super(Material.IRON);
        setResistance(2.0f);
        setHardness(1.5f);
        setSoundType(SoundType.METAL);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(BlocksTC.crucible);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!worldIn.isRemote){
            if(playerIn.getHeldItem(hand).isEmpty()){
                worldIn.playSound(null, pos, SoundsTC.wand, SoundCategory.BLOCKS, 1.0f, 2.0f);
                worldIn.setBlockState(pos, BlocksTC.crucible.getDefaultState());
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
