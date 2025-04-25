package com.keletu.renaissance_core.container;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.keletu.renaissance_core.client.gui.GuiPechBackpack;
import com.keletu.renaissance_core.client.gui.GuiThaumaturge;
import com.keletu.renaissance_core.entity.Thaumaturge;
import com.keletu.renaissance_core.items.ItemPechBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;


public class GUIHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                ItemStack cloakStack = getBaubleStack(player);
                if (!cloakStack.isEmpty() && cloakStack.getItem() instanceof ItemPechBackpack) {
                    return new ContainerPack(player.inventory, world, cloakStack);
                }
                break;
            case 1:
                return new ContainerThaumaturge(player.inventory, world, (Thaumaturge) world.getEntityByID(x));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                ItemStack cloakStack = getBaubleStack(player);
                if (!cloakStack.isEmpty() && cloakStack.getItem() instanceof ItemPechBackpack) {
                    return new GuiPechBackpack(player.inventory, world, cloakStack);
                }
                break;
            case 1:
                return new GuiThaumaturge(player.inventory, world, (Thaumaturge) world.getEntityByID(x));
        }
        return null;
    }

    public static ItemStack getBaubleStack(EntityPlayer player) {
        try {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            if (baubles != null) {
                for (int i = 0; i < baubles.getSlots(); i++) {
                    ItemStack stack = baubles.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemPechBackpack) {
                        return stack;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemStack.EMPTY;
    }
}