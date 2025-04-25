package com.keletu.renaissance_core.client.gui;

import com.keletu.renaissance_core.entity.Thaumaturge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;


public class InventoryThaumaturge extends InventoryBasic {
    private Thaumaturge theMerchant;
    private EntityPlayer thePlayer;

    public InventoryThaumaturge(IInventoryChangedListener listener, EntityPlayer par1EntityPlayer, Thaumaturge par2IMerchant) {
        super("container.thaumaturge", false, 5);
        addInventoryChangeListener(listener);
        thePlayer = par1EntityPlayer;
        theMerchant = par2IMerchant;
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return theMerchant.getAnger() <= 0;
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0;
    }
}