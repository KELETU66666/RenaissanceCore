package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.container.GUIHandler;
import com.keletu.renaissance_core.packet.PacketOpenPackGui;
import com.keletu.renaissance_core.packet.PacketTogglePontifexRobe;
import com.keletu.renaissance_core.packet.PacketZap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber
public class ZapHandler {

    public static KeyBinding zepKey;
    public static KeyBinding openCloakGui;
    public static KeyBinding toggleRobe;
    private static boolean keyPressedToggleRobe = false;

    @SideOnly(Side.CLIENT)
    public static void registerKeybinds() {
        zepKey = new KeyBinding("key.zap", Keyboard.KEY_H, "key.categories.renaissancecore");
        openCloakGui = new KeyBinding("key.open_backpack", Keyboard.KEY_O, "key.categories.renaissancecore");
        toggleRobe = new KeyBinding("key.toggle_pontifex", Keyboard.KEY_C, "key.categories.renaissancecore");

        ClientRegistry.registerKeyBinding(zepKey);
        ClientRegistry.registerKeyBinding(openCloakGui);
    }
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (event.phase != TickEvent.Phase.START || Minecraft.getMinecraft().isGamePaused() || player == null)
            return;

        if (zepKey.isPressed()) {
            if (!Minecraft.getMinecraft().isGamePaused()) {
                RenaissanceCore.packetInstance.sendToServer(new PacketZap(true));
            }
        }

        if(openCloakGui.isPressed() && GUIHandler.getBaubleStack(player) != ItemStack.EMPTY) {
            RenaissanceCore.packetInstance.sendToServer(new PacketOpenPackGui());
        }

        if (toggleRobe.isPressed()) {
            if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                if (!keyPressedToggleRobe) {
                    RenaissanceCore.packetInstance.sendToServer(new PacketTogglePontifexRobe(player));
                }
                keyPressedToggleRobe = true;
            }
        } else {
            keyPressedToggleRobe = false;
        }
    }
}
