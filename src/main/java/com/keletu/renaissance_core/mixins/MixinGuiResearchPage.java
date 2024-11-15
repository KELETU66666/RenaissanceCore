package com.keletu.renaissance_core.mixins;

import com.keletu.renaissance_core.events.CursedEvents;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.client.gui.GuiResearchPage;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.lib.events.HudHandler;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.awt.*;

@Mixin(GuiResearchPage.class)
public abstract class MixinGuiResearchPage extends GuiScreen {
    @Shadow(remap = false) protected int paneHeight;

    @Shadow(remap = false)
    ResourceLocation tex1;

    @Shadow(remap = false) abstract void drawPopupAt(int x, int y, int mx, int my, String text);

    @Shadow(remap = false)
    boolean[] hasResearch;

    @Shadow(remap = false)
    boolean[] hasItem;

    @Shadow(remap = false) abstract void drawStackAt(ItemStack itemstack, int x, int y, int mx, int my, boolean clickthrough);

    @Shadow(remap = false)
    boolean[] hasCraft;

    @Shadow(remap = false)
    boolean[] hasKnow;

    @Shadow(remap = false)
    boolean hasAllRequisites;

    @Shadow(remap = false)
    int hrx;

    @Shadow(remap = false)
    int hry;

    @Shadow(remap = false)
    boolean hold;

    @Shadow(remap = false) abstract boolean mouseInside(int x, int y, int w, int h, int mx, int my);

    @Shadow(remap = false)
    ResourceLocation dummyMap;

    @Shadow(remap = false)
    ResourceLocation dummyChest;

    @Shadow(remap = false)
    ResourceLocation dummyFlask;

    @Shadow(remap = false)
    ResourceLocation dummyResearch;

    @Shadow(remap = false)
    IPlayerKnowledge playerKnowledge;
    @Shadow(remap = false) private ResearchEntry research;
    @Shadow(remap = false)
    boolean isComplete;
    @Shadow(remap = false) private int currentStage;

    @Inject(method = "drawRequirements", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void drawRequirements(int x, int mx, int my, ResearchStage stage, CallbackInfo ci) {
        int y = (height - paneHeight) / 2 - 16 + 210;
        GL11.glPushMatrix();
        boolean b = false;
        if (stage.getResearch() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.25f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x - 12, y - 1, 200, 232, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.research");
            Object loc = null;
            if (hasResearch != null) {
                if (hasResearch.length != stage.getResearch().length) {
                    hasResearch = new boolean[stage.getResearch().length];
                }
                int ss = 18;
                if (stage.getResearch().length > 6) {
                    ss = 110 / stage.getResearch().length;
                }
                for (int a = 0; a < stage.getResearch().length; ++a) {
                    String key = stage.getResearch()[a];
                    loc = ((stage.getResearchIcon()[a] != null) ? new ResourceLocation(stage.getResearchIcon()[a]) : dummyResearch);
                    String text = I18n.translateToLocal("research." + key + ".text");
                    if (key.startsWith("!")) {
                        String k = key.replaceAll("!", "");
                        Aspect as = Aspect.aspects.get(k);
                        if (as != null) {
                            loc = as;
                            text = as.getName();
                        }
                    }
                    ResearchEntry re = ResearchCategories.getResearch(key);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    if (re != null && re.getIcons() != null) {
                        int idx = (int)(System.currentTimeMillis() / 1000L % re.getIcons().length);
                        loc = re.getIcons()[idx];
                        text = re.getLocalizedName();
                    }
                    else if (key.startsWith("m_")) {
                        loc = dummyMap;
                    }
                    else if (key.startsWith("c_")) {
                        loc = dummyChest;
                    }
                    else if (key.startsWith("f_")) {
                        loc = dummyFlask;
                    }
                    else {
                        GlStateManager.color(0.5f, 0.75f, 1.0f, 1.0f);
                    }
                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    if (loc instanceof Aspect) {
                        mc.renderEngine.bindTexture(((Aspect)loc).getImage());
                        Color cc = new Color(((Aspect)loc).getColor());
                        GlStateManager.color(cc.getRed() / 255.0f, cc.getGreen() / 255.0f, cc.getBlue() / 255.0f, 1.0f);
                        UtilsFX.drawTexturedQuadFull((float)(x - 15 + shift), (float)y, zLevel);
                    }
                    else if (loc instanceof ResourceLocation) {
                        mc.renderEngine.bindTexture((ResourceLocation)loc);
                        UtilsFX.drawTexturedQuadFull((float)(x - 15 + shift), (float)y, zLevel);
                    }
                    else if (loc instanceof ItemStack) {
                        RenderHelper.enableGUIStandardItemLighting();
                        GL11.glDisable(2896);
                        GL11.glEnable(32826);
                        GL11.glEnable(2903);
                        GL11.glEnable(2896);
                        itemRender.renderItemAndEffectIntoGUI(InventoryUtils.cycleItemStack(loc), x - 15 + shift, y);
                        GL11.glDisable(2896);
                        GL11.glDepthMask(true);
                        GL11.glEnable(2929);
                    }
                    GL11.glPopMatrix();
                    if (hasResearch[a]) {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        mc.renderEngine.bindTexture(tex1);
                        GlStateManager.disableDepth();
                        drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                        GlStateManager.enableDepth();
                    }
                    drawPopupAt(x - 15 + shift, y, mx, my, text);
                    shift += ss;
                }
            }
        }
        if (stage.getObtain() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.25f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x - 12, y - 1, 200, 216, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.obtain");
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (hasItem != null) {
                if (hasItem.length != stage.getObtain().length) {
                    hasItem = new boolean[stage.getObtain().length];
                }
                int ss2 = 18;
                if (stage.getObtain().length > 6) {
                    ss2 = 110 / stage.getObtain().length;
                }
                for (int idx2 = 0; idx2 < stage.getObtain().length; ++idx2) {
                    ItemStack stack = InventoryUtils.cycleItemStack(stage.getObtain()[idx2], idx2);
                    drawStackAt(stack, x - 15 + shift, y, mx, my, true);
                    if (hasItem[idx2]) {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        mc.renderEngine.bindTexture(tex1);
                        GlStateManager.disableDepth();
                        drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                        GlStateManager.enableDepth();
                    }
                    shift += ss2;
                }
            }
        }
        if (stage.getCraft() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.25f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x - 12, y - 1, 200, 200, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.craft");
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (hasCraft != null) {
                if (hasCraft.length != stage.getCraft().length) {
                    hasCraft = new boolean[stage.getCraft().length];
                }
                int ss2 = 18;
                if (stage.getCraft().length > 6) {
                    ss2 = 110 / stage.getCraft().length;
                }
                for (int idx2 = 0; idx2 < stage.getCraft().length; ++idx2) {
                    ItemStack stack = InventoryUtils.cycleItemStack(stage.getCraft()[idx2], idx2);
                    drawStackAt(stack, x - 15 + shift, y, mx, my, true);
                    if (hasCraft[idx2]) {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        mc.renderEngine.bindTexture(tex1);
                        GlStateManager.disableDepth();
                        drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                        GlStateManager.enableDepth();
                    }
                    shift += ss2;
                }
            }
        }
        if (stage.getKnow() != null && !CursedEvents.hasThaumiumCursed(mc.player)) {
            y -= 18;
            b = true;
            int shift = 24;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.25f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x - 12, y - 1, 200, 184, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.know");
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (hasKnow != null) {
                if (hasKnow.length != stage.getKnow().length) {
                    hasKnow = new boolean[stage.getKnow().length];
                }
                int ss2 = 18;
                if (stage.getKnow().length > 6) {
                    ss2 = 110 / stage.getKnow().length;
                }
                for (int idx2 = 0; idx2 < stage.getKnow().length; ++idx2) {
                    ResearchStage.Knowledge kn = stage.getKnow()[idx2];
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glPushMatrix();
                    mc.renderEngine.bindTexture(HudHandler.KNOW_TYPE[kn.type.ordinal()]);
                    GL11.glTranslatef((float)(x - 15 + shift), (float)y, 0.0f);
                    GL11.glScaled(0.0625, 0.0625, 0.0625);
                    drawTexturedModalRect(0, 0, 0, 0, 255, 255);
                    if (kn.type.hasFields() && kn.category != null) {
                        mc.renderEngine.bindTexture(kn.category.icon);
                        GL11.glTranslatef(32.0f, 32.0f, 1.0f);
                        GL11.glPushMatrix();
                        GL11.glScaled(0.75, 0.75, 0.75);
                        drawTexturedModalRect(0, 0, 0, 0, 255, 255);
                        GL11.glPopMatrix();
                    }
                    GL11.glPopMatrix();
                    String am = "" + (hasKnow[idx2] ? "" : TextFormatting.RED) + kn.amount;
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)(x - 15 + shift + 16 - mc.fontRenderer.getStringWidth(am) / 2), (float)(y + 12), 5.0f);
                    GL11.glScaled(0.5, 0.5, 0.5);
                    mc.fontRenderer.drawStringWithShadow(am, 0.0f, 0.0f, 16777215);
                    GL11.glPopMatrix();
                    if (hasKnow[idx2]) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef(0.0f, 0.0f, 1.0f);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        mc.renderEngine.bindTexture(tex1);
                        drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
                        GL11.glPopMatrix();
                    }
                    String s = I18n.translateToLocal("tc.type." + kn.type.toString().toLowerCase());
                    if (kn.type.hasFields() && kn.category != null) {
                        s = s + ": " + ResearchCategories.getCategoryName(kn.category.key);
                    }
                    drawPopupAt(x - 15 + shift, y, mx, my, s);
                    shift += ss2;
                }
            }
        }
        if (stage.getResearch() == null && stage.getObtain() == null && stage.getCraft() == null) {
            b = true;
            int shift = 24;
        }
        if (b) {
            y -= 12;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            mc.renderEngine.bindTexture(tex1);
            drawTexturedModalRect(x + 4, y - 2, 24, 184, 96, 8);
            if (hasAllRequisites) {
                hrx = x + 20;
                hry = y - 6;
                if (hold) {
                    String s2 = I18n.translateToLocal("tc.stage.hold");
                    int m = mc.fontRenderer.getStringWidth(s2);
                    mc.fontRenderer.drawStringWithShadow(s2, x + 52 - m / 2.0f, (float)(y - 4), 16777215);
                }
                else {
                    if (mouseInside(hrx, hry, 64, 12, mx, my)) {
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    else {
                        GlStateManager.color(0.8f, 0.8f, 0.9f, 1.0f);
                    }
                    mc.renderEngine.bindTexture(tex1);
                    drawTexturedModalRect(hrx, hry, 84, 216, 64, 12);
                    String s2 = I18n.translateToLocal("tc.stage.complete");
                    int m = mc.fontRenderer.getStringWidth(s2);
                    mc.fontRenderer.drawStringWithShadow(s2, x + 52 - m / 2.0f, (float)(y - 4), 16777215);
                }
            }
        }
        GL11.glPopMatrix();
        ci.cancel();
    }

    @Inject(method = "checkRequisites", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void mixinCheckRequisites(CallbackInfo ci) {
        if (research.getStages() != null) {
            isComplete = playerKnowledge.isResearchComplete(research.getKey());
            while (currentStage >= research.getStages().length) {
                --currentStage;
            }
            if (currentStage < 0) {
                return;
            }
            hasAllRequisites = true;
            hasItem = null;
            hasCraft = null;
            hasResearch = null;
            hasKnow = null;
            ResearchStage stage = research.getStages()[currentStage];
            Object[] o = stage.getObtain();
            if (o != null) {
                hasItem = new boolean[o.length];
                for (int a = 0; a < o.length; ++a) {
                    ItemStack ts = ItemStack.EMPTY;
                    boolean ore = false;
                    if (o[a] instanceof ItemStack) {
                        ts = (ItemStack)o[a];
                    }
                    else {
                        NonNullList<ItemStack> nnl = OreDictionary.getOres((String)o[a]);
                        ts = nnl.get(0);
                        ore = true;
                    }
                    if (!(hasItem[a] = InventoryUtils.isPlayerCarryingAmount(mc.player, ts, ore))) {
                        hasAllRequisites = false;
                    }
                }
            }
            Object[] c = stage.getCraft();
            if (c != null) {
                hasCraft = new boolean[c.length];
                for (int a2 = 0; a2 < c.length; ++a2) {
                    if (!playerKnowledge.isResearchKnown("[#]" + stage.getCraftReference()[a2])) {
                        hasAllRequisites = false;
                        hasCraft[a2] = false;
                    }
                    else {
                        hasCraft[a2] = true;
                    }
                }
            }
            String[] r = stage.getResearch();
            if (r != null) {
                hasResearch = new boolean[r.length];
                for (int a3 = 0; a3 < r.length; ++a3) {
                    if (!ThaumcraftCapabilities.knowsResearchStrict(mc.player, r[a3])) {
                        hasAllRequisites = false;
                        hasResearch[a3] = false;
                    }
                    else {
                        hasResearch[a3] = true;
                    }
                }
            }
            ResearchStage.Knowledge[] k = stage.getKnow();
            if (k != null && !CursedEvents.hasThaumiumCursed(mc.player)) {
                hasKnow = new boolean[k.length];
                for (int a4 = 0; a4 < k.length; ++a4) {
                    int pk = playerKnowledge.getKnowledge(k[a4].type, k[a4].category);
                    if (pk < k[a4].amount) {
                        hasAllRequisites = false;
                        hasKnow[a4] = false;
                    }
                    else {
                        hasKnow[a4] = true;
                    }
                }
            }
            ci.cancel();
        }
    }
}
