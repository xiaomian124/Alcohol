package com.xiaomian124.alcohol.music;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

public class MusicPlayerListener implements Listener {

    private final AlcoholPlugin plugin;

    public MusicPlayerListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.JUKEBOX) return;

        BlockState state = block.getState();
        if (!(state instanceof TileState tileState)) return;
        if (!tileState.getPersistentDataContainer()
                .has(ItemKeys.MUSIC_PLAYER, PersistentDataType.BYTE)) return;

        event.setCancelled(true);

        MusicPlayerGUI gui = new MusicPlayerGUI(plugin, block.getLocation());
        event.getPlayer().openInventory(gui.getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MusicPlayerGUI gui)) return;
        event.setCancelled(true);

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) return;

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= MusicPlayerGUI.SIZE) return;

        MusicPlayer mp = MusicPlayerManager.getOrCreate(gui.getLocation());

        switch (slot) {
            case MusicPlayerGUI.DISC_SLOT -> {
                MusicPlayerManager.toggleLoop(mp);
                gui.showingLoopMode = true;
                gui.refresh();

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!gui.getInventory().getViewers().isEmpty()) {
                        gui.showingLoopMode = false;
                        gui.refresh();
                    }
                }, 40L);
            }
            case MusicPlayerGUI.PREV_SLOT -> {
                MusicPlayerManager.prev(mp);
                gui.refresh();
            }
            case MusicPlayerGUI.PLAY_SLOT -> {
                if (mp.playing) {
                    MusicPlayerManager.pause(mp);
                } else {
                    MusicPlayerManager.resume(mp);
                }
                gui.refresh();
            }
            case MusicPlayerGUI.NEXT_SLOT -> {
                MusicPlayerManager.next(mp);
                gui.refresh();
            }
            default -> {}
        }
    }
}