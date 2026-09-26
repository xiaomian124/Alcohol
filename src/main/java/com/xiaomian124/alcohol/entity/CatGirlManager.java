package com.xiaomian124.alcohol.entity;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.xiaomian124.alcohol.AlcoholPlugin;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CatGirlManager {

    public static final NamespacedKey CATGIRL_KEY =
            new NamespacedKey("alcohol", "catgirl");

    private static final Map<UUID, Mannequin> activeCatGirls = new HashMap<>();

    private static final String CATGIRL_SKIN_VALUE =
            "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y0OGZkZmY2ZGMyZWNiYTU3MGQxNjA5YWJhOGE1ZDNhYjM4NDRlMmJiZWM2YjZlODcyNmE2ZWI5ODc2Njc5In19fQ==";

    public static void spawnCatGirl(Player player) {
        removeCatGirl(player);

        Location spawnLoc = player.getLocation().add(
                player.getLocation().getDirection().multiply(2));
        spawnLoc.setY(player.getLocation().getY());

        Mannequin catGirl = (Mannequin) player.getWorld()
                .spawnEntity(spawnLoc, EntityType.MANNEQUIN);

        catGirl.customName(
                Component.text("CatGirl520").color(NamedTextColor.LIGHT_PURPLE));
        catGirl.setCustomNameVisible(true);
        catGirl.setInvulnerable(true);

        catGirl.getPersistentDataContainer().set(
                CATGIRL_KEY, PersistentDataType.BYTE, (byte) 1);

        applySkin(catGirl);

        Vector dir = player.getLocation().toVector()
                .subtract(catGirl.getLocation().toVector());
        Location lookAt = catGirl.getLocation().setDirection(dir);
        catGirl.teleport(lookAt);

        player.getWorld().spawnParticle(Particle.CRIT,
                spawnLoc.clone().add(0, 1, 0),
                25, 0.4, 0.6, 0.4, 0.1);
        player.getWorld().playSound(spawnLoc,
                Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

        activeCatGirls.put(player.getUniqueId(), catGirl);

        startFollowTask(player, catGirl);
        startDialog(player);
    }

    private static void applySkin(Mannequin mannequin) {
        try {
            PlayerProfile profile = Bukkit.createProfile(
                    UUID.randomUUID(), "CatGirl520");
            profile.setProperty(new ProfileProperty(
                    "textures", CATGIRL_SKIN_VALUE));
            ResolvableProfile resolvable =
                    ResolvableProfile.resolvableProfile(profile);
            mannequin.setProfile(resolvable);
        } catch (Exception e) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("猫娘皮肤设置失败: " + e.getMessage());
        }
    }

    private static void startFollowTask(Player player, Mannequin catGirl) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !catGirl.isValid()) {
                    this.cancel();
                    activeCatGirls.remove(player.getUniqueId());
                    return;
                }

                Location catLoc = catGirl.getLocation();
                Location playerLoc = player.getLocation();

                if (!catLoc.getWorld().equals(playerLoc.getWorld())) {
                    catGirl.teleport(playerLoc);
                    return;
                }

                double dist = catLoc.distance(playerLoc);
                Location newLoc = catLoc.clone();

                if (dist > 20) {
                    newLoc = playerLoc.clone().add(
                            playerLoc.getDirection().multiply(-2));
                    newLoc.setY(playerLoc.getY());
                } else if (dist > 2.5) {
                    Vector dir = playerLoc.toVector()
                            .subtract(catLoc.toVector()).setY(0).normalize();
                    newLoc = catLoc.clone().add(dir.multiply(0.08));
                    newLoc.setY(playerLoc.getY());
                } else {
                    return;
                }

                Vector lookDir = playerLoc.toVector().subtract(newLoc.toVector());
                newLoc.setDirection(lookDir);
                catGirl.teleport(newLoc);

                if (System.currentTimeMillis() % 1000 < 50) {
                    player.getWorld().spawnParticle(Particle.HEART,
                            newLoc.clone().add(0, 2.2, 0),
                            1, 0.3, 0.3, 0.3, 0);
                }
            }
        }.runTaskTimer(AlcoholPlugin.getInstance(), 0L, 1L);
    }

    private static void startDialog(Player player) {
        sendChat(player, "你好呀！" + player.getName());

        Bukkit.getScheduler().runTaskLater(AlcoholPlugin.getInstance(), () -> {
            if (player.isOnline()) {
                sendChat(player, "我调的这杯酒好喝吗❤，好喝我下次再给你调一杯");
            }
        }, 60L);

        Bukkit.getScheduler().runTaskLater(AlcoholPlugin.getInstance(), () -> {
            if (player.isOnline()) {
                sendChat(player, "下次再见咯！拜拜！");
            }
        }, 440L);
    }

    private static void sendChat(Player player, String message) {
        player.sendMessage(Component.text("<CatGirl520> ")
                .color(NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(message).color(NamedTextColor.WHITE)));
        player.playSound(player.getLocation(),
                Sound.ENTITY_CAT_ROYAL_AMBIENT, 1.0f, 1.0f);
    }

    public static void removeCatGirl(Player player) {
        Mannequin catGirl = activeCatGirls.remove(player.getUniqueId());
        if (catGirl != null && catGirl.isValid()) {
            Location loc = catGirl.getLocation();
            catGirl.getWorld().spawnParticle(Particle.POOF,
                    loc.clone().add(0, 1, 0),
                    20, 0.4, 0.6, 0.4, 0.05);
            catGirl.getWorld().playSound(loc,
                    Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

            catGirl.remove();
        }
    }
}