package com.xiaomian124.alcohol.apple;

import com.xiaomian124.alcohol.AlcoholPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ApplePressManager {

    private ApplePressManager() {}

    private static final Set<String> REGISTERED = new HashSet<>();
    private static final Map<String, ApplePressGUI> PRESSES = new HashMap<>();
    private static final Map<String, SavedState> STATES = new HashMap<>();

    public static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static void register(Location loc) {
        REGISTERED.add(key(loc));
    }

    public static void unregister(Location loc) {
        String k = key(loc);
        REGISTERED.remove(k);
        PRESSES.remove(k);
        STATES.remove(k);
    }

    public static boolean isApplePress(Location loc) {
        return REGISTERED.contains(key(loc));
    }

    public static Set<String> getRegisteredKeys() {
        return new HashSet<>(REGISTERED);
    }

    public static Location parseLocation(String k) {
        String[] parts = k.split(":");
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

    public static ApplePressGUI getOrCreate(AlcoholPlugin plugin, Block block) {
        return PRESSES.computeIfAbsent(key(block.getLocation()),
                k -> new ApplePressGUI(plugin, block));
    }

    public static ApplePressGUI get(Location loc) {
        return PRESSES.get(key(loc));
    }

    public static void saveState(Location loc, ItemStack[] items,
                                 int mashRemaining, int juiceRemaining) {
        STATES.put(key(loc),
                new SavedState(items, mashRemaining, juiceRemaining));
    }

    public static void saveContents(Location loc, ItemStack[] items,
                                    boolean mashActive, int mashRemaining,
                                    boolean juiceActive, int juiceRemaining) {
        saveState(loc, items, mashRemaining, juiceRemaining);
    }

    public static void saveContents(Location loc, ItemStack[] items,
                                    int mashRemaining, int juiceRemaining) {
        saveState(loc, items, mashRemaining, juiceRemaining);
    }

    public static SavedState loadState(Location loc) {
        return STATES.get(key(loc));
    }

    public static ItemStack[] loadContents(Location loc) {
        SavedState st = STATES.get(key(loc));
        return st == null ? null : st.items;
    }

    public static void tickAll() {
        Iterator<ApplePressGUI> it = PRESSES.values().iterator();
        while (it.hasNext()) {
            ApplePressGUI gui = it.next();
            Block block = gui.getBlock();
            if (block.getType() != Material.LOOM) {
                it.remove();
                continue;
            }
            gui.tick();
        }
    }

    public static class SavedState {
        public final ItemStack[] items;
        public final int mashRemaining;
        public final int juiceRemaining;

        public SavedState(ItemStack[] items, int mashRemaining, int juiceRemaining) {
            this.items = items;
            this.mashRemaining = mashRemaining;
            this.juiceRemaining = juiceRemaining;
        }
    }
}