package com.xiaomian124.alcohol;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public final class BeerEffects {

    private BeerEffects() {}

    public static List<Component> getEffectLines(Player player, BeerType type) {
        List<Component> list = new ArrayList<>();
        switch (type) {
            case WHEAT_BEER -> {
                list.add(pos(lang(player, "beer.wheat_beer.effect.0")));
                list.add(pos(lang(player, "beer.wheat_beer.effect.1")));
            }
            case BARLEY_BEER -> {
                list.add(pos(lang(player, "beer.barley_beer.effect.0")));
                list.add(pos(lang(player, "beer.barley_beer.effect.1")));
                list.add(pos(lang(player, "beer.barley_beer.effect.2")));
            }
            case HOPS_BEER -> {
                list.add(pos(lang(player, "beer.hops_beer.effect.0")));
                list.add(pos(lang(player, "beer.hops_beer.effect.1")));
                list.add(pos(lang(player, "beer.hops_beer.effect.2")));
            }
            case NETTLE_BEER -> {
                list.add(pos(lang(player, "beer.nettle_beer.effect.0")));
                list.add(pos(lang(player, "beer.nettle_beer.effect.1")));
                list.add(pos(lang(player, "beer.nettle_beer.effect.2")));
            }
            case OATS_BEER -> {
                list.add(pos(lang(player, "beer.oats_beer.effect.0")));
                list.add(pos(lang(player, "beer.oats_beer.effect.1")));
                list.add(pos(lang(player, "beer.oats_beer.effect.2")));
            }
            case COCOA_BEER -> {
                list.add(pos(lang(player, "beer.cocoa_beer.effect.0")));
                list.add(pos(lang(player, "beer.cocoa_beer.effect.1")));
                list.add(pos(lang(player, "beer.cocoa_beer.effect.2")));
            }

            case BELGRAVIA -> {
                list.add(pos(lang(player, "beer.belgravia.effect.0")));
                list.add(pos(lang(player, "beer.belgravia.effect.1")));
            }
            case ISLAY -> {
                list.add(pos(lang(player, "beer.islay.effect.0")));
                list.add(pos(lang(player, "beer.islay.effect.1")));
            }
            case BOURBON -> {
                list.add(pos(lang(player, "beer.bourbon.effect.0")));
                list.add(pos(lang(player, "beer.bourbon.effect.1")));
            }
            case MACALLAN -> {
                list.add(pos(lang(player, "beer.macallan.effect.0")));
                list.add(pos(lang(player, "beer.macallan.effect.1")));
            }
            case GLENFIDDICH -> {
                list.add(pos(lang(player, "beer.glenfiddich.effect.0")));
                list.add(pos(lang(player, "beer.glenfiddich.effect.1")));
            }
            case BAINS -> list.add(pos(lang(player, "beer.bains.effect.0")));
            case SUNTORY -> list.add(pos(lang(player, "beer.suntory.effect.0")));
            case BUFFALO_TRACE -> list.add(neg(lang(player, "beer.buffalo_trace.effect.0")));
            case JIM_BEAM -> list.add(pos(lang(player, "beer.jim_beam.effect.0")));

            case HONEY_WINE -> list.add(pos(lang(player, "beer.honey_wine.effect.0")));
            case APPLE_FLAVOR_WINE -> list.add(pos(lang(player, "beer.apple_flavor_wine.effect.0")));
            case APPLE_WINE -> list.add(pos(lang(player, "beer.apple_wine.effect.0")));
            case VITALITY_WINE -> list.add(pos(lang(player, "beer.vitality_wine.effect.0")));
            case SUNSHINE_WINE -> list.add(pos(lang(player, "beer.sunshine_wine.effect.0")));
            case CENTURY_WHITE_WINE -> list.add(pos(lang(player, "beer.century_white_wine.effect.0")));
            case PINOT_NOIR -> list.add(pos(lang(player, "beer.pinot_noir.effect.0")));
            case FLAVOR_RED_WINE -> list.add(pos(lang(player, "beer.flavor_red_wine.effect.0")));
            case MERLOT -> list.add(pos(lang(player, "beer.merlot.effect.0")));
            case CHERRY_WINE -> list.add(pos(lang(player, "beer.cherry_wine.effect.0")));
            case SCUBA_DIVING -> list.add(pos(lang(player, "beer.scuba_diving.effect.0")));
            case TROPICAL_CREEPER_WINE -> list.add(neg(lang(player, "beer.tropical_creeper_wine.effect.0")));
            case TROPICAL_SEA_WINE -> list.add(pos(lang(player, "beer.tropical_sea_wine.effect.0")));
            case LAMBRUSCO -> list.add(pos(lang(player, "beer.lambrusco.effect.0")));
            case SPIDER_SPECIAL_WINE -> list.add(pos(lang(player, "beer.spider_special_wine.effect.0")));
            case FROST_WHITE_WINE -> list.add(neg(lang(player, "beer.frost_white_wine.effect.0")));
            case AEGIS_WHITE_WINE -> list.add(pos(lang(player, "beer.aegis_white_wine.effect.0")));
            case BLAZE_WALKER_WINE -> list.add(pos(lang(player, "beer.blaze_walker_wine.effect.0")));
            case CHORUS_WINE -> list.add(pos(lang(player, "beer.chorus_wine.effect.0")));
            case OMEN_WINE -> list.add(neg(lang(player, "beer.omen_wine.effect.0")));
            case BOUNCE_WHITE_WINE -> list.add(pos(lang(player, "beer.bounce_white_wine.effect.0")));
            case MAGNETIC_WINE -> list.add(pos(lang(player, "beer.magnetic_wine.effect.0")));
            case HEALTH_RED_WINE -> {
                list.add(pos(lang(player, "beer.health_red_wine.effect.0")));
                list.add(pos(lang(player, "beer.health_red_wine.effect.1")));
                list.add(pos(lang(player, "beer.health_red_wine.effect.2")));
            }
            case SPIDER_RED_WINE -> list.add(pos(lang(player, "beer.spider_red_wine.effect.0")));
            case CATGIRL_RED_WINE -> list.add(pos(lang(player, "beer.catgirl_red_wine.effect.0")));
            case ELDER_WINE -> list.add(pos(lang(player, "beer.elder_wine.effect.0")));

            default -> {}
        }
        return list;
    }

    public static void apply(Player p, BeerType type) {
        switch (type) {
            case WHEAT_BEER -> {
                v(p, PotionEffectType.STRENGTH, 200, 0);
                v(p, PotionEffectType.INSTANT_HEALTH, 200, 0);
            }
            case BARLEY_BEER -> {
                v(p, PotionEffectType.STRENGTH, 200, 0);
                v(p, PotionEffectType.INSTANT_HEALTH, 200, 0);
                v(p, PotionEffectType.HASTE, 200, 0);
            }
            case HOPS_BEER -> {
                v(p, PotionEffectType.JUMP_BOOST, 200, 0);
                v(p, PotionEffectType.STRENGTH, 200, 1);
                v(p, PotionEffectType.SPEED, 200, 0);
            }
            case NETTLE_BEER -> {
                v(p, PotionEffectType.INSTANT_HEALTH, 200, 0);
                v(p, PotionEffectType.HASTE, 200, 0);
                v(p, PotionEffectType.INVISIBILITY, 200, 0);
            }
            case OATS_BEER -> {
                v(p, PotionEffectType.NIGHT_VISION, 200, 0);
                v(p, PotionEffectType.STRENGTH, 200, 0);
                v(p, PotionEffectType.INSTANT_HEALTH, 200, 0);
            }
            case COCOA_BEER -> {
                v(p, PotionEffectType.INSTANT_HEALTH, 200, 0);
                v(p, PotionEffectType.HASTE, 200, 0);
                v(p, PotionEffectType.REGENERATION, 200, 1);
            }

            case BELGRAVIA -> {
                v(p, PotionEffectType.INSTANT_HEALTH, 400, 1);
                v(p, PotionEffectType.JUMP_BOOST, 400, 1);
            }
            case ISLAY -> {
                v(p, PotionEffectType.RESISTANCE, 400, 0);
                v(p, PotionEffectType.FIRE_RESISTANCE, 400, 0);
            }
            case BOURBON -> {
                v(p, PotionEffectType.CONDUIT_POWER, 400, 0);
                v(p, PotionEffectType.DOLPHINS_GRACE, 400, 0);
            }
            case MACALLAN -> {
                v(p, PotionEffectType.LUCK, 400, 1);
                v(p, PotionEffectType.ABSORPTION, 400, 0);
            }
            case GLENFIDDICH -> {
                v(p, PotionEffectType.GLOWING, 400, 0);
                v(p, PotionEffectType.HEALTH_BOOST, 400, 2);
            }
            case BAINS -> v(p, PotionEffectType.HERO_OF_THE_VILLAGE, 800, 0);
            case SUNTORY -> v(p, PotionEffectType.SLOW_FALLING, 800, 0);
            case BUFFALO_TRACE -> v(p, PotionEffectType.LEVITATION, 800, 0);
            case JIM_BEAM -> v(p, PotionEffectType.INVISIBILITY, 800, 0);

            case APPLE_FLAVOR_WINE -> v(p, PotionEffectType.STRENGTH, 800, 2);
            case APPLE_WINE -> v(p, PotionEffectType.RESISTANCE, 1200, 0);
            case VITALITY_WINE -> v(p, PotionEffectType.INSTANT_HEALTH, 400, 2);
            case SUNSHINE_WINE -> v(p, PotionEffectType.GLOWING, 800, 0);
            case CENTURY_WHITE_WINE -> v(p, PotionEffectType.HEALTH_BOOST, 600, 2);
            case PINOT_NOIR -> v(p, PotionEffectType.JUMP_BOOST, 1200, 0);
            case FLAVOR_RED_WINE -> v(p, PotionEffectType.HASTE, 1200, 0);
            case MERLOT -> v(p, PotionEffectType.NIGHT_VISION, 1800, 0);
            case CHERRY_WINE -> v(p, PotionEffectType.SATURATION, 1000, 0);
            case SCUBA_DIVING -> v(p, PotionEffectType.WATER_BREATHING, 1800, 0);
            case OMEN_WINE -> v(p, PotionEffectType.BAD_OMEN, 800, 0);
            case HEALTH_RED_WINE -> {
                v(p, PotionEffectType.HEALTH_BOOST, 300, 4);
                v(p, PotionEffectType.REGENERATION, 300, 4);
                v(p, PotionEffectType.INSTANT_HEALTH, 300, 4);
            }

            default -> SpecialEffectManager.apply(p, type);
        }
        EffectStorage.saveAll();
    }

    private static void v(Player p, PotionEffectType type, int ticks, int amp) {
        p.addPotionEffect(new PotionEffect(type, ticks, amp,
                true, true, true));
        DrinkEffectDisplay.record(p, type, ticks, amp);

        com.xiaomian124.alcohol.SpecialEffectManager.markDirty();
    }

    private static String lang(Player player, String key) {
        return LanguageManager.getRaw(player, key);
    }

    @Deprecated
    public static List<Component> getEffectLines(BeerType type) {
        return getEffectLines(null, type);
    }

    private static Component pos(String text) {
        return Component.text(text).color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static Component neg(String text) {
        return Component.text(text).color(NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false);
    }
}