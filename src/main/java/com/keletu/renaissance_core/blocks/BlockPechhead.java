package com.keletu.renaissance_core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPechhead extends Block {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 3);

    public BlockPechhead() {
        super(Material.CIRCUITS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, 1));
        this.setSoundType(SoundType.STONE);
        this.setHardness(5.0f);
        this.setResistance(10.0f);
    }

    /** @deprecated */
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        int var6 = MathHelper.floor((double)(entity.rotationYaw / 90.0f) + 0.5) & 3;
        world.setBlockState(pos, state.withProperty(ROTATION, var6), 2);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(ROTATION);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ROTATION, meta % 4);
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ROTATION);
    }
}