package com.keletu.renaissance_core.module.botania;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.lib.UtilsFX;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModFluffBlocks;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lexicon.page.PageRecipe;
import vazkii.botania.common.lexicon.page.PageText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageInfusionRecipe<T extends InfusionRecipe> extends PageRecipe {

    private static final ResourceLocation petalOverlay = new ResourceLocation(RenaissanceCore.MODID, "textures/gui/infusionoverlay.png");

    private final List<T> recipes;
    private int ticksElapsed = 0;
    private int recipeAt = 0;
    public static final int SPACE = 22;

    public PageInfusionRecipe(String unlocalizedName, List<T> recipes) {
        super(unlocalizedName);
        this.recipes = recipes;
    }

    public PageInfusionRecipe(String unlocalizedName, T recipe) {
        this(unlocalizedName, Collections.singletonList(recipe));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderRecipe(IGuiLexiconEntry gui, int mx, int my) {
        T recipe = recipes.get(recipeAt);
        TextureManager render = Minecraft.getMinecraft().renderEngine;

        int twidth = gui.getWidth() - 30;
        int tx = gui.getLeft() + 15;
        int ty = gui.getTop() + 5;

        String inst = TextFormatting.DARK_GRAY + new TextComponentTranslation("tc.inst").getFormattedText() + new TextComponentTranslation("tc.inst." + Math.min((recipe.getInstability(Minecraft.getMinecraft().player, null, null) / 4), 5)).getUnformattedText();

        PageText.renderText(tx, ty, twidth, gui.getHeight(), inst);

        renderItemAtGridPos(gui, 3, 0, (ItemStack) recipe.getRecipeOutput(), false);
        renderItemAtGridPos(gui, 2, 1, recipe.getRecipeInput().getMatchingStacks()[0], false);

        List<Ingredient> inputs = recipe.getComponents();
        int degreePerInput = (int) (360F / inputs.size());
        float currentDegree = ConfigHandler.lexiconRotatingItems ? GuiScreen.isShiftKeyDown() ? ticksElapsed : ticksElapsed + ClientTickHandler.partialTicks : 0;
        int inputIndex = ticksElapsed / 40;

        for (Object obj : inputs) {
            Object input = obj;
            if (input instanceof String) {
                NonNullList<ItemStack> list = OreDictionary.getOres((String) input);
                int size = list.size();
                input = list.get(size - inputIndex % size - 1);
            }

            renderIngredientAtAngle(gui, currentDegree, input);

            currentDegree += degreePerInput;
        }

        int x = 13;
        for (Aspect aspect : recipe.getAspects().getAspects()) {
            UtilsFX.drawTag(gui.getLeft() + x, gui.getTop() + 110, aspect, recipe.getAspects().getAmount(aspect), 0, 0);
            x += 17;
        }

        //renderManaBar(gui, recipe, mx, my);

        render.bindTexture(petalOverlay);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        ((GuiScreen) gui).drawTexturedModalRect(gui.getLeft(), gui.getTop(), 0, 0, gui.getWidth(), gui.getHeight());
        GlStateManager.disableBlend();
    }

    @SideOnly(Side.CLIENT)
    public void renderIngredientAtAngle(IGuiLexiconEntry gui, float angle, Object stack) {
        ItemStack stk = ItemStack.EMPTY;
        if (stack instanceof OreIngredient) {
            stk = ((OreIngredient) stack).getMatchingStacks()[0];
        } else if (stack instanceof Ingredient) {
            stk = ((Ingredient) stack).getMatchingStacks()[0];
        }
        if (!stk.isEmpty()) {
            ItemStack workStack = stk.copy();
            if (workStack.getItemDamage() == 32767 || workStack.getItemDamage() == -1) {
                workStack.setItemDamage(0);
            }

            angle -= 90.0F;
            int radius = 32;
            double xPos = (double) gui.getLeft() + Math.cos((double) angle * Math.PI / 180.0) * (double) radius + (double) (gui.getWidth() / 2) - 8.0;
            double yPos = (double) gui.getTop() + Math.sin((double) angle * Math.PI / 180.0) * (double) radius + 53.0;
            this.renderItem(gui, xPos, yPos, workStack, false);
        }
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
        for (T r : recipes)
            list.add((ItemStack) r.getRecipeOutput());

        return list;
    }

    public static void init() {
        LexiconData.brewery.pages.set(2,
                new PageInfusionRecipe<>("renaissance_core.research", new InfusionRecipe("PLANTBREWERY", new ItemStack(ModBlocks.brewery),
                        7,
                        new AspectList().add(Aspect.MAN, 30).add(Aspect.MECHANISM, 50).add(Aspect.ENTROPY, 30),
                        Items.BREWING_STAND,
                        "ingotGold",
                        new ItemStack(ModBlocks.manaGlass),
                        new ItemStack(ModFluffBlocks.livingrockSlab),
                        new ItemStack(ModFluffBlocks.livingrockSlab),
                        new ItemStack(ModFluffBlocks.livingrockSlab),
                        new ItemStack(ModBlocks.manaGlass),
                        "ingotGold",
                        new ItemStack(ModBlocks.manaGlass),
                        new ItemStack(ModFluffBlocks.livingrockSlab),
                        new ItemStack(ModFluffBlocks.livingrockSlab),
                        new ItemStack(ModFluffBlocks.livingrockSlab),
                        new ItemStack(ModBlocks.manaGlass)
                ))
        );

        LexiconData.runicAltar.pages.set(3,
                new PageInfusionRecipe<>("renaissance_core.research", new InfusionRecipe("BOTANYALTAR@3", new ItemStack(ModBlocks.runeAltar),
                        8,
                        new AspectList().add(Aspect.AURA, 60).add(Aspect.CRAFT, 60).add(Aspect.PLANT, 100).add(Aspect.EXCHANGE, 30),
                        new ItemStack(BlocksTC.infusionMatrix),
                        "livingrock",
                        "ingotManasteel",
                        "gemPrismarine",
                        "manaPearl",
                        "livingrock",
                        "ingotManasteel",
                        "gemPrismarine",
                        "manaPearl"
                ))
        );

        LexiconData.terrasteel.pages.set(1,
                new PageInfusionRecipe<>("renaissance_core.research", new InfusionRecipe("TERRAPLATE@3", new ItemStack(ModBlocks.terraPlate),
                        12,
                        new AspectList().add(Aspect.AIR, 125).add(Aspect.WATER, 125).add(Aspect.FIRE, 125).add(Aspect.EARTH, 125).add(Aspect.ENTROPY, 125).add(Aspect.ORDER, 125).add(Aspect.MAGIC, 75),
                        new ItemStack(ModBlocks.runeAltar),
                        "runeWaterB",
                        "runeFireB",
                        "runeEarthB",
                        "runeAirB",
                        "runeSpringB",
                        "runeSummerB",
                        "runeAutumnB",
                        "runeWinterB",
                        "runeManaB",
                        "runeLustB",
                        "runeGluttonyB",
                        "runeGreedB",
                        "runeSlothB",
                        "runeWrathB",
                        "runeEnvyB",
                        "runePrideB"
                ))
        );

        LexiconData.terraBlade.pages.set(1,
                new PageInfusionRecipe<>("renaissance_core.research", new InfusionRecipe("TERRASWORD", new ItemStack(ModItems.terraSword),
                        8,
                        new AspectList().add(Aspect.EARTH, 110).add(Aspect.EXCHANGE, 75).add(Aspect.LIGHT, 75).add(Aspect.AVERSION, 50),
                        ItemsTC.elementalSword,
                        "ingotTerrasteel",
                        "ingotTerrasteel"
                ))
        );

        LexiconData.terraPick.pages.set(5,
                new PageInfusionRecipe<>("renaissance_core.research", new InfusionRecipe("TERRAPICK", new ItemStack(ModItems.terraPick),
                        8,
                        new AspectList().add(Aspect.EARTH, 110).add(Aspect.EXCHANGE, 75).add(Aspect.ENTROPY, 75).add(Aspect.TOOL, 50),
                        ItemsTC.elementalPick,
                        "ingotTerrasteel",
                        "ingotTerrasteel"
                ))
        );

        LexiconData.terraAxe.pages.set(1,
                new PageInfusionRecipe<>("renaissance_core.research", new InfusionRecipe("TERRAAXE", new ItemStack(ModItems.terraAxe),
                        8,
                        new AspectList().add(Aspect.EARTH, 110).add(Aspect.PLANT, 75).add(Aspect.ENTROPY, 75).add(Aspect.TOOL, 50),
                        ItemsTC.elementalAxe,
                        "ingotTerrasteel",
                        "ingotTerrasteel"
                ))
        );
        LexiconData.alfhomancyIntro.pages.set(2,
                new PageInfusionRecipe<>("renaissance_core.research", new InfusionRecipe("ALFPORTAL", new ItemStack(ModBlocks.alfPortal),
                        16,
                        new AspectList().add(Aspect.VOID, 125).add(Aspect.EXCHANGE, 125).add(Aspect.PLANT, 125),
                        new ItemStack(ModBlocks.alchemyCatalyst),
                        "ingotVoid",
                        "ingotTerrasteel",
                        new ItemStack(ModBlocks.livingwood, 1, 5),
                        new ItemStack(BlocksTC.metalAlchemical),
                        "glowstone",
                        "ingotVoid",
                        "ingotTerrasteel",
                        new ItemStack(ModBlocks.livingwood, 1, 5),
                        new ItemStack(BlocksTC.metalAlchemical),
                        "glowstone"
                ))
        );

        LexiconData.gaiaRitual.pages.set(1,
                new PageInfusionRecipe<>("renaissance_core.research", new InfusionRecipe("WORLDOFGAIA", new ItemStack(ModBlocks.pylon, 1, 2),
                        66,
                        new AspectList().add(Aspect.DARKNESS, 66).add(Aspect.EXCHANGE, 66).add(Aspect.TRAP, 66).add(Aspect.CRYSTAL, 66).add(Aspect.PLANT, 66),
                        new ItemStack(ModBlocks.pylon, 1, 0),
                        new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumicaugmentation", "material")), 1, 5),
                        "elvenDragonstone",
                        new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumicaugmentation", "material")), 1, 5),
                        "elvenDragonstone",
                        new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumicaugmentation", "material")), 1, 5),
                        "elvenDragonstone"
                ))
        );
    }
}
