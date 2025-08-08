package com.keletu.renaissance_core.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import thaumcraft.api.aspects.AspectList;

import java.util.ArrayList;

public class PolishRecipe {
    static ArrayList<Tuple> polishmentRecipes = new ArrayList<>();

    public static void addPolishmentRecipe(ItemStack item, AspectList tags) {
        polishmentRecipes.add(new Tuple(item, tags));
    }

    public static void removePolismentRecipe(ItemStack item) {
        for (int i = 0; i < polishmentRecipes.size(); i++) {
            if (ItemStack.areItemStacksEqual((ItemStack) polishmentRecipes.get(i).getFirst(), item)) {
                polishmentRecipes.remove(i);
                break;
            }
        }
    }

    public static AspectList getPolishmentRecipe(ItemStack res) {
        for (Tuple t : polishmentRecipes) {
            if (t.getFirst() instanceof ItemStack) {
                if(res == null || res.isEmpty())
                    return null;

                if(res.getTagCompound() != null)
                    return null;

                if (((ItemStack) t.getFirst()).isItemEqual(res)) {
                    return (AspectList) t.getSecond();
                }
            }
        }
        return null;
    }

}
