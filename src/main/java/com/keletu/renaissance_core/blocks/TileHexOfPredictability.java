package com.keletu.renaissance_core.blocks;

import com.keletu.renaissance_core.entity.EntityHexRift;
import com.keletu.renaissance_core.entity.EntityThaumGib;
import com.keletu.renaissance_core.items.RCItems;
import com.keletu.renaissance_core.util.ChainedRiftRecipe;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.tiles.TileThaumcraft;

import java.util.List;

public class TileHexOfPredictability extends TileThaumcraft implements IAspectContainer, ITickable {

    public boolean isMaster;
    public boolean isSlave;

    public boolean hasRift;
    public AspectList essentia;

    public int heat;

    public TileHexOfPredictability() {
        isMaster = false;
        isSlave = false;
        hasRift = false;
        heat = 0;
        essentia = new AspectList();
    }

    public boolean canUpdate() {
        return !isSlave;
    }

    public void update() {

        if (!this.world.isRemote) {
            if (BlockHexOfPredictability.checkTiles(this.world, this.pos)) {
                if (!this.isMaster) {
                    this.isMaster = true;
                    this.markDirty();
                    this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                }
            } else {
                if (this.isMaster) {
                    this.isMaster = false;
                    this.markDirty();
                    this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                }
            }

            if (isMaster) {
                if (world.getTotalWorldTime() % 60 == 0) {
                    List<EntityHexRift> rifts = world.getEntitiesWithinAABB(EntityHexRift.class, new AxisAlignedBB(pos.getX() - 1.0, pos.getY(), pos.getZ() - 1.0, pos.getX() + 1.0, pos.getY() + 4.0, pos.getZ() + 1.0));
                    if (!rifts.isEmpty() && !hasRift) {
                        hasRift = true;
                        this.markDirty();
                        this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    } else if (rifts.isEmpty() && hasRift) {
                        hasRift = false;
                        essentia.aspects.clear();
                        this.markDirty();
                        this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    }
                }
                if (hasRift) {
                    if (heat >= 2400) {
                        List<EntityHexRift> rifts = world.getEntitiesWithinAABB(EntityHexRift.class, new AxisAlignedBB(pos.getX() - 1.0, pos.getY(), pos.getZ() - 1.0, pos.getX() + 1.0, pos.getY() + 4.0, pos.getZ() + 1.0));
                        if (!rifts.isEmpty()) {
                            rifts.get(0).setCollapse(true);
                            hasRift = false;
                            heat = 0;
                            essentia.aspects.clear();
                            this.markDirty();
                            this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                        }
                    }
                    List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX() - 2.0, pos.getY(), pos.getZ() - 2.0, pos.getX() + 2.0, pos.getY() + 4.0, pos.getZ() + 2.0));
                    List<EntityItem> near = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX() - 8.0, pos.getY() - 3.0, pos.getZ() - 8.0, pos.getX() + 8.0, pos.getY() + 3.0, pos.getZ() + 8.0));
                    if (essentia.size() != 0 && !near.isEmpty()) {
                        for (EntityItem e : near) {
                            ItemStack es = e.getItem();
                            if (es != null && es.getItem() == RCItems.void_slag) {
                                heat += 2;
                                this.markDirty();
                                this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                            }
                        }
                    }
                    if (list.size() == 1) {
                        EntityItem item = list.get(0);
                        if (!item.isDead) {
                            if (heat > 0) {
                                if (world.getTotalWorldTime() % 60 == 0 && world.rand.nextInt(10) >= 8) {
                                    int rand = 3 + world.rand.nextInt(3);
                                    for (int i = 0; i < rand; i++) {
                                        EntityItem slag = new EntityItem(this.world, pos.getX() + (-0.5 + world.rand.nextDouble()) * 2.0, pos.getY() + 2, pos.getZ() + (-0.5 + world.rand.nextDouble()) * 2.0, new ItemStack(RCItems.void_slag, 1));
                                        this.world.spawnEntity(slag);
                                        EntityThaumGib.setEntityMotionFromVector(slag, new Vector3(slag.posX + (-0.5 + world.rand.nextDouble()) * 2.0, slag.posY + 1.0, slag.posZ + (-0.5 + world.rand.nextDouble()) * 2.0), 0.2F);
                                    }
                                }
                                heat -= 3;
                                this.markDirty();
                                this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                            } else {
                                String thrower = item.getThrower();
                                if (thrower != null) {
                                    ChainedRiftRecipe recipe = ChainedRiftRecipe.findMatchingRiftRecipe(world.getPlayerEntityByName(thrower), essentia, item.getItem());
                                    if (recipe != null) {
                                        item.setDead();
                                        ItemStack resultStack = recipe.getRecipeOutput().copy();
                                        EntityItem result = new EntityItem(this.world, item.posX, item.posY, item.posZ, resultStack);
                                        this.world.spawnEntity(result);

                                        EntityPlayer p = this.world.getPlayerEntityByName(thrower);
                                        if (p != null) {
                                            FMLCommonHandler.instance().firePlayerCraftingEvent(p, resultStack, new InventoryFake(item.getItem()));
                                        }

                                        //for (int a = 0; a < Thaumcraft.proxy.particleCount(10); ++a) {
                                        //    ThaumicConcilium.proxy.sparkles(this.world, (int) result.posX, (int) (result.posY), (int) result.posZ);
                                        //}
                                        EntityThaumGib.setEntityMotionFromVector(result, new Vector3(result.posX + (-0.5 + world.rand.nextDouble()) * 5.0, result.posY + 1.0, result.posZ + (-0.5 + world.rand.nextDouble()) * 5.0), 0.4F);
                                        essentia.aspects.clear();
                                        this.markDirty();
                                        this.world.notifyBlockUpdate(this.pos, world.getBlockState(pos), world.getBlockState(pos), 3);

                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void readSyncNBT(NBTTagCompound nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        isMaster = nbttagcompound.getBoolean("isMaster");
        isSlave = nbttagcompound.getBoolean("IsSlave");
        hasRift = nbttagcompound.getBoolean("hasRift");
        essentia.readFromNBT((NBTTagCompound) nbttagcompound.getTag("essentia"));
        heat = nbttagcompound.getInteger("heat");
    }

    @Override
    public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("isMaster", isMaster);
        nbttagcompound.setBoolean("isSlave", isSlave);
        nbttagcompound.setBoolean("hasRift", hasRift);
        NBTTagCompound compound1 = new NBTTagCompound();
        essentia.writeToNBT(compound1);
        nbttagcompound.setTag("essentia", compound1);
        nbttagcompound.setInteger("heat", heat);

        return super.writeSyncNBT(nbttagcompound);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-4, -2, -4), this.pos.add(4, 2, 4));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        if (world.getBlockState(pkt.getPos()) != null) {
            TileEntity tile = world.getTileEntity(pkt.getPos());
            if (tile instanceof TileHexOfPredictability) {
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
        return essentia;
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