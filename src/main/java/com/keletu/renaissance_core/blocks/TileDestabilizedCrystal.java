package com.keletu.renaissance_core.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileDestabilizedCrystal extends TileThaumcraft implements IAspectContainer {
    public short orientation;
    public String aspect;
    public int capacity;
    public boolean draining;

    public TileDestabilizedCrystal() {
        this.orientation = 1;
        this.aspect = null;
        this.capacity = 1;
        this.draining = false;
    }

    public boolean isUsableByPlayer(final EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64.0;
    }

    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        this.orientation = nbttagcompound.getShort("orientation");
        this.capacity = nbttagcompound.getInteger("capacity");
        if (nbttagcompound.hasKey("aspect")) {
            final String asp = nbttagcompound.getString("aspect");
            this.aspect = asp;
        }
        this.draining = nbttagcompound.getBoolean("draining");
        final String de = nbttagcompound.getString("drainer");
    }

    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        super.writeSyncNBT(nbttagcompound);
        nbttagcompound.setShort("orientation", this.orientation);
        nbttagcompound.setInteger("capacity", this.capacity);
        nbttagcompound.setBoolean("draining", this.draining);
        if (this.aspect != null) {
            nbttagcompound.setString("aspect", this.aspect);
        }
        return nbttagcompound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        if (world.getBlockState(pkt.getPos()) != null) {
            TileEntity tile = world.getTileEntity(pkt.getPos());
            if (tile instanceof TileDestabilizedCrystal) {
                tile.readFromNBT(pkt.getNbtCompound());
            }
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new SPacketUpdateTileEntity(this.pos, 0, nbttagcompound);
    }

    @Override
    public AspectList getAspects() {
        if (this.aspect == null) {
            return null;
        }
        return new AspectList().add(Aspect.aspects.get(this.aspect), this.capacity);
    }

    @Override
    public void setAspects(AspectList aspects) {

    }

    public void updateAmount() {
        if (this.capacity > 0) {
            this.capacity -= 1;
        }

    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }

    public void handleInputStack(EntityPlayer player, ItemStack stack) {
        if (stack.getItem() == ItemsTC.crystalEssence) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(stack.getTagCompound());
            Aspect crystalAspect = aspects.getAspects()[0];
            if (crystalAspect != null) {
                if (this.aspect != null && this.aspect.equals(crystalAspect.getTag())) {
                    this.capacity += 1;
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                    this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    this.markDirty();
                } else if (this.aspect == null || this.capacity == 0) {
                    if (aspects.size() > 0) {
                        this.aspect = aspects.getAspects()[0].getTag();
                        if (!player.capabilities.isCreativeMode) {
                            stack.shrink(1);
                            if (stack.getCount() == 0) {
                                stack = ItemStack.EMPTY;
                            }
                        }
                        this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                        this.markDirty();
                    }
                }
            }
        }
    }
}