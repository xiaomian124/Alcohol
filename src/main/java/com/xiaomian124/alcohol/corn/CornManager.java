package com.xiaomian124.alcohol.corn;

import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class CornManager {

    private CornManager() {}

    public static final int STAGE_MIN = 4000;
    public static final int STAGE_MAX = 8000;
    public static final int MAX_HARVEST = 3;

    private static final double BEE_RANGE = 2.0;
    private static final double BEE_POLLINATE_CHANCE = 0.005;

    private static final Map<String, CornPlant> PLANTS = new HashMap<>();

    private static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static void register(Location baseLoc, int stage) {
        CornPlant plant = new CornPlant(baseLoc);
        plant.stage = stage;
        PLANTS.put(key(baseLoc), plant);
    }

    public static List<CornPlant> getAllPlants() {
        return new ArrayList<>(PLANTS.values());
    }

    public static void restorePlant(CornPlant plant) {
        PLANTS.put(key(plant.baseLocation), plant);
    }

    public static void remove(Location baseLoc) {
        PLANTS.remove(key(baseLoc));
    }

    public static CornPlant get(Location baseLoc) {
        return PLANTS.get(key(baseLoc));
    }

    public static CornPlant findByBlock(Location blockLoc) {
        CornPlant p = PLANTS.get(key(blockLoc));
        if (p != null) return p;
        Location below = blockLoc.clone().subtract(0, 1, 0);
        return PLANTS.get(key(below));
    }

    public static void tickAll() {
        Iterator<Map.Entry<String, CornPlant>> it = PLANTS.entrySet().iterator();
        while (it.hasNext()) {
            CornPlant plant = it.next().getValue();
            Block base = plant.baseLocation.getBlock();

            if (!isValidBlock(base, plant.stage)) {
                it.remove();
                continue;
            }

            if (plant.stage >= 4) continue;

            if (tryBeePollinate(plant)) continue;

            plant.stageTicks++;

            if (plant.stageThreshold <= 0) {
                plant.stageThreshold = ThreadLocalRandom.current()
                        .nextInt(STAGE_MIN, STAGE_MAX + 1);
            }

            if (plant.stageTicks >= plant.stageThreshold) {
                plant.stageTicks = 0;
                plant.stageThreshold = 0;
                advanceStage(plant);
            }
        }
    }

    private static boolean tryBeePollinate(CornPlant plant) {
        Location center = plant.baseLocation.clone().add(0.5, 0.5, 0.5);

        if (ThreadLocalRandom.current().nextDouble() > BEE_POLLINATE_CHANCE) {
            return false;
        }

        for (Entity entity : center.getWorld().getNearbyEntities(
                center, BEE_RANGE, BEE_RANGE, BEE_RANGE)) {
            if (!(entity instanceof Bee bee)) continue;
            if (!bee.hasNectar()) continue;

            advanceStage(plant);
            return true;
        }
        return false;
    }

    private static boolean isValidBlock(Block block, int stage) {
        Material m = block.getType();
        switch (stage) {
            case 1: return m == Material.WHEAT;
            case 2: return m == Material.FERN;
            case 3:
            case 4: return m == Material.DARK_OAK_SAPLING;
            default: return false;
        }
    }

    public static void advanceStage(CornPlant plant) {
        Block base = plant.baseLocation.getBlock();

        if (plant.stage == 1) {
            base.setType(Material.FERN, false);
            plant.stage = 2;
            plant.stageTicks = 0;
            plant.stageThreshold = 0;

        } else if (plant.stage == 2) {
            base.setType(Material.DARK_OAK_SAPLING, false);
            plant.stage = 3;
            plant.stageTicks = 0;
            plant.stageThreshold = 0;

        } else if (plant.stage == 3) {
            Location headLoc = plant.baseLocation.clone().add(0, 1, 0);
            Block headBlock = headLoc.getBlock();

            if (headBlock.getType() == Material.PLAYER_HEAD) {
                BlockState hs = headBlock.getState();
                if (hs instanceof Skull hsSkull
                        && hsSkull.getPersistentDataContainer()
                        .has(ItemKeys.CORN, PersistentDataType.BYTE)) {
                    plant.stage = 4;
                    plant.stageTicks = 0;
                    plant.stageThreshold = 0;
                    return;
                }
                return;
            }

            if (headBlock.getType() != Material.AIR) return;

            placeCornHead(headBlock);
            plant.stage = 4;
            plant.stageTicks = 0;
            plant.stageThreshold = 0;
        }
    }

    public static void placeCornHead(Block block) {
        block.setType(Material.PLAYER_HEAD, false);
        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        ItemStack temp = ItemFactory.createCorn();
        if (temp.getItemMeta() instanceof SkullMeta sm) {
            skull.setOwnerProfile(sm.getOwnerProfile());
        }
        skull.setRotation(BlockFace.NORTH);

        skull.getPersistentDataContainer().set(
                ItemKeys.CORN, PersistentDataType.BYTE, (byte) 1);

        skull.update(true, false);
    }

    public static void placeStageOne(Block block) {
        block.setType(Material.WHEAT, false);
        BlockData data = block.getBlockData();
        if (data instanceof Ageable ageable) {
            ageable.setAge(0);
            block.setBlockData(ageable, false);
        }
    }

    public static boolean fertilize(CornPlant plant) {
        if (plant == null) return false;
        if (plant.stage >= 4) return false;
        advanceStage(plant);
        return true;
    }
}