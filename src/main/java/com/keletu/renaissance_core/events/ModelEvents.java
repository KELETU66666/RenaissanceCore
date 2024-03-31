package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.blocks.tile.TileEtherealBloom;
import com.keletu.renaissance_core.client.TileEtherealBloomRenderer;
import com.keletu.renaissance_core.items.RFItems;
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
        defaultModel(RFItems.rift_feed);
        defaultModel(RFItems.etherealBloomItem);

        defaultModel(RFItems.bloodyThaumiumItem);
        defaultModel(RFItems.bloodyVoidItem);
        defaultModel(RFItems.bloodyIchoriumItem);
        defaultModel(RFItems.thaumicRuneItem);
        defaultModel(RFItems.botanyRuneItem);

        defaultModel(RFItems.mana_slate);
        defaultModel(RFItems.thaumic_slate);

        defaultModel(RFItems.botany_sacrificial_dagger);
        defaultModel(RFItems.thaumic_sacrificial_dagger);

        defaultModel(RFItems.botany_dagger_of_sacrifice);
        defaultModel(RFItems.thaumic_dagger_of_sacrifice);
    }

    static void defaultModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void modelRegistryEvent(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEtherealBloom.class, new TileEtherealBloomRenderer());
    }
}
