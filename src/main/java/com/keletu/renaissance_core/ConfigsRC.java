package com.keletu.renaissance_core;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    @Config.Comment("when cursed player use arcane workbench, vis increase (default: -100)")
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

    @Mod.EventBusSubscriber
    private static class EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(RenaissanceCore.MODID)) {
                ConfigManager.sync(RenaissanceCore.MODID, Config.Type.INSTANCE);
            }
        }
    }
}