/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.keletu.renaissance_core.compat.jei;

import com.keletu.renaissance_core.blocks.RCBlocks;
import com.keletu.renaissance_core.util.ChainedRiftRecipe;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.IThaumcraftRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@JEIPlugin
public class RenaissanceCorePlugin implements IModPlugin {
    public static RiftCategory riftCategory;
    public static IJeiRuntime runtime;
    public static HashMap<IRecipeWrapper, String> recipes = new HashMap<>();


    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if(Loader.isModLoaded("thaumicjei")) {
            riftCategory = new RiftCategory(registry.getJeiHelpers().getGuiHelper());
            registry.addRecipeCategories(riftCategory);
        }
    }

    @Override
    public void register(IModRegistry registry) {
        if(Loader.isModLoaded("thaumicjei")){
            registry.addRecipeCatalyst(new ItemStack(RCBlocks.hex_of_predictability), riftCategory.getUid());

            List<RiftCategory.CrucibleWrapper> crucibleWrappers = new ArrayList<>();
            for (ResourceLocation string : ThaumcraftApi.getCraftingRecipes().keySet()) {
                IThaumcraftRecipe recipe = ThaumcraftApi.getCraftingRecipes().get(string);
                if (recipe instanceof ChainedRiftRecipe) {
                    crucibleWrappers.add(new RiftCategory.CrucibleWrapper((ChainedRiftRecipe) recipe));
                }
            }
            registry.addRecipes(crucibleWrappers, riftCategory.getUid());
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        if(Loader.isModLoaded("thaumicjei")) {
            runtime = jeiRuntime;

            recipes.clear();
            for (String uuid : new String[]{riftCategory.getUid()}) {
                jeiRuntime.getRecipeRegistry().getRecipeWrappers(jeiRuntime.getRecipeRegistry().getRecipeCategory(uuid)).forEach(o -> {
                    recipes.put((IRecipeWrapper) o, uuid);
                });
            }
        }
    }
}