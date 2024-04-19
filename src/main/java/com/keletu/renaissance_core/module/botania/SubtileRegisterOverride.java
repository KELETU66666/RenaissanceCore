package com.keletu.renaissance_core.module.botania;

import com.google.common.collect.BiMap;
import com.keletu.renaissance_core.ConfigsRF;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.common.lib.LibBlockNames;

import java.lang.reflect.Field;

public class SubtileRegisterOverride {

    public final boolean successInject;
    final BiMap<String, Class<? extends SubTileEntity>> subTiles;

    public SubtileRegisterOverride() {
        subTiles = getStaticField(BotaniaAPI.class, "subTiles");

        successInject = (subTiles != null);

    }

    public void reRegisterSubtile() {
            //重新注册热爆花（产魔8000，阻止刷TNT）
        registerSubTileForce(LibBlockNames.SUBTILE_ENTROPINNYUM, SubTileEntropinnyumModified.class);
            //重新注册火红莲（禁止堆）
        registerSubTileForce(LibBlockNames.SUBTILE_ENDOFLAME, SubTileEndoflameModified.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T getStaticField(Class<?> type, String name) {
        try {
            Field field = null;
            field = type.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerSubTileForce(String key, Class<? extends SubTileEntity> subtileClass) {
        subTiles.forcePut(key, subtileClass);
    }
}