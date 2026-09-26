package com.xiaomian124.alcohol;

import org.bukkit.Color;
import org.bukkit.Material;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public enum BeerType {

    WHEAT_BEER("小麦啤酒", "充满麦香味的啤酒",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFkNjZkMDliMWI5MzQ2OWNjZDc5ZmNhOGU5Mzc5MjY2ZGZiZDhkYmRlMjQxZDMzMjhiMzA4MTY3NzIxMTJjOSJ9fX0="),
    BARLEY_BEER("大麦啤酒", "散发大麦香气的啤酒",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZiOTI1ZDc5MzllYzBkOTI3MDU3ODdkOWYwMGEyYzdiNTVjODQyOTM3Yjg4ZDliYjM4NWM2MDMyNjMwNjZhMSJ9fX0="),
    HOPS_BEER("酒花啤酒", "浓烈酒花香气的啤酒",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M2YjRhN2JkODI0NDE2MTliYmMxMWQ5YjhlMGU2NGFlOGI5NWYyZTQwYjM5MjEzNTVmY2M1NDM0MzI2MDE3In19fQ=="),
    NETTLE_BEER("荨麻啤酒", "草本清香味的啤酒",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGY3ZGM4YzkxZjdhMjRlY2Q5YzVlOWQ0ZDhjMzlmMGFjODMzM2FlNDg1MzU1OWFjYjhiMDM4NjZmOWQifX19"),
    OATS_BEER("燕麦啤酒", "柔和燕麦风味的啤酒",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2OGI5ODA5OGEwYzRhMjllMjA0NjcwNDYzMDkxZGI2MDcwZTc3ZDg2NzY5ZDk4NWY2YmVmNDA3NWU1In19fQ=="),
    COCOA_BEER("可可啤酒", "可可浓郁香气的啤酒",
            Material.PLAYER_HEAD, null, true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhjZThhODAyMDMxYWQ1M2RlZTdhMDViOTRmMmYxMThkMzgzNDk1MjI4MDcwNDM3YWE3ZGNlZDZjZjlhYWRmYSJ9fX0="),

    BELGRAVIA("Belgravia威士忌", "醇厚顺滑的威士忌",
            Material.POTION, Color.fromRGB(0xC68B36), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjU4OWZkMDAxMzM2ZTcyM2JmN2RmNWMwM2YyZmI4MDYxOTQ2NTQ0YjljODI5YzI3NmI3ZWNhZTQ4NGFhYmY4OCJ9fX0="),
    ISLAY("Islay单一麦芽威士忌", "泥煤烟熏风味的威士忌",
            Material.POTION, Color.fromRGB(0x8B4513), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJjYTBkN2Q0NTA0ZWQ5YjNkZWYxNGE0NmRlZDEzYTQ1NDY4MWEyMTlkODhmNThjMGIzYjU4MWVjYjJmYzk0NyJ9fX0="),
    BOURBON("Bourbon威士忌", "美式经典威士忌",
            Material.POTION, Color.fromRGB(0xB8860B), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDlhOGQ1ZTZmYzE5Mzk0NDUzZWViMmIwOWU5ZmFiODczN2E3NmZjYTU0OTQxNTE3Y2Q5MDY1NmY3NWIxYmRhNCJ9fX0="),
    MACALLAN("Macallan威士忌", "雪莉桶陈酿威士忌",
            Material.POTION, Color.fromRGB(0x8B2500), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIwMzUxYmMzNGYwNTQ4YjE2ZDhiMTE1MDM4NWFmMjkwZjY0Y2UyODcwYTgyMzM2YzAyZjVmYjExNDQ5NDg0NyJ9fX0="),
    GLENFIDDICH("Glenfiddich威士忌", "清新果香威士忌",
            Material.POTION, Color.fromRGB(0xDAA520), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg1NWFmMjllOTJkMzgwYTg3NDQyZjliMTViMDI5YmJiNTkyNmE4YTFmNDVmNWQzOWJkNWRjNThiZTYxODk3NyJ9fX0="),
    BAINS("Bain's威士忌", "南非风格威士忌",
            Material.POTION, Color.fromRGB(0xCD950C), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE5OTk1YzM5OGFkMDJhZTQxYjMxMDlmOTljM2IwMWM4OGI0MjVjNDRkYmQzZDFiZmNlMjY2NjI3OTcwYzhhYyJ9fX0="),
    SUNTORY("Suntory威士忌", "日式细腻威士忌",
            Material.POTION, Color.fromRGB(0xD4A76A), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M0MmIwMTdiNmRmYTk2OWFhMGM2ZWFhOTdkNjJkMzVhNGEwZTE3NGViYjljMzQ2OWVmNjE1OGViNGYyMDgyOCJ9fX0="),
    BUFFALO_TRACE("Buffalo Trace威士忌", "波本经典威士忌",
            Material.POTION, Color.fromRGB(0xA0522D), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjJjMTdjNzBlNmFjYWE1Mzk5YWU5ODY2OTYxODViODQ5YWRiZGUzM2ZjMTRlMmUzYTg0MDgxMjc4Y2Y2NjM3NyJ9fX0="),
    JIM_BEAM("Jim Beam威士忌", "经典波本威士忌",
            Material.POTION, Color.fromRGB(0x8B5A2B), true, true,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU3M2JlZjQwNDc4OTY1NmE2ZmQyMTc0OWU4NWY2OTI0Y2ZlODQ4NmFjMDZhNzgxOTRhZDc1ZjM0YzJiMTRhNSJ9fX0="),

    RED_GRAPE_JUICE("红葡萄汁", "压榨自红葡萄的果汁，可用于陈酿",
            Material.POTION, Color.fromRGB(0x8B1A4A), false, false, ""),
    WHITE_GRAPE_JUICE("白葡萄汁", "压榨自白葡萄的果汁，可用于陈酿",
            Material.POTION, Color.fromRGB(0xF0E68C), false, false, ""),
    TAIGA_RED_GRAPE_JUICE("针叶林红葡萄汁", "来自针叶林红葡萄的果汁，可用于陈酿",
            Material.POTION, Color.fromRGB(0x6B0F32), false, false, ""),
    TAIGA_WHITE_GRAPE_JUICE("针叶林白葡萄汁", "来自针叶林白葡萄的果汁，可用于陈酿",
            Material.POTION, Color.fromRGB(0xD4D46A), false, false, ""),
    JUNGLE_RED_GRAPE_JUICE("丛林红葡萄汁", "来自丛林红葡萄的果汁，可用于陈酿",
            Material.POTION, Color.fromRGB(0xC41E3A), false, false, ""),
    JUNGLE_WHITE_GRAPE_JUICE("丛林白葡萄汁", "来自丛林白葡萄的果汁，可用于陈酿",
            Material.POTION, Color.fromRGB(0xE8E8A0), false, false, ""),
    SAVANNA_RED_GRAPE_JUICE("热带草原红葡萄汁", "来自热带草原红葡萄的果汁，可用于陈酿",
            Material.POTION, Color.fromRGB(0xA52A2A), false, false, ""),
    SAVANNA_WHITE_GRAPE_JUICE("热带草原白葡萄汁", "来自热带草原白葡萄的果汁，可用于陈酿",
            Material.POTION, Color.fromRGB(0xE6C84D), false, false, ""),

    APPLE_JUICE("苹果汁", "清甜爽口的苹果汁",
            Material.POTION, Color.fromRGB(0xF4A460), false, false, ""),

    HONEY_WINE("蜂蜜酒", "甜蜜醇厚的蜂蜜酒",
            Color.fromRGB(0xFFC727),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk4NThhZGY2ODYxZTQwYmRlYjcwMjBiNjkyZjJlNWUyMWFhZDlmZWY0MTgwYzQxMWRiZmQ5NmYxMTJkZjNkIn19fQ=="),
    APPLE_FLAVOR_WINE("苹果风味酒", "苹果清香的甜酒",
            Color.fromRGB(0xFFE4A0),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4MDY3NTJkZTc2NDM5YzBjZmRlOTI2YjRiNzY5MmZiOTcxNWJlYWRhNjkzMmY1ZDQyY2ZkNGMzNDlhNiJ9fX0="),
    APPLE_WINE("苹果酒", "苹果发酵而成的果酒",
            Color.fromRGB(0xF0A500),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4MDY3NTJkZTc2NDM5YzBjZmRlOTI2YjRiNzY5MmZiOTcxNWJlYWRhNjkzMmY1ZDQyY2ZkNGMzNDlhNiJ9fX0="),
    VITALITY_WINE("活力之酒", "蕴含活力能量的白葡萄酒",
            Color.fromRGB(0xFFF176),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQyZjUzNjJkZDJhYWIyOGFmNGViNDEzODllMjhmMjBjNWU2ZDU2Y2JhOWE1MDExNWIyOGVkYTU5YmNlZWYifX19"),
    SUNSHINE_WINE("阳光葡萄酒", "充满阳光气息的白葡萄酒",
            Color.fromRGB(0xFFB74D),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFhNzIyYjZiMzU5YWJmYjgyODIxOTg1MWYxOTI0ODFjZGJiYzE3ZDM4NmYxNmI2NzkzMDk3YzdkZjhlMTI2ZCJ9fX0="),
    CENTURY_WHITE_WINE("世纪白葡萄酒", "陈年酿制的白葡萄酒",
            Color.fromRGB(0xF5E1A4),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE1ZDViZjQ5M2RjNjRlNzljMjlhNTJjMDkwZTRjM2I3NzRkY2JlMWY5ZGE3YTFhMDVjZjczZTk3Zjc3YTg2In19fQ=="),
    PINOT_NOIR("Pinot Noir红葡萄酒", "经典黑皮诺红葡萄酒",
            Color.fromRGB(0x7B1F2E),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmOWFhN2FjMTgyOTY3MDdmYmU0YjNmNjBkZDdkNWZkYzIzNjdkMzU4YzVjODNjMmM0MWE4YzljOTE3OGYyIn19fQ=="),
    FLAVOR_RED_WINE("风味红葡萄酒", "风味浓郁的甜红葡萄酒",
            Color.fromRGB(0xA62B32),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFjM2E3MmJkZTZhNzhhYTBhMGIwZTU3NDkxZDBkODJhYTFiNjQ0NTUzMTU1YWMxNDA0M2IzMTdmNjdmOTkifX19"),
    MERLOT("Merlot红葡萄酒", "柔顺的梅洛红葡萄酒",
            Color.fromRGB(0x6B1A2A),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmOWFhN2FjMTgyOTY3MDdmYmU0YjNmNjBkZDdkNWZkYzIzNjdkMzU4YzVjODNjMmM0MWE4YzljOTE3OGYyIn19fQ=="),
    CHERRY_WINE("樱桃风味酒", "樱桃风味的果酒",
            Color.fromRGB(0xC2185B),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRkZjZmZTAyZTVjYjM2YjE1OGFlNGMxZjQ0M2E0M2YzYTE2YmUxNzlhYTIwOGI1NjExNzM3ZjM1N2JlZTg3In19fQ=="),
    SCUBA_DIVING("Scuba Diving葡萄酒", "潜水者特调的葡萄酒",
            Color.fromRGB(0x1E88E5),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyYWZhNjc5OGYxYTRiOTgxNjRmMzczMDFmODkwY2UxZDViZTNiNjg5ZTBkZDI0YjQ1MDkyN2NlOTk4MmIifX19"),
    TROPICAL_CREEPER_WINE("热带苦力怕葡萄酒", "热带风情葡萄酒",
            Color.fromRGB(0x4CAF50),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE1ZDViZjQ5M2RjNjRlNzljMjlhNTJjMDkwZTRjM2I3NzRkY2JlMWY5ZGE3YTFhMDVjZjczZTk3Zjc3YTg2In19fQ=="),
    TROPICAL_SEA_WINE("热带海味葡萄酒", "热带海洋气息的葡萄酒",
            Color.fromRGB(0x009688),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWJhYWNkZDM4OTg2YTZjYWUwZDAxZTRmNjJiYTRmZWIxYmRlMzRiZmJiNGRjNWU3Y2NkODU0ZjY2MjNiIn19fQ=="),
    LAMBRUSCO("Lambrusco红葡萄酒", "微泡红葡萄酒",
            Color.fromRGB(0x9C27B0),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQyZjUzNjJkZDJhYWIyOGFmNGViNDEzODllMjhmMjBjNWU2ZDU2Y2JhOWE1MDExNWIyOGVkYTU5YmNlZWYifX19"),
    SPIDER_SPECIAL_WINE("Spider特调葡萄酒", "蜘蛛特调的神秘葡萄酒",
            Color.fromRGB(0x6A1B9A),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQyZjUzNjJkZDJhYWIyOGFmNGViNDEzODllMjhmMjBjNWU2ZDU2Y2JhOWE1MDExNWIyOGVkYTU5YmNlZWYifX19"),
    FROST_WHITE_WINE("冰霜白葡萄酒", "冰寒彻骨的白葡萄酒",
            Color.fromRGB(0xB3E5FC),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2JhYjY3NjFjNTQ2MjY4YzNmMDRmZjZiMjljMzg4MWM3YjQ3ZjVmZGU5ZjQyNWQ2MmQ5NTk1ODQxOWQyIn19fQ=="),
    AEGIS_WHITE_WINE("神盾白葡萄酒", "具备防护力的白葡萄酒",
            Color.fromRGB(0xE0E0E0),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M5NmJjOGM1Nzg2YmJiZjlmYjg5ZWJiZDU3ZTkxZDkwM2NmYjU1MWJhODY4OGYxYWVmZGZkMzNkZWFiZTkifX19"),
    BLAZE_WALKER_WINE("烈焰行者葡萄酒", "灼热气息的葡萄酒",
            Color.fromRGB(0xFF5722),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRkZjZmZTAyZTVjYjM2YjE1OGFlNGMxZjQ0M2E0M2YzYTE2YmUxNzlhYTIwOGI1NjExNzM3ZjM1N2JlZTg3In19fQ=="),
    CHORUS_WINE("紫颂果风味葡萄酒", "紫颂果风味的葡萄酒",
            Color.fromRGB(0x9C27B0),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFjM2E3MmJkZTZhNzhhYTBhMGIwZTU3NDkxZDBkODJhYTFiNjQ0NTUzMTU1YWMxNDA0M2IzMTdmNjdmOTkifX19"),
    OMEN_WINE("不详果实特调酒", "散发着不祥气息的特调",
            Color.fromRGB(0x455A64),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFhNzIyYjZiMzU5YWJmYjgyODIxOTg1MWYxOTI0ODFjZGJiYzE3ZDM4NmYxNmI2NzkzMDk3YzdkZjhlMTI2ZCJ9fX0="),
    BOUNCE_WHITE_WINE("Bounce白葡萄酒", "弹跳感十足的白葡萄酒",
            Color.fromRGB(0xA5D6A7),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE1ZDViZjQ5M2RjNjRlNzljMjlhNTJjMDkwZTRjM2I3NzRkY2JlMWY5ZGE3YTFhMDVjZjczZTk3Zjc3YTg2In19fQ=="),
    MAGNETIC_WINE("磁吸之酒", "带着磁性的特殊酒",
            Color.fromRGB(0x8BC34A),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE2YTY3ODlhODUwYjY2NDNkZTlkOWM3MjEwNzk2NTYzM2Y1NzFmYTk5YzNjZjdjNDdhZmI3ZTMwOTNkOWUifX19"),
    HEALTH_RED_WINE("Health红葡萄酒", "有益健康的红葡萄酒",
            Color.fromRGB(0xE91E63),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFiOGI0NDJhZjE1NmQ2NTRmNDUyNTVmM2ZkMzZlOGFkNmM2MWFlNTVlZjQxZTgwNDEyZTdmYTZjZDI4M2UyIn19fQ=="),
    SPIDER_RED_WINE("斯柏德红葡萄酒", "蜘蛛风味的红葡萄酒",
            Color.fromRGB(0x8E2449),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRkZjZmZTAyZTVjYjM2YjE1OGFlNGMxZjQ0M2E0M2YzYTE2YmUxNzlhYTIwOGI1NjExNzM3ZjM1N2JlZTg3In19fQ=="),
    CATGIRL_RED_WINE("猫娘特调红葡萄酒", "猫娘精心调制的红葡萄酒",
            Color.fromRGB(0xE1BEE7),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE1ZDkyZDQyNjI3ODhjOTYwODhlNDRlMDE1NzBkY2U5MzgxODRiZjQ3ZjUzMTFkZjI5NmMzZDliNzhkYTUifX19"),
    ELDER_WINE("耄耋特调葡萄酒", "哈气大师特制的葡萄酒",
            Color.fromRGB(0xB8860B),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY0MmZlMWQ1NTVjNTdkMzkzYWM1ZTcxNTdmNTc5OGQ2YjY5Zjc2ZTVkMmJhMTJkNTM4YjNkYWJkODY2NDE4In19fQ==");

    private final String displayName;
    private final String loreLine;
    private final Material material;
    private final Color potionColor;
    private final boolean placeable;
    private final boolean hasQuality;
    private final String texture;

    BeerType(String displayName, String loreLine,
             Material material, Color potionColor,
             boolean placeable, boolean hasQuality, String texture) {
        this.displayName = displayName;
        this.loreLine = loreLine;
        this.material = material;
        this.potionColor = potionColor;
        this.placeable = placeable;
        this.hasQuality = hasQuality;
        this.texture = texture;
    }

    BeerType(String displayName, String loreLine, Color potionColor, String texture) {
        this(displayName, loreLine, Material.POTION, potionColor, true, false, texture);
    }

    public String getDisplayName() { return displayName; }
    public String getLoreLine() { return loreLine; }
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