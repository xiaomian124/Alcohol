package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RecipeUnlockListener implements Listener {

    private final AlcoholPlugin plugin;

    public RecipeUnlockListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            player.discoverRecipes(RecipeManager.getAllKeys());
        }, 20L);
    }
}