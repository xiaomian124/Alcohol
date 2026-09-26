package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CustomItemProtectionListener implements Listener {

    private static NamespacedKey[] PROTECTED_KEYS;

    public static void init() {
        PROTECTED_KEYS = new NamespacedKey[]{
                ItemKeys.DRY_WHEAT,
                ItemKeys.BARLEY,
                ItemKeys.DRIED_BARLEY,
                ItemKeys.OATS,
                ItemKeys.DRIED_OATS,
                ItemKeys.CORN_SEED,
                ItemKeys.CORN,
                ItemKeys.CORN_KERNELS,
                ItemKeys.DRIED_CORN,
                ItemKeys.WILD_NETTLE,
                ItemKeys.HOPS,
                ItemKeys.YEAST,
                ItemKeys.HARVEST_SHEARS,
                ItemKeys.BEER
        };
    }

    private static boolean isProtected(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        for (NamespacedKey key : PROTECTED_KEYS) {
            if (key != null && pdc.has(key, PersistentDataType.BYTE)) return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlace(BlockPlaceEvent event) {
        if (isProtected(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        ItemStack hand = event.getItem();
        if (!isProtected(hand)) return;

        Action action = event.getAction();
        Block clicked = event.getClickedBlock();

        if (ItemFactory.isCustom(hand, ItemKeys.CORN_SEED)
                && action == Action.RIGHT_CLICK_BLOCK
                && clicked != null
                && clicked.getType() == Material.FARMLAND) {
            return;
        }

        if (ItemFactory.isBeer(hand)) return;
        if (ItemFactory.getGrapeSeedType(hand) != null) return;
        if (ItemFactory.getGrapeType(hand) != null) return;
        if (ItemFactory.isCherry(hand)) return;
        if (ItemFactory.isRottenCherry(hand)) return;
        if (ItemFactory.isCustom(hand, ItemKeys.CORN)) return;

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (isProtected(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (isProtected(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDispense(BlockDispenseEvent event) {
        if (isProtected(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack[] matrix = event.getInventory().getMatrix();
        if (!containsProtected(matrix)) return;

        ItemStack result = event.getInventory().getResult();
        if (!isProtected(result)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack[] matrix = event.getInventory().getMatrix();
        if (!containsProtected(matrix)) return;

        ItemStack result = event.getRecipe().getResult();
        if (!isProtected(result)) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                player.updateInventory();
            }
        }
    }

    private static boolean containsProtected(ItemStack[] matrix) {
        for (ItemStack item : matrix) {
            if (isProtected(item)) return true;
        }
        return false;
    }
}