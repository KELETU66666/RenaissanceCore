package com.keletu.renaissance_core.compat.crafttweaker;

import com.keletu.renaissance_core.util.PolishRecipe;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.aspects.AspectList;

@ZenRegister
@ZenClass("mods.renaissance_core.PolishmentRecipe")
public class CrTPolishmentRecipe {

    @ZenMethod
    public static void addPolishmentRecipe(IItemStack stack, String aspectList) {
        CraftTweakerAPI.apply(new Add(TweakerHelper.getStack(stack), TweakerHelper.parseAspects(aspectList)));
    }

    //@ZenMethod
    //public static void removePolishmentRecipe(IItemStack stack) {
    //    CraftTweakerAPI.apply(new Remove(TweakerHelper.getStack(stack)));
    //}

    public static class Add implements IAction {

        public final ItemStack stack;

        public final AspectList list;

        public Add(ItemStack stack, AspectList list) {
            this.stack = stack;
            this.list = list;
        }

        @Override
        public void apply() {
            PolishRecipe.addPolishmentRecipe(stack, list);
        }

        @Override
        public String describe() {
            return "Adding a Polishment Recipe to item: " + stack;
        }
    }

    /*public static class Remove implements IAction {

        public final ItemStack stack;

        public Remove(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void apply() {
            PolishRecipe.removePolismentRecipe(stack);
        }

        @Override
        public String describe() {
            return "Removing a Polishment Recipe from item: " + stack;
        }
    }*/
}