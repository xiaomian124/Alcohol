package com.xiaomian124.alcohol.cherry;

import com.xiaomian124.alcohol.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class CherryManager {

    private CherryManager() {}

    private static final Set<String> SAPLINGS = new HashSet<>();
    private static final Map<String, CherryTree> TREE_BY_LEAF = new HashMap<>();
    private static final Map<String, CherryTree> TREE_BY_LOG = new HashMap<>();

    private static final int RIPEN_INTERVAL = 60;
    private static final double RIPEN_CHANCE = 0.02;
    private static final int PARTICLE_INTERVAL = 40;

    private static int ripenTick = 0;
    private static int particleTick = 0;

    public static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static Location parseKey(String k) {
        String[] parts = k.split(":");
        if (parts.length != 4) return null;
        World w = Bukkit.getWorld(parts[0]);
        if (w == null) return null;
        try {
            return new Location(w,
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) { return null; }
    }

    public static void registerSapling(Location loc) {
        SAPLINGS.add(key(loc));
    }

    public static boolean isSapling(Location loc) {
        return SAPLINGS.contains(key(loc));
    }

    public static void removeSapling(Location loc) {
        SAPLINGS.remove(key(loc));
    }

    public static void registerTree(CherryTree tree) {
        for (String k : tree.logs) TREE_BY_LOG.put(k, tree);
        for (String k : tree.leaves) TREE_BY_LEAF.put(k, tree);
    }

    public static CherryTree getTreeByLog(Location loc) {
        return TREE_BY_LOG.get(key(loc));
    }

    public static CherryTree getTreeByLeaf(Location loc) {
        return TREE_BY_LEAF.get(key(loc));
    }

    public static void unregisterTree(CherryTree tree) {
        for (String k : tree.logs) TREE_BY_LOG.remove(k);
        for (String k : tree.leaves) TREE_BY_LEAF.remove(k);
    }

    public static void addLeaf(CherryTree tree, Location loc) {
        tree.leaves.add(key(loc));
        TREE_BY_LEAF.put(key(loc), tree);
    }

    public static void tickAll() {
        ripenTick++;
        if (ripenTick >= RIPEN_INTERVAL) {
            ripenTick = 0;
            for (CherryTree tree : new HashSet<>(TREE_BY_LEAF.values())) {
                for (String k : new HashSet<>(tree.leaves)) {
                    if (ThreadLocalRandom.current().nextDouble() > RIPEN_CHANCE) continue;
                    Location loc = parseKey(k);
                    if (loc == null) continue;
                    Block block = loc.getBlock();
                    if (block.getType() == Material.OAK_LEAVES) {
                        block.setType(Material.JUNGLE_LEAVES, false);
                        if (block.getBlockData() instanceof Leaves leaves) {
                            leaves.setPersistent(true);
                            block.setBlockData(leaves, false);
                        }
                    }
                }
            }
        }

        particleTick++;
        if (particleTick >= PARTICLE_INTERVAL) {
            particleTick = 0;
            for (String k : new HashSet<>(TREE_BY_LEAF.keySet())) {
                Location loc = parseKey(k);
                if (loc == null) continue;
                Block block = loc.getBlock();
                if (block.getType() == Material.JUNGLE_LEAVES) {
                    block.getWorld().spawnParticle(
                            Particle.HAPPY_VILLAGER,
                            block.getLocation().add(0.5, 0.5, 0.5),
                            1, 0.3, 0.3, 0.3, 0);
                }
            }
        }
    }

    public static void dropCherryFromHarvest(Block block) {
        block.getWorld().dropItemNaturally(
                block.getLocation().add(0.5, 0.5, 0.5),
                ItemFactory.createCherry());
        if (ThreadLocalRandom.current().nextDouble() < 0.20) {
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createRottenCherry());
        }
    }

    public static void dropCherryFromBreak(Block block, boolean matured) {
        double r = ThreadLocalRandom.current().nextDouble();
        if (r < 0.30) {
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    new org.bukkit.inventory.ItemStack(Material.STICK));
        }
        double cherryChance = matured ? 0.40 : 0.15;
        if (ThreadLocalRandom.current().nextDouble() < cherryChance) {
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createCherry());
        }
        if (ThreadLocalRandom.current().nextDouble() < 0.10) {
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createRottenCherry());
        }
    }

    public static Set<String> getAllSaplingKeys() {
        return new HashSet<>(SAPLINGS);
    }

    public static List<CherryTree> getAllTrees() {
        return new ArrayList<>(new HashSet<>(TREE_BY_LOG.values()));
    }

    public static void restoreSapling(Location loc) {
        SAPLINGS.add(key(loc));
    }

    public static void restoreTree(CherryTree tree) {
        for (String k : tree.logs) TREE_BY_LOG.put(k, tree);
        for (String k : tree.leaves) TREE_BY_LEAF.put(k, tree);
    }
}