package com.keletu.renaissance_core;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class RCLoader implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        ArrayList<String> list = new ArrayList<>();
        list.add("mixins.renaissance_core.json");
        if (!Loader.isModLoaded("oldresearch"))
            list.add("mixins.rc_research_change.json");
        if(Loader.isModLoaded("botania"))
            list.add("mixins.rc_botania.json");
        return list;
    }
}