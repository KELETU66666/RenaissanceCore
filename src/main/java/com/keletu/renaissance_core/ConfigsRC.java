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

    @Config.LangKey("when cursed player sleep, have 1/2 chance give temp warp, the point is (default: 15)")
    public static int cursedSleepWarpPoint = 15;

    @Config.LangKey("when cursed player sleep, have 1/2 chance pollute flux, the point is (default: 30)")
    public static float cursedSleepPollution = 30;

    @Config.LangKey("bats spawn nearby cursed player will transform to hellfire bat (default: true)")
    public static boolean cursedPlayerTransformBats = true;

    @Config.LangKey("when cursed player use arcane workbench, vis increase (default: -200)")
    public static int cursedVisIncreasePercentage = -200;

    @Config.LangKey("cursed player actual warp multiplied (default: 2)")
    public static float cursedWarpJudgeIncreasePercentage = 2;

    @Config.LangKey("when chunk's aura lower than (default: 1), player can't regen health automatically")
    @Config.RangeDouble(min = 0, max = 0.8)
    public static double cursedPlayerRegenHealthAura = 0.8;

    @Config.LangKey("the vis will decrease player health regen multiply (default: 5) point(s)")
    @Config.RangeDouble(min = 0)
    public static float cursedPlayerRegenHealthVis = 5;

    @Config.LangKey("Protection Field's Protection Range")
    public static double protectionRange = 256D;
}