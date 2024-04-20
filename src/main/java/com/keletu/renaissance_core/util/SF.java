package com.keletu.renaissance_core.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SF {  //SomeFuncs

    public static int getCount(IItemHandler handler, ItemStack checked){
        if (handler == null)
            return 0;
        int slots = handler.getSlots();
        int count = 0;
        for (int i = 0; i < slots; i++){
            ItemStack is = handler.getStackInSlot(i);
            if (is.getItem() == checked.getItem() && (is.hasTagCompound() == checked.hasTagCompound())
                && (!is.hasTagCompound() || is.getTagCompound().equals(checked.getTagCompound()))){
                count += is.getCount();
            }
        }
        return count;
    }

    public static int extract(IItemHandler handler, ItemStack extract){ //returns how many could not be extracted
        if (handler == null)
            return extract.getCount();
        int slots = handler.getSlots();
        int toExtract = extract.getCount();
        for (int i = 0; i < slots; i++){
            ItemStack is = handler.getStackInSlot(i);
            if (is.getItem() == extract.getItem() && (is.hasTagCompound() == extract.hasTagCompound())
                && (!is.hasTagCompound() || is.getTagCompound().equals(extract.getTagCompound()))){
                int count = is.getCount();
                if (count >= toExtract){
                    handler.extractItem(i, toExtract, false);
                    return 0;
                }
                toExtract -= count;
                handler.extractItem(i, count, false);
            }
        }
        return toExtract;
    }
}