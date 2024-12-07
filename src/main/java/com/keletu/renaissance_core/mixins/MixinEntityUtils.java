package com.keletu.renaissance_core.mixins;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.lib.utils.EntityUtils;

@Pseudo
@Mixin(value = {EntityUtils.class})
public abstract class MixinEntityUtils {
    @Inject(method = {"makeTainted"}, at = {@At(value = "HEAD")}, remap = false, cancellable = true)
    private static void mixinMakeTainted(EntityLivingBase target, CallbackInfo ci) {
        ci.cancel();
    }
}
