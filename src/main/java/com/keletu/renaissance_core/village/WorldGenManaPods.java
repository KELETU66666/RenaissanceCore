// Decompiled with: CFR 0.152
// Class Version: 6
package com.keletu.renaissance_core.village;

import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.blocks.tile.TileManaPod;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenManaPods
extends WorldGenerator {
    public boolean generate(World par1World, Random par2Random, BlockPos position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        BlockPos pos = new BlockPos(x, y, z);
        int l = x;
        int i1 = z;
        while (y < Math.min(128, par1World.getHeight(x, z))) {
            if (par1World.isAirBlock(pos) && par1World.isAirBlock(pos.add(0, -1, 0))) {
                if (RFBlocks.mana_pod.canPlaceBlockOnSide(par1World, pos, EnumFacing.DOWN)) {
                    par1World.setBlockState(pos, RFBlocks.mana_pod.getStateFromMeta(2 + par2Random.nextInt(5)), 2);
                    TileEntity tile = par1World.getTileEntity(pos);
                    if (tile == null || !(tile instanceof TileManaPod)) break;
                    ((TileManaPod)tile).checkGrowth();
                    break;
                }
            } else {
                x = l + par2Random.nextInt(4) - par2Random.nextInt(4);
                z = i1 + par2Random.nextInt(4) - par2Random.nextInt(4);
            }
            ++y;
        }
        return true;
    }
}
