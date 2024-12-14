package com.keletu.renaissance_core.recipes;

import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import thaumcraft.api.items.ItemsTC;

import javax.annotation.Nonnull;

public class VerdantCharmToRing extends ShapelessRecipes {
    private static final Item charm = ItemsTC.charmVerdant;

    public VerdantCharmToRing() {
        super("", ItemStack.EMPTY, NonNullList.from(Ingredient.EMPTY, Ingredient.fromItem(charm)));
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
        ItemStack item = ItemStack.EMPTY;

        for(int i = 0; i < var1.getSizeInventory(); i++) {
            ItemStack stack = var1.getStackInSlot(i);
            if(!stack.isEmpty() && stack.getItem() == charm)
                item = stack;
        }

        if(item.isEmpty())
            return ItemStack.EMPTY;

        ItemStack result = new ItemStack(RCItems.verdantRing);

        if (item.getTagCompound() != null)
            result.setTagCompound(item.getTagCompound());

        return result;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}