package com.keletu.renaissance_core.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.lib.SoundsTC;

import java.util.List;

public class ItemCrimsonAnnales extends Item implements IWarpingGear {

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (!ThaumcraftCapabilities.knowsResearch(player, "!CrimsonAnnales")) {
                if (ThaumcraftCapabilities.knowsResearch(player, "ELDRITCH_SPIRE") && ThaumcraftCapabilities.knowsResearch(player, "THAUMIC_CONCILIUM")) {
                    ThaumcraftApi.internalMethods.completeResearch(player, "!CrimsonAnnales");
                    world.playSound(null, player.getPosition(), SoundsTC.learn, SoundCategory.PLAYERS, 0.75F, 1.0F);
                } else {
                    player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tooltip.rc_book.2")).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag par4) {
        super.addInformation(stack, player, list, par4);
        if (stack.getItem() == RCItems.crimson_annales) {
            list.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("tooltip.rc_book.0"));
            list.add(TextFormatting.DARK_BLUE + I18n.translateToLocal("tooltip.rc_book.1"));
        }
    }

    @Override
    public int getWarp(ItemStack itemStack, EntityPlayer entityPlayer) {
        return 5;
    }
}
