package com.xiaomian124.alcohol;

import net.kyori.adventure.text.format.NamedTextColor;

public enum BeerQuality {

    HIGH     ("高品质", NamedTextColor.GOLD),
    EXCELLENT("优秀",   NamedTextColor.LIGHT_PURPLE),
    GOOD     ("良好",   NamedTextColor.YELLOW),
    MEDIUM   ("中等",   NamedTextColor.DARK_AQUA),
    NORMAL   ("一般",   NamedTextColor.DARK_GREEN),
    POOR     ("较差",   NamedTextColor.GREEN),
    BAD      ("差劲",   NamedTextColor.RED),
    TERRIBLE ("非常差", NamedTextColor.DARK_RED);

    private final String label;
    private final NamedTextColor color;

    BeerQuality(String label, NamedTextColor color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel()   { return label; }
    public NamedTextColor getColor() { return color; }

    public static BeerQuality fromPenalty(int penalty) {
        int idx = Math.min(Math.max(penalty, 0), values().length - 1);
        return values()[idx];
    }
}