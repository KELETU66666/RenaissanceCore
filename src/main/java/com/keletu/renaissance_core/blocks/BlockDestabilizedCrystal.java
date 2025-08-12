package com.keletu.renaissance_core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;

import java.util.Random;

public class BlockDestabilizedCrystal extends BlockContainer {
    public BlockDestabilizedCrystal() {
        super(Material.GLASS);
        this.setHardness(0.7F);
        this.setResistance(1.0F);
        this.setLightLevel(0.5F);
        //this.setSoundType(SoundsTC.CRYSTAL);
    }

    /*
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        int size = Aspect.aspects.size();
        for(int var4 = 0; var4 < size; var4++) {
            ItemStack is = new ItemStack(par1, 1);
            par3List.add(is);
        }
        //ItemStack is = new ItemStack().setTagCompound(new NBTTagCompound().se);
    }
     */

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random random) {
        //int md = world.getBlockMetadata(i, j, k);
        TileDestabilizedCrystal dct = (TileDestabilizedCrystal) world.getTileEntity(pos);
        if (random.nextInt(2) == 0) {
            FXDispatcher.INSTANCE.spark((double) pos.getX() + 0.3 + (double) (world.rand.nextFloat() * 0.4F), (double) pos.getY() + 0.3 + (double) (world.rand.nextFloat() * 0.4F), (double) pos.getZ() + 0.3 + (double) (world.rand.nextFloat() * 0.4F), 0.2F + random.nextFloat() * 0.1F, 0.65f + world.rand.nextFloat() * 0.1f, 1.0f, 1.0f, 0.8f);
        }

    }

    public int colorMultiplier(IBlockAccess par1iBlockAccess, BlockPos pos) {
        int md = this.getMetaFromState(par1iBlockAccess.getBlockState(pos));
        TileDestabilizedCrystal dct = (TileDestabilizedCrystal) par1iBlockAccess.getTileEntity(pos);
        if (dct.aspect == null) {
            return 0xFFFFFF;
        } else return Aspect.aspects.get(dct.aspect).getColor();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }


    //@Override
    //public boolean isNormalCube(IBlockState state) {
    //    return false;
    //}

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer activator, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileDestabilizedCrystal) {
                TileDestabilizedCrystal dct = (TileDestabilizedCrystal) tile;
                if (activator.getHeldItem(hand) != null && !activator.getHeldItem(hand).isEmpty()) {
                    dct.handleInputStack(activator, activator.getHeldItem(hand));
                }
            }
        }
        return true;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDestabilizedCrystal();
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int md) {
        return null;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos l) {
        super.neighborChanged(state, world, pos, block, l);
        TileDestabilizedCrystal tes;
        if (this.checkIfAttachedToBlock((World) world, pos)) {
            tes = (TileDestabilizedCrystal) world.getTileEntity(pos);
            int i1 = tes.orientation;
            boolean flag = !world.isSideSolid(pos.add(-1, 0, 0), EnumFacing.byIndex(5), true) && i1 == 5;

            if (!world.isSideSolid(pos.add(1, 0, 0), EnumFacing.byIndex(4), true) && i1 == 4) {
                flag = true;
            }

            if (!world.isSideSolid(pos.add(0, 0, -1), EnumFacing.byIndex(3), true) && i1 == 3) {
                flag = true;
            }

            if (!world.isSideSolid(pos.add(0, 0, 1), EnumFacing.byIndex(2), true) && i1 == 2) {
                flag = true;
            }

            if (!world.isSideSolid(pos.add(0, -1, 0), EnumFacing.byIndex(1), true) && i1 == 1) {
                flag = true;
            }

            if (!world.isSideSolid(pos.add(0, 1, 0), EnumFacing.byIndex(0), true) && i1 == 0) {
                flag = true;
            }

            if (flag) {
                //this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
                ((World) world).setBlockToAir(pos);
            }

        } /*else if (md == 7) {
            DestabilizedCrystalTile tes = (DestabilizedCrystalTile)world.getTileEntity(i, j, k);
            EnumFacing fd = EnumFacing.byIndex(tes.orientation).getOpposite();
            if (world.isAirBlock(i + fd.offsetX, j + fd.offsetY, k + fd.offsetZ)) {
                this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
                world.setBlockToAir(i, j, k);
            }

        }*/
    }

    private boolean checkIfAttachedToBlock(World world, BlockPos pos) {
        if (!this.canPlaceBlockAt(world, pos)) {
            //this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockToAir(pos);
            return false;
        } else {
            return true;
        }
    }

    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing l) {
        if (l.getIndex() == 0 && world.isSideSolid(pos.add(0, 1, 0), EnumFacing.byIndex(0))) {
            return true;
        } else if (l.getIndex() == 1 && world.isSideSolid(pos.add(0, -1, 0), EnumFacing.byIndex(1))) {
            return true;
        } else if (l.getIndex() == 2 && world.isSideSolid(pos.add(0, 0, 1), EnumFacing.byIndex(2))) {
            return true;
        } else if (l.getIndex() == 3 && world.isSideSolid(pos.add(0, 0, -1), EnumFacing.byIndex(3))) {
            return true;
        } else if (l.getIndex() == 4 && world.isSideSolid(pos.add(1, 0, 0), EnumFacing.byIndex(4))) {
            return true;
        } else {
            return l.getIndex() == 5 && world.isSideSolid(pos.add(-1, 0, 0), EnumFacing.byIndex(5));
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        if (world.isSideSolid(pos.add(-1, 0, 0), EnumFacing.byIndex(5))) {
            return true;
        } else if (world.isSideSolid(pos.add(1, 0, 0), EnumFacing.byIndex(4))) {
            return true;
        } else if (world.isSideSolid(pos.add(0, 0, -1), EnumFacing.byIndex(3))) {
            return true;
        } else if (world.isSideSolid(pos.add(0, 0, 1), EnumFacing.byIndex(2))) {
            return true;
        } else {
            return world.isSideSolid(pos.add(0, -1, 0), EnumFacing.byIndex(1)) || world.isSideSolid(pos.add(0, 1, 0), EnumFacing.byIndex(0));
        }
    }

}