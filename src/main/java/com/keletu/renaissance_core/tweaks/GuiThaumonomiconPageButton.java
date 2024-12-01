package com.keletu.renaissance_core.tweaks;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.Thaumcraft;

public class GuiThaumonomiconPageButton extends GuiButton {
	private final GuiContainer parentGui;
	public static final ResourceLocation background =
			new ResourceLocation(RenaissanceCore.MODID, "textures/gui/book_button.png");

	public GuiThaumonomiconPageButton(int buttonId, GuiContainer parentGui, int x, int y, int width, int height) {
		super(buttonId, x, parentGui.getGuiTop() + y, width, height, "button.thaumonomicon");
		this.parentGui = parentGui;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(this.hovered && enabled) {
			mc.player.openGui(Thaumcraft.instance, 12, mc.player.world, 0, 0, 0);
		}
		return this.hovered;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			int x = this.x + this.parentGui.getGuiLeft();

			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(background);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
			int k = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 200);
			if (k == 1) {
				this.drawTexturedModalRect(x, this.y, 21, 0, 20, 18);
			} else {
				this.drawTexturedModalRect(x, this.y, 21, 19, 20, 18);
				this.drawCenteredString(fontrenderer, I18n.format(this.displayString), x + 5, this.y + this.height, 0xffffff);
			}
			GlStateManager.popMatrix();

			this.mouseDragged(mc, mouseX, mouseY);
		}
	}

}