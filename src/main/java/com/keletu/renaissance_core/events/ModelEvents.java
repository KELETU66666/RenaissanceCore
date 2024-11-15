package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.blocks.tile.TileEtherealBloom;
import com.keletu.renaissance_core.blocks.tile.TileManaPod;
import com.keletu.renaissance_core.client.TileEtherealBloomRenderer;
import com.keletu.renaissance_core.client.render.TileManaPodRenderer;
import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ModelEvents {
    @SubscribeEvent
    public static void regModels(ModelRegistryEvent event) {
        defaultModel(RCItems.rift_feed);
        defaultModel(RCItems.etherealBloomItem);
        defaultModel(RCItems.mana_bean);
        defaultModel(RCItems.arcane_lime_powder);
        defaultModel(RCItems.dice12);

        defaultModel(RCItems.pechHeadNormal);
        defaultModel(RCItems.pechHeadHunter);
        defaultModel(RCItems.pechHeadThaumaturge);
    }

    static void defaultModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void modelRegistryEvent(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEtherealBloom.class, new TileEtherealBloomRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileManaPod.class, new TileManaPodRenderer());
    }
}
