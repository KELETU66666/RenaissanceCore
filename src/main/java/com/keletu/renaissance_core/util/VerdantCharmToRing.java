package com.keletu.renaissance_core.util;

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

    public VerdantCharmToRing(Item output) {
        super("netherized:upgrade_netherite", new ItemStack(output), NonNullList.from(Ingredient.EMPTY, Ingredient.fromItem(charm)));
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
        ItemStack item = new ItemStack(charm);
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