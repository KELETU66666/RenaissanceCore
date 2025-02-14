package com.keletu.renaissance_core.module.botania;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.lib.UtilsFX;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lexicon.page.PageRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageCrucibleRecipe extends PageRecipe {

    private static final ResourceLocation elvenTradeOverlay = new ResourceLocation(RenaissanceCore.MODID, "textures/gui/crucibleoverlay.png");

    final List<CrucibleRecipe> recipes;
    int ticksElapsed = 0;
    int recipeAt = 0;

    public PageCrucibleRecipe(String unlocalizedName, List<CrucibleRecipe> recipes) {
        super(unlocalizedName);
        this.recipes = recipes;
    }

    public PageCrucibleRecipe(String unlocalizedName, CrucibleRecipe recipe) {
        this(unlocalizedName, Collections.singletonList(recipe));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderRecipe(IGuiLexiconEntry gui, int mx, int my) {
        CrucibleRecipe recipe = recipes.get(recipeAt);
        TextureManager render = Minecraft.getMinecraft().renderEngine;
        render.bindTexture(elvenTradeOverlay);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        ((GuiScreen) gui).drawTexturedModalRect(gui.getLeft(), gui.getTop(), 0, 0, gui.getWidth(), gui.getHeight());
        GlStateManager.disableBlend();

        renderItemAtOutputPos(gui, 0, 0, recipe.getRecipeOutput());

        ItemStack stk = recipe.getCatalyst().getMatchingStacks()[0];

        int x = 20;
        for (Aspect aspect : recipe.getAspects().getAspects()) {
            UtilsFX.drawTag(gui.getLeft() + x, gui.getTop() + 50, aspect, recipe.getAspects().getAmount(aspect), 0, 0);
            x += 17;
        }

        renderItemAtInputPos(gui, 0, stk);
    }


    @SideOnly(Side.CLIENT)
    public void renderItemAtInputPos(IGuiLexiconEntry gui, int x, ItemStack stack) {
        if (stack.isEmpty())
            return;
        stack = stack.copy();

        if (stack.getItemDamage() == Short.MAX_VALUE)
            stack.setItemDamage(0);

        int xPos = gui.getLeft() + x * 20 + 45;
        int yPos = gui.getTop() + 14;
        ItemStack stack1 = stack.copy();
        if (stack1.getItemDamage() == -1)
            stack1.setItemDamage(0);

        renderItem(gui, xPos, yPos, stack1, false);
    }

    @SideOnly(Side.CLIENT)
    public void renderItemAtOutputPos(IGuiLexiconEntry gui, int x, int y, ItemStack stack) {
        if (stack.isEmpty())
            return;
        stack = stack.copy();

        if (stack.getItemDamage() == Short.MAX_VALUE)
            stack.setItemDamage(0);

        int xPos = gui.getLeft() + x * 20 + 94;
        int yPos = gui.getTop() + y * 20 + 52;

        ItemStack stack1 = stack.copy();
        if (stack1.getItemDamage() == -1) {
            stack1.setItemDamage(0);
        }

        renderItem(gui, xPos, yPos, stack1, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        if (GuiScreen.isShiftKeyDown())
            return;

        if (ticksElapsed % 20 == 0) {
            recipeAt++;

            if (recipeAt == recipes.size())
                recipeAt = 0;
        }
        ++ticksElapsed;
    }

    @Override
    public List<ItemStack> getDisplayedRecipes() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (CrucibleRecipe r : recipes)
            list.add(r.getRecipeOutput());

        return list;
    }

    public static void init() {
        LexiconData.flowers.pages.remove(7);
        LexiconData.flowers.pages.set(7,
                new PageCrucibleRecipe("renaissance_core.research", new CrucibleRecipe(
                        "BASICBOTABY",
                        new ItemStack(ModItems.fertilizer, 3),
                        new ItemStack(Items.DYE, 1, 15),
                        new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.PLANT, 10))));
    }
}
