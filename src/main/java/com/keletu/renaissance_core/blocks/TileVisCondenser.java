package com.keletu.renaissance_core.blocks;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.packet.PacketFXLightning;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.TileThaumcraft;

import java.util.List;

public class TileVisCondenser extends TileThaumcraft implements ITickable {
    public int cooldown;

    public TileVisCondenser() {
        cooldown = 0;
    }

    public boolean canUpdate() {
        return true;
    }

    public void update() {
        if (cooldown > 0) {
            --cooldown;
        }
        if (world.rand.nextInt(20) == 0) {
            TileEntity tileEntity = world.getTileEntity(pos.add(0, 1, 0));
            if (tileEntity instanceof TileDestabilizedCrystal) {
                if (!world.isRemote) {
                    world.playSound(null, pos.add(0, 1, 0), SoundsTC.jacobs, SoundCategory.BLOCKS, 0.01F, 1F);
                }
            }
        }
        //super.update();
    }

    public void sendEssentia(TileDestabilizedCrystal crystal) {
        for (int x = -8; x <= 8; ++x) {
            for (int y = -8; y <= 8; ++y) {
                for (int z = -8; z <= 8; ++z) {
                    TileEntity tile = world.getTileEntity(crystal.getPos().add(x, y, z));
                    if (tile instanceof TileHexOfPredictability) {
                        if (((TileHexOfPredictability) tile).isMaster && ((TileHexOfPredictability) tile).hasRift) {

                            int c;
                            if (crystal.aspect != null) {
                                c = Aspect.aspects.get(crystal.aspect).getColor();
                            } else {
                                c = 0xFFFFFF;
                            }
                            if (!world.isRemote) {
                                ((TileHexOfPredictability) tile).essentia.add(Aspect.getAspect(crystal.aspect), crystal.capacity);
                                ((TileHexOfPredictability) tile).heat = 1200;
                                crystal.capacity = 0;
                                crystal.markDirty();
                                world.notifyBlockUpdate(crystal.getPos(), world.getBlockState(crystal.getPos()), world.getBlockState(crystal.getPos()), 3);
                                tile.markDirty();
                                world.notifyBlockUpdate(tile.getPos(), world.getBlockState(tile.getPos()), world.getBlockState(tile.getPos()), 3);
                                world.playSound(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5, SoundsTC.shock, SoundCategory.BLOCKS, 0.8F, world.rand.nextFloat() * 0.1F + 0.9F, true);
                                RenaissanceCore.packetInstance.sendToAllAround(new PacketFXLightning(this.getPos().getX() + 0.5F, this.getPos().getY() + 1.5F, this.getPos().getZ() + 0.5F, tile.getPos().getX() + 0.5F, tile.getPos().getY() + 1.5F, tile.getPos().getZ() + 0.5F, c, 0.01F), new NetworkRegistry.TargetPoint(world.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 32.0));
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void shoot(TileDestabilizedCrystal crystal, BlockPos pos, int power) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (cooldown != 0) return;
        if (crystal.capacity == 0) return;
        int blastPower = Math.min(crystal.capacity, 16);

        int c;
        if (crystal.aspect != null) {
            c = Aspect.aspects.get(crystal.aspect).getColor();
        } else {
            c = 0xFFFFFF;
        }
        AxisAlignedBB box = new AxisAlignedBB(Math.min(crystal.getPos().getX() - 0.6, pos.getX() - 0.6), crystal.getPos().getY() - 0.3, Math.min(crystal.getPos().getZ() - 0.6, pos.getZ() - 0.6), Math.max(pos.getX() + 0.6, crystal.getPos().getX()), pos.getY() + 0.3, Math.max(pos.getZ() + 0.6, crystal.getPos().getZ() + 0.6));
        if (!this.world.isRemote) {
            crystal.capacity -= blastPower;
            crystal.markDirty();
            this.world.notifyBlockUpdate(crystal.getPos(), world.getBlockState(crystal.getPos()), world.getBlockState(crystal.getPos()), 3);
            List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, box);
            for (EntityLivingBase e : list) {
                e.attackEntityFrom(DamageSource.MAGIC, MathHelper.clamp(blastPower + 1 - power, 0, 16));
                if (e.getHealth() <= 0) {
                    ItemCrystalEssence itemEssence = (ItemCrystalEssence) ItemsTC.crystalEssence;
                    ItemStack wispyEssence = new ItemStack(itemEssence, 1, 0);
                    AspectList aspectsCompound = AspectHelper.getEntityAspects(e);
                    itemEssence.setAspects(wispyEssence, new AspectList().add(aspectsCompound.getAspects()[e.world.rand.nextInt(aspectsCompound.size())], 2));
                    e.entityDropItem(wispyEssence, 0.2f);
                }
            }
            world.playSound(null, this.getPos().add(0.5, 0.5, 0.5), SoundsTC.shock, SoundCategory.BLOCKS, 0.8F, world.rand.nextFloat() * 0.1F + 0.9F);
            RenaissanceCore.packetInstance.sendToAllAround(new PacketFXLightning(this.getPos().getX() + 0.5F, this.getPos().getY() + 1.5F, this.getPos().getZ() + 0.5F, x + 0.5F, y + 0.5F, z + 0.5F, c, 0.01F), new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 32.0));
        }
    }

    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        cooldown = nbttagcompound.getInteger("cooldown");
        super.readSyncNBT(nbttagcompound);
    }

    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("cooldown", cooldown);
        return super.writeSyncNBT(nbttagcompound);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos().getX(), this.getPos().getY() - 1.0, this.getPos().getZ(), this.getPos().getX() + 1.0, this.getPos().getY() + 2.0, this.getPos().getZ() + 1.0);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        if (world.getBlockState(pkt.getPos()) != null) {
            TileEntity tile = world.getTileEntity(pkt.getPos());
            if (tile instanceof TileVisCondenser) {
                tile.readFromNBT(pkt.getNbtCompound());
            }
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new SPacketUpdateTileEntity(this.getPos(), 0, nbttagcompound);
    }
}