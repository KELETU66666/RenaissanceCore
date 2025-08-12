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

import com.buuz135.thaumicjei.ThaumcraftJEIPlugin;
import com.buuz135.thaumicjei.ThaumicJEI;
import com.buuz135.thaumicjei.category.IHasResearch;
import com.buuz135.thaumicjei.drawable.AlphaDrawable;
import com.buuz135.thaumicjei.drawable.ItemStackDrawable;
import com.buuz135.thaumicjei.ingredient.AspectIngredientRender;
import com.buuz135.thaumicjei.util.ResearchUtils;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.RCBlocks;
import com.keletu.renaissance_core.util.ChainedRiftRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.utils.InventoryUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RiftCategory implements IRecipeCategory<RiftCategory.CrucibleWrapper> {

    public static final String UUID = "RENAISSANCE_RIFT";
    public static final int ASPECT_Y = 66;
    public static final int ASPECT_X = 66;
    public static final int SPACE = 22;

    private final IGuiHelper helper;

    public RiftCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return new ItemStack(RCBlocks.hex_of_predictability).getDisplayName();
    }

    @Override
    public String getModName() {
        return ThaumicJEI.MOD_NAME;
    }

    @Override
    public IDrawable getBackground() {
        return new AlphaDrawable(new ResourceLocation(RenaissanceCore.MODID, "textures/gui/rift_book.png"), 2, 5, 109, 129, 0, 0, 10, 10);
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return new ItemStackDrawable(new ItemStack(RCBlocks.hex_of_predictability));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CrucibleWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, 61 - 6, 8);
        recipeLayout.getItemStacks().set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

        recipeLayout.getItemStacks().init(1, true, 2, 2);
        recipeLayout.getItemStacks().set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        int center = (ingredients.getInputs(ThaumcraftJEIPlugin.ASPECT_LIST).size() * SPACE) / 2;
        int x = 0;
        for (List<AspectList> aspectList : ingredients.getInputs(ThaumcraftJEIPlugin.ASPECT_LIST)) {
            recipeLayout.getIngredientsGroup(ThaumcraftJEIPlugin.ASPECT_LIST).init(x + 1, true, new AspectIngredientRender(), ASPECT_X - center + x * SPACE, ASPECT_Y, 16, 16, 0, 0);
            recipeLayout.getIngredientsGroup(ThaumcraftJEIPlugin.ASPECT_LIST).set(x + 1, aspectList);
            ++x;
        }
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png"));
        GL11.glEnable(3042);
        Gui.drawModalRectWithCustomSizedTexture(16, 6, 199, 168, 26, 26, 512, 512);
        GL11.glDisable(3042);
    }

    public static class CrucibleWrapper implements IHasResearch, IRecipeWrapper {

        private final ChainedRiftRecipe recipe;


        public CrucibleWrapper(ChainedRiftRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(InventoryUtils.cycleItemStack(recipe.catalyst)));
            ingredients.setInputs(ThaumcraftJEIPlugin.ASPECT_LIST, Arrays.stream(recipe.aspects.getAspectsSortedByAmount()).map(aspect -> new AspectList().add(aspect, recipe.aspects.getAmount(aspect))).collect(Collectors.toList()));
            ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            if (!ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, getResearch()))
                minecraft.getRenderItem().renderItemIntoGUI(new ItemStack(Blocks.BARRIER), 22, 14);
        }

        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY) {
            if (!ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, getResearch()) && mouseX > 22 && mouseX < 40 && mouseY > 14 && mouseY < 30) {
                return ResearchUtils.generateMissingResearchList(getResearch());
            }
            return null;
        }

        @Override
        public String[] getResearch() {
            return new String[]{recipe.getResearch()};
        }
    }

}