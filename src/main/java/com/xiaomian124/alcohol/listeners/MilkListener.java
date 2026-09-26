package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.DrinkEffectDisplay;
import com.xiaomian124.alcohol.SpecialEffectManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class MilkListener implements Listener {

    @EventHandler
    public void onDrinkMilk(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.MILK_BUCKET) return;

        Player player = event.getPlayer();

        SpecialEffectManager.clearAll(player);

        DrinkEffectDisplay.clearAll(player);
    }
}