package com.xiaomian124.alcohol;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DrinkEffectDisplay {

    private DrinkEffectDisplay() {}

    private static final Map<UUID, List<TrackedEffect>> ACTIVE = new HashMap<>();

    public static class TrackedEffect {
        public final PotionEffectType type;
        public final String displayName;
        public final long endTime;
        public final int amplifier;

        public TrackedEffect(PotionEffectType type, String displayName,
                             long endTime, int amplifier) {
            this.type = type;
            this.displayName = displayName;
            this.endTime = endTime;
            this.amplifier = amplifier;
        }
    }

    public static void record(Player p, PotionEffectType type,
                              int durationTicks, int amplifier) {
        String name = getEffectName(type);
        long end = System.currentTimeMillis() + durationTicks * 50L;
        addOrReplace(p, new TrackedEffect(type, name, end, amplifier));
    }

    public static void recordSpecial(Player p, String displayName, int durationTicks) {
        long end = System.currentTimeMillis() + durationTicks * 50L;
        addOrReplace(p, new TrackedEffect(null, displayName, end, 0));
    }

    private static void addOrReplace(Player p, TrackedEffect effect) {
        List<TrackedEffect> list = ACTIVE.computeIfAbsent(
                p.getUniqueId(), k -> new ArrayList<>());

        if (effect.type != null) {
            list.removeIf(e -> e.type == effect.type);
        } else {
            list.removeIf(e -> e.displayName.equals(effect.displayName));
        }
        list.add(effect);
    }

    public static void tickAll() {
        long now = System.currentTimeMillis();

        Iterator<Map.Entry<UUID, List<TrackedEffect>>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, List<TrackedEffect>> entry = it.next();
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p == null || !p.isOnline()) {
                it.remove();
                continue;
            }

            List<TrackedEffect> list = entry.getValue();
            list.removeIf(e -> e.endTime <= now);

            if (list.isEmpty()) {
                it.remove();
                continue;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                TrackedEffect e = list.get(i);
                long remainMs = e.endTime - now;
                long remainSec = (remainMs + 999) / 1000;

                if (i > 0) sb.append(" | ");

                sb.append(e.displayName);
                if (e.amplifier > 0) {
                    sb.append(" ").append(toRoman(e.amplifier + 1));
                }
                sb.append(" ").append(formatTime(remainSec));
            }

            p.sendActionBar(Component.text(sb.toString())
                    .color(NamedTextColor.YELLOW));
        }
    }

    public static void clearAll(Player p) {
        ACTIVE.remove(p.getUniqueId());
    }

    public static Map<UUID, List<TrackedEffect>> snapshot() {
        Map<UUID, List<TrackedEffect>> copy = new HashMap<>();
        for (var e : ACTIVE.entrySet()) {
            copy.put(e.getKey(), new ArrayList<>(e.getValue()));
        }
        return copy;
    }

    public static void restore(UUID uuid, List<TrackedEffect> effects) {
        if (effects == null || effects.isEmpty()) return;
        long now = System.currentTimeMillis();
        effects.removeIf(e -> e.endTime <= now);
        if (!effects.isEmpty()) {
            ACTIVE.put(uuid, effects);
        }
    }

    private static String formatTime(long seconds) {
        if (seconds >= 60) {
            long min = seconds / 60;
            long sec = seconds % 60;
            return String.format("%d:%02d", min, sec);
        }
        return seconds + "s";
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(n);
        };
    }

    private static String getEffectName(PotionEffectType type) {
        String key = null;
        if (type.equals(PotionEffectType.STRENGTH)) key = "effect.name.strength";
        else if (type.equals(PotionEffectType.REGENERATION)) key = "effect.name.regeneration";
        else if (type.equals(PotionEffectType.RESISTANCE)) key = "effect.name.resistance";
        else if (type.equals(PotionEffectType.INSTANT_HEALTH)) key = "effect.name.instant_health";
        else if (type.equals(PotionEffectType.HASTE)) key = "effect.name.haste";
        else if (type.equals(PotionEffectType.JUMP_BOOST)) key = "effect.name.jump_boost";
        else if (type.equals(PotionEffectType.SPEED)) key = "effect.name.speed";
        else if (type.equals(PotionEffectType.INVISIBILITY)) key = "effect.name.invisibility";
        else if (type.equals(PotionEffectType.NIGHT_VISION)) key = "effect.name.night_vision";
        else if (type.equals(PotionEffectType.FIRE_RESISTANCE)) key = "effect.name.fire_resistance";
        else if (type.equals(PotionEffectType.CONDUIT_POWER)) key = "effect.name.conduit_power";
        else if (type.equals(PotionEffectType.DOLPHINS_GRACE)) key = "effect.name.dolphins_grace";
        else if (type.equals(PotionEffectType.LUCK)) key = "effect.name.luck";
        else if (type.equals(PotionEffectType.ABSORPTION)) key = "effect.name.absorption";
        else if (type.equals(PotionEffectType.GLOWING)) key = "effect.name.glowing";
        else if (type.equals(PotionEffectType.HEALTH_BOOST)) key = "effect.name.health_boost";
        else if (type.equals(PotionEffectType.HERO_OF_THE_VILLAGE)) key = "effect.name.hero_of_the_village";
        else if (type.equals(PotionEffectType.SLOW_FALLING)) key = "effect.name.slow_falling";
        else if (type.equals(PotionEffectType.LEVITATION)) key = "effect.name.levitation";
        else if (type.equals(PotionEffectType.SATURATION)) key = "effect.name.saturation";
        else if (type.equals(PotionEffectType.WATER_BREATHING)) key = "effect.name.water_breathing";
        else if (type.equals(PotionEffectType.BAD_OMEN)) key = "effect.name.bad_omen";
        else if (type.equals(PotionEffectType.POISON)) key = "effect.name.poison";

        if (key == null) return type.getKey().getKey();
        return LanguageManager.getRaw(null, key);
    }
}