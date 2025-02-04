package com.keletu.renaissance_core.module.botania;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.ILexicon;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.gui.lexicon.GuiLexiconEntry;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.helper.PlayerHelper;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lexicon.page.PageRecipe;
import vazkii.botania.common.lexicon.page.PageText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LexiconRecipeArcaneWorkbench extends PageRecipe {

    private static final ResourceLocation craftingOverlay = new ResourceLocation(RenaissanceCore.MODID, "textures/gui/arcanecrafting_overlay.png");
    int relativeMouseX;
    int relativeMouseY;
    ItemStack tooltipStack;
    ItemStack tooltipContainerStack;
    boolean tooltipEntry;
    static boolean mouseDownLastTick = false;
    IArcaneRecipe recipe;
    int ticksElapsed = 0;
    boolean oreDictRecipe, shapelessRecipe;


    public LexiconRecipeArcaneWorkbench(String unlocalizedName, IArcaneRecipe recipe) {
        super(unlocalizedName);
        this.recipe = recipe;
        this.tooltipStack = ItemStack.EMPTY;
        this.tooltipContainerStack = ItemStack.EMPTY;
    }

    @Override
    public void onPageAdded(LexiconEntry entry, int index) {
        LexiconRecipeMappings.map(this.recipe.getRecipeOutput(), entry, index);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
		/*if(ticksElapsed % 20 == 0) {
			recipeAt++;

			if(recipeAt == recipes.size())
				recipeAt = 0;
		}*/
        ++ticksElapsed;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderScreen(IGuiLexiconEntry gui, int mx, int my) {
        relativeMouseX = mx;
        relativeMouseY = my;

        renderRecipe(gui, mx, my);

        int width = gui.getWidth() - 30;
        int height = gui.getHeight();
        int x = gui.getLeft() + 16;
        int y = gui.getTop() + height - 30;
        PageText.renderText(x, y, width, height, getUnlocalizedName());

        if (!tooltipStack.isEmpty()) {
            List<String> tooltipData = tooltipStack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
            List<String> parsedTooltip = new ArrayList<>();
            boolean first = true;

            for (String s : tooltipData) {
                String s_ = s;
                if (!first)
                    s_ = TextFormatting.GRAY + s;
                parsedTooltip.add(s_);
                first = false;
            }

            FontRenderer font = Minecraft.getMinecraft().fontRenderer;
            int tooltipHeight = tooltipData.size() * 10 + 2;
            int tooltipWidth = parsedTooltip.stream().map(font::getStringWidth).max((a, b) -> a - b).orElse(0);
            int rmx = mx + 12;
            int rmy = my - 12;

            vazkii.botania.client.core.helper.RenderHelper.renderTooltip(mx, my, parsedTooltip);
            GlStateManager.disableDepth();
            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(tooltipStack, parsedTooltip, rmx, rmy, font, tooltipWidth, tooltipHeight));
            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(tooltipStack, parsedTooltip, rmx, rmy, font, tooltipWidth, tooltipHeight));
            GlStateManager.enableDepth();

            int tooltipY = 8 + tooltipData.size() * 11;

            if (tooltipEntry) {
                vazkii.botania.client.core.helper.RenderHelper.renderTooltipOrange(mx, my + tooltipY, Collections.singletonList(TextFormatting.GRAY + I18n.format("botaniamisc.clickToRecipe")));
                tooltipY += 18;
            }

            if (!tooltipContainerStack.isEmpty())
                vazkii.botania.client.core.helper.RenderHelper.renderTooltipGreen(mx, my + tooltipY, Arrays.asList(TextFormatting.AQUA + I18n.format("botaniamisc.craftingContainer"), tooltipContainerStack.getDisplayName()));
        }

        tooltipStack = tooltipContainerStack = ItemStack.EMPTY;
        tooltipEntry = false;
        GlStateManager.disableBlend();
        mouseDownLastTick = Mouse.isButtonDown(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderRecipe(IGuiLexiconEntry gui, int mx, int my) {
        oreDictRecipe = shapelessRecipe = false;

        renderArcaneCraftingRecipe(gui, recipe);

        TextureManager render = Minecraft.getMinecraft().renderEngine;
        render.bindTexture(craftingOverlay);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        ((GuiScreen) gui).drawTexturedModalRect(gui.getLeft(), gui.getTop(), 0, 0, gui.getWidth(), gui.getHeight());

        int iconX = gui.getLeft() + 115;
        int iconY = gui.getTop() + 12;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (shapelessRecipe) {
            ((GuiScreen) gui).drawTexturedModalRect(iconX, iconY, 240, 0, 16, 16);

            if (mx >= iconX && my >= iconY && mx < iconX + 16 && my < iconY + 16)
                RenderHelper.renderTooltip(mx, my, Collections.singletonList(I18n.format("botaniamisc.shapeless")));

            iconY += 20;
        }

        render.bindTexture(craftingOverlay);
        GlStateManager.enableBlend();

        if (oreDictRecipe) {
            ((GuiScreen) gui).drawTexturedModalRect(iconX, iconY, 240, 16, 16, 16);

            if (mx >= iconX && my >= iconY && mx < iconX + 16 && my < iconY + 16)
                RenderHelper.renderTooltip(mx, my, Collections.singletonList(I18n.format("botaniamisc.oredict")));
        }
        GlStateManager.disableBlend();
    }

    @SideOnly(Side.CLIENT)
    public void renderArcaneCraftingRecipe(IGuiLexiconEntry gui, IArcaneRecipe recipe) {
        if (recipe == null)
            return;

        int twidth = gui.getWidth() - 30;
        int tx = gui.getLeft() + 15;
        int ty = gui.getTop() + 10;

        PageText.renderText(tx, ty, twidth, gui.getHeight(), "Vis: " + recipe.getVis());
        if (recipe instanceof ShapedArcaneRecipe) {
            int width = ((ShapedArcaneRecipe) recipe).getWidth();
            int height = ((ShapedArcaneRecipe) recipe).getHeight();
            oreDictRecipe = true;

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    Ingredient input = recipe.getIngredients().get(y * width + x);
                    ItemStack[] stacks = input.getMatchingStacks();
                    if (stacks.length > 0) {
                        renderItemAtGridPos(gui, 1 + x, 1 + y, stacks[(ticksElapsed / 40) % stacks.length], true);
                    }
                }
        } else if (recipe instanceof ShapelessArcaneRecipe) {
            shapelessRecipe = true;

            drawGrid:
            {
                for (int y = 0; y < 3; y++)
                    for (int x = 0; x < 3; x++) {
                        int index = y * 3 + x;

                        if (index >= recipe.getIngredients().size())
                            break drawGrid;

                        Ingredient input = recipe.getIngredients().get(index);
                        if (input != Ingredient.EMPTY) {
                            ItemStack[] stacks = input.getMatchingStacks();
                            renderItemAtGridPos(gui, 1 + x, 1 + y, stacks[(ticksElapsed / 40) % stacks.length], true);
                        }
                    }
            }
        }

        renderItemAtGridPos(gui, 2, 0, recipe.getRecipeOutput(), false);

        int crystalAmount = 0;
        if (recipe.getCrystals() != null) {
            for (Aspect aspect : recipe.getCrystals().getAspectsSortedByAmount()) {
                ItemStack crystal = new ItemStack(ItemsTC.crystalEssence);
                ((ItemCrystalEssence) ItemsTC.crystalEssence).setAspects(crystal, new AspectList().add(aspect, 1));
                crystal.setCount(recipe.getCrystals().getAmount(aspect));
                this.renderItemAtArcaneGridPos(gui, 1, crystalAmount * 21, crystal, false);
                ++crystalAmount;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderItemAtArcaneGridPos(IGuiLexiconEntry gui, int x, int position, ItemStack stack, boolean accountForContainer) {
        if (!stack.isEmpty()) {
            stack = stack.copy();
            if (stack.getItemDamage() == 32767) {
                stack.setItemDamage(0);
            }

            int xPos = gui.getLeft() + x * 12 + position;
            int yPos = gui.getTop() + 4 * 29 + 23;
            ItemStack stack1 = stack.copy();
            if (stack1.getItemDamage() == -1) {
                stack1.setItemDamage(0);
            }

            this.renderItem(gui, xPos, yPos, stack1, accountForContainer);
        }
    }

    public static void init() {
        LexiconData.virus.pages.set(1,
                new LexiconRecipeArcaneWorkbench("renaissance_core.research", new ShapelessArcaneRecipe(new ResourceLocation(""),
                        "HORSEVIRUS",
                        50,
                        new AspectList().add(Aspect.WATER, 2).add(Aspect.ENTROPY, 2),
                        new ItemStack(ModItems.virus, 1, 0),
                        new Object[]{
                                new ItemStack(Items.SKULL, 1, 2),
                                ModItems.vineBall,
                                new ItemStack(BlocksTC.crystalTaint),
                                "manaPearl",
                                Items.MAGMA_CREAM,
                                Items.FERMENTED_SPIDER_EYE
                        }
                ))
        );

        LexiconData.virus.pages.set(2,
                new LexiconRecipeArcaneWorkbench("renaissance_core.research", new ShapelessArcaneRecipe(new ResourceLocation(""),
                        "HORSEVIRUS",
                        50,
                        new AspectList().add(Aspect.WATER, 2).add(Aspect.ENTROPY, 2),
                        new ItemStack(ModItems.virus, 1, 1),
                        new Object[]{
                                new ItemStack(Items.SKULL, 1, 0),
                                ModItems.vineBall,
                                new ItemStack(BlocksTC.crystalTaint),
                                "manaPearl",
                                Items.MAGMA_CREAM,
                                Items.FERMENTED_SPIDER_EYE
                        }
                ))
        );

        LexiconData.alchemy.pages.set(1,
                new LexiconRecipeArcaneWorkbench("renaissance_core.research", new ShapedArcaneRecipe(new ResourceLocation(""),
                        "ALCHEMYCATALYST",
                        200,
                        new AspectList().add(Aspect.ORDER, 2).add(Aspect.WATER, 1).add(Aspect.EARTH, 1),
                        new ItemStack(ModBlocks.alchemyCatalyst),
                        "LPL",
                        "BAB",
                        "LSL",
                        'L', "livingrock",
                        'P', "manaPearl",
                        'S', "ingotManasteel",
                        'B', Items.BREWING_STAND,
                        'A', new ItemStack(BlocksTC.metalAlchemical)
                ))
        );

        LexiconData.flasks.pages.set(2,
                new LexiconRecipeArcaneWorkbench("renaissance_core.research", new ShapedArcaneRecipe(new ResourceLocation(""),
                        "LARGERVIAL",
                        75,
                        new AspectList().add(Aspect.WATER, 2),
                        new ItemStack(ModItems.vial, 1, 1),
                        "GBG",
                        "G G",
                        "GGG",
                        'B', new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thaumicaugmentation", "warded_button_greatwood"))),
                        'G', new ItemStack(ModBlocks.elfGlass)
                ))
        );

        LexiconData.decorativeBlocks.pages.set(9,
                new LexiconRecipeArcaneWorkbench("renaissance_core.research", new ShapedArcaneRecipe(new ResourceLocation(""),
                        "ALFPORTAL",
                        100,
                        new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5),
                        new ItemStack(ModBlocks.livingwood, 6, 5),
                        "LPL",
                        "PAP",
                        "LPL",
                        'L', "ingotTerrasteel",
                        'P', "livingwood",
                        'A', "glowstone"
                ))
        );

        LexiconData.pylon.pages.set(1,
                new LexiconRecipeArcaneWorkbench("renaissance_core.research", new ShapedArcaneRecipe(new ResourceLocation(""),
                        "MANACRYSTAL",
                        100,
                        new AspectList().add(Aspect.WATER, 2).add(Aspect.ORDER, 2).add(Aspect.AIR, 2),
                        new ItemStack(ModBlocks.pylon, 1, 0),
                        "LHL",
                        "PAP",
                        "LHL",
                        'L', new ItemStack(BlocksTC.crystalWater),
                        'H', new ItemStack(ModBlocks.storage, 1, 0),
                        'P', "ingotGold",
                        'A', new ItemStack(ModBlocks.storage, 1, 3)
                ))
        );

        LexiconData.alfhomancyIntro.pages.set(3,
                new LexiconRecipeArcaneWorkbench("renaissance_core.research", new ShapedArcaneRecipe(new ResourceLocation(""),
                        "ALFCRYSTAL",
                        100,
                        new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 10).add(Aspect.AIR, 5),
                        new ItemStack(ModBlocks.pylon, 1, 1),
                        "LPL",
                        "PAP",
                        "LPL",
                        'L', new ItemStack(BlocksTC.crystalEarth),
                        'P', "ingotTerrasteel",
                        'A', new ItemStack(ModBlocks.pylon, 1, 0)
                ))
        );
    }

    @SideOnly(Side.CLIENT)
    public void renderItem(IGuiLexiconEntry gui, double xPos, double yPos, ItemStack stack, boolean accountForContainer) {
        RenderItem render = Minecraft.getMinecraft().getRenderItem();
        boolean mouseDown = Mouse.isButtonDown(0);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 0.0);
        render.renderItemAndEffectIntoGUI(stack, 0, 0);
        render.renderItemOverlays(Minecraft.getMinecraft().fontRenderer, stack, 0, 0);
        GlStateManager.popMatrix();
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        int xpi = (int) xPos;
        int ypi = (int) yPos;
        if (this.relativeMouseX >= xpi && this.relativeMouseY >= ypi && this.relativeMouseX <= xpi + 16 && this.relativeMouseY <= ypi + 16) {
            this.tooltipStack = stack;
            LexiconRecipeMappings.EntryData data = LexiconRecipeMappings.getDataForStack(this.tooltipStack);
            ItemStack book = PlayerHelper.getFirstHeldItemClass(Minecraft.getMinecraft().player, ILexicon.class);
            if (data != null && (data.entry != gui.getEntry() || data.page != gui.getPageOn()) && book != null && ((ILexicon) book.getItem()).isKnowledgeUnlocked(book, data.entry.getKnowledgeType())) {
                this.tooltipEntry = true;
                if (!mouseDownLastTick && mouseDown && GuiScreen.isShiftKeyDown()) {
                    GuiLexiconEntry newGui = new GuiLexiconEntry(data.entry, (GuiScreen) gui);
                    newGui.page = data.page;
                    Minecraft.getMinecraft().displayGuiScreen(newGui);
                }
            } else {
                this.tooltipEntry = false;
            }

            if (accountForContainer) {
                ItemStack containerStack = stack.getItem().getContainerItem(stack);
                if (!containerStack.isEmpty()) {
                    this.tooltipContainerStack = containerStack;
                }
            }
        }

        GlStateManager.disableLighting();
    }
}
