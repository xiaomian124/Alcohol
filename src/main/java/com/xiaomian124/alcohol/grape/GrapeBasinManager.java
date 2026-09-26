package com.xiaomian124.alcohol.grape;

import com.xiaomian124.alcohol.AlcoholPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class GrapeBasinManager {

    private GrapeBasinManager() {}

    public static final int MAX_LEVEL = 6;
    public static final int TOP_LEVEL = 8;
    public static final int MAX_JUICE = 2;
    public static final int NEEDED_JUMPS = 3;

    private static final Set<String> REGISTERED = new HashSet<>();
    private static final Map<String, GrapeBasin> BASINS = new HashMap<>();

    public static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static void register(Location loc) {
        String k = key(loc);
        REGISTERED.add(k);
        BASINS.put(k, new GrapeBasin(loc));
    }

    public static void unregister(Location loc) {
        String k = key(loc);
        REGISTERED.remove(k);
        BASINS.remove(k);
    }

    public static boolean isGrapeBasin(Location loc) {
        return REGISTERED.contains(key(loc));
    }

    public static GrapeBasin get(Location loc) {
        return BASINS.get(key(loc));
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

    public static int getLevel(Block block) {
        if (block.getBlockData() instanceof Levelled levelled) {
            return levelled.getLevel();
        }
        return 0;
    }

    public static void setLevel(Block block, int level) {
        if (block.getBlockData() instanceof Levelled levelled) {
            int max = levelled.getMaximumLevel();
            levelled.setLevel(Math.max(0, Math.min(max, level)));
            block.setBlockData(levelled, false);
        }
    }

    public static void restoreState(Location loc, GrapeType type,
                                    int juiceCount, int jumpCount) {
        String k = key(loc);
        GrapeBasin basin = BASINS.get(k);
        if (basin == null) {
            basin = new GrapeBasin(loc);
            BASINS.put(k, basin);
        }
        basin.grapeType = type;
        basin.juiceCount = juiceCount;
        basin.jumpCount = jumpCount;
    }
}