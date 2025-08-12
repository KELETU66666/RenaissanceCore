package com.keletu.renaissance_core.blocks;

import com.keletu.renaissance_core.client.particle.ParticleRedstonePublic;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockVisCondenser extends Block implements ITileEntityProvider {

    public BlockVisCondenser() {
        super(Material.IRON);
        this.setHardness(0.7F);
        this.setResistance(1.0F);
        this.setLightLevel(0.5F);
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.7F, 1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        TileVisCondenser condenserTile = (TileVisCondenser) world.getTileEntity(pos);
        if (condenserTile != null && condenserTile.cooldown != 0) {
            for (int iter = 0; iter < 5; iter++) {
                final ParticleRedstonePublic fx = new ParticleRedstonePublic(world, (double) pos.getX() + 0.5, (double) pos.getY() + 1.0, (double) pos.getZ() + 0.5, 3.0F, 150.0F / 255.0F, 0.0F);
                FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
            }
        }
        TileEntity tileEntity = world.getTileEntity(pos.add(0, 1, 0));

        if (tileEntity != null && tileEntity instanceof TileDestabilizedCrystal) {
            TileDestabilizedCrystal tile = (TileDestabilizedCrystal) tileEntity;
            Color c;
            if (tile.aspect != null) {
                c = new Color(Aspect.aspects.get(tile.aspect).getColor());
            } else {
                c = new Color(0xFFFFFF);
            }

            if (random.nextInt(2) == 0)
                FXDispatcher.INSTANCE.arcLightning(i, (double) j + 1.2 + (-0.5 + random.nextDouble()), k, (double) i + 0.9, (double) j + 1.2 + (-0.5 + random.nextDouble()), k, (float) c.getRed() / 255.0F, (float) c.getGreen() / 255.0F, (float) c.getBlue() / 255.0F, 0.01f);
            if (random.nextInt(2) == 0)
                FXDispatcher.INSTANCE.arcLightning(i, (double) j + 1.2 + (-0.5 + random.nextDouble()), (double) k + 0.9, i, (double) j + 1.2 + (-0.5 + random.nextDouble()), k, (float) c.getRed() / 255.0F, (float) c.getGreen() / 255.0F, (float) c.getBlue() / 255.0F, 0.01f);
            if (random.nextInt(2) == 0)
                FXDispatcher.INSTANCE.arcLightning((double) i + 0.9, (double) j + 1.2 + (-0.5 + random.nextDouble()), k, (double) i + 0.9, (double) j + 1.2 + (-0.5 + random.nextDouble()), (double) k + 0.9, (float) c.getRed() / 255.0F, (float) c.getGreen() / 255.0F, (float) c.getBlue() / 255.0F, 0.01f);
            if (random.nextInt(2) == 0)
                FXDispatcher.INSTANCE.arcLightning((double) i + 0.9, (double) j + 1.2 + (-0.5 + random.nextDouble()), (double) k + 0.9, i, (double) j + 1.2 + (-0.5 + random.nextDouble()), (double) k + 0.9, (float) c.getRed() / 255.0F, (float) c.getGreen() / 255.0F, (float) c.getBlue() / 255.0F, 0.01f);

        }

    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);
        TileVisCondenser tile = (TileVisCondenser) world.getTileEntity(pos);
        TileEntity tile1 = world.getTileEntity(pos.add(0, 1, 0));
        if (!(tile1 instanceof TileDestabilizedCrystal)) return;
        TileDestabilizedCrystal crystal = (TileDestabilizedCrystal) tile1;
        if (crystal.aspect == null) return;
        if (crystal.capacity == 0) return;
        if (tile.cooldown != 0) return;
        //System.out.println("TR...");
        //System.out.println("1 = " + world.isBlockProvidingPowerTo(x+1, y, z, 5));
        //System.out.println("2 = " + world.isBlockProvidingPowerTo(x-1, y, z, 4));
        //System.out.println("3 = " + world.isBlockProvidingPowerTo(x, y, z+1, 3));
        // System.out.println("4 = " + world.isBlockProvidingPowerTo(x, y, z-1, 2));

        RayTraceResult mop;
        //5
        int power = world.getStrongPower(pos.add(1, 0, 0), EnumFacing.byIndex(5));
        if (power != 0) {
            mop = world.rayTraceBlocks(new Vec3d(pos.add(-1, 1, 0)), new Vec3d(pos.add(-power, 1, 0)), true);
            if (mop == null) {
                tile.shoot(crystal, pos.add(-power, 1, 0), power);
            } else {
                tile.shoot(crystal, mop.getBlockPos(), power);
            }
            tile.cooldown = 40;
        }
        power = world.getStrongPower(pos.add(-1, 0, 0), EnumFacing.byIndex(4));
        if (power != 0) {
            mop = world.rayTraceBlocks(new Vec3d(pos.add(1, 1, 0)), new Vec3d(pos.add(power, 1, 0)), true);
            if (mop == null) {
                tile.shoot(crystal, pos.add(power, 1, 0), power);
            } else {
                tile.shoot(crystal, mop.getBlockPos(), power);
            }
            tile.cooldown = 40;

        }
        power = world.getStrongPower(pos.add(0, 0, 1), EnumFacing.byIndex(3));
        if (power != 0) {
            mop = world.rayTraceBlocks(new Vec3d(pos.add(0, 1, -1)), new Vec3d(pos.add(0, 1, -power)), true);
            if (mop == null) {
                tile.shoot(crystal, pos.add(0, 1, -power), power);
            } else {
                tile.shoot(crystal, mop.getBlockPos(), power);
            }
            tile.cooldown = 40;

        }
        power = world.getStrongPower(pos.add(0, 0, -1), EnumFacing.byIndex(2));
        if (power != 0) {
            mop = world.rayTraceBlocks(new Vec3d(pos.add(0, 1, 1)), new Vec3d(pos.add(0, 1, power)), true);
            if (mop == null) {
                tile.shoot(crystal, pos.add(0, 1, power), power);
            } else {
                tile.shoot(crystal, mop.getBlockPos(), power);
            }
            tile.cooldown = 40;
        }

        power = world.getStrongPower(pos.add(0, -1, 0), EnumFacing.byIndex(0));
        if (power != 0 && crystal.capacity != 0) {
            tile.sendEssentia(crystal);
        }
        tile.markDirty();
        world.notifyBlockUpdate(tile.getPos(), world.getBlockState(tile.getPos()), world.getBlockState(tile.getPos()), 3);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState metadata) {
        return new TileVisCondenser();
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int md) {
        return null;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(RCBlocks.vis_condenser));
        return list;
    }

}