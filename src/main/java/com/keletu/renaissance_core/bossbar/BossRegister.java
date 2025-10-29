package com.keletu.renaissance_core.bossbar;

import java.util.ArrayList;
import java.util.List;

/*
 * From mod EnderModpackTweaks by MasterEnderman
 * Link to it's repo: https://github.com/Ender-Development/EnderModpackTweaks
 * Those codes under MIT License.
 * Modified by keletu.
 *
 * */
public class BossRegister {
    private static final List<BossType> BOSSES = new ArrayList<>();

    public static BossType getBossType(String entity) {
        return BOSSES.stream().filter(bossType -> bossType.getEntity().equals(entity)).findFirst().orElse(null);
    }

    static {
        BOSSES.add(new BossType("thaumcraft:eldritchgolem", "eldritch_construct", "bar_eldritch", 30, 8, 327, 345, 64, 329, 10, 9));
        BOSSES.add(new BossType("thaumicaugmentation:eldritch_golem", "eldritch_construct", "bar_eldritch", 30, 8, 327, 345, 64, 329, 10, 9));
        BOSSES.add(new BossType("thaumcraft:eldritchwarden", "eldritch_warden", "bar_eldritch", 26, 7, 346, 360, 64, 329, 10, 7));
        BOSSES.add(new BossType("thaumicaugmentation:eldritch_warden", "eldritch_warden", "bar_eldritch", 26, 7, 346, 360, 64, 329, 10, 7));
        BOSSES.add(new BossType("thaumcraft:cultistleader", "crimson_praetor", "bar_crimson", 30, 14, 182, 210, 62, 182, 2, 2));
        BOSSES.add(new BossType("thaumcraft:taintaclegiant", "taintacle_giant", "bar_eldritch", 24, 8, 328, 344, 50, "bar_taintacle", 382, 6, 6));
        BOSSES.add(new BossType("thaumicaugmentation:primal_wisp", "primal_wisp", "bar_primal", 33, 4, 328, 336, 75, "bar_primalwisp", 328, 6, 6));
        BOSSES.add(new BossType("thaumicconcilium:brightest_one", "the_brightest", "bar_eldritch", 37, 18, 325, 361, 77, 329, 10, 6));
        //BOSSES.add(new BossType("minecraft:ender_dragon", "ender_dragon", "pink_progress", 15, 12, 162, 186, 41));
        //BOSSES.add(new BossType("mowziesmobs:ferrous_wroughtnaut", "ferrous_wroughtnaut", "red_progress", 17, 11, 163, 190, 54));
        //BOSSES.add(new BossType("mowziesmobs:frostmaw", "frostmaw", "ice_progress", 13, 20, 150, 190, 49));
        //BOSSES.add(new BossType("da:void_blossom", "void_blossom", "green_progress", 16, 8, 169, 185, 47));
        //BOSSES.add(new BossType("da:night_lich", "night_lich", "blue_progress", 18, 22, 175, 219, 49));
        //BOSSES.add(new BossType("da:great_wyrk", "ancient_wyrk", "ice_progress", 15, 13, 172, 198, 50));
        //BOSSES.add(new BossType("da:flame_knight", "flame_knight", "red_progress", 30, 21, 167, 200, 53));
        //BOSSES.add(new BossType("srparasites:anc_dreadnaut", "anc_dreadnaut", "awaken_progress", 25, 7, 167, 181, 66));
        //BOSSES.add(new BossType("srparasites:anc_overlord", "anc_overlord", "awaken_progress", 30, 8, 167, 183, 63));
        //BOSSES.add(new BossType("srparasites:beckon_siv", "beckon_siv", "parasitic_progress", 43, 8, 165, 181, 65));
        //BOSSES.add(new BossType("srparasites:dispatcher_siv", "dispatcher_siv", "parasitic_progress", 44, 8, 165, 181, 66));
        //BOSSES.add(new BossType("rats:marbled_cheese_golem", "ratlanteanautomaton", "yellow_progress", 13, 5, 174, 183, 43));
        //BOSSES.add(new BossType("rats:black_death", "blackdeath", "red_progress", 15, 20, 156, 196, 42));
        //BOSSES.add(new BossType("rats:neo_ratlantean", "neoratlantean", "blue_progress", 12, 6, 174, 186, 42));
    }
}