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

    public static List<Component> getEffectLines(BeerType type) {
        List<Component> list = new ArrayList<>();
        switch (type) {
            case WHEAT_BEER -> {
                list.add(pos("力量 (00:10)"));
                list.add(pos("瞬间治疗 (00:10)"));
            }
            case BARLEY_BEER -> {
                list.add(pos("力量 (00:10)"));
                list.add(pos("瞬间治疗 (00:10)"));
                list.add(pos("急迫 (00:10)"));
            }
            case HOPS_BEER -> {
                list.add(pos("跳跃提升 (00:10)"));
                list.add(pos("力量 II (00:10)"));
                list.add(pos("速度 (00:10)"));
            }
            case NETTLE_BEER -> {
                list.add(pos("瞬间治疗 (00:10)"));
                list.add(pos("急迫 (00:10)"));
                list.add(pos("隐身 (00:10)"));
            }
            case OATS_BEER -> {
                list.add(pos("夜视 (00:10)"));
                list.add(pos("力量 (00:10)"));
                list.add(pos("瞬间治疗 (00:10)"));
            }
            case COCOA_BEER -> {
                list.add(pos("瞬间治疗 (00:10)"));
                list.add(pos("急迫 (00:10)"));
                list.add(pos("生命恢复 II (00:10)"));
            }

            case BELGRAVIA -> {
                list.add(pos("瞬间治疗 II (00:20)"));
                list.add(pos("跳跃提升 II (00:20)"));
            }
            case ISLAY -> {
                list.add(pos("抗性提升 (00:20)"));
                list.add(pos("防火 (00:20)"));
            }
            case BOURBON -> {
                list.add(pos("潮涌能量 (00:20)"));
                list.add(pos("海豚的恩惠 (00:20)"));
            }
            case MACALLAN -> {
                list.add(pos("幸运 II (00:20)"));
                list.add(pos("伤害吸收 (00:20)"));
            }
            case GLENFIDDICH -> {
                list.add(pos("发光 (00:20)"));
                list.add(pos("生命提升 III (00:20)"));
            }
            case BAINS -> {
                list.add(pos("村庄英雄 (00:40)"));
            }
            case SUNTORY -> {
                list.add(pos("缓降 (00:40)"));
            }
            case BUFFALO_TRACE -> {
                list.add(neg("飘浮 (00:40)"));
            }
            case JIM_BEAM -> {
                list.add(pos("隐身 (00:40)"));
            }

            case HONEY_WINE -> list.add(pos("蜂蜜 (00:10)"));
            case APPLE_FLAVOR_WINE -> list.add(pos("力量 III (00:40)"));
            case APPLE_WINE -> list.add(pos("抗性提升 (01:00)"));
            case VITALITY_WINE -> list.add(pos("瞬间治疗 III (00:20)"));
            case SUNSHINE_WINE -> list.add(pos("发光 (00:40)"));
            case CENTURY_WHITE_WINE -> list.add(pos("生命提升 III (00:30)"));
            case PINOT_NOIR -> list.add(pos("跳跃提升 (01:00)"));
            case FLAVOR_RED_WINE -> list.add(pos("急迫 (01:00)"));
            case MERLOT -> list.add(pos("夜视 (01:30)"));
            case CHERRY_WINE -> list.add(pos("饱和 (00:50)"));
            case SCUBA_DIVING -> list.add(pos("水下呼吸 (01:30)"));
            case TROPICAL_CREEPER_WINE -> list.add(neg("爆炸 (00:01)"));
            case TROPICAL_SEA_WINE -> list.add(pos("水上行者 (01:00)"));
            case LAMBRUSCO -> list.add(pos("狂欢 (00:30)"));
            case SPIDER_SPECIAL_WINE -> list.add(pos("攀爬 (00:30)"));
            case FROST_WHITE_WINE -> list.add(neg("冰冻 (00:20)"));
            case AEGIS_WHITE_WINE -> list.add(pos("护盾 (00:30)"));
            case BLAZE_WALKER_WINE -> list.add(pos("岩浆行者 (00:40)"));
            case CHORUS_WINE -> list.add(pos("瞬移 (00:15)"));
            case OMEN_WINE -> list.add(neg("不祥之兆 (00:40)"));
            case BOUNCE_WHITE_WINE -> list.add(pos("飞升 (00:10)"));
            case MAGNETIC_WINE -> list.add(pos("磁吸 (00:20)"));
            case HEALTH_RED_WINE -> {
                list.add(pos("生命提升 V (00:15)"));
                list.add(pos("生命恢复 V (00:15)"));
                list.add(pos("瞬间治疗 V (00:15)"));
            }
            case SPIDER_RED_WINE -> list.add(pos("免疫蜘蛛网 (00:40)"));
            case CATGIRL_RED_WINE -> list.add(pos("猫娘 (00:25)"));
            case ELDER_WINE -> list.add(pos("耄耋 (02:00)"));

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

    private static Component pos(String text) {
        return Component.text(text).color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static Component neg(String text) {
        return Component.text(text).color(NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false);
    }
}