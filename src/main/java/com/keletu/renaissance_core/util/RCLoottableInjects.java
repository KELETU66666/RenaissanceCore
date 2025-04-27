package com.keletu.renaissance_core.util;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thecodex6824.thaumicaugmentation.api.TALootTables;

@EventBusSubscriber(modid = RenaissanceCore.MODID)
public class RCLoottableInjects {
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(TALootTables.PEDESTAL_RARE)) {
            LootPool main = event.getTable().getPool("pedestal_rare");
            main.addEntry(new LootEntryItem(RCItems.crimson_annales, 3, 0, new LootFunction[0], new LootCondition[0], "loottable:crimson_annales"));
        }
    }
}