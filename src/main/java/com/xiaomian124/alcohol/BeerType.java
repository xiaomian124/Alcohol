package com.xiaomian124.alcohol;

import org.bukkit.Color;
import org.bukkit.Material;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public enum BeerType {

    WHEAT_BEER("beer.wheat_beer.name", "beer.wheat_beer.lore",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFkNjZkMDliMWI5MzQ2OWNjZDc5ZmNhOGU5Mzc5MjY2ZGZiZDhkYmRlMjQxZDMzMjhiMzA4MTY3NzIxMTJjOSJ9fX0="),
    BARLEY_BEER("beer.barley_beer.name", "beer.barley_beer.lore",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZiOTI1ZDc5MzllYzBkOTI3MDU3ODdkOWYwMGEyYzdiNTVjODQyOTM3Yjg4ZDliYjM4NWM2MDMyNjMwNjZhMSJ9fX0="),
    HOPS_BEER("beer.hops_beer.name", "beer.hops_beer.lore",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M2YjRhN2JkODI0NDE2MTliYmMxMWQ5YjhlMGU2NGFlOGI5NWYyZTQwYjM5MjEzNTVmY2M1NDM0MzI2MDE3In19fQ=="),
    NETTLE_BEER("beer.nettle_beer.name", "beer.nettle_beer.lore",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGY3ZGM4YzkxZjdhMjRlY2Q5YzVlOWQ0ZDhjMzlmMGFjODMzM2FlNDg1MzU1OWFjYjhiMDM4NjZmOWQifX19"),
    OATS_BEER("beer.oats_beer.name", "beer.oats_beer.lore",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2OGI5ODA5OGEwYzRhMjllMjA0NjcwNDYzMDkxZGI2MDcwZTc3ZDg2NzY5ZDk4NWY2YmVmNDA3NWU1In19fQ=="),
    COCOA_BEER("beer.cocoa_beer.name", "beer.cocoa_beer.lore",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhjZThhODAyMDMxYWQ1M2RlZTdhMDViOTRmMmYxMThkMzgzNDk1MjI4MDcwNDM3YWE3ZGNlZDZjZjlhYWRmYSJ9fX0="),

    BELGRAVIA("beer.belgravia.name", "beer.belgravia.lore",
            Material.POTION, Color.fromRGB(0xC68B36), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjU4OWZkMDAxMzM2ZTcyM2JmN2RmNWMwM2YyZmI4MDYxOTQ2NTQ0YjljODI5YzI3NmI3ZWNhZTQ4NGFhYmY4OCJ9fX0="),
    ISLAY("beer.islay.name", "beer.islay.lore",
            Material.POTION, Color.fromRGB(0x8B4513), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJjYTBkN2Q0NTA0ZWQ5YjNkZWYxNGE0NmRlZDEzYTQ1NDY4MWEyMTlkODhmNThjMGIzYjU4MWVjYjJmYzk0NyJ9fX0="),
    BOURBON("beer.bourbon.name", "beer.bourbon.lore",
            Material.POTION, Color.fromRGB(0xB8860B), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDlhOGQ1ZTZmYzE5Mzk0NDUzZWViMmIwOWU5ZmFiODczN2E3NmZjYTU0OTQxNTE3Y2Q5MDY1NmY3NWIxYmRhNCJ9fX0="),
    MACALLAN("beer.macallan.name", "beer.macallan.lore",
            Material.POTION, Color.fromRGB(0x8B2500), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIwMzUxYmMzNGYwNTQ4YjE2ZDhiMTE1MDM4NWFmMjkwZjY0Y2UyODcwYTgyMzM2YzAyZjVmYjExNDQ5NDg0NyJ9fX0="),
    GLENFIDDICH("beer.glenfiddich.name", "beer.glenfiddich.lore",
            Material.POTION, Color.fromRGB(0xDAA520), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg1NWFmMjllOTJkMzgwYTg3NDQyZjliMTViMDI5YmJiNTkyNmE4YTFmNDVmNWQzOWJkNWRjNThiZTYxODk3NyJ9fX0="),
    BAINS("beer.bains.name", "beer.bains.lore",
            Material.POTION, Color.fromRGB(0xCD950C), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE5OTk1YzM5OGFkMDJhZTQxYjMxMDlmOTljM2IwMWM4OGI0MjVjNDRkYmQzZDFiZmNlMjY2NjI3OTcwYzhhYyJ9fX0="),
    SUNTORY("beer.suntory.name", "beer.suntory.lore",
            Material.POTION, Color.fromRGB(0xD4A76A), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M0MmIwMTdiNmRmYTk2OWFhMGM2ZWFhOTdkNjJkMzVhNGEwZTE3NGViYjljMzQ2OWVmNjE1OGViNGYyMDgyOCJ9fX0="),
    BUFFALO_TRACE("beer.buffalo_trace.name", "beer.buffalo_trace.lore",
            Material.POTION, Color.fromRGB(0xA0522D), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjJjMTdjNzBlNmFjYWE1Mzk5YWU5ODY2OTYxODViODQ5YWRiZGUzM2ZjMTRlMmUzYTg0MDgxMjc4Y2Y2NjM3NyJ9fX0="),
    JIM_BEAM("beer.jim_beam.name", "beer.jim_beam.lore",
            Material.POTION, Color.fromRGB(0x8B5A2B), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU3M2JlZjQwNDc4OTY1NmE2ZmQyMTc0OWU4NWY2OTI0Y2ZlODQ4NmFjMDZhNzgxOTRhZDc1ZjM0YzJiMTRhNSJ9fX0="),

    RED_GRAPE_JUICE("beer.red_grape_juice.name", "beer.red_grape_juice.lore",
            Material.POTION, Color.fromRGB(0x8B1A4A), false, false, ""),
    WHITE_GRAPE_JUICE("beer.white_grape_juice.name", "beer.white_grape_juice.lore",
            Material.POTION, Color.fromRGB(0xF0E68C), false, false, ""),
    TAIGA_RED_GRAPE_JUICE("beer.taiga_red_grape_juice.name", "beer.taiga_red_grape_juice.lore",
            Material.POTION, Color.fromRGB(0x6B0F32), false, false, ""),
    TAIGA_WHITE_GRAPE_JUICE("beer.taiga_white_grape_juice.name", "beer.taiga_white_grape_juice.lore",
            Material.POTION, Color.fromRGB(0xD4D46A), false, false, ""),
    JUNGLE_RED_GRAPE_JUICE("beer.jungle_red_grape_juice.name", "beer.jungle_red_grape_juice.lore",
            Material.POTION, Color.fromRGB(0xC41E3A), false, false, ""),
    JUNGLE_WHITE_GRAPE_JUICE("beer.jungle_white_grape_juice.name", "beer.jungle_white_grape_juice.lore",
            Material.POTION, Color.fromRGB(0xE8E8A0), false, false, ""),
    SAVANNA_RED_GRAPE_JUICE("beer.savanna_red_grape_juice.name", "beer.savanna_red_grape_juice.lore",
            Material.POTION, Color.fromRGB(0xA52A2A), false, false, ""),
    SAVANNA_WHITE_GRAPE_JUICE("beer.savanna_white_grape_juice.name", "beer.savanna_white_grape_juice.lore",
            Material.POTION, Color.fromRGB(0xE6C84D), false, false, ""),

    APPLE_JUICE("beer.apple_juice.name", "beer.apple_juice.lore",
            Material.POTION, Color.fromRGB(0xF4A460), false, false, ""),

    HONEY_WINE("beer.honey_wine.name", "beer.honey_wine.lore",
            Color.fromRGB(0xFFC727),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk4NThhZGY2ODYxZTQwYmRlYjcwMjBiNjkyZjJlNWUyMWFhZDlmZWY0MTgwYzQxMWRiZmQ5NmYxMTJkZjNkIn19fQ=="),
    APPLE_FLAVOR_WINE("beer.apple_flavor_wine.name", "beer.apple_flavor_wine.lore",
            Color.fromRGB(0xFFE4A0),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4MDY3NTJkZTc2NDM5YzBjZmRlOTI2YjRiNzY5MmZiOTcxNWJlYWRhNjkzMmY1ZDQyY2ZkNGMzNDlhNiJ9fX0="),
    APPLE_WINE("beer.apple_wine.name", "beer.apple_wine.lore",
            Color.fromRGB(0xF0A500),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4MDY3NTJkZTc2NDM5YzBjZmRlOTI2YjRiNzY5MmZiOTcxNWJlYWRhNjkzMmY1ZDQyY2ZkNGMzNDlhNiJ9fX0="),
    VITALITY_WINE("beer.vitality_wine.name", "beer.vitality_wine.lore",
            Color.fromRGB(0xFFF176),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQyZjUzNjJkZDJhYWIyOGFmNGViNDEzODllMjhmMjBjNWU2ZDU2Y2JhOWE1MDExNWIyOGVkYTU5YmNlZWYifX19"),
    SUNSHINE_WINE("beer.sunshine_wine.name", "beer.sunshine_wine.lore",
            Color.fromRGB(0xFFB74D),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFhNzIyYjZiMzU5YWJmYjgyODIxOTg1MWYxOTI0ODFjZGJiYzE3ZDM4NmYxNmI2NzkzMDk3YzdkZjhlMTI2ZCJ9fX0="),
    CENTURY_WHITE_WINE("beer.century_white_wine.name", "beer.century_white_wine.lore",
            Color.fromRGB(0xF5E1A4),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE1ZDViZjQ5M2RjNjRlNzljMjlhNTJjMDkwZTRjM2I3NzRkY2JlMWY5ZGE3YTFhMDVjZjczZTk3Zjc3YTg2In19fQ=="),
    PINOT_NOIR("beer.pinot_noir.name", "beer.pinot_noir.lore",
            Color.fromRGB(0x7B1F2E),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmOWFhN2FjMTgyOTY3MDdmYmU0YjNmNjBkZDdkNWZkYzIzNjdkMzU4YzVjODNjMmM0MWE4YzljOTE3OGYyIn19fQ=="),
    FLAVOR_RED_WINE("beer.flavor_red_wine.name", "beer.flavor_red_wine.lore",
            Color.fromRGB(0xA62B32),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFjM2E3MmJkZTZhNzhhYTBhMGIwZTU3NDkxZDBkODJhYTFiNjQ0NTUzMTU1YWMxNDA0M2IzMTdmNjdmOTkifX19"),
    MERLOT("beer.merlot.name", "beer.merlot.lore",
            Color.fromRGB(0x6B1A2A),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmOWFhN2FjMTgyOTY3MDdmYmU0YjNmNjBkZDdkNWZkYzIzNjdkMzU4YzVjODNjMmM0MWE4YzljOTE3OGYyIn19fQ=="),
    CHERRY_WINE("beer.cherry_wine.name", "beer.cherry_wine.lore",
            Color.fromRGB(0xC2185B),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRkZjZmZTAyZTVjYjM2YjE1OGFlNGMxZjQ0M2E0M2YzYTE2YmUxNzlhYTIwOGI1NjExNzM3ZjM1N2JlZTg3In19fQ=="),
    SCUBA_DIVING("beer.scuba_diving.name", "beer.scuba_diving.lore",
            Color.fromRGB(0x1E88E5),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyYWZhNjc5OGYxYTRiOTgxNjRmMzczMDFmODkwY2UxZDViZTNiNjg5ZTBkZDI0YjQ1MDkyN2NlOTk4MmIifX19"),
    TROPICAL_CREEPER_WINE("beer.tropical_creeper_wine.name", "beer.tropical_creeper_wine.lore",
            Color.fromRGB(0x4CAF50),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE1ZDViZjQ5M2RjNjRlNzljMjlhNTJjMDkwZTRjM2I3NzRkY2JlMWY5ZGE3YTFhMDVjZjczZTk3Zjc3YTg2In19fQ=="),
    TROPICAL_SEA_WINE("beer.tropical_sea_wine.name", "beer.tropical_sea_wine.lore",
            Color.fromRGB(0x009688),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWJhYWNkZDM4OTg2YTZjYWUwZDAxZTRmNjJiYTRmZWIxYmRlMzRiZmJiNGRjNWU3Y2NkODU0ZjY2MjNiIn19fQ=="),
    LAMBRUSCO("beer.lambrusco.name", "beer.lambrusco.lore",
            Color.fromRGB(0x9C27B0),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQyZjUzNjJkZDJhYWIyOGFmNGViNDEzODllMjhmMjBjNWU2ZDU2Y2JhOWE1MDExNWIyOGVkYTU5YmNlZWYifX19"),
    SPIDER_SPECIAL_WINE("beer.spider_special_wine.name", "beer.spider_special_wine.lore",
            Color.fromRGB(0x6A1B9A),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQyZjUzNjJkZDJhYWIyOGFmNGViNDEzODllMjhmMjBjNWU2ZDU2Y2JhOWE1MDExNWIyOGVkYTU5YmNlZWYifX19"),
    FROST_WHITE_WINE("beer.frost_white_wine.name", "beer.frost_white_wine.lore",
            Color.fromRGB(0xB3E5FC),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2JhYjY3NjFjNTQ2MjY4YzNmMDRmZjZiMjljMzg4MWM3YjQ3ZjVmZGU5ZjQyNWQ2MmQ5NTk1ODQxOWQyIn19fQ=="),
    AEGIS_WHITE_WINE("beer.aegis_white_wine.name", "beer.aegis_white_wine.lore",
            Color.fromRGB(0xE0E0E0),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5NmJjOGM1Nzg2YmJiZjlmYjg5ZWJiZDU3ZTkxZDkwM2NmYjU1MWJhODY4OGYxYWVmZGZkMzNkZWFiZTkifX19"),
    BLAZE_WALKER_WINE("beer.blaze_walker_wine.name", "beer.blaze_walker_wine.lore",
            Color.fromRGB(0xFF5722),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRkZjZmZTAyZTVjYjM2YjE1OGFlNGMxZjQ0M2E0M2YzYTE2YmUxNzlhYTIwOGI1NjExNzM3ZjM1N2JlZTg3In19fQ=="),
    CHORUS_WINE("beer.chorus_wine.name", "beer.chorus_wine.lore",
            Color.fromRGB(0x9C27B0),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFjM2E3MmJkZTZhNzhhYTBhMGIwZTU3NDkxZDBkODJhYTFiNjQ0NTUzMTU1YWMxNDA0M2IzMTdmNjdmOTkifX19"),
    OMEN_WINE("beer.omen_wine.name", "beer.omen_wine.lore",
            Color.fromRGB(0x455A64),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFhNzIyYjZiMzU5YWJmYjgyODIxOTg1MWYxOTI0ODFjZGJiYzE3ZDM4NmYxNmI2NzkzMDk3YzdkZjhlMTI2ZCJ9fX0="),
    BOUNCE_WHITE_WINE("beer.bounce_white_wine.name", "beer.bounce_white_wine.lore",
            Color.fromRGB(0xA5D6A7),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE1ZDViZjQ5M2RjNjRlNzljMjlhNTJjMDkwZTRjM2I3NzRkY2JlMWY5ZGE3YTFhMDVjZjczZTk3Zjc3YTg2In19fQ=="),
    MAGNETIC_WINE("beer.magnetic_wine.name", "beer.magnetic_wine.lore",
            Color.fromRGB(0x8BC34A),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE2YTY3ODlhODUwYjY2NDNkZTlkOWM3MjEwNzk2NTYzM2Y1NzFmYTk5YzNjZjdjNDdhZmI3ZTMwOTNkOWUifX19"),
    HEALTH_RED_WINE("beer.health_red_wine.name", "beer.health_red_wine.lore",
            Color.fromRGB(0xE91E63),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFiOGI0NDJhZjE1NmQ2NTRmNDUyNTVmM2ZkMzZlOGFkNmM2MWFlNTVlZjQxZTgwNDEyZTdmYTZjZDI4M2UyIn19fQ=="),
    SPIDER_RED_WINE("beer.spider_red_wine.name", "beer.spider_red_wine.lore",
            Color.fromRGB(0x8E2449),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRkZjZmZTAyZTVjYjM2YjE1OGFlNGMxZjQ0M2E0M2YzYTE2YmUxNzlhYTIwOGI1NjExNzM3ZjM1N2JlZTg3In19fQ=="),
    CATGIRL_RED_WINE("beer.catgirl_red_wine.name", "beer.catgirl_red_wine.lore",
            Color.fromRGB(0xE1BEE7),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE1ZDkyZDQyNjI3ODhjOTYwODhlNDRlMDE1NzBkY2U5MzgxODRiZjQ3ZjUzMTFkZjI5NmMzZDliNzhkYTUifX19"),
    ELDER_WINE("beer.elder_wine.name", "beer.elder_wine.lore",
            Color.fromRGB(0xB8860B),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY0MmZlMWQ1NTVjNTdkMzkzYWM1ZTcxNTdmNTc5OGQ2YjY5Zjc2ZTVkMmJhMTJkNTM4YjNkYWJkODY2NDE4In19fQ==");

    private final String nameKey;
    private final String loreKey;
    private final Material material;
    private final Color potionColor;
    private final boolean placeable;
    private final boolean hasQuality;
    private final String texture;

    BeerType(String nameKey, String loreKey,
             Material material, Color potionColor,
             boolean placeable, boolean hasQuality, String texture) {
        this.nameKey = nameKey;
        this.loreKey = loreKey;
        this.material = material;
        this.potionColor = potionColor;
        this.placeable = placeable;
        this.hasQuality = hasQuality;
        this.texture = texture;
    }

    BeerType(String nameKey, String loreKey, Color potionColor, String texture) {
        this(nameKey, loreKey, Material.POTION, potionColor, true, false, texture);
    }

    public String getNameKey() { return nameKey; }
    public String getLoreKey() { return loreKey; }
    public String getDisplayName() { return LanguageManager.getRaw(null, nameKey); }
    public String getLoreLine()    { return LanguageManager.getRaw(null, loreKey); }
    public Material getMaterial() { return material; }
    public Color getPotionColor() { return potionColor; }
    public boolean isPlaceable() { return placeable; }
    public boolean hasQuality() { return hasQuality; }
    public String getTexture() { return texture; }

    public boolean isJuice() {
        return !placeable;
    }

    public UUID getSkinUUID() {
        return UUID.nameUUIDFromBytes(
                ("alcohol:beer:" + name()).getBytes(StandardCharsets.UTF_8));
    }
}