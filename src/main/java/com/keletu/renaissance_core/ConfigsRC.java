package com.keletu.renaissance_core;

import net.minecraftforge.common.config.Config;

@Config(modid = RenaissanceCore.MODID)
public class ConfigsRC {
    @Config.LangKey("Pods Aspect amuont")
    @Config.RangeInt(min = 1, max = 128)
    @Config.RequiresMcRestart
    public static int podAspect = 5;

    @Config.LangKey("ChampionsChance")
    @Config.RangeInt(min = 1, max = 100)
    @Config.RequiresMcRestart
    public static int championChance = 7;

    @Config.LangKey("Max flowers that endoflame's efficiency begin to reduce")
    public static int endoFlameMaxFlowers = 5;

    @Config.LangKey("Set how much MANA a TNT can generate for Entropinnyum")
    public static int ENTROPINNYUM_GENERATING = 8000;

    @Config.LangKey("Changes Botania Recipes")
    public static boolean CHANGE_BOTANIA_RECIPE = false;
    @Config.LangKey("Protection Field's Protection Range")
    public static double protectionRange = 256D;
}