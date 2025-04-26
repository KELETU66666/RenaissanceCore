package com.keletu.renaissance_core.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemResources extends Item {

    public ItemResources() {
        this.hasSubtypes = true;
        this.addPropertyOverride(new ResourceLocation("meta"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (stack.getMetadata() == 1) {
                    return 1.0F;
                }
                return 0.0F;
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return stack.getItemDamage() == 2;
    }

    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getItem() == RCItems.coins)
            switch (stack.getItemDamage()) {
                default:
                    return I18n.format("item.rc_coins.name.0");
                case 1:
                    return I18n.format("item.rc_coins.name.1");
                case 2:
                    return I18n.format("item.rc_coins.name.2");
            }
        else return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag par4) {
        super.addInformation(stack, player, list, par4);
        if (stack.getItem() == RCItems.crimson_annales) {
            list.add(TextFormatting.DARK_PURPLE + I18n.format("tooltip.rc_book.0"));
            list.add(TextFormatting.DARK_BLUE + I18n.format("tooltip.rc_book.1"));
        }

    }
}
