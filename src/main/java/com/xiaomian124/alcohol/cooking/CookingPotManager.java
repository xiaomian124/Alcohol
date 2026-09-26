package com.xiaomian124.alcohol.cooking;

import com.xiaomian124.alcohol.AlcoholPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class CookingPotManager {

    private CookingPotManager() {}

    private static final Set<String> REGISTERED = new HashSet<>();

    private static final Map<String, Integer> WATER_LEVELS = new HashMap<>();
    private static final Map<String, CookingPotGUI> POTS = new HashMap<>();
    private static final Map<String, ItemStack[]> SAVED_CONTENTS = new HashMap<>();

    public static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static void register(Location loc) {
        String k = key(loc);
        REGISTERED.add(k);
        WATER_LEVELS.put(k, 0);
    }

    public static void unregister(Location loc) {
        String k = key(loc);
        REGISTERED.remove(k);
        WATER_LEVELS.remove(k);
        POTS.remove(k);
        SAVED_CONTENTS.remove(k);
    }

    public static boolean isCookingPot(Location loc) {
        return REGISTERED.contains(key(loc));
    }

    public static int getWater(Location loc) {
        return WATER_LEVELS.getOrDefault(key(loc), 0);
    }

    public static void setWater(Location loc, int level) {
        WATER_LEVELS.put(key(loc), Math.max(0, Math.min(3, level)));
    }

    public static CookingPotGUI getOrCreate(AlcoholPlugin plugin, Block block) {
        return POTS.computeIfAbsent(key(block.getLocation()),
                k -> new CookingPotGUI(plugin, block));
    }

    public static CookingPotGUI get(Location loc) {
        return POTS.get(key(loc));
    }

    public static void remove(Location loc) {
        unregister(loc);
    }

    public static void saveContents(Location loc, ItemStack[] items) {
        SAVED_CONTENTS.put(key(loc), items);
    }

    public static ItemStack[] loadContents(Location loc) {
        return SAVED_CONTENTS.get(key(loc));
    }

    public static void updateCauldronBlock(Block block, int level) {
        int clamped = Math.max(0, Math.min(3, level));
        if (clamped == 0) {
            if (block.getType() != Material.CAULDRON) {
                block.setType(Material.CAULDRON, false);
            }
            return;
        }
        BlockData data = Material.WATER_CAULDRON.createBlockData();
        if (data instanceof Levelled levelled) {
            levelled.setLevel(clamped);
        }
        block.setBlockData(data, false);
    }

    public static Set<String> getRegisteredKeys() {
        return new HashSet<>(REGISTERED);
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

    public static void tickAll() {
        Iterator<CookingPotGUI> it = POTS.values().iterator();
        while (it.hasNext()) {
            CookingPotGUI gui = it.next();
            Block block = gui.getBlock();
            if (block.getType() != Material.CAULDRON
                    && block.getType() != Material.WATER_CAULDRON) {
                it.remove();
                continue;
            }
            gui.tick();
        }
    }
}