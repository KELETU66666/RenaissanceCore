package com.keletu.renaissance_core.util;

import com.keletu.renaissance_core.compat.crafttweaker.TweakerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IThaumcraftRecipe;

import java.util.ArrayList;
import java.util.List;

public class ChainedRiftRecipe implements IThaumcraftRecipe {
    static ArrayList<ChainedRiftRecipe> riftRecipes = new ArrayList();

    private final ItemStack recipeOutput;

    public Object catalyst;
    public AspectList aspects;
    public String key;

    public int hash;

    public static void addRiftRecipe(ResourceLocation registry, ChainedRiftRecipe recipe) {
        ThaumcraftApi.getCraftingRecipes().put(registry, recipe);
    }

    public ChainedRiftRecipe(String researchKey, ItemStack result, Object cat, AspectList tags) {
        recipeOutput = result;
        this.aspects = tags;
        this.key = researchKey;
        this.catalyst = cat;
        if (cat instanceof String) {
            this.catalyst = OreDictionary.getOres((String) cat);
        }
        String hc = researchKey + result.toString();
        for (Aspect tag : tags.getAspects()) {
            hc += tag.getTag() + tags.getAmount(tag);
        }
        if (cat instanceof ItemStack) {
            hc += ((ItemStack) cat).toString();
        } else if (cat instanceof ArrayList && ((ArrayList<ItemStack>) catalyst).size() > 0) {
            for (ItemStack is : (ArrayList<ItemStack>) catalyst) {
                hc += is.toString();
            }
        }

        hash = hc.hashCode();
    }

    public static ChainedRiftRecipe addChainedRiftRecipe(String key, ItemStack result, Object catalyst, AspectList tags) {
        ChainedRiftRecipe recipe = new ChainedRiftRecipe(key, result, catalyst, tags);
        riftRecipes.add(recipe);
        return recipe;
    }

    public static ChainedRiftRecipe findMatchingRiftRecipe(EntityPlayer username, AspectList aspects, ItemStack lastDrop) {
        int highest = 0;
        int index = -1;

        for (int a = 0; a < riftRecipes.size(); ++a) {
            ChainedRiftRecipe recipe = riftRecipes.get(a);
            ItemStack temp = lastDrop.copy();
            temp.setCount(1);
            if (ThaumcraftCapabilities.knowsResearch(username, recipe.key) && recipe.matches(aspects, temp)) {
                int result = recipe.aspects.size();
                if (result > highest) {
                    highest = result;
                    index = a;
                }
            }
        }
        if (index < 0) {
            return null;
        } else {
            new AspectList();
            return riftRecipes.get(index);
        }
    }


    public boolean matches(AspectList itags, ItemStack cat) {
        if (catalyst instanceof ItemStack &&
                !ThaumcraftApiHelper.areItemsEqual((ItemStack) catalyst, cat)) {
            return false;
        } else if (catalyst instanceof ArrayList && ((ArrayList<ItemStack>) catalyst).size() > 0) {
            List<ItemStack> ores = ((ArrayList<ItemStack>) catalyst);
            if (!ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{cat}, ores))
                return false;
        }
        if (itags == null) return false;
        for (Aspect tag : aspects.getAspects()) {
            if (itags.getAmount(tag) < aspects.getAmount(tag)) return false;
        }
        return true;
    }

    public boolean catalystMatches(ItemStack cat) {
        if (catalyst instanceof ItemStack && ThaumcraftApiHelper.areItemsEqual((ItemStack) catalyst, cat)) {
            return true;
        } else if (catalyst instanceof ArrayList && ((ArrayList<ItemStack>) catalyst).size() > 0) {
            List<ItemStack> ores = ((ArrayList<ItemStack>) catalyst);
            return ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{cat}, ores);
        }
        return false;
    }

    public AspectList removeMatching(AspectList itags) {
        AspectList temptags = new AspectList();
        temptags.aspects.putAll(itags.aspects);

        for (Aspect tag : aspects.getAspects()) {
            temptags.remove(tag, aspects.getAmount(tag));
        }

        itags = temptags;
        return itags;
    }

    public ItemStack getRecipeOutput() {
        return recipeOutput;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChainedRiftRecipe) {
            ChainedRiftRecipe recipe = (ChainedRiftRecipe) o;
            return key.equals(recipe.key) && catalyst.equals(recipe.catalyst) && TweakerHelper.aspectsToString(aspects).equals(TweakerHelper.aspectsToString(recipe.aspects))
                    && ItemStack.areItemStacksEqual(recipeOutput, recipe.getRecipeOutput());
        }
        return false;
    }

    @Override
    public String getResearch() {
        return key;
    }

    @Override
    public String getGroup() {
        return "";
    }
}