package com.keletu.renaissance_core.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IWarpingGear;

import java.util.List;

public class ItemResources extends Item implements IWarpingGear {

    EnumRarity rarity;

    public ItemResources(EnumRarity rarity) {
        this.rarity = rarity;
    }

    public ItemResources() {
        this(EnumRarity.COMMON);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return this.rarity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag par4) {
        super.addInformation(stack, player, list, par4);
        if (stack.getItem() == RCItems.research_notes_crimson) {
            list.add(new TextComponentTranslation("thaumicaugmentation.text.research_notes_eldritch").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_PURPLE)).getFormattedText());
        }

    }

    @Override
    public int getWarp(ItemStack itemStack, EntityPlayer entityPlayer) {
        return itemStack.getItem() == RCItems.research_notes_crimson ? 5 : 0;
    }
}
