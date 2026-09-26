package com.xiaomian124.alcohol.grape;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Leaves;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class GrapeManager {

    private GrapeManager() {}

    public static final int MAX_HARVEST = 8;
    public static final int GRAPE_MAX_STAGE = 3;

    public static final int GROUND_GROW_MIN = 6000;
    public static final int GROUND_GROW_MAX = 12000;
    public static final int VINE_GROW_MIN = 4800;
    public static final int VINE_GROW_MAX = 9600;

    private static final int PARTICLE_INTERVAL = 40;
    private static int particleTick = 0;

    public static final Map<String, GrapePlant> PLANTS = new HashMap<>();

    public static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static void register(Location anchor, GrapeType type) {
        PLANTS.put(key(anchor), new GrapePlant(anchor, type));
    }

    public static void remove(Location anchor) {
        PLANTS.remove(key(anchor));
    }

    public static GrapePlant get(Location anchor) {
        return PLANTS.get(key(anchor));
    }

    public static GrapePlant findByBlock(Location loc) {
        GrapePlant p = PLANTS.get(key(loc));
        if (p != null) return p;
        p = PLANTS.get(key(loc.clone().subtract(0, 1, 0)));
        if (p != null) return p;
        return PLANTS.get(key(loc.clone().add(0, 1, 0)));
    }

    public static void makeLeavesPersistent(Block block) {
        if (block.getBlockData() instanceof Leaves leaves) {
            leaves.setPersistent(true);
            block.setBlockData(leaves, false);
        }
    }

    public static void tickAll() {
        Iterator<GrapePlant> it = PLANTS.values().iterator();
        while (it.hasNext()) {
            GrapePlant p = it.next();
            Block anchor = p.anchorLocation.getBlock();

            if (!isValidAnchor(p, anchor)) {
                it.remove();
                continue;
            }

            GrapeType.GrowthMode mode = p.type.getGrowthMode();
            if (mode == GrapeType.GrowthMode.GROUND) {
                tickGround(p, anchor);
            } else {
                tickVineOrFence(p, anchor);
            }
        }

        particleTick++;
        if (particleTick >= PARTICLE_INTERVAL) {
            particleTick = 0;
            for (GrapePlant p : PLANTS.values()) {
                if (isRipe(p)) {
                    Block anchor = p.anchorLocation.getBlock();
                    anchor.getWorld().spawnParticle(
                            Particle.HAPPY_VILLAGER,
                            anchor.getLocation().add(0.5, 0.5, 0.5),
                            1, 0.3, 0.3, 0.3, 0);
                }
            }
        }
    }

    private static boolean isValidAnchor(GrapePlant p, Block anchor) {
        GrapeType.GrowthMode mode = p.type.getGrowthMode();
        Material m = anchor.getType();

        if (mode == GrapeType.GrowthMode.GROUND) {
            return m == Material.SWEET_BERRY_BUSH;
        } else if (mode == GrapeType.GrowthMode.VINE) {
            return m == Material.VINE;
        } else if (mode == GrapeType.GrowthMode.FENCE) {
            return m == Material.DARK_OAK_LEAVES || m == Material.JUNGLE_LEAVES;
        }
        return false;
    }

    private static void tickGround(GrapePlant p, Block block) {
        if (!(block.getBlockData() instanceof Ageable ageable)) return;
        int age = ageable.getAge();
        int maxAge = ageable.getMaximumAge();
        if (age >= maxAge) return;

        p.growTicks++;
        if (p.growThreshold <= 0) {
            p.growThreshold = ThreadLocalRandom.current()
                    .nextInt(GROUND_GROW_MIN, GROUND_GROW_MAX + 1);
        }
        if (p.growTicks >= p.growThreshold) {
            p.growTicks = 0;
            p.growThreshold = 0;
            ageable.setAge(Math.min(maxAge, age + 1));
            block.setBlockData(ageable, false);
        }
    }

    private static void tickVineOrFence(GrapePlant p, Block anchor) {
        if (p.growthStage >= GRAPE_MAX_STAGE) return;

        p.growTicks++;
        if (p.growThreshold <= 0) {
            p.growThreshold = ThreadLocalRandom.current()
                    .nextInt(VINE_GROW_MIN, VINE_GROW_MAX + 1);
        }
        if (p.growTicks < p.growThreshold) return;

        p.growTicks = 0;
        p.growThreshold = 0;
        advanceStage(p, anchor);
    }

    public static void advanceStage(GrapePlant p, Block anchor) {
        p.growthStage++;

        if (p.growthStage >= GRAPE_MAX_STAGE) {
            GrapeType.GrowthMode mode = p.type.getGrowthMode();
            if (mode == GrapeType.GrowthMode.FENCE) {
                anchor.setType(Material.JUNGLE_LEAVES, false);
                makeLeavesPersistent(anchor);
            }
        }
    }

    public static boolean isRipe(GrapePlant p) {
        if (p == null) return false;
        if (p.harvestCount >= MAX_HARVEST) return false;

        Block anchor = p.anchorLocation.getBlock();
        GrapeType.GrowthMode mode = p.type.getGrowthMode();

        if (mode == GrapeType.GrowthMode.GROUND) {
            if (anchor.getBlockData() instanceof Ageable ageable) {
                return ageable.getAge() >= 2;
            }
            return false;
        } else if (mode == GrapeType.GrowthMode.VINE) {
            return p.growthStage >= GRAPE_MAX_STAGE
                    && anchor.getType() == Material.VINE;
        } else if (mode == GrapeType.GrowthMode.FENCE) {
            return p.growthStage >= GRAPE_MAX_STAGE
                    && anchor.getType() == Material.JUNGLE_LEAVES;
        }
        return false;
    }

    public static boolean fertilize(GrapePlant p) {
        if (p == null) return false;
        if (isRipe(p)) return false;

        GrapeType.GrowthMode mode = p.type.getGrowthMode();
        Block anchor = p.anchorLocation.getBlock();

        // 地面
        if (mode == GrapeType.GrowthMode.GROUND) {
            if (anchor.getBlockData() instanceof Ageable ageable) {
                int age = ageable.getAge();
                int maxAge = ageable.getMaximumAge();
                if (age >= maxAge) return false;
                ageable.setAge(Math.min(maxAge, age + 1));
                anchor.setBlockData(ageable, false);
                return true;
            }
            return false;
        }

        if (p.growthStage >= GRAPE_MAX_STAGE) return false;

        p.growTicks = 0;
        p.growThreshold = 0;
        advanceStage(p, anchor);
        return true;
    }

    public static void resetAfterHarvest(GrapePlant p) {
        Block anchor = p.anchorLocation.getBlock();
        GrapeType.GrowthMode mode = p.type.getGrowthMode();

        if (mode == GrapeType.GrowthMode.GROUND) {
            if (anchor.getBlockData() instanceof Ageable ageable) {
                ageable.setAge(0);
                anchor.setBlockData(ageable, false);
            }
        } else if (mode == GrapeType.GrowthMode.VINE) {
            p.growthStage = 0;
            p.growTicks = 0;
            p.growThreshold = 0;
        } else if (mode == GrapeType.GrowthMode.FENCE) {
            anchor.setType(Material.DARK_OAK_LEAVES, false);
            makeLeavesPersistent(anchor);
            p.growthStage = 0;
            p.growTicks = 0;
            p.growThreshold = 0;
        }
    }

    public static void restorePlant(GrapePlant plant) {
        PLANTS.put(key(plant.anchorLocation), plant);
    }
}