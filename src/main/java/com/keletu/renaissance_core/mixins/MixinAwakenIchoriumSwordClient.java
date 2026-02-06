package com.keletu.renaissance_core.mixins;

import com.nekokittygames.thaumictinkerer.common.items.Kami.Tools.IchoriumSwordAdv;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(IchoriumSwordAdv.class)
public abstract class MixinAwakenIchoriumSwordClient extends ItemSword {

    public MixinAwakenIchoriumSwordClient(ToolMaterial material) {
        super(material);
    }

    @Inject(method = {"addInformation"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void mixinOnLeftClickEntity(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn, CallbackInfo ci) {
        if (stack.getTagCompound() != null && stack.getTagCompound().getInteger("awaken") == 1) {
            tooltip.add(TextFormatting.RED + I18n.translateToLocal("tip.awakensword.mutant.name1"));
        } else if (stack.getTagCompound() != null && stack.getTagCompound().getInteger("awaken") == 2) {
            tooltip.add(TextFormatting.BLUE + I18n.translateToLocal("tip.awakensword.mutant.name2"));
        } else {
            tooltip.add(TextFormatting.DARK_GREEN + I18n.translateToLocal("tip.awakensword.mutant.name0"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
        ci.cancel();
    }
}
