package com.keletu.renaissance_core.blocks;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.EntitySpecialItem;

import java.util.List;
import java.util.Random;

public class QuicksilverCrucibleBlock extends BlockContainer {

    protected static final AxisAlignedBB AABB_LEGS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
    protected static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

    private int delay = 0;

    public QuicksilverCrucibleBlock() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(17.0F);
        this.setSoundType(SoundType.METAL);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World w, BlockPos pos, Random r) {
        if (r.nextInt(10) == 0) {
            TileEntity te = w.getTileEntity(pos);
            if (te instanceof QuicksilverCrucibleTile && ((QuicksilverCrucibleTile)te).aspects.getAmount(Aspect.EXCHANGE) > 0) {
                w.playSound(pos.getX(), pos.getY(), pos.getZ(), new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":melted")), SoundCategory.BLOCKS, 1.1F + r.nextFloat() * 0.1F, 1.2F + r.nextFloat() * 0.2F, false);
            }
        }

    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote) {
                QuicksilverCrucibleTile tile = (QuicksilverCrucibleTile)world.getTileEntity(pos);
                if (tile != null && entity instanceof EntityItem && !(entity instanceof EntitySpecialItem)) {
                    tile.attemptSmelt((EntityItem)entity);
                } else {
                    ++this.delay;
                    if (this.delay < 10) {
                        return;
                    }

                    this.delay = 0;
                    if (entity instanceof EntityLivingBase && tile != null) {
                        entity.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
                        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 2.0F + world.rand.nextFloat() * 0.4F, false);
                    }
                }
        }

    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_LEGS);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof QuicksilverCrucibleTile) {
            float var10000 = (float)((QuicksilverCrucibleTile)te).aspects.visSize();
            ((QuicksilverCrucibleTile)te).getClass();
            float r = var10000 / 100.0F;
            return MathHelper.floor(r * 14.0F) + (((QuicksilverCrucibleTile)te).aspects.visSize() > 0 ? 1 : 0);
        }
        return 0;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof QuicksilverCrucibleTile) {
            ((QuicksilverCrucibleTile)te).getBellows();
        }

        super.onNeighborChange(world, pos, neighbor);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new QuicksilverCrucibleTile();
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return null;
    }
}