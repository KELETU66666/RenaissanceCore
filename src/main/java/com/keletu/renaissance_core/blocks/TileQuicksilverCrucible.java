package com.keletu.renaissance_core.blocks;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.crafting.TileCrucible;

public class TileQuicksilverCrucible extends TileEntity implements IAspectContainer, ITickable {
    public AspectList aspects = new AspectList();
    int bellows = -1;
    int cooldown;

    public TileQuicksilverCrucible() {
        cooldown = 0;
    }

    public TileQuicksilverCrucible(boolean created) {
        super();
        if (created) {
            aspects.add(Aspect.EXCHANGE, 20);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.aspects.readFromNBT(nbttagcompound);
        cooldown = nbttagcompound.getInteger("cooldown");
        super.readFromNBT(nbttagcompound);

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        this.aspects.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("cooldown", cooldown);
        super.writeToNBT(nbttagcompound);
        return nbttagcompound;
    }

    public void update() {

        if (cooldown > 0) {
            cooldown--;
        }

        if (cooldown == 0 && !world.isBlockPowered(getPos())) {
            sendQuicksilver();
        }
        if (this.world.isRemote) {
            if (this.aspects.getAmount(Aspect.EXCHANGE) > 0 && !world.isBlockPowered(getPos())) {
                for (int i = 0; i < 5; i++) {
                    int x = 5 + this.world.rand.nextInt(22);
                    int y = 5 + this.world.rand.nextInt(22);
                    FXDispatcher.INSTANCE.crucibleBubble((float) this.getPos().getX() + (float) x / 32.0F, (float) this.getPos().getY() + 0.3F + (aspects.getAmount(Aspect.EXCHANGE) / 100.0F), (float) this.getPos().getZ() + (float) y / 32.0F, 0.6F, 0.6F, 0.6F);
                }
            }
        }
    }

    public void sendQuicksilver() {
        if (this.aspects.getAmount(Aspect.EXCHANGE) > 0) {
            for (int x = -8; x <= 8; ++x) {
                for (int y = -8; y <= 8; ++y) {
                    for (int z = -8; z <= 8; ++z) {
                        TileEntity tile = world.getTileEntity(new BlockPos(this.getPos().getX() + x, this.getPos().getY() + y, this.getPos().getZ() + z));
                        if (tile instanceof TileCrucible) {
                            if (((TileCrucible) tile).aspects.size() >= 2) {
                                Aspect[] aspects = ((TileCrucible) tile).aspects.getAspects();
                                Aspect combo = ResearchManager.getCombinationResult(aspects[aspects.length - 1], aspects[aspects.length - 2]);
                                if (combo != null) {
                                    if (!world.isRemote) {
                                        AspectList list = new AspectList();
                                        for (int i = 0; i < aspects.length - 2; i++) {
                                            list.add(aspects[i], ((TileCrucible) tile).aspects.getAmount(aspects[i]));
                                        }
                                        list.add(combo, 1);
                                        if (((TileCrucible) tile).aspects.getAmount(aspects[aspects.length - 2]) > 1) {
                                            list.add(aspects[aspects.length - 2], ((TileCrucible) tile).aspects.getAmount(aspects[aspects.length - 2]) - 1);
                                        }
                                        if (((TileCrucible) tile).aspects.getAmount(aspects[aspects.length - 1]) > 1) {
                                            list.add(aspects[aspects.length - 1], ((TileCrucible) tile).aspects.getAmount(aspects[aspects.length - 1]) - 1);
                                        }
                                        ((TileCrucible) tile).aspects.aspects.clear();
                                        ((TileCrucible) tile).aspects.merge(list);
                                        this.aspects.remove(Aspect.EXCHANGE, 1);
                                        this.cooldown = 20;
                                        this.markDirty();
                                        world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
                                        tile.markDirty();
                                        world.notifyBlockUpdate(tile.getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
                                    } else {
                                        RenaissanceCore.proxy.quicksilverFlow(world, tile.getPos().getX(), tile.getPos().getY() + 1.0, tile.getPos().getZ(), getPos().getX(), getPos().getY() + 1.0, getPos().getZ());
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.cooldown = 20;
        this.markDirty();
        world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
    }


    public void attemptSmelt(EntityItem entity) {
        boolean bubble = false;
        ItemStack item = entity.getItem();
        int stacksize = item.getCount();

        for (int a = 0; a < stacksize; ++a) {
            if (item.getItem() == ItemsTC.quicksilver) {
                this.aspects.add(Aspect.EXCHANGE, 2);
                stacksize--;
                bubble = true;
                if (this.aspects.getAmount(Aspect.EXCHANGE) > 64) {
                    this.aspects.remove(Aspect.EXCHANGE);
                    this.aspects.add(Aspect.EXCHANGE, 64);
                }
            }
        }

        if (bubble) {
            this.world.playSound(null, entity.getPosition(), SoundsTC.bubble, SoundCategory.BLOCKS, 0.2F, 1.0F + this.world.rand.nextFloat() * 0.4F);
            this.world.notifyBlockUpdate(this.getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
            this.world.addBlockEvent(new BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()), RCBlocks.quicksilver_crucible, 2, 1);
        }

        if (stacksize <= 0) {
            entity.setDead();
        } else {
            item.setCount(stacksize);
            entity.setItem(item);
        }

        this.markDirty();
        this.world.notifyBlockUpdate(this.getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
    }


    public void getBellows() {
        this.bellows = 0;

        for (int a = 2; a < 6; ++a) {
            EnumFacing dir = EnumFacing.byIndex(a);
            int xx = this.getPos().getX() + dir.getXOffset();
            int zz = this.getPos().getZ() + dir.getZOffset();
            Block bi = this.world.getBlockState(new BlockPos(xx, this.getPos().getY(), zz)).getBlock();
            int md = bi.getMetaFromState(this.world.getBlockState(new BlockPos(xx, this.getPos().getY(), zz)));
            if (bi == BlocksTC.bellows && md == 0) {
                ++this.bellows;
            }
        }

    }

    private void spill() {

    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        if (world.isBlockLoaded(pkt.getPos())) {
            TileEntity tile = world.getTileEntity(pkt.getPos());
            if (tile instanceof TileQuicksilverCrucible) {
                tile.readFromNBT(pkt.getNbtCompound());
            }
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new SPacketUpdateTileEntity(new BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()), 0, nbttagcompound);
    }


    @Override
    public AspectList getAspects() {
        if (this.aspects == null) {
            return null;
        }
        return aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {

    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
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
}