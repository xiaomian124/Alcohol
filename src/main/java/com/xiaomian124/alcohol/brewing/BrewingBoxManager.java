package com.xiaomian124.alcohol.brewing;

import com.xiaomian124.alcohol.AlcoholPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class BrewingBoxManager {

    private BrewingBoxManager() {}

    private static final Map<String, BrewingBoxGUI> BOXES = new HashMap<>();

    private static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static Collection<String> getKeys() {
        return new ArrayList<>(BOXES.keySet());
    }

    public static Collection<BrewingBoxGUI> getAll() {
        return new ArrayList<>(BOXES.values());
    }

    public static Location parseLocation(String key) {
        String[] parts = key.split(":");
        if (parts.length != 4) return null;
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;
        try {
            return new Location(world,
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static BrewingBoxGUI getOrCreate(AlcoholPlugin plugin, Block block) {
        return BOXES.computeIfAbsent(key(block.getLocation()),
                k -> new BrewingBoxGUI(plugin, block));
    }

    public static BrewingBoxGUI get(Location loc) {
        return BOXES.get(key(loc));
    }

    public static void remove(Location loc) {
        BOXES.remove(key(loc));
    }

    public static void tickAll() {
        Iterator<BrewingBoxGUI> it = BOXES.values().iterator();
        while (it.hasNext()) {
            BrewingBoxGUI gui = it.next();
            if (gui.getBlock().getType() != Material.SMOKER) {
                it.remove();
                continue;
            }
            gui.tick();
        }
    }
}