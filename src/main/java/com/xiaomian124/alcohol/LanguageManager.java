package com.xiaomian124.alcohol;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class LanguageManager {

    private LanguageManager() {}

    private static final String[] BUILTIN_LOCALES = {"en_US", "zh_CN"};
    private static final String FALLBACK_DEFAULT_LOCALE = "en_US";

    private static volatile String defaultLocale = FALLBACK_DEFAULT_LOCALE;

    private static final String CONFIG_KEY = "default-locale";
    private static final Map<String, YamlConfiguration> LANG = new HashMap<>();
    private static final Map<UUID, String> PLAYER_LOCALE = new HashMap<>();

    public static void init(AlcoholPlugin plugin) {
        LANG.clear();
        PLAYER_LOCALE.clear();

        releaseBuiltinLang(plugin);
        plugin.saveDefaultConfig();

        defaultLocale = loadDefaultLocale(plugin);

        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        File[] files = langDir.listFiles((d, n) -> n.endsWith(".yml"));
        if (files != null) {
            for (File f : files) {
                String code = f.getName().substring(0, f.getName().length() - 4);
                LANG.put(code, YamlConfiguration.loadConfiguration(f));
            }
        }

        if (LANG.isEmpty()) {
            try (InputStream in = LanguageManager.class.getClassLoader()
                    .getResourceAsStream("lang/" + FALLBACK_DEFAULT_LOCALE + ".yml")) {
                if (in != null) {
                    LANG.put(FALLBACK_DEFAULT_LOCALE, YamlConfiguration.loadConfiguration(
                            new InputStreamReader(in, StandardCharsets.UTF_8)));
                }
            } catch (Exception ignored) {}
        }

        if (!LANG.containsKey(defaultLocale)) {
            String resolved = null;
            for (String key : LANG.keySet()) {
                if (key.equalsIgnoreCase(defaultLocale)) { resolved = key; break; }
            }
            if (resolved == null && LANG.containsKey(FALLBACK_DEFAULT_LOCALE)) {
                resolved = FALLBACK_DEFAULT_LOCALE;
            }
            if (resolved == null && !LANG.isEmpty()) {
                resolved = LANG.keySet().iterator().next();
            }
            if (resolved != null) {
                if (!resolved.equals(defaultLocale)) {
                    plugin.getLogger().warning("Configured default-locale '" + defaultLocale
                            + "' has no matching language file, falling back to '"
                            + resolved + "'");
                }
                defaultLocale = resolved;
            }
        }

        plugin.getLogger().info("Loaded " + LANG.size() + " languages: " + LANG.keySet()
                + " (default: " + defaultLocale + ")");
    }

    private static String loadDefaultLocale(AlcoholPlugin plugin) {
        String value = plugin.getConfig().getString(CONFIG_KEY);
        if (value == null || value.isBlank()) {
            value = System.getProperty("alcohol.locale", FALLBACK_DEFAULT_LOCALE);
            plugin.getConfig().set(CONFIG_KEY, value);
            try {
                plugin.saveConfig();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to write default-locale into config.yml: "
                        + e.getMessage());
            }
        }
        return value.trim();
    }

    public static String getDefaultLocale() {
        return defaultLocale;
    }

    private static void releaseBuiltinLang(AlcoholPlugin plugin) {
        for (String code : BUILTIN_LOCALES) {
            String path = "lang/" + code + ".yml";
            File out = new File(plugin.getDataFolder(), path);
            if (!out.exists()) {
                try {
                    plugin.saveResource(path, false);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to release built-in language files " + path + ": " + e.getMessage());
                }
            }
        }
    }

    private static String getLocale(Player player) {
        if (player == null) return fallbackLocale();

        String cached = PLAYER_LOCALE.get(player.getUniqueId());
        if (cached != null) {
            if (LANG.containsKey(cached)) return cached;
            PLAYER_LOCALE.remove(player.getUniqueId());
        }

        String locale = player.getLocale();
        String code = null;

        for (String key : LANG.keySet()) {
            if (key.equalsIgnoreCase(locale)) { code = key; break; }
        }

        if (code == null && locale != null) {
            String main = locale.split("_")[0];
            for (String key : LANG.keySet()) {
                if (key.split("_")[0].equalsIgnoreCase(main)) { code = key; break; }
            }
        }

        if (code == null) code = fallbackLocale();

        PLAYER_LOCALE.put(player.getUniqueId(), code);
        return code;
    }

    private static String fallbackLocale() {
        if (LANG.containsKey(defaultLocale)) return defaultLocale;
        for (String key : LANG.keySet()) {
            if (key.equalsIgnoreCase(defaultLocale)) return key;
        }
        return LANG.isEmpty() ? defaultLocale : LANG.keySet().iterator().next();
    }

    public static void clearCache(UUID uuid) {
        PLAYER_LOCALE.remove(uuid);
    }

    public static String getRaw(Player player, String key) {
        YamlConfiguration cfg = LANG.get(getLocale(player));
        if (cfg == null) cfg = LANG.get(fallbackLocale());
        if (cfg == null) return key;

        String v = cfg.getString(key);
        if (v == null) {
            YamlConfiguration def = LANG.get(fallbackLocale());
            if (def != null && def != cfg) v = def.getString(key);
        }
        return v == null ? key : v;
    }

    public static String getRaw(Player player, String key, String... placeholders) {
        String s = getRaw(player, key);
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            s = s.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
        }
        return s;
    }

    public static Component get(Player player, String key, String... placeholders) {
        String raw = getRaw(player, key, placeholders);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }

    public static Component getDefault(String key, String... placeholders) {
        return get(null, key, placeholders);
    }
}