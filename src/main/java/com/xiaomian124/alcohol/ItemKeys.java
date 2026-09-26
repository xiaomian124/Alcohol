package com.xiaomian124.alcohol;

import org.bukkit.NamespacedKey;

public final class ItemKeys {

    public static NamespacedKey DRY_WHEAT;
    public static NamespacedKey BARLEY;
    public static NamespacedKey DRIED_BARLEY;
    public static NamespacedKey OATS;
    public static NamespacedKey DRIED_OATS;
    public static NamespacedKey DRIED_CORN;
    public static NamespacedKey CORN_SEED;
    public static NamespacedKey CORN;
    public static NamespacedKey CORN_KERNELS;
    public static NamespacedKey WILD_NETTLE;
    public static NamespacedKey HOPS;
    public static NamespacedKey YEAST;

    public static NamespacedKey HARVEST_SHEARS;
    public static NamespacedKey COOKING_POT;
    public static NamespacedKey BREWING_BOX;
    public static NamespacedKey APPLE_PRESS;
    public static NamespacedKey APPLE_MASH;
    public static NamespacedKey APPLE_JUICE;
    public static NamespacedKey AGING_BARREL;
    public static NamespacedKey MUSIC_PLAYER;

    public static NamespacedKey BEER;
    public static NamespacedKey BEER_TYPE;
    public static NamespacedKey BEER_QUALITY;
    public static NamespacedKey GRAPE_BASIN;

    public static NamespacedKey RED_GRAPE_SEED;
    public static NamespacedKey RED_GRAPE;
    public static NamespacedKey WHITE_GRAPE_SEED;
    public static NamespacedKey WHITE_GRAPE;
    public static NamespacedKey SAVANNA_RED_GRAPE_SEED;
    public static NamespacedKey SAVANNA_RED_GRAPE;
    public static NamespacedKey SAVANNA_WHITE_GRAPE_SEED;
    public static NamespacedKey SAVANNA_WHITE_GRAPE;
    public static NamespacedKey JUNGLE_RED_GRAPE_SEED;
    public static NamespacedKey JUNGLE_RED_GRAPE;
    public static NamespacedKey JUNGLE_WHITE_GRAPE_SEED;
    public static NamespacedKey JUNGLE_WHITE_GRAPE;
    public static NamespacedKey TAIGA_RED_GRAPE_SEED;
    public static NamespacedKey TAIGA_RED_GRAPE;
    public static NamespacedKey TAIGA_WHITE_GRAPE_SEED;
    public static NamespacedKey TAIGA_WHITE_GRAPE;
    public static NamespacedKey CHERRY;
    public static NamespacedKey ROTTEN_CHERRY;

    private ItemKeys() {}

    public static void init(AlcoholPlugin plugin) {
        DRY_WHEAT       = new NamespacedKey(plugin, "dry_wheat");
        BARLEY          = new NamespacedKey(plugin, "barley");
        DRIED_BARLEY    = new NamespacedKey(plugin, "dried_barley");
        OATS            = new NamespacedKey(plugin, "oats");
        DRIED_OATS      = new NamespacedKey(plugin, "dried_oats");
        DRIED_CORN      = new NamespacedKey(plugin, "dried_corn");
        CORN_SEED       = new NamespacedKey(plugin, "corn_seed");
        CORN            = new NamespacedKey(plugin, "corn");
        CORN_KERNELS    = new NamespacedKey(plugin, "corn_kernels");
        WILD_NETTLE     = new NamespacedKey(plugin, "wild_nettle");
        HOPS            = new NamespacedKey(plugin, "hops");
        YEAST           = new NamespacedKey(plugin, "yeast");

        HARVEST_SHEARS  = new NamespacedKey(plugin, "harvest_shears");
        COOKING_POT     = new NamespacedKey(plugin, "cooking_pot");
        BREWING_BOX     = new NamespacedKey(plugin, "brewing_box");
        APPLE_PRESS     = new NamespacedKey(plugin, "apple_press");
        APPLE_MASH      = new NamespacedKey(plugin, "apple_mash");
        APPLE_JUICE     = new NamespacedKey(plugin, "apple_juice");
        AGING_BARREL    = new NamespacedKey(plugin, "aging_barrel");
        MUSIC_PLAYER = new NamespacedKey(plugin, "music_player");

        BEER            = new NamespacedKey(plugin, "beer");
        BEER_TYPE       = new NamespacedKey(plugin, "beer_type");
        BEER_QUALITY    = new NamespacedKey(plugin, "beer_quality");
        GRAPE_BASIN     = new NamespacedKey(plugin, "grape_basin");

        RED_GRAPE_SEED           = new NamespacedKey(plugin, "red_grape_seed");
        RED_GRAPE                = new NamespacedKey(plugin, "red_grape");
        WHITE_GRAPE_SEED         = new NamespacedKey(plugin, "white_grape_seed");
        WHITE_GRAPE              = new NamespacedKey(plugin, "white_grape");
        SAVANNA_RED_GRAPE_SEED   = new NamespacedKey(plugin, "savanna_red_grape_seed");
        SAVANNA_RED_GRAPE        = new NamespacedKey(plugin, "savanna_red_grape");
        SAVANNA_WHITE_GRAPE_SEED = new NamespacedKey(plugin, "savanna_white_grape_seed");
        SAVANNA_WHITE_GRAPE      = new NamespacedKey(plugin, "savanna_white_grape");
        JUNGLE_RED_GRAPE_SEED    = new NamespacedKey(plugin, "jungle_red_grape_seed");
        JUNGLE_RED_GRAPE         = new NamespacedKey(plugin, "jungle_red_grape");
        JUNGLE_WHITE_GRAPE_SEED  = new NamespacedKey(plugin, "jungle_white_grape_seed");
        JUNGLE_WHITE_GRAPE       = new NamespacedKey(plugin, "jungle_white_grape");
        TAIGA_RED_GRAPE_SEED     = new NamespacedKey(plugin, "taiga_red_grape_seed");
        TAIGA_RED_GRAPE          = new NamespacedKey(plugin, "taiga_red_grape");
        TAIGA_WHITE_GRAPE_SEED   = new NamespacedKey(plugin, "taiga_white_grape_seed");
        TAIGA_WHITE_GRAPE        = new NamespacedKey(plugin, "taiga_white_grape");
        CHERRY                   = new NamespacedKey(plugin, "cherry");
        ROTTEN_CHERRY            = new NamespacedKey(plugin, "rotten_cherry");
    }
}