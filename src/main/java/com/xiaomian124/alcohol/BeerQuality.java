package com.xiaomian124.alcohol;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public enum BeerQuality {

    HIGH     ("quality.high",     NamedTextColor.GOLD),
    EXCELLENT("quality.excellent",NamedTextColor.LIGHT_PURPLE),
    GOOD     ("quality.good",     NamedTextColor.YELLOW),
    MEDIUM   ("quality.medium",   NamedTextColor.DARK_AQUA),
    NORMAL   ("quality.normal",   NamedTextColor.DARK_GREEN),
    POOR     ("quality.poor",     NamedTextColor.GREEN),
    BAD      ("quality.bad",      NamedTextColor.RED),
    TERRIBLE ("quality.terrible", NamedTextColor.DARK_RED);

    private final String key;
    private final NamedTextColor color;

    BeerQuality(String key, NamedTextColor color) {
        this.key = key;
        this.color = color;
    }

    public String getLabel(Player player) { return LanguageManager.getRaw(player, key); }

    @Deprecated
    public String getLabel()   { return LanguageManager.getRaw(null, key); }
    public NamedTextColor getColor() { return color; }

    public static BeerQuality fromPenalty(int penalty) {
        int idx = Math.min(Math.max(penalty, 0), values().length - 1);
        return values()[idx];
    }
}