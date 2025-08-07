package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.util.PolishRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.client.gui.GuiResearchPage;
import thaumcraft.client.lib.UtilsFX;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

@Mod.EventBusSubscriber
public class ClientGuiEvent {

    public static ResourceLocation TEX_CRYSTAL = new ResourceLocation("renaissance_core", "textures/gui/crystal_book.png");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onDrawPostGui(GuiScreenEvent.DrawScreenEvent event) {
        GuiScreen screen = event.getGui();
        if (screen instanceof GuiResearchPage) {
            Field index = ReflectionHelper.findField(GuiResearchPage.class, "recipePage");
            Field recipes = ReflectionHelper.findField(GuiResearchPage.class, "recipeLists");
            Field shown = ReflectionHelper.findField(GuiResearchPage.class, "shownRecipe");

            try {
                int recipePage = (int) index.get(screen);
                LinkedHashMap maps = (LinkedHashMap) recipes.get(screen);
                ResourceLocation loc = (ResourceLocation) shown.get(screen);

                if (maps == null || loc == null) {
                    return;
                }
                List<Object> list = (List<Object>) maps.get(loc);

                if (list != null && !list.isEmpty()) {
                    Object recipe = list.get(recipePage % list.size());

                    if (recipe instanceof InfusionRecipe) {
                        drawPolishEnchantmentRecipe((InfusionRecipe) recipe, event);
                    }
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void drawPolishEnchantmentRecipe(InfusionRecipe recipe, GuiScreenEvent.DrawScreenEvent event) {
        GuiScreen screen = event.getGui();

        int x = (screen.width - 256) / 2 + 128;
        int y = (screen.height - 256) / 2 + 128;

        AspectList list = PolishRecipe.getPolishmentRecipe((ItemStack) recipe.recipeOutput);
        if (list != null) {
            GL11.glPushMatrix();
            (Minecraft.getMinecraft()).renderEngine.bindTexture(TEX_CRYSTAL);
            GL11.glTranslatef((x - 13), (y - 124), 0.0F);
            GL11.glScalef(0.5F, 0.5F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
            screen.drawTexturedModalRect(30, 0, 46, 45, 161, 162);
            GL11.glPopMatrix();

            UtilsFX.drawTag((x + 43), (y - 85), list.getAspects()[0], list.getAmount(list.getAspects()[0]), 0, event.getGui().zLevel);
        }
    }
}