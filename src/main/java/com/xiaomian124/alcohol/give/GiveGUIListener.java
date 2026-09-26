package com.xiaomian124.alcohol.give;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class GiveGUIListener implements Listener {

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof GiveGUI)) return;
        if (!(event.getPlayer() instanceof Player p)) return;

        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof GiveGUI)) return;
        if (!(event.getPlayer() instanceof Player p)) return;

        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0f, 1.0f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GiveGUI gui)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= GiveGUI.SIZE) return;

        if (slot == 45 && gui.hasPrev()) {
            gui.setPage(gui.getPage() - 1);
            return;
        }
        if (slot == 53 && gui.hasNext()) {
            gui.setPage(gui.getPage() + 1);
            return;
        }

        GiveableItem item = gui.getItemAtSlot(slot);
        if (item == null) return;
        if (!(event.getWhoClicked() instanceof Player p)) return;

        ItemStack give = item.create();
        var leftover = p.getInventory().addItem(give);
        boolean dropped = !leftover.isEmpty();
        if (dropped) {
            leftover.values().forEach(drop ->
                    p.getWorld().dropItemNaturally(p.getLocation(), drop));
        }
        p.updateInventory();

        p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

        if (dropped) {
            p.sendMessage(Component.text("你的背包已满，部分物品已掉落在地上。")
                    .color(NamedTextColor.YELLOW));
        }
    }
}