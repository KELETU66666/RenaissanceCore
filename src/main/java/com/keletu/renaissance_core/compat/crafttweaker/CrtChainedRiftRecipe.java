package com.keletu.renaissance_core.compat.crafttweaker;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.util.ChainedRiftRecipe;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.aspects.AspectList;

@ZenRegister
@ZenClass("mods.thaumicconcilium.ChainedRiftRecipe")
public class CrtChainedRiftRecipe {

    @ZenMethod
    public static void addRecipe(String id, String researchKey, IItemStack result, IItemStack catalyst, String aspectList) {
        CraftTweakerAPI.apply(new Add(id, researchKey, TweakerHelper.getStack(result), TweakerHelper.getStack(catalyst), TweakerHelper.parseAspects(aspectList)));
    }

    //@ZenMethod
    //public static void removeRecipe(String id) {
    //    MineTweakerAPI.apply(new Remove(id));
    //}

    public static class Add implements IAction {

        public final String researchKey, id;

        public final AspectList list;

        public final ItemStack result, catalyst;

        public Add(String id, String key, ItemStack result, ItemStack catalyst, AspectList aspectList) {
            this.id = id;
            this.researchKey = key;
            this.result = result;
            this.catalyst = catalyst;
            this.list = aspectList;
        }

        @Override
        public void apply() {
            ChainedRiftRecipe.addRiftRecipe(new ResourceLocation(RenaissanceCore.MODID, id), ChainedRiftRecipe.addChainedRiftRecipe(researchKey, result, catalyst, list));
        }

        @Override
        public String describe() {
            return "Adding a Chained Rift Recipe with id: " + id;
        }
    }

   /* public static class Remove implements IAction {

        public final String id;

        public final ChainedRiftRecipe recipe;

        public Remove(String id) {
            this.id = id;
            this.recipe = Thaumonomicon.recipes.get(id) instanceof ChainedRiftRecipe ? (ChainedRiftRecipe) Thaumonomicon.recipes.get(id) : null;
        }

        @Override
        public void apply() {
            ThaumicConciliumApi.removeChainedRiftRecipe(id);
        }

        @Override
        public boolean canUndo() {
            return !Thaumonomicon.recipes.containsKey(id) && recipe != null;
        }

        @Override
        public void undo() {
            ThaumicConciliumApi.addChainedRecipe(id, recipe.key, recipe.getRecipeOutput(), recipe.catalyst, recipe.aspects);
        }

        @Override
        public String describe() {
            return "Removing a Chained Rift Recipe with id: " + id;
        }

        @Override
        public String describeUndo() {
            return "Restoring a Chained Rift Recipe with id: " + id;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }*/
}