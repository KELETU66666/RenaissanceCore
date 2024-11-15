package com.keletu.renaissance_core.mixins;

import com.keletu.renaissance_core.events.CursedEvents;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;

@Mixin(PacketSyncProgressToServer.class)
public class MixinPacketSyncProgressToServer {
    @Inject(method = "checkRequisites", at = @At(value = "RETURN", ordinal = 5), remap = false, cancellable = true)
    private void checkRequisites(EntityPlayer player, String key, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(CursedEvents.hasThaumiumCursed(player));
    }
}
