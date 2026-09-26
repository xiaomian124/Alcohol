package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class SmeltingListener implements Listener {

    private static final int NEVER_FINISH = Integer.MAX_VALUE / 2;

    @EventHandler
    public void onStartSmelt(FurnaceStartSmeltEvent event) {
        if (event.getBlock().getType() != Material.SMOKER) return;

        ItemStack source = event.getSource();
        if (source == null || source.getType() == Material.AIR) return;

        Material type = source.getType();
        boolean shouldBlock = false;

        if (ItemFactory.isCustom(source, ItemKeys.DRY_WHEAT)
                || ItemFactory.isCustom(source, ItemKeys.DRIED_BARLEY)
                || ItemFactory.isCustom(source, ItemKeys.DRIED_OATS)
                || ItemFactory.isCustom(source, ItemKeys.DRIED_CORN)) {
            shouldBlock = true;
        }

        if (type == Material.BEETROOT_SEEDS
                && !ItemFactory.isCustom(source, ItemKeys.CORN_KERNELS)) {
            shouldBlock = true;
        }

        if (shouldBlock) {
            event.setTotalCookTime(NEVER_FINISH);
        }
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        if (event.getBlock().getType() != Material.SMOKER) return;

        ItemStack source = event.getSource();
        if (source == null || source.getType() == Material.AIR) return;

        Material type = source.getType();

        if (type == Material.BEETROOT_SEEDS) {
            if (ItemFactory.isCustom(source, ItemKeys.CORN_KERNELS)) {
                event.setResult(ItemFactory.createDriedCorn());
            } else {
                event.setCancelled(true);
            }
            return;
        }

        if (type == Material.WHEAT) {
            if (ItemFactory.isCustom(source, ItemKeys.DRY_WHEAT)
                    || ItemFactory.isCustom(source, ItemKeys.DRIED_BARLEY)
                    || ItemFactory.isCustom(source, ItemKeys.DRIED_OATS)) {
                event.setCancelled(true);
                return;
            }

            if (ItemFactory.isCustom(source, ItemKeys.BARLEY)) {
                event.setResult(ItemFactory.createDriedBarley());
                return;
            }

            if (ItemFactory.isCustom(source, ItemKeys.OATS)) {
                event.setResult(ItemFactory.createDriedOats());
            }
        }
    }
}