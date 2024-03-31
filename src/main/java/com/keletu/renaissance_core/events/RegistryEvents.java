package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.blocks.tile.TileEtherealBloom;
import com.keletu.renaissance_core.items.RFItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
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

        GameRegistry.registerTileEntity(TileEtherealBloom.class, new ResourceLocation(RenaissanceCore.MODID, "ethereal_bloom").toString());
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

        event.getRegistry().registerAll(RFItems.thaumic_sacrificial_dagger);
        event.getRegistry().registerAll(RFItems.botany_sacrificial_dagger);

        event.getRegistry().registerAll(RFItems.thaumic_dagger_of_sacrifice);
        event.getRegistry().registerAll(RFItems.botany_dagger_of_sacrifice);

        event.getRegistry().registerAll(RFItems.mana_slate);
        event.getRegistry().registerAll(RFItems.thaumic_slate);
    }
}
