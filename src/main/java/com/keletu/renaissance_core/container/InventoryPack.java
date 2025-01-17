package com.keletu.renaissance_core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryPack implements IInventory {
    private Container container;
    public ItemStack[] stackList;

    public InventoryPack(Container par1Container) {
        this.container = par1Container;
        this.stackList = new ItemStack[27];
        for(int i = 0; i < this.stackList.length; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stackList) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if(i >= this.getSizeInventory()) return ItemStack.EMPTY;
        return this.stackList[i];
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        if (!this.stackList[i].isEmpty()) {
            ItemStack itemstack = this.stackList[i];
            this.stackList[i] = ItemStack.EMPTY;
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (!this.stackList[i].isEmpty()) {
            ItemStack itemstack;
            if (this.stackList[i].getCount() <= j) {
                itemstack = this.stackList[i];
                this.stackList[i] = ItemStack.EMPTY;
                this.markDirty();
                return itemstack;
            }
            itemstack = this.stackList[i].splitStack(j);
            if (this.stackList[i].getCount() == 0) {
                this.stackList[i] = ItemStack.EMPTY;
            }
            this.container.onCraftMatrixChanged(this);
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        this.stackList[i] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.container.onCraftMatrixChanged(this);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public String getName() {
        return "container.kitchengadgets.cloak_pouch";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(this.getName());
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.stackList.length; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
    }
}