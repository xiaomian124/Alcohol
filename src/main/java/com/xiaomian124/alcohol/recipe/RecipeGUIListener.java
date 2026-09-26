package com.xiaomian124.alcohol.recipe;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RecipeGUIListener implements Listener {

    @EventHandler
    public void onViewerClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof RecipeViewerGUI gui)) return;
        e.setCancelled(true);

        int slot = e.getRawSlot();
        if (slot < 0 || slot >= RecipeViewerGUI.SIZE) return;

        if (slot == 45 && gui.hasPrev()) { gui.setPage(gui.getPage() - 1); return; }
        if (slot == 53 && gui.hasNext()) { gui.setPage(gui.getPage() + 1); return; }

        MachineType machine = gui.getMachineAtSlot(slot);
        if (machine == null) return;

        Player p = (Player) e.getWhoClicked();

        if (machine == MachineType.MUSIC_PLAYER) {
            if (e.getClick() == ClickType.RIGHT) {
                MachineRecipeGUI.open(p, machine, MachineRecipeGUI.Mode.CRAFTING);
            }
            return;
        }

        if (e.getClick() == ClickType.RIGHT) {
            MachineRecipeGUI.open(p, machine, MachineRecipeGUI.Mode.CRAFTING);
        } else {
            MachineRecipeGUI.open(p, machine, MachineRecipeGUI.Mode.RECIPES);
        }
    }

    @EventHandler
    public void onMachineClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof MachineRecipeGUI gui)) return;
        e.setCancelled(true);

        int slot = e.getRawSlot();
        if (slot == 36) gui.prev();
        else if (slot == 44) gui.next();
    }
}