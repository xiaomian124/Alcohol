package com.xiaomian124.alcohol.recipe;

import com.xiaomian124.alcohol.LanguageManager;
import org.bukkit.Material;

public enum MachineType {

    COOKING_POT(Material.CAULDRON),
    BREWING_BOX(Material.SMOKER),
    APPLE_PRESS(Material.LOOM),
    AGING_BARREL(Material.BARREL),
    GRAPE_BASIN(Material.COMPOSTER),
    MUSIC_PLAYER(Material.JUKEBOX);

    public final Material icon;

    MachineType(Material icon) {
        this.icon = icon;
    }

    public String langKey() {
        return "machine." + name().toLowerCase() + ".name";
    }

    public String getDisplayName() {
        return LanguageManager.getRaw(null, langKey());
    }

    public static MachineType byName(String name) {
        if (name == null) return null;
        for (MachineType t : values()) {
            if (t.name().equalsIgnoreCase(name)) return t;
        }
        return null;
    }
}