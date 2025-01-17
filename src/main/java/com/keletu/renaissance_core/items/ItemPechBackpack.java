package com.keletu.renaissance_core.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemPechBackpack extends Item implements IBauble {
    public ItemPechBackpack() {
        this.maxStackSize = 1;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.BODY;
    }

    public ItemStack[] getStoredItems(ItemStack item) {
        ItemStack[] stackList = new ItemStack[27];

        if (item.hasTagCompound()) {
            NBTTagList inv = item.getTagCompound().getTagList("InternalInventory", 10);

            for (int i = 0; i < inv.tagCount(); i++) {
                NBTTagCompound tag = inv.getCompoundTagAt(i);
                int slot = tag.getByte("Slot") & 0xFF;

                if ((slot >= 0) && (slot < stackList.length)) {
                    stackList[slot] = new ItemStack(tag);
                }
            }
        }
        return stackList;
    }

    public void setStoredItems(ItemStack item, ItemStack[] stackList) {
        NBTTagList inv = new NBTTagList();

        for (int i = 0; i < stackList.length; i++) {
            if (!stackList[i].isEmpty()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                stackList[i].writeToNBT(tag);
                inv.appendTag(tag);
            }
        }
        if (!item.hasTagCompound()) {
            item.setTagCompound(new NBTTagCompound());
        }
        item.getTagCompound().setTag("InternalInventory", inv);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
