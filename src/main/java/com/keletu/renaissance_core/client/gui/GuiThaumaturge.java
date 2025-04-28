package com.keletu.renaissance_core.client.gui;

import com.keletu.renaissance_core.container.ContainerThaumaturge;
import com.keletu.renaissance_core.entity.EntityThaumaturge;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.lib.SoundsTC;

import java.io.IOException;


@SideOnly(Side.CLIENT)
public class GuiThaumaturge extends GuiContainer {
    EntityThaumaturge thaumaturge;
    ResourceLocation tex;

    public GuiThaumaturge(InventoryPlayer par1InventoryPlayer, World world, EntityThaumaturge thaumaturge) {
        super(new ContainerThaumaturge(par1InventoryPlayer, world, thaumaturge));
        tex = new ResourceLocation("thaumcraft", "textures/gui/gui_pech.png");
        xSize = 175;
        ySize = 232;
        this.thaumaturge = thaumaturge;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int var5 = (width - xSize) / 2;
        int var6 = (height - ySize) / 2;
        GL11.glEnable(3042);
        drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
        if (thaumaturge.isValued(inventorySlots.getSlot(0).getStack()) && !inventorySlots.getSlot(0).getStack().isEmpty() && inventorySlots.getSlot(1).getStack().isEmpty() && inventorySlots.getSlot(2).getStack().isEmpty() && inventorySlots.getSlot(3).getStack().isEmpty() && inventorySlots.getSlot(4).getStack().isEmpty()) {
            drawTexturedModalRect(var5 + 67, var6 + 24, 176, 0, 25, 25);
        }
        GL11.glDisable(3042);
    }

    protected void mouseClicked(int mx, int my, int par3) throws IOException {
        super.mouseClicked(mx, my, par3);
        int gx = (width - xSize) / 2;
        int gy = (height - ySize) / 2;
        int var7 = mx - (gx + 67);
        int var8 = my - (gy + 24);
        if (var7 >= 0 && var8 >= 0 && var7 < 25 && var8 < 25 && thaumaturge.isValued(inventorySlots.getSlot(0).getStack()) && !inventorySlots.getSlot(0).getStack().isEmpty() && inventorySlots.getSlot(1).getStack().isEmpty() && inventorySlots.getSlot(2).getStack().isEmpty() && inventorySlots.getSlot(3).getStack().isEmpty() && inventorySlots.getSlot(4).getStack().isEmpty()) {
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, 0);
            playButton();
        }
    }

    private void playButton() {
        mc.getRenderViewEntity().playSound(SoundsTC.pech_dice, 0.5f, 0.95f + mc.getRenderViewEntity().world.rand.nextFloat() * 0.1f);
    }
}