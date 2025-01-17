package com.keletu.renaissance_core.container;

import com.keletu.renaissance_core.items.ItemPechBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ContainerPack extends Container  implements IInventoryChangedListener {
    private World worldObj;
    public IInventory input;
    ItemStack cloak = ItemStack.EMPTY;
    EntityPlayer player = null;
    private int pouchSlotAmount = 27;

    public ContainerPack(InventoryPlayer iinventory, World world, ItemStack cloak) {
        this.worldObj = world;
        this.player = iinventory.player;
        this.cloak = cloak;
        this.input = new InventoryPack(this);

        for (int a = 0; a < pouchSlotAmount; a++) {
            this.addSlotToContainer(new Slot(this.input, a, 8 + a % 9 * 18, 9 + a / 9 * 18));
        }

        bindPlayerInventory(iinventory);

        if (!world.isRemote && !cloak.isEmpty()) {
            try {
                ItemStack[] storedItems = ((ItemPechBackpack) this.cloak.getItem()).getStoredItems(this.cloak);
                for (int i = 0; i < storedItems.length && i < pouchSlotAmount; i++) {
                    if (storedItems[i] != null && !storedItems[i].isEmpty()) {
                        this.input.setInventorySlotContents(i, storedItems[i]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        NonNullList<ItemStack> list = NonNullList.withSize(inventorySlots.size(), ItemStack.EMPTY);
        for (int i = 0; i < inventorySlots.size(); i++) {
            Slot slot = inventorySlots.get(i);
            if (slot != null) {
                list.set(i, slot.getStack());
            }
        }
        return list;
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 82 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 140));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < pouchSlotAmount) {
                if (!this.mergeItemStack(itemstack1, pouchSlotAmount, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, pouchSlotAmount, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!this.worldObj.isRemote) {
            ItemStack[] items = new ItemStack[pouchSlotAmount];
            for (int i = 0; i < pouchSlotAmount; i++) {
                items[i] = input.getStackInSlot(i);
            }
            ((ItemPechBackpack) this.cloak.getItem()).setStoredItems(this.cloak, items);
        }
    }

    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
        if (!this.player.world.isRemote) {
            ItemStack[] items = new ItemStack[pouchSlotAmount];
            for (int i = 0; i < pouchSlotAmount; i++) {
                items[i] = input.getStackInSlot(i);
            }
            ((ItemPechBackpack) this.cloak.getItem()).setStoredItems(this.cloak, items);
        }
        return stack;
    }

    @Override
    public void onInventoryChanged(IInventory invBasic) {
        this.detectAndSendChanges();
    }
}