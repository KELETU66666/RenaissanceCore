package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.capability.IT12Capability;
import com.keletu.renaissance_core.events.CursedEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRCElixir extends Item {
    public ItemRCElixir() {
        this.setMaxStackSize(16);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (!worldIn.isRemote)
            entityLiving.curePotionEffects(stack); // FORGE - move up so stack.shrink does not turn stack into air

        if (entityLiving instanceof EntityPlayer) {
            IT12Capability.get((EntityPlayer) entityLiving).setCanPickOffT12(true);
        }

        if (entityLiving instanceof EntityPlayer && !((EntityPlayer) entityLiving).capabilities.isCreativeMode && !worldIn.isRemote) {
            EntityItem item = new EntityItem(worldIn, entityLiving.posX, entityLiving.posY, entityLiving.posZ, new ItemStack(ItemsTC.phial));
            stack.shrink(1);
            worldIn.spawnEntity(item);
        }

        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }


    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return RCItems.rarityEric;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }


    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(ItemsTC.phial);
    }

    @Override
    public Item setContainerItem(Item containerItem) {
        return ItemsTC.phial;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (Minecraft.getMinecraft().player != null && CursedEvents.hasThaumiumCursed(Minecraft.getMinecraft().player))
            list.add(TextFormatting.DARK_RED + I18n.format("tooltip.t12_elixir.tip1"));
        else
            list.add(I18n.format("tooltip.t12_elixir.tip2"));
    }
}