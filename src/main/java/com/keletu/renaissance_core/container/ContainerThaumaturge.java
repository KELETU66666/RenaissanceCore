package com.keletu.renaissance_core.container;

import com.keletu.renaissance_core.client.gui.InventoryThaumaturge;
import com.keletu.renaissance_core.entity.Thaumaturge;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.slot.SlotOutput;

import java.util.ArrayList;
import java.util.List;


public class ContainerThaumaturge extends Container implements IInventoryChangedListener {
    private final Thaumaturge thaumaturge;
    private final InventoryThaumaturge inventory;
    private final EntityPlayer player;
    private final World theWorld;

    public ContainerThaumaturge(InventoryPlayer par1InventoryPlayer, World par3World, Thaumaturge par2IMerchant) {
        thaumaturge = par2IMerchant;
        theWorld = par3World;
        player = par1InventoryPlayer.player;
        inventory = new InventoryThaumaturge(this, par1InventoryPlayer.player, par2IMerchant);
        thaumaturge.trading = true;
        addSlotToContainer(new Slot(inventory, 0, 36, 29));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                addSlotToContainer(new SlotOutput(inventory, 1 + j + i * 2, 106 + 18 * j, 20 + 18 * i));
            }
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    public InventoryThaumaturge getMerchantInventory() {
        return inventory;
    }

    public void onInventoryChanged(IInventory invBasic) {
    }

    public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2) {
        if (par2 == 0) {
            generateContents();
            return true;
        }
        return super.enchantItem(par1EntityPlayer, par2);
    }

    private void generateContents() {
        if (!theWorld.isRemote && !inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty() && inventory.getStackInSlot(2).isEmpty() && inventory.getStackInSlot(3).isEmpty() && inventory.getStackInSlot(4).isEmpty() && thaumaturge.isValued(inventory.getStackInSlot(0))) {
            ItemStack offer = this.inventory.getStackInSlot(0);
            /*if (offer.getItem() instanceof ItemResearchNotes) {
                if (ResearchManager.getData(offer) != null) {
                    if (ResearchManager.getData(offer).isComplete()) {
                        ItemStack research = null;
                        int choise = this.theWorld.rand.nextInt(4);
                        switch (choise) {
                            case 0:
                                research = ResearchManager.createNote(new ItemStack(ConfigItems.itemResearchNotes), "VISCONDUCTOR", this.theWorld);
                                this.mergeItemStack(research, 1, 5, false);
                                this.inventory.decrStackSize(0, 1);
                                break;
                            case 1:
                            case 2:
                                research = ResearchManager.createNote(new ItemStack(ConfigItems.itemResearchNotes), "POSITIVEBURSTFOCI", this.theWorld);
                                this.mergeItemStack(research, 1, 5, false);
                                this.inventory.decrStackSize(0, 1);
                                break;
                            case 3:
                                research = ResearchManager.createNote(new ItemStack(ConfigItems.itemResearchNotes), "IMPULSEFOCI", this.theWorld);
                                this.mergeItemStack(research, 1, 5, false);
                                this.inventory.decrStackSize(0, 1);
                                break;
                            default:
                                research = ResearchManager.createNote(new ItemStack(ConfigItems.itemResearchNotes), "VISCONDUCTOR", this.theWorld);
                                this.mergeItemStack(research, 1, 5, false);
                                this.inventory.decrStackSize(0, 1);
                                break;
                        }
                        return;
                    }
                }
            }*/
            int value = this.thaumaturge.getValue(this.inventory.getStackInSlot(0));
            if (this.theWorld.rand.nextInt(5) == 0) {
                value += this.theWorld.rand.nextInt(2);
            } else if (this.theWorld.rand.nextBoolean()) {
                value -= this.theWorld.rand.nextInt(2);
            }

            //Thaumaturge var10000 = this.thaumaturge;
            ArrayList<List> pos = Thaumaturge.tradeInventory;

            int sum_of_weight = 0;
            for (int i = 0; i < pos.size(); i++) {
                sum_of_weight += (Integer) pos.get(i).get(0);
            }
            this.inventory.decrStackSize(0, 1);
            int rnd = this.theWorld.rand.nextInt(sum_of_weight);
            for (int i = 0; i < pos.size(); i++) {
                if (rnd < (Integer) pos.get(i).get(0)) {
                    if (value > this.theWorld.rand.nextInt(10)) {
                        this.mergeItemStack(((ItemStack) pos.get(i).get(1)).copy(), 1, 5, false);
                        if (value < this.theWorld.rand.nextInt(10)) {
                            return;
                        }
                    }
                }
                rnd -= (Integer) pos.get(i).get(0);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return thaumaturge.getAnger() <= 0;
    }

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 == 0) {
                if (!mergeItemStack(itemstack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (par2 >= 1 && par2 < 5) {
                if (!mergeItemStack(itemstack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (par2 != 0 && par2 >= 5 && par2 < 41 && !mergeItemStack(itemstack2, 0, 1, true)) {
                return ItemStack.EMPTY;
            }
            if (itemstack2.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(par1EntityPlayer, itemstack2);
        }
        return itemstack;
    }

    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        this.thaumaturge.trading = false;
        if (!this.theWorld.isRemote) {
            for(int a = 0; a < 5; ++a) {
                ItemStack itemstack = this.inventory.removeStackFromSlot(a);
                if (itemstack != null) {
                    EntityItem ei = par1EntityPlayer.dropItem(itemstack, false);
                    if (ei != null) {
                        ei.setThrower("ThaumaturgeDrop");
                    }
                }
            }
        }
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
        boolean flag1 = false;
        int k = p_75135_2_;
        if (p_75135_4_) {
            k = p_75135_3_ - 1;
        }

        Slot slot;
        ItemStack itemstack1;
        if (stack.isStackable()) {
            while(stack.getCount() > 0 && (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_)) {
                slot = this.inventorySlots.get(k);
                itemstack1 = slot.getStack();
                if (!itemstack1.isEmpty() && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
                    int l = itemstack1.getCount() + stack.getCount();
                    if (l <= stack.getMaxStackSize()) {
                        stack.setCount(0);
                        itemstack1.setCount(l);
                        slot.onSlotChanged();
                        flag1 = true;
                    } else if (itemstack1.getCount() < stack.getMaxStackSize()) {
                        stack.setCount(stack.getCount() - stack.getMaxStackSize() - itemstack1.getCount());
                        itemstack1.setCount(stack.getMaxStackSize());
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }

                if (p_75135_4_) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        if (stack.getCount() > 0) {
            if (p_75135_4_) {
                k = p_75135_3_ - 1;
            } else {
                k = p_75135_2_;
            }

            while(!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_) {
                slot = this.inventorySlots.get(k);
                itemstack1 = slot.getStack();
                if (itemstack1.isEmpty()) {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.setCount(0);
                    flag1 = true;
                    break;
                }

                if (p_75135_4_) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        return flag1;
    }
}