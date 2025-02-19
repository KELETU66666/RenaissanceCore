package com.keletu.renaissance_core.mixins;

import com.keletu.renaissance_core.ConfigsRC;
import net.minecraft.util.text.translation.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.GuiResearchBrowser;

import java.util.ArrayList;

@Mixin(GuiResearchBrowser.class)
public class MixinResearchItem {

    @Shadow(remap = false)
    private
    ResearchEntry currentHighlight;

    @Inject(method = "genResearchBackgroundFixedPost", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    public void injectGenResearchBackgroundFixedPost(int mx, int my, float par3, int locX, int locY, CallbackInfo ci, int c, ArrayList<String> text) {
        if (!getLocalizedText().equals(currentHighlight.getName() + ".text") || ConfigsRC.debugMode)
            text.add(getLocalizedText());
    }

    public String getLocalizedText() {
        return I18n.translateToLocal(currentHighlight.getName() + ".text");
    }
}
