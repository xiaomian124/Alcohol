package com.xiaomian124.alcohol.recipe;

import org.bukkit.Material;

public enum MachineType {

    COOKING_POT("烹饪锅", Material.CAULDRON),
    BREWING_BOX("酿造炉", Material.SMOKER),
    APPLE_PRESS("苹果压榨器", Material.LOOM),
    AGING_BARREL("陈酿桶", Material.BARREL),
    GRAPE_BASIN("葡萄藤盆", Material.COMPOSTER),
    MUSIC_PLAYER("吧台音乐播放器", Material.JUKEBOX);

    public final String displayName;
    public final Material icon;

    MachineType(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public static MachineType byName(String name) {
        if (name == null) return null;
        for (MachineType t : values()) {
            if (t.name().equalsIgnoreCase(name)) return t;
        }
        return null;
    }
}