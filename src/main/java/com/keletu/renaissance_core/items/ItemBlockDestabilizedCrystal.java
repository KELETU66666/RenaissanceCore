package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.blocks.TileDestabilizedCrystal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockDestabilizedCrystal extends ItemBlock {
    public ItemBlockDestabilizedCrystal(Block block) {
        super(block);
        this.setTranslationKey(block.getTranslationKey());
        this.setRegistryName(block.getRegistryName());
        this.setMaxDamage(0);
        this.setHasSubtypes(false);
    }

    public int getMetadata(int par1) {
        return par1;
    }

    public String getTranslationKey(ItemStack par1ItemStack) {
        return super.getTranslationKey();
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (placed) {
            try {
                TileDestabilizedCrystal ts = (TileDestabilizedCrystal) world.getTileEntity(pos);
                ts.orientation = (short) side.getIndex();
                //if (world.getTileEntity(pos.add(0, -1, 0)) instanceof VisCondenserTile){
                //    ts.draining = true;
                //}
            } catch (Exception var14) {
            }
        }
        return placed;
    }
}