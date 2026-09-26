package com.xiaomian124.alcohol;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class EffectStorage {

    private EffectStorage() {}

    private static File file;

    public static void init(AlcoholPlugin plugin) {
        file = new File(plugin.getDataFolder(), "effects.yml");
    }

    public static void saveAll() {
        if (file == null) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("EffectStorage.file 为 null");
            return;
        }

        YamlConfiguration cfg = new YamlConfiguration();

        int eIdx = 0;
        for (Map.Entry<UUID, Map<BeerType, Long>> entry
                : SpecialEffectManager.snapshotActive().entrySet()) {
            for (Map.Entry<BeerType, Long> e : entry.getValue().entrySet()) {
                String base = "effects." + eIdx++;
                cfg.set(base + ".player", entry.getKey().toString());
                cfg.set(base + ".type", e.getKey().name());
                cfg.set(base + ".end", e.getValue());
            }
        }

        int dIdx = 0;
        for (Map.Entry<UUID, List<DrinkEffectDisplay.TrackedEffect>> entry
                : DrinkEffectDisplay.snapshot().entrySet()) {
            for (DrinkEffectDisplay.TrackedEffect te : entry.getValue()) {
                String base = "display." + dIdx++;
                cfg.set(base + ".player", entry.getKey().toString());
                cfg.set(base + ".typeKey",
                        te.type == null ? null : te.type.getKey().toString());
                cfg.set(base + ".name", te.displayName);
                cfg.set(base + ".end", te.endTime);
                cfg.set(base + ".amp", te.amplifier);
            }
        }

        int webIdx = 0;
        for (Map.Entry<UUID, Set<Location>> entry
                : SpecialEffectManager.snapshotHiddenWebs().entrySet()) {
            for (Location loc : entry.getValue()) {
                String base = "webs." + webIdx++;
                cfg.set(base + ".player", entry.getKey().toString());
                cfg.set(base + ".world", loc.getWorld().getName());
                cfg.set(base + ".x", loc.getBlockX());
                cfg.set(base + ".y", loc.getBlockY());
                cfg.set(base + ".z", loc.getBlockZ());
            }
        }

        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            cfg.save(file);
        } catch (IOException e) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("保存 effects.yml 失败: " + e.getMessage());
        }
    }

    public static void loadAll() {
        if (file == null || !file.exists()) return;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        long now = System.currentTimeMillis();

        ConfigurationSection effSec = cfg.getConfigurationSection("effects");
        if (effSec != null) {
            Map<UUID, Map<BeerType, Long>> grouped = new HashMap<>();

            for (String idx : effSec.getKeys(false)) {
                ConfigurationSection s = effSec.getConfigurationSection(idx);
                if (s == null) continue;
                try {
                    UUID uuid = UUID.fromString(s.getString("player"));
                    BeerType type = BeerType.valueOf(s.getString("type"));
                    long end = s.getLong("end");
                    if (end > now) {
                        grouped.computeIfAbsent(uuid, k -> new HashMap<>())
                                .put(type, end);
                    }
                } catch (Exception ignored) {}
            }

            for (Map.Entry<UUID, Map<BeerType, Long>> e : grouped.entrySet()) {
                SpecialEffectManager.restoreActive(e.getKey(), e.getValue());
            }
        }

        ConfigurationSection dispSec = cfg.getConfigurationSection("display");
        if (dispSec != null) {
            Map<UUID, List<DrinkEffectDisplay.TrackedEffect>> grouped = new HashMap<>();

            for (String idx : dispSec.getKeys(false)) {
                ConfigurationSection s = dispSec.getConfigurationSection(idx);
                if (s == null) continue;
                try {
                    UUID uuid = UUID.fromString(s.getString("player"));
                    long end = s.getLong("end");
                    if (end <= now) continue;

                    String name = s.getString("name", "");
                    int amp = s.getInt("amp", 0);

                    PotionEffectType type = null;
                    String typeKey = s.getString("typeKey");
                    if (typeKey != null && !typeKey.isEmpty()) {
                        try {
                            NamespacedKey nk = NamespacedKey.fromString(typeKey);
                            if (nk != null) type = Registry.EFFECT.get(nk);
                        } catch (Exception ignored) {}
                    }

                    grouped.computeIfAbsent(uuid, k -> new ArrayList<>())
                            .add(new DrinkEffectDisplay.TrackedEffect(type, name, end, amp));
                } catch (Exception ignored) {}
            }

            for (Map.Entry<UUID, List<DrinkEffectDisplay.TrackedEffect>> e
                    : grouped.entrySet()) {
                DrinkEffectDisplay.restore(e.getKey(), e.getValue());
            }
        }

        ConfigurationSection webSec = cfg.getConfigurationSection("webs");
        if (webSec != null) {
            for (String idx : webSec.getKeys(false)) {
                ConfigurationSection s = webSec.getConfigurationSection(idx);
                if (s == null) continue;
                try {
                    UUID uuid = UUID.fromString(s.getString("player"));
                    World w = org.bukkit.Bukkit.getWorld(s.getString("world"));
                    if (w == null) continue;
                    Location loc = new Location(w,
                            s.getInt("x"), s.getInt("y"), s.getInt("z"));
                    SpecialEffectManager.restoreHiddenWeb(uuid, loc);
                } catch (Exception ignored) {}
            }
        }
    }
}