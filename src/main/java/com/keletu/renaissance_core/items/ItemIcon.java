package com.keletu.renaissance_core.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemIcon extends Item {

    public ItemIcon() {
        this.hasSubtypes = true;

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
                return 0.0F;
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getItem() == RCItems.item_icon)
            switch (stack.getItemDamage()) {
                default:
                    return I18n.translateToLocal("item.icon.name.0");
                case 1:
                    return I18n.translateToLocal("item.icon.name.1");
                case 2:
                    return I18n.translateToLocal("item.icon.name.2");
            }
        else return super.getItemStackDisplayName(stack);
    }
}
