package com.keletu.renaissance_core;

import net.minecraftforge.common.config.Config;

@Config(modid = RenaissanceCore.MODID)
public class ConfigsRF
{
	@Config.LangKey("Pods Aspect amuont")
	@Config.RangeInt(min = 1, max = 128)
	@Config.RequiresMcRestart
	public static int podAspect = 5;

	@Config.LangKey("ChampionsChance")
	@Config.RangeInt(min = 1, max = 100)
	@Config.RequiresMcRestart
	public static int championChance = 7;
}