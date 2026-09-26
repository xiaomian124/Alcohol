package com.xiaomian124.alcohol.grape;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.concurrent.ThreadLocalRandom;

public class GrapeListener implements Listener {

    private final AlcoholPlugin plugin;

    public GrapeListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    private static boolean isGroundBlock(Material m) {
        return m == Material.GRASS_BLOCK
                || m == Material.DIRT
                || m == Material.MYCELIUM
                || m == Material.COARSE_DIRT
                || m == Material.PODZOL
                || m == Material.ROOTED_DIRT;
    }

    private static boolean isLog(Material m) {
        String n = m.name();
        return n.endsWith("_LOG") || n.endsWith("_WOOD")
                || n.endsWith("_STEM") || n.endsWith("_HYPHAE");
    }

    private static boolean isFence(Material m) {
        return m.name().endsWith("_FENCE") || m == Material.NETHER_BRICK_FENCE;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        ItemStack hand;
        if (slot == EquipmentSlot.HAND) {
            if (mainHand.getType() != Material.AIR) {
                hand = mainHand;
            } else if (offHand.getType() != Material.AIR) {
                return;
            } else {
                hand = null;
            }
        } else if (slot == EquipmentSlot.OFF_HAND) {
            if (mainHand.getType() != Material.AIR) return;
            hand = offHand;
            if (hand.getType() == Material.AIR) return;
        } else {
            return;
        }

        if (hand != null && hand.getType() != Material.AIR) {

            if (ItemFactory.getGrapeType(hand) != null) {
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                }
                return;
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (block == null) return;

                GrapeType seedType = ItemFactory.getGrapeSeedType(hand);
                if (seedType != null) {
                    event.setCancelled(true);
                    handlePlant(player, hand, block, event.getBlockFace(), seedType);
                    return;
                }

                if (hand.getType() == Material.BONE_MEAL) {
                    GrapePlant plant = GrapeManager.findByBlock(block.getLocation());
                    if (plant == null) return;

                    event.setCancelled(true);

                    if (GrapeManager.fertilize(plant)) {
                        block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                                block.getLocation().add(0.5, 0.5, 0.5),
                                8, 0.3, 0.3, 0.3, 0);
                        block.getWorld().playSound(block.getLocation(),
                                Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.0f);
                        if (player.getGameMode() != GameMode.CREATIVE) {
                            hand.setAmount(hand.getAmount() - 1);
                        }
                    }
                    return;
                }
            }
        }

        if (action != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        GrapePlant plant = GrapeManager.findByBlock(block.getLocation());
        if (plant == null) return;

        event.setCancelled(true);
        if (!GrapeManager.isRipe(plant)) return;
        harvest(player, plant, block);
    }

    private void handlePlant(Player player, ItemStack seed, Block clicked,
                             BlockFace clickedFace, GrapeType type) {
        GrapeType.GrowthMode mode = type.getGrowthMode();

        if (mode == GrapeType.GrowthMode.GROUND) {
            if (!isGroundBlock(clicked.getType())) return;
            Block above = clicked.getRelative(BlockFace.UP);
            if (above.getType() != Material.AIR) return;

            above.setType(Material.SWEET_BERRY_BUSH, false);
            if (above.getBlockData() instanceof Ageable ageable) {
                ageable.setAge(0);
                above.setBlockData(ageable, false);
            }
            GrapeManager.register(above.getLocation(), type);
            consumeSeed(player, seed);
            return;
        }

        if (mode == GrapeType.GrowthMode.VINE) {
            if (!isLog(clicked.getType())) return;
            if (clickedFace == null
                    || clickedFace == BlockFace.UP
                    || clickedFace == BlockFace.DOWN) return;

            Block vineBlock = clicked.getRelative(clickedFace);
            if (vineBlock.getType() != Material.AIR) return;

            vineBlock.setType(Material.VINE, false);
            if (vineBlock.getBlockData() instanceof MultipleFacing mf) {
                for (BlockFace face : BlockFace.values()) {
                    if (mf.getAllowedFaces().contains(face)) {
                        mf.setFace(face, false);
                    }
                }
                mf.setFace(clickedFace.getOppositeFace(), true);
                vineBlock.setBlockData(mf, false);
            }

            GrapeManager.register(vineBlock.getLocation(), type);
            consumeSeed(player, seed);
            return;
        }

        if (mode == GrapeType.GrowthMode.FENCE) {
            if (!isFence(clicked.getType())) return;

            Block above = clicked.getRelative(BlockFace.UP);
            Block below = clicked.getRelative(BlockFace.DOWN);

            if (isFence(above.getType())) {
                GrapePlant plant = new GrapePlant(above.getLocation(), type);
                plant.originalFenceMaterial = above.getType();

                above.setType(Material.DARK_OAK_LEAVES, false);
                GrapeManager.makeLeavesPersistent(above);

                GrapeManager.PLANTS.put(GrapeManager.key(above.getLocation()), plant);
                consumeSeed(player, seed);
                return;
            }

            if (isFence(below.getType())) {
                GrapePlant plant = new GrapePlant(clicked.getLocation(), type);
                plant.originalFenceMaterial = clicked.getType();

                clicked.setType(Material.DARK_OAK_LEAVES, false);
                GrapeManager.makeLeavesPersistent(clicked);

                GrapeManager.PLANTS.put(GrapeManager.key(clicked.getLocation()), plant);
                consumeSeed(player, seed);
            }
        }
    }

    private static void consumeSeed(Player player, ItemStack seed) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            seed.setAmount(seed.getAmount() - 1);
        }
        player.playSound(player.getLocation(), Sound.ITEM_CROP_PLANT, 1.0f, 1.0f);
    }

    private void harvest(Player player, GrapePlant plant, Block block) {
        GrapeType type = plant.type;

        block.getWorld().dropItemNaturally(
                block.getLocation().add(0.5, 0.5, 0.5),
                ItemFactory.createGrape(type));

        plant.harvestCount++;

        GrapeType.GrowthMode mode = type.getGrowthMode();

        if (plant.harvestCount >= GrapeManager.MAX_HARVEST) {
            if (mode == GrapeType.GrowthMode.GROUND) {
                block.setType(Material.DEAD_BUSH, false);
            } else if (mode == GrapeType.GrowthMode.VINE) {
                Location loc = block.getLocation().add(0.5, 0.5, 0.5);
                block.getWorld().spawnParticle(Particle.BLOCK,
                        loc, 20, 0.3, 0.3, 0.3,
                        Material.VINE.createBlockData());
                block.getWorld().playSound(loc,
                        Sound.BLOCK_GRASS_BREAK, 1.0f, 1.0f);
                block.setType(Material.AIR, false);
            } else if (mode == GrapeType.GrowthMode.FENCE) {
                Material fenceMat = plant.originalFenceMaterial != null
                        ? plant.originalFenceMaterial : Material.OAK_FENCE;
                block.setType(fenceMat, true);
            }
            GrapeManager.remove(plant.anchorLocation);
            return;
        }

        GrapeManager.resetAfterHarvest(plant);
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent event) {
        Block source = event.getSource();
        Block target = event.getBlock();

        if (source.getType() == Material.VINE
                || target.getType() == Material.VINE) {

            if (GrapeManager.findByBlock(source.getLocation()) != null
                    || GrapeManager.findByBlock(target.getLocation()) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        GrapeType type = ItemFactory.getGrapeType(event.getItem());
        if (type == null) return;

        Player player = event.getPlayer();

        if (ThreadLocalRandom.current().nextDouble() < 0.15) {
            ItemStack seed = ItemFactory.createGrapeSeed(type);
            player.getInventory().addItem(seed).forEach((i, drop) ->
                    player.getWorld().dropItemNaturally(player.getLocation(), drop));
            player.sendActionBar(Component.text("你在葡萄里吃出了" + type.getSeedName())
                    .color(NamedTextColor.YELLOW));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        GrapePlant plant = GrapeManager.findByBlock(block.getLocation());
        if (plant == null) return;

        GrapeType.GrowthMode mode = plant.type.getGrowthMode();

        if (mode == GrapeType.GrowthMode.GROUND
                && block.getType() == Material.SWEET_BERRY_BUSH) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createGrapeSeed(plant.type));
            GrapeManager.remove(plant.anchorLocation);
            return;
        }

        if (mode == GrapeType.GrowthMode.VINE
                && block.getType() == Material.VINE) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createGrapeSeed(plant.type));
            GrapeManager.remove(plant.anchorLocation);
            return;
        }

        if (mode == GrapeType.GrowthMode.FENCE) {
            if (block.getType() == Material.DARK_OAK_LEAVES
                    || block.getType() == Material.JUNGLE_LEAVES) {
                event.setCancelled(true);
                event.setDropItems(false);
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(plant.type));
                Material fenceMat = plant.originalFenceMaterial != null
                        ? plant.originalFenceMaterial : Material.OAK_FENCE;
                block.setType(fenceMat, true);
                GrapeManager.remove(plant.anchorLocation);
            }
        }
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        if (GrapeManager.get(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        Material m = block.getType();
        if (m != Material.DARK_OAK_LEAVES && m != Material.JUNGLE_LEAVES) return;

        if (GrapeManager.findByBlock(block.getLocation()) != null) {
            event.setCancelled(true);
        }
    }
}