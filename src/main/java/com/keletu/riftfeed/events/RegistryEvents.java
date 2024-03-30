package com.keletu.riftfeed.events;

import com.keletu.riftfeed.RiftFeedMod;
import com.keletu.riftfeed.blocks.RFBlocks;
import com.keletu.riftfeed.blocks.tile.TileEtherealBloom;
import com.keletu.riftfeed.items.RFItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class RegistryEvents {
    @SubscribeEvent
    public static void regBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(RFBlocks.ethereal_bloom);
        event.getRegistry().registerAll(RFBlocks.thaumic_rune);
        event.getRegistry().registerAll(RFBlocks.mana_rune);
        event.getRegistry().registerAll(RFBlocks.bloody_thaumium);
        event.getRegistry().registerAll(RFBlocks.bloody_void);
        event.getRegistry().registerAll(RFBlocks.bloody_ichorium);

        GameRegistry.registerTileEntity(TileEtherealBloom.class, new ResourceLocation(RiftFeedMod.MODID, "ethereal_bloom").toString());
    }

    @SubscribeEvent
    public static void regItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(RFItems.rift_feed);
        event.getRegistry().registerAll(RFItems.etherealBloomItem);

        event.getRegistry().registerAll(RFItems.bloodyThaumiumItem);
        event.getRegistry().registerAll(RFItems.bloodyVoidItem);
        event.getRegistry().registerAll(RFItems.bloodyIchoriumItem);
        event.getRegistry().registerAll(RFItems.thaumicRuneItem);
        event.getRegistry().registerAll(RFItems.botanyRuneItem);

        event.getRegistry().registerAll(RFItems.mana_slate);
        event.getRegistry().registerAll(RFItems.thaumic_slate);
    }
}
