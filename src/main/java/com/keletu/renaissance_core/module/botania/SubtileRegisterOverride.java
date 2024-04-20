package com.keletu.renaissance_core.module.botania;

import com.google.common.collect.BiMap;
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
        registerSubTileForce(LibBlockNames.SUBTILE_ENTROPINNYUM, SubTileEntropinnyumModified.class);
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