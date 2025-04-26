package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ModelEvents {
    @SubscribeEvent
    public static void regModels(ModelRegistryEvent event) {
        defaultModel(RCItems.arcane_lime_powder);
        defaultModel(RCItems.dice12);
        defaultModel(RCItems.pech_backpack);
        defaultModel(RCItems.elixir);
        defaultModel(RCItems.coins);
        defaultModel(RCItems.coins, 1);
        defaultModel(RCItems.coins, 2);
        defaultModel(RCItems.pontifex_hood);
        defaultModel(RCItems.pontifex_robe);
        defaultModel(RCItems.pontifex_legs);
        defaultModel(RCItems.pontifex_boots);
        defaultModel(RCItems.molot);
        defaultModel(RCItems.crimson_annales);

        defaultModel(RCItems.pechHeadNormal);
        defaultModel(RCItems.pechHeadHunter);
        defaultModel(RCItems.pechHeadThaumaturge);
        defaultModel(RCItems.quicksilverCrucible);
    }

    static void defaultModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    static void defaultModel(Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
