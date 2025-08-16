package com.keletu.renaissance_core.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.lib.events.PlayerEvents;

@Mixin(PlayerEvents.class)
public class MixinPlayerEvents {
    @Inject(
            method = "getRunicCharge(Lnet/minecraft/item/ItemStack;)I",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private static void modifyRunicCharge(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int base = 0;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("TC.RUNIC")) {
            base += stack.getTagCompound().getInteger("TC.RUNIC");
        }
        cir.setReturnValue(base);
    }
    
    @Inject(
            method = "getFinalWarp",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private static void mixinGetFinalWarp(ItemStack stack, EntityPlayer player, CallbackInfoReturnable<Integer> cir) {
        if (stack != null && !stack.isEmpty()) {
            int warp = 0;
            if (stack.getItem() instanceof IWarpingGear) {
                IWarpingGear armor = (IWarpingGear) stack.getItem();
                warp += armor.getWarp(stack, player);
            }

            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("TC.WARP")) {
                warp += stack.getTagCompound().getInteger("TC.WARP");
            }

            cir.setReturnValue(warp);
        } else {
            cir.setReturnValue(0);
        }
    }
}
