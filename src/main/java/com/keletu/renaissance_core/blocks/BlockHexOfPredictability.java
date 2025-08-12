package com.keletu.renaissance_core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.blocks.BlocksTC;

import java.util.Random;

public class BlockHexOfPredictability extends Block implements ITileEntityProvider {

    public BlockHexOfPredictability() {
        super(Material.ROCK);
        this.setHardness(0.7F);
        this.setResistance(100.0F);
        this.setLightLevel(0.5F);
        this.setSoundType(SoundType.STONE);
        //this.setCreativeTab(ThaumicConcilium.tabTC);
    }


    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        for (int xx = -1; xx <= 1; ++xx) {
            for (int zz = -1; zz <= 1; ++zz) {
                world.notifyNeighborsOfStateChange(pos.add(xx, 0, zz), this, true);
                if (!world.isRemote) {
                    TileEntity tile = world.getTileEntity(pos.add(xx, 0, zz));
                    if (tile instanceof TileHexOfPredictability) {
                        if (((TileHexOfPredictability) tile).isMaster) {
                            restoreBlocks(world, pos);
                        }
                    }
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState meta, Random r, int p_149650_3_) {
        return Items.AIR;
    }

    private void restoreBlocks(World w, BlockPos pos) {
        for (int xx = -1; xx <= 1; ++xx) {
            for (int zz = -1; zz <= 1; ++zz) {
                Block block = w.getBlockState(pos.add(xx, 0, zz)).getBlock();
                if (block == this) {
                    w.setBlockState(pos.add(xx, 0, zz), BlocksTC.stoneAncient.getDefaultState(), 3);
                    w.notifyNeighborsOfStateChange(pos.add(xx, 0, zz), w.getBlockState(pos.add(xx, 0, zz)).getBlock(), true);
                    w.notifyBlockUpdate(pos.add(xx, 0, zz), w.getBlockState(pos.add(xx, 0, zz)), w.getBlockState(pos.add(xx, 0, zz)), 3);
                }
            }
        }
        TileEntity tile = w.getTileEntity(pos);
        if (tile instanceof TileHexOfPredictability) {
            if (!w.isRemote && ((TileHexOfPredictability) tile).isMaster) {
                w.setBlockState(pos, Blocks.BEDROCK.getDefaultState());
            }
        }


    }

    public static boolean checkStructure(World w, BlockPos pos) {
        int count = 0;
        boolean flag = false;
        for (int xx = -2; xx <= 2; ++xx) {
            for (int zz = -2; zz <= 2; ++zz) {
                Block b = w.getBlockState(pos.add(xx, 0, zz)).getBlock();
                if (w.getTileEntity(pos.add(xx, 0, zz)) instanceof TileHexOfPredictability) {
                    flag = true;
                    break;
                }
                if (b == BlocksTC.stoneAncient) {
                    count++;
                }
            }
        }
        return count == 8 && !flag;
    }

    public static boolean checkTiles(World w, BlockPos pos) {
        int count = 0;
        for (int xx = -1; xx <= 1; ++xx) {
            for (int zz = -1; zz <= 1; ++zz) {
                if (xx == 0 && zz == 0) continue;
                TileEntity tile = w.getTileEntity(pos.add(xx, 0, zz));
                if (tile instanceof TileHexOfPredictability && !((TileHexOfPredictability) tile).isMaster) {
                    count++;
                }
            }
        }
        if (count == 8) {
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if (xx == 0 && zz == 0) continue;
                    TileEntity tile = w.getTileEntity(pos.add(xx, 0, zz));
                    if (tile instanceof TileHexOfPredictability && !((TileHexOfPredictability) tile).isSlave) {
                        ((TileHexOfPredictability) tile).isSlave = true;
                        w.notifyBlockUpdate(pos.add(xx, 0, zz), w.getBlockState(pos.add(xx, 0, zz)), w.getBlockState(pos.add(xx, 0, zz)), 3);
                        tile.markDirty();
                    }
                }
            }
        }
        return count == 8;

    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer activator, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState metadata) {
        return new TileHexOfPredictability();
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int md) {
        return null;
    }
}