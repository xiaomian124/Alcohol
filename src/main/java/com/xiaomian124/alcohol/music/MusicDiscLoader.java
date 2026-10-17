package com.xiaomian124.alcohol.music;

import com.xiaomian124.alcohol.AlcoholPlugin;
import org.bukkit.Material;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class MusicDiscLoader {

    private MusicDiscLoader() {}

    private static final Material[] ICONS = {
            Material.MUSIC_DISC_13,
            Material.MUSIC_DISC_CAT,
            Material.MUSIC_DISC_BLOCKS,
            Material.MUSIC_DISC_CHIRP,
            Material.MUSIC_DISC_FAR,
            Material.MUSIC_DISC_MALL,
            Material.MUSIC_DISC_MELLOHI,
            Material.MUSIC_DISC_STAL,
            Material.MUSIC_DISC_STRAD,
            Material.MUSIC_DISC_WARD,
            Material.MUSIC_DISC_11,
            Material.MUSIC_DISC_WAIT,
            Material.MUSIC_DISC_OTHERSIDE,
            Material.MUSIC_DISC_5,
            Material.MUSIC_DISC_PIGSTEP,
            Material.MUSIC_DISC_RELIC,
            Material.MUSIC_DISC_CREATOR,
            Material.MUSIC_DISC_CREATOR_MUSIC_BOX,
            Material.MUSIC_DISC_PRECIPICE,
            Material.MUSIC_DISC_RELIC
    };

    public static List<MusicDisc> loadAll() {
        List<MusicDisc> result = new ArrayList<>();

        try {
            var pluginFile = AlcoholPlugin.class.getProtectionDomain()
                    .getCodeSource().getLocation().getFile();
            java.io.File jarFile = new java.io.File(
                    java.net.URLDecoder.decode(pluginFile, "UTF-8"));

            if (!jarFile.isFile()) {
                AlcoholPlugin.getInstance().getLogger()
                        .warning("Packaged jar not detected, cannot scan songs automatically.");
                return result;
            }

            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                int index = 0;

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (!name.startsWith("songs/") || !name.endsWith(".nbs")) continue;
                    if (entry.isDirectory()) continue;

                    String fileName = name.substring(
                            "songs/".length(), name.length() - 4);

                    Material icon = ICONS[index % ICONS.length];

                    MusicDisc disc = new MusicDisc(fileName, fileName, icon);
                    result.add(disc);
                    index++;
                }
            }

            AlcoholPlugin.getInstance().getLogger()
                    .info("Automatically loaded " + result.size() + " built-in nbs songs.");
        } catch (Exception e) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("Failed to scan nbs songs: " + e.getMessage());
        }

        return result;
    }
}