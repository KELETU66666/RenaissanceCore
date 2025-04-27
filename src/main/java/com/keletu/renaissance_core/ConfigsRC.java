package com.keletu.renaissance_core;

import net.minecraftforge.common.config.Config;

@Config(modid = RenaissanceCore.MODID)
public class ConfigsRC {
    @Config.Comment("Max flowers that endoflame's efficiency begin to reduce")
    public static int endoFlameMaxFlowers = 5;

    @Config.Comment("Set how much MANA a TNT can generate for Entropinnyum")
    public static int ENTROPINNYUM_GENERATING = 8000;

    @Config.LangKey("changeBotaniaRecipe")
    @Config.Comment("Changes Botania Recipes")
    public static boolean CHANGE_BOTANIA_RECIPE = false;

    @Config.LangKey("cursedSleepWarpPoint")
    @Config.Comment("when cursed player sleep, have 1/2 chance give temp warp, the point is (default: 15)")
    public static int cursedSleepWarpPoint = 15;

    @Config.LangKey("cursedSleepPollution")
    @Config.Comment("when cursed player sleep, have 1/2 chance pollute flux, the point is (default: 30)")
    public static float cursedSleepPollution = 30F;

    @Config.LangKey("canRemovePermanentWarp")
    @Config.Comment("can cursed player can remove permanent warps (default: false)")
    public static boolean canRemovePermanentWarp = false;

    @Config.LangKey("cursedVisIncreasePercentage")
    @Config.Comment("when cursed player use arcane workbench, vis increase (default: -150)")
    public static int cursedVisIncreasePercentage = -100;

    @Config.LangKey("cursedWarpJudgeIncreasePercentage")
    @Config.Comment("cursed player actual warp multiplied (default: 2)")
    public static float cursedWarpJudgeIncreasePercentage = 2F;

    @Config.LangKey("cursedPlayerRegenHealthAura")
    @Config.Comment("when chunk's aura lower than (default: 1), player can't regen health automatically")
    @Config.RangeDouble(min = 0, max = 0.8)
    public static double cursedPlayerRegenHealthAura = 0.8F;

    @Config.LangKey("cursedPlayerRegenHealthVis")
    @Config.Comment("the vis will decrease player health regen multiply (default: 5) point(s)")
    @Config.RangeDouble(min = 0)
    public static float cursedPlayerRegenHealthVis = 5F;

    @Config.LangKey("thaumaturgeSpawnChance")
    @Config.Comment("Thaumaturge Golem spawn chance")
    @Config.RangeDouble(min = 0)
    public static int thaumaturgeSpawnChance = 10;

    @Config.LangKey("vengefulGolemSpawnChance")
    @Config.Comment("Vengeful Golem spawn chance")
    @Config.RangeDouble(min = 0)
    public static int vengefulGolemSpawnChance = 10;

    @Config.LangKey("quicksilverImmortality")
    @Config.Comment("Will quicksilver elemental be immune to non-fire attacks.")
    public static boolean quicksilverImmortality = true;

    @Config.LangKey("quicksilverElementalSpawnChance")
    @Config.RangeDouble(min = 0, max = 99)
    public static int quicksilverElementalSpawnChance = 10;
    @Config.LangKey("dissolvedSpawnChance")
    @Config.RangeDouble(min = 0, max = 99)
    public static int dissolvedSpawnChance = 10;
    @Config.LangKey("overanimatedSpawnChance")
    @Config.RangeDouble(min = 0, max = 99)
    public static int overanimatedSpawnChance = 10;

    @Config.LangKey("strayedMirrorSpawnChance")
    @Config.RangeDouble(min = 0, max = 99)
    public static int strayedMirrorSpawnChance = 10;

    @Config.LangKey("paranoidWarriorSpawnChance")
    @Config.RangeDouble(min = 0, max = 99)
    public static int paranoidWarriorSpawnChance = 10;

    @Config.LangKey("madThaumaturgeSpawnChance")
    @Config.RangeDouble(min = 0, max = 99)
    public static int madThaumaturgeSpawnChance = 10;

    @Config.LangKey("madThaumaturgeSpawnChance")
    @Config.RangeDouble(min = 0, max = 99)
    public static int madThaumaturgeReplacesBrainyZombieChance = 80;


    @Config.LangKey("madThaumaturgeSpawnChance")
    @Config.RangeDouble(min = 0, max = 99)
    public static int crimsonPaladinReplacesCultistWarriorChance = 80;

}