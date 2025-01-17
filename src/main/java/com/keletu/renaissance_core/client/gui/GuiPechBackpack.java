package com.keletu.renaissance_core.client.gui;

import com.keletu.renaissance_core.container.ContainerPack;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiPechBackpack extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("renaissance_core:textures/gui/pech_backpack.png");
    InventoryPlayer playerInventory;

    public GuiPechBackpack(InventoryPlayer inventoryPlayer, World world, ItemStack cloak) {
        super(new ContainerPack(inventoryPlayer, world, cloak));
        playerInventory = inventoryPlayer;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("container.inventory"), 4, this.ySize - 92, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
    }
}