package com.xiaomian124.alcohol.cooking;

import com.xiaomian124.alcohol.AlcoholPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import com.xiaomian124.alcohol.recipe.MachineRecipeGUI;
import com.xiaomian124.alcohol.recipe.MachineType;
import org.bukkit.entity.Player;

public class CookingPotListener implements Listener {

    private final AlcoholPlugin plugin;

    public CookingPotListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.CAULDRON
                && block.getType() != Material.WATER_CAULDRON) return;
        if (!CookingPotManager.isCookingPot(block.getLocation())) return;

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (player.isSneaking()) {
            event.setCancelled(true);
            int water = CookingPotManager.getWater(block.getLocation());

            if (hand.getType() == Material.WATER_BUCKET) {
                if (water >= 3) {
                    player.sendActionBar(Component.text("烹饪锅储水量已满")
                            .color(NamedTextColor.RED));
                    return;
                }
                CookingPotManager.setWater(block.getLocation(), 3);
                CookingPotManager.updateCauldronBlock(block, 3);
                hand.setType(Material.BUCKET);
                player.sendActionBar(Component.text("烹饪锅储水量 3/3")
                        .color(NamedTextColor.YELLOW));
                return;
            }

            if (isWaterBottle(hand)) {
                if (water >= 3) {
                    player.sendActionBar(Component.text("烹饪锅储水量已满")
                            .color(NamedTextColor.RED));
                    return;
                }
                int newWater = water + 1;
                CookingPotManager.setWater(block.getLocation(), newWater);
                CookingPotManager.updateCauldronBlock(block, newWater);
                hand.setType(Material.GLASS_BOTTLE);
                player.sendActionBar(Component.text("烹饪锅储水量 " + newWater + "/3")
                        .color(NamedTextColor.YELLOW));
                player.playSound(player.getLocation(),
                        Sound.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON,
                        1.0f, 1.0f);
                return;
            }
            return;
        }

        event.setCancelled(true);
        CookingPotGUI gui = CookingPotManager.getOrCreate(plugin, block);
        player.openInventory(gui.getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof CookingPotGUI gui)) return;

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            int slot = event.getRawSlot();
            if (slot == CookingPotGUI.OUTPUT_SLOT || gui.isInputSlot(slot)) {
                if (slot == CookingPotGUI.OUTPUT_SLOT) {
                    Bukkit.getScheduler().runTaskLater(plugin,
                            gui::syncFromInventory, 1L);
                }
                return;
            }
            event.setCancelled(true);
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= CookingPotGUI.SIZE) return;

        if (slot == CookingPotGUI.TIMER_SLOT) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player p) {
                MachineRecipeGUI.open(p, MachineType.COOKING_POT, MachineRecipeGUI.Mode.RECIPES);
            }
            return;
        }

        if (gui.isInputSlot(slot)) return;

        if (slot == CookingPotGUI.OUTPUT_SLOT) {
            ItemStack cursor = event.getCursor();
            if (cursor != null && cursor.getType() != Material.AIR) {
                event.setCancelled(true);
                return;
            }
            Bukkit.getScheduler().runTaskLater(plugin,
                    gui::syncFromInventory, 1L);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof CookingPotGUI gui)) return;
        ItemStack[] items = new ItemStack[CookingPotGUI.SIZE];
        for (int i = 0; i < CookingPotGUI.SIZE; i++) {
            items[i] = event.getInventory().getItem(i);
        }
        CookingPotManager.saveContents(gui.getBlock().getLocation(), items);
        gui.syncFromInventory();
    }

    private static boolean isWaterBottle(ItemStack item) {
        if (item == null || item.getType() != Material.POTION) return false;
        if (!(item.getItemMeta() instanceof PotionMeta pm)) return false;
        return pm.getBasePotionType() == PotionType.WATER;
    }
}