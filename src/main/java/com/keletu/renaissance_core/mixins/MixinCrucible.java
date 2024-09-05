package com.keletu.renaissance_core.mixins;

import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.items.RFItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.blocks.crafting.BlockCrucible;
import thaumcraft.common.tiles.crafting.TileCrucible;

@Pseudo
@Mixin(value = {BlockCrucible.class})
public abstract class MixinCrucible {

    @Inject(method = {"onEntityCollision"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void mixinOnEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity, CallbackInfo ci) {
        if (!world.isRemote) {
            TileCrucible tile = (TileCrucible) world.getTileEntity(pos);
            if (tile != null && entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() == RFItems.arcane_lime_powder && tile.tank.getFluidAmount() > 0) {
                world.removeTileEntity(pos);
                world.setBlockState(pos, RFBlocks.full_crucible.getDefaultState());
                ((EntityItem) entity).getItem().shrink(1);
                ci.cancel();
            }
        }
    }

    @Inject(method = {"onBlockActivated"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void mixinOnBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (!player.isSneaking() && player.getHeldItem(hand).getItem() == RFItems.arcane_lime_powder && side == EnumFacing.UP) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileCrucible) {
                TileCrucible tile = (TileCrucible) te;
                if (tile.tank.getFluidAmount() > 0) {
                    player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    world.removeTileEntity(pos);
                    world.setBlockState(pos, RFBlocks.full_crucible.getDefaultState());
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
