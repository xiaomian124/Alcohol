package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.BeerType;
import com.xiaomian124.alcohol.SpecialEffectManager;
import com.xiaomian124.alcohol.entity.CatGirlManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

public class SpecialEffectListener implements Listener {

    private final AlcoholPlugin plugin;

    public SpecialEffectListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (SpecialEffectManager.hasEffect(player, BeerType.AEGIS_WHITE_WINE)) {
                switch (event.getCause()) {
                    case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, PROJECTILE,
                         ENTITY_EXPLOSION, BLOCK_EXPLOSION, MAGIC -> {
                        event.setCancelled(true);
                        return;
                    }
                    default -> {}
                }
            }
        }

        if (event.getEntity() instanceof Cat cat) {
            if (cat.getPersistentDataContainer()
                    .has(SpecialEffectManager.ELDER_CAT_KEY,
                            PersistentDataType.BYTE)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getEntity() instanceof Mannequin mannequin) {
            if (mannequin.getPersistentDataContainer()
                    .has(CatGirlManager.CATGIRL_KEY,
                            PersistentDataType.BYTE)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (event.getFrom().getY() == event.getTo().getY()) return;

        boolean jumped = event.getTo().getY() > event.getFrom().getY()
                && Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0.3;
        if (jumped) {
            SpecialEffectManager.onPlayerJump(p);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            SpecialEffectManager.restoreEntitiesFor(p);
        }, 40L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        SpecialEffectManager.removeAttachedOnQuit(event.getPlayer());
    }
}