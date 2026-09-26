package com.xiaomian124.alcohol.corn;

import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CornListener implements Listener {

    @EventHandler
    public void onPlant(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null || clicked.getType() != Material.FARMLAND) return;

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!ItemFactory.isCustom(hand, ItemKeys.CORN_SEED)) return;

        Block above = clicked.getRelative(BlockFace.UP);
        if (above.getType() != Material.AIR) return;

        event.setCancelled(true);

        CornManager.placeStageOne(above);
        CornManager.register(above.getLocation(), 1);

        if (player.getGameMode() != GameMode.CREATIVE) {
            hand.setAmount(hand.getAmount() - 1);
        }
        player.playSound(above.getLocation(), Sound.ITEM_CROP_PLANT, 1.0f, 1.0f);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onRightClickCorn(PlayerInteractEvent event) {
        if (event.getHand() == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        ItemStack hand = event.getItem();
        if (hand == null) return;
        if (!ItemFactory.isCustom(hand, ItemKeys.CORN)) return;

        Player player = event.getPlayer();

        if (player.isSneaking() && action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlaceCorn(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (ItemFactory.isCustom(item, ItemKeys.CORN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFertilize(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        CornPlant plant = CornManager.findByBlock(clicked.getLocation());
        if (plant == null) return;
        if (plant.stage >= 4) return;

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand.getType() != Material.BONE_MEAL) return;

        event.setCancelled(true);
        CornManager.fertilize(plant);

        Location loc = clicked.getLocation().add(0.5, 0.5, 0.5);
        clicked.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                loc, 8, 0.3, 0.3, 0.3, 0);
        clicked.getWorld().playSound(loc, Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.0f);

        if (player.getGameMode() != GameMode.CREATIVE) {
            hand.setAmount(hand.getAmount() - 1);
        }
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        if (CornManager.findByBlock(block.getLocation()) == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.PLAYER_HEAD) {
            BlockState state = block.getState();
            boolean isCornHead = false;
            if (state instanceof Skull skull) {
                isCornHead = skull.getPersistentDataContainer()
                        .has(ItemKeys.CORN, PersistentDataType.BYTE);
            }
            if (!isCornHead) return;

            CornPlant plant = CornManager.findByBlock(block.getLocation());
            if (plant == null) return;

            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createCorn());

            block.setType(Material.AIR, false);

            plant.harvestedTimes++;
            if (plant.harvestedTimes >= CornManager.MAX_HARVEST) {
                Block base = plant.baseLocation.getBlock();
                base.setType(Material.DEAD_BUSH, false);
                CornManager.remove(plant.baseLocation);
            } else {
                plant.stage = 3;
                plant.stageTicks = 0;
                plant.stageThreshold = 0;
            }
            return;
        }

        CornPlant plant = CornManager.findByBlock(block.getLocation());
        if (plant == null) return;

        Material type = block.getType();

        if (type == Material.DARK_OAK_SAPLING) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createCornSeed());

            if (plant.stage == 4) {
                Location headLoc = block.getLocation().add(0, 1, 0);
                Block headBlock = headLoc.getBlock();
                if (headBlock.getType() == Material.PLAYER_HEAD) {
                    BlockState headState = headBlock.getState();
                    if (headState instanceof Skull headSkull
                            && headSkull.getPersistentDataContainer()
                            .has(ItemKeys.CORN, PersistentDataType.BYTE)) {
                        block.getWorld().dropItemNaturally(
                                headLoc.clone().add(0.5, 0.5, 0.5),
                                ItemFactory.createCorn());
                        headBlock.setType(Material.AIR, false);
                    }
                }
            }
            CornManager.remove(plant.baseLocation);
            return;
        }

        if (type == Material.WHEAT || type == Material.FERN) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createCornSeed());
            CornManager.remove(plant.baseLocation);
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) return;
        if (!ItemFactory.isCustom(result, ItemKeys.CORN_KERNELS)) return;

        if (!isValidCornMatrix(event.getInventory().getMatrix())) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (!ItemFactory.isCustom(result, ItemKeys.CORN_KERNELS)) return;

        if (isValidCornMatrix(event.getInventory().getMatrix())) return;

        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player player) {
            player.updateInventory();
        }
    }

    private static boolean isValidCornMatrix(ItemStack[] matrix) {
        int cornCount = 0;
        int nonAirCount = 0;
        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) continue;
            nonAirCount++;
            if (ItemFactory.isCustom(item, ItemKeys.CORN)) {
                cornCount++;
            }
        }
        return nonAirCount == 1 && cornCount == 1;
    }
}