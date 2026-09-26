package com.xiaomian124.alcohol.apple;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemKeys;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
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
import org.bukkit.persistence.PersistentDataType;
import com.xiaomian124.alcohol.recipe.MachineRecipeGUI;
import com.xiaomian124.alcohol.recipe.MachineType;
import org.bukkit.entity.Player;

public class ApplePressListener implements Listener {

    private final AlcoholPlugin plugin;

    public ApplePressListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.LOOM) return;
        if (!ApplePressManager.isApplePress(block.getLocation())) return;

        Player player = event.getPlayer();
        event.setCancelled(true);

        ApplePressGUI gui = ApplePressManager.getOrCreate(plugin, block);
        player.openInventory(gui.getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ApplePressGUI gui)) return;

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            int slot = event.getRawSlot();
            if (!(slot >= 0 && slot < ApplePressGUI.SIZE
                    && gui.isOutputSlot(slot))) {
                event.setCancelled(true);
            }
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= ApplePressGUI.SIZE) return;

        if (gui.isHeadSlot(slot)) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player p) {
                MachineRecipeGUI.open(p, MachineType.APPLE_PRESS, MachineRecipeGUI.Mode.RECIPES);
            }
            return;
        }

        if (gui.isInputSlot(slot) || gui.isOutputSlot(slot)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ApplePressGUI gui)) return;
        ApplePressManager.saveContents(
                gui.getBlock().getLocation(),
                gui.getContents(),
                gui.isMashActive(), gui.getMashRemaining(),
                gui.isJuiceActive(), gui.getJuiceRemaining());
    }
}