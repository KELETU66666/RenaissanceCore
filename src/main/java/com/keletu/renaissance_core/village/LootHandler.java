package com.keletu.renaissance_core.village;

import com.google.common.collect.ImmutableList;
import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public final class LootHandler {

	private static final List<String> TABLES = ImmutableList.of(
			"inject/abandoned_mineshaft", "inject/desert_pyramid",
			"inject/jungle_temple", "inject/simple_dungeon",
			"inject/stronghold_corridor", "inject/village_blacksmith"
			);

	public LootHandler() {
		for (String s : TABLES) {
			LootTableList.register(new ResourceLocation(RenaissanceCore.MODID, s));
		}
	}

	@SubscribeEvent
	public void lootLoad(LootTableLoadEvent evt) {
		String prefix = "minecraft:chests/";
		String name = evt.getName().toString();

		if (name.startsWith(prefix)) {
			String file = name.substring(name.indexOf(prefix) + prefix.length());
			switch (file) {
			case "abandoned_mineshaft":
			case "desert_pyramid":
			case "jungle_temple":
			case "simple_dungeon":
			case "stronghold_corridor":
			case "village_blacksmith": evt.getTable().addPool(getInjectPool(file)); break;
			default: break;
			}
		}
	}

	private LootPool getInjectPool(String entryName) {
		return new LootPool(new LootEntry[] { getInjectEntry(entryName, 1) }, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "te_inject_pool");
	}

	private LootEntryTable getInjectEntry(String name, int weight) {
		return new LootEntryTable(new ResourceLocation(RenaissanceCore.MODID, "inject/" + name), weight, 0, new LootCondition[0], "te_inject_entry");
	}

}