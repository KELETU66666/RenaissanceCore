package com.keletu.renaissance_core.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
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
import thaumcraft.common.lib.SoundsTC;

import javax.annotation.Nullable;

public class ItemResearchPage extends Item {

    public ItemResearchPage() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);

        this.addPropertyOverride(new ResourceLocation("meta"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (stack.getMetadata() == 1) {
                    return 1.0F;
                }
                if (stack.getMetadata() == 2) {
                    return 2.0F;
                }
                if (stack.getMetadata() == 3) {
                    return 3.0F;
                }
                if (stack.getMetadata() == 4) {
                    return 4.0F;
                }
                return 0.0F;
            }
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            switch (stack.getItemDamage()) {
                case 0:
                    if (!ThaumcraftCapabilities.knowsResearch(player, "!RunicWindings")) {
                        if (ThaumcraftCapabilities.knowsResearch(player, "RUNICSHIELDING") && ThaumcraftCapabilities.knowsResearch(player, "FM_CRYSTAL_SCRIBING_TOOLS")) {
                            ThaumcraftApi.internalMethods.completeResearch(player, "!RunicWindings");
                            world.playSound(null, player.getPosition(), SoundsTC.learn, SoundCategory.PLAYERS, 0.75F, 1.0F);
                            stack.shrink(1);
                        } else {
                            player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tooltip.rc_book.4")).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                        }
                    }
                    break;
                case 1:
                    if (!ThaumcraftCapabilities.knowsResearch(player, "!FocusImpulse")) {
                        if (ThaumcraftCapabilities.knowsResearch(player, "FOCUSBOLT") && ThaumcraftCapabilities.knowsResearch(player, "FOCUSPROJECTILE")) {
                            ThaumcraftApi.internalMethods.completeResearch(player, "!FocusImpulse");
                            world.playSound(null, player.getPosition(), SoundsTC.learn, SoundCategory.PLAYERS, 0.75F, 1.0F);
                            stack.shrink(1);
                        } else {
                            player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tooltip.rc_book.4")).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                        }
                    }
                    break;
                case 2:
                    if (!ThaumcraftCapabilities.knowsResearch(player, "!FocusPositiveBurst")) {
                        if (ThaumcraftCapabilities.knowsResearch(player, "FOCUSHEAL") && ThaumcraftCapabilities.knowsResearch(player, "FOCUSCURSE")) {
                            ThaumcraftApi.internalMethods.completeResearch(player, "!FocusPositiveBurst");
                            world.playSound(null, player.getPosition(), SoundsTC.learn, SoundCategory.PLAYERS, 0.75F, 1.0F);
                            stack.shrink(1);
                        } else {
                            player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tooltip.rc_book.4")).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                        }
                    }
                    break;
                case 3:
                    if (!ThaumcraftCapabilities.knowsResearch(player, "!FocusReflection")) {
                        if (ThaumcraftCapabilities.knowsResearch(player, "TWOND_LETHE_WATER") && ThaumcraftCapabilities.knowsResearch(player, "BASEELDRITCH")) {
                            ThaumcraftApi.internalMethods.completeResearch(player, "!FocusReflection");
                            world.playSound(null, player.getPosition(), SoundsTC.learn, SoundCategory.PLAYERS, 0.75F, 1.0F);
                            stack.shrink(1);
                        } else {
                            player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tooltip.rc_book.4")).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                        }
                    }
                    break;
                case 4:
                    if (!ThaumcraftCapabilities.knowsResearch(player, "!BottleThickTaint")) {
                        if (ThaumcraftCapabilities.knowsResearch(player, "BOTTLETAINT")) {
                            ThaumcraftApi.internalMethods.completeResearch(player, "!BottleThickTaint");
                            world.playSound(null, player.getPosition(), SoundsTC.learn, SoundCategory.PLAYERS, 0.75F, 1.0F);
                            stack.shrink(1);
                        } else {
                            player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("tooltip.rc_book.4")).setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                        }
                    }
                    break;
            }
        }
        return super.onItemRightClick(world, player, hand);
    }
}
