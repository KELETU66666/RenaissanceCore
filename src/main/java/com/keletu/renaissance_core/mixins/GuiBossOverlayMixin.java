package com.keletu.renaissance_core.mixins;

import com.keletu.renaissance_core.bossbar.ImprovedBossBarRenderer;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.world.BossInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


/*
 * From mod EnderModpackTweaks by MasterEnderman
 * Link to it's repo: https://github.com/Ender-Development/EnderModpackTweaks
 * Those codes under MIT License.
 *
 * */
@Mixin(GuiBossOverlay.class)
public class GuiBossOverlayMixin {
    @Shadow
    @Final
    private Minecraft client;

    @Unique
    public ImprovedBossBarRenderer enderModpackTweaks$improvedBossBarRenderer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initMixin(Minecraft clientIn, CallbackInfo ci) {
        enderModpackTweaks$improvedBossBarRenderer = new ImprovedBossBarRenderer(client);
    }

    @ModifyArgs(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;bossBarRenderPre(Lnet/minecraft/client/gui/ScaledResolution;Lnet/minecraft/client/gui/BossInfoClient;III)Lnet/minecraftforge/client/event/RenderGameOverlayEvent$BossInfo;", remap = false))
    private void renderBossHealthModifyArgs(Args args) {
        //if (CfgFeatures.BOSS_BAR.enable) {
        int overlayHeight = enderModpackTweaks$improvedBossBarRenderer.getOverlayHeight(args.get(1));
        args.set(4, overlayHeight == 0 ? args.get(4) : overlayHeight);
        //}
    }

    @WrapMethod(method = "render")
    private void renderWrap(int x, int y, BossInfo info, Operation<Void> original) {
        if (/*CfgFeatures.BOSS_BAR.enable && */enderModpackTweaks$improvedBossBarRenderer.render(x, y, info)) {
            return;
        }
        original.call(x, y, info);
    }
}