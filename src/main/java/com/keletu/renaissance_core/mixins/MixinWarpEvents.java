package com.keletu.renaissance_core.mixins;

import com.keletu.renaissance_core.ConfigsRC;
import com.keletu.renaissance_core.events.CursedEvents;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.lib.events.WarpEvents;

@Mixin(WarpEvents.class)
public class MixinWarpEvents {
    @Inject(method = "getWarpFromGear", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private static void getWarpFromGear(EntityPlayer player, CallbackInfoReturnable<Integer> cir) {
        int w = cir.getReturnValue();

        if (CursedEvents.hasThaumiumCursed(player))
            w *= ConfigsRC.cursedWarpJudgeIncreasePercentage;

        cir.setReturnValue(w);
    }
}