package com.xiaomian124.alcohol.aging;

import com.xiaomian124.alcohol.AlcoholPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import com.xiaomian124.alcohol.recipe.MachineRecipeGUI;
import com.xiaomian124.alcohol.recipe.MachineType;
import org.bukkit.entity.Player;

public class AgingBarrelListener implements Listener {

    private final AlcoholPlugin plugin;

    public AgingBarrelListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.BARREL) return;
        if (!AgingBarrelManager.isAgingBarrel(block.getLocation())) return;

        Player player = event.getPlayer();
        event.setCancelled(true);

        AgingBarrelGUI gui = AgingBarrelManager.getOrCreate(plugin, block);
        player.openInventory(gui.getInventory());
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.BARREL) return;
        if (!AgingBarrelManager.isAgingBarrel(block.getLocation())) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        event.setCancelled(true);

        AgingBarrelGUI gui = AgingBarrelManager.getOrCreate(plugin, block);

        if (gui.getStorageType() == null && gui.getStoragePercent() <= 0) {
            return;
        }

        gui.setStorageState(null, 0);

        player.sendActionBar(
                Component.text("已倒掉所有的葡萄汁，酒量储存清空")
                        .color(NamedTextColor.YELLOW));

        player.playSound(block.getLocation(),
                Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.0f);

        block.getWorld().spawnParticle(org.bukkit.Particle.SPLASH,
                block.getLocation().add(0.5, 0.5, 0.5),
                15, 0.3, 0.3, 0.3, 0.05);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AgingBarrelGUI gui)) return;

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            int slot = event.getRawSlot();
            if (!(slot >= 0 && slot < AgingBarrelGUI.SIZE
                    && gui.isOutputSlot(slot))) {
                event.setCancelled(true);
            }
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= AgingBarrelGUI.SIZE) return;

        if (gui.isHeadSlot(slot)) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player p) {
                MachineRecipeGUI.open(p, MachineType.AGING_BARREL, MachineRecipeGUI.Mode.RECIPES);
            }
            return;
        }

        if (gui.isStorageDisplaySlot(slot)) {
            event.setCancelled(true);
            return;
        }

        if (gui.isInputSlot(slot) || gui.isOutputSlot(slot)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof AgingBarrelGUI gui)) return;
        AgingBarrelManager.saveState(
                gui.getBlock().getLocation(),
                gui.getContents(),
                gui.getStorageType(),
                gui.getStoragePercent(),
                gui.getAgingRemaining());
    }
}