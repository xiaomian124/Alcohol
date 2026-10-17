package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import com.xiaomian124.alcohol.aging.AgingBarrelGUI;
import com.xiaomian124.alcohol.aging.AgingBarrelManager;
import com.xiaomian124.alcohol.apple.ApplePressGUI;
import com.xiaomian124.alcohol.apple.ApplePressManager;
import com.xiaomian124.alcohol.brewing.BrewingBoxGUI;
import com.xiaomian124.alcohol.brewing.BrewingBoxManager;
import com.xiaomian124.alcohol.cooking.CookingPotGUI;
import com.xiaomian124.alcohol.cooking.CookingPotManager;
import com.xiaomian124.alcohol.grape.GrapeBasinManager;
import com.xiaomian124.alcohol.music.MusicPlayerManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class BlockListener implements Listener {

    private final AlcoholPlugin plugin;

    public BlockListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Block block = event.getBlockPlaced();
        BlockState state = block.getState();

        if (ItemFactory.isCustom(item, ItemKeys.COOKING_POT)) {
            CookingPotManager.register(block.getLocation());
            return;
        }

        if (ItemFactory.isCustom(item, ItemKeys.APPLE_PRESS)) {
            ApplePressManager.register(block.getLocation());
            if (state instanceof TileState tileState) {
                tileState.getPersistentDataContainer().set(
                        ItemKeys.APPLE_PRESS, PersistentDataType.BYTE, (byte) 1);
                tileState.update(true, false);
            }
            return;
        }

        if (ItemFactory.isCustom(item, ItemKeys.AGING_BARREL)) {
            AgingBarrelManager.register(block.getLocation());
            return;
        }

        if (ItemFactory.isCustom(item, ItemKeys.GRAPE_BASIN)) {
            GrapeBasinManager.register(block.getLocation());
            GrapeBasinManager.setLevel(block, 0);
            return;
        }

        if (ItemFactory.isCustom(item, ItemKeys.BREWING_BOX)) {
            if (state instanceof TileState tileState) {
                tileState.getPersistentDataContainer().set(
                        ItemKeys.BREWING_BOX, PersistentDataType.BYTE, (byte) 1);
                tileState.update(true, false);
            }
            return;
        }

        if (ItemFactory.isCustom(item, ItemKeys.MUSIC_PLAYER)) {
            if (state instanceof TileState tileState) {
                tileState.getPersistentDataContainer().set(
                        ItemKeys.MUSIC_PLAYER, PersistentDataType.BYTE, (byte) 1);
                tileState.update(true, false);
            }
            MusicPlayerManager.getOrCreate(block.getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (CookingPotManager.isCookingPot(block.getLocation())) {
            CookingPotGUI gui = CookingPotManager.get(block.getLocation());
            if (gui != null) {
                dropSlots(block, gui.getInventory(), CookingPotGUI.getDropSlots());
            }
            CookingPotManager.unregister(block.getLocation());

            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createCookingPot(player));
            return;
        }

        if (ApplePressManager.isApplePress(block.getLocation())) {
            ApplePressGUI gui = ApplePressManager.get(block.getLocation());
            if (gui != null) {
                dropSlots(block, gui.getInventory(), ApplePressGUI.getDropSlots());
            }
            ApplePressManager.unregister(block.getLocation());

            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createApplePress(player));
            return;
        }

        if (AgingBarrelManager.isAgingBarrel(block.getLocation())) {
            AgingBarrelGUI gui = AgingBarrelManager.get(block.getLocation());
            if (gui != null) {
                dropSlots(block, gui.getInventory(), AgingBarrelGUI.getDropSlots());
            }
            AgingBarrelManager.unregister(block.getLocation());

            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createAgingBarrel(player));
            return;
        }

        if (GrapeBasinManager.isGrapeBasin(block.getLocation())) {
            GrapeBasinManager.unregister(block.getLocation());
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createGrapeBasin(player));
            return;
        }

        BlockState state = block.getState();
        if (state instanceof TileState tileState
                && tileState.getPersistentDataContainer()
                .has(ItemKeys.BREWING_BOX, PersistentDataType.BYTE)) {

            BrewingBoxGUI gui = BrewingBoxManager.get(block.getLocation());
            if (gui != null) {
                dropSlots(block, gui.getInventory(), BrewingBoxGUI.getDropSlots());

                for (ItemStack pending : gui.getPendingOutputs()) {
                    if (pending != null && pending.getType() != Material.AIR) {
                        block.getWorld().dropItemNaturally(
                                block.getLocation().add(0.5, 0.5, 0.5), pending);
                    }
                }
            }
            BrewingBoxManager.remove(block.getLocation());

            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createBrewingBox(player));
            return;
        }

        if (state instanceof TileState tileState
                && tileState.getPersistentDataContainer()
                .has(ItemKeys.MUSIC_PLAYER, PersistentDataType.BYTE)) {

            MusicPlayerManager.remove(block.getLocation());
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createMusicPlayer(player));
        }
    }

    private static void dropSlots(Block block, Inventory inv, int[] slots) {
        for (int slot : slots) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5), item);
            }
        }
    }
}