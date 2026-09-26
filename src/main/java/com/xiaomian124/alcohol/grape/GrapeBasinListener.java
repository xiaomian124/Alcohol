package com.xiaomian124.alcohol.grape;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.BeerQuality;
import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrapeBasinListener implements Listener {

    private final AlcoholPlugin plugin;
    private final Map<UUID, Boolean> lastOnGround = new HashMap<>();

    public GrapeBasinListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!ItemFactory.isCustom(item, ItemKeys.GRAPE_BASIN)) return;

        Block block = event.getBlockPlaced();
        if (block.getType() != Material.COMPOSTER) return;

        GrapeBasinManager.register(block.getLocation());
        GrapeBasinManager.setLevel(block, 0);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!GrapeBasinManager.isGrapeBasin(block.getLocation())) return;

        GrapeBasinManager.unregister(block.getLocation());

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(
                block.getLocation().add(0.5, 0.5, 0.5),
                ItemFactory.createGrapeBasin());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.COMPOSTER) return;
        if (!GrapeBasinManager.isGrapeBasin(block.getLocation())) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        GrapeBasin basin = GrapeBasinManager.get(block.getLocation());
        if (basin == null) return;

        if (player.isSneaking()) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand.getType() == Material.GLASS_BOTTLE) {
                takeJuice(player, block, basin, hand);
            }
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        GrapeType mainType = ItemFactory.getGrapeType(mainHand);
        GrapeType offType = ItemFactory.getGrapeType(offHand);

        GrapeType useType = null;
        boolean useOffHand = false;

        if (mainType != null) {
            useType = mainType;
        } else if (offType != null) {
            useType = offType;
            useOffHand = true;
        }

        if (useType == null) return;

        tryAddGrape(player, block, basin, useType, useOffHand);
    }

    private void tryAddGrape(Player player, Block block, GrapeBasin basin,
                             GrapeType type, boolean useOffHand) {
        int level = GrapeBasinManager.getLevel(block);
        if (level >= GrapeBasinManager.MAX_LEVEL) return;

        if (basin.grapeType != null && basin.grapeType != type) return;

        if (basin.grapeType == null) {
            basin.grapeType = type;
        }

        ItemStack hand = useOffHand
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();
        if (player.getGameMode() != GameMode.CREATIVE) {
            hand.setAmount(hand.getAmount() - 1);
        }
        player.updateInventory();

        GrapeBasinManager.setLevel(block, level + 1);
        player.playSound(block.getLocation(),
                Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 1.0f, 1.0f);
        block.getWorld().spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER,
                block.getLocation().add(0.5, 1.1, 0.5),
                5, 0.3, 0.2, 0.3, 0);
    }

    private void takeJuice(Player player, Block block, GrapeBasin basin, ItemStack bottle) {
        if (basin.juiceCount <= 0) {
            player.sendActionBar(
                    Component.text("还没有可以取的葡萄汁")
                            .color(NamedTextColor.RED));
            return;
        }

        GrapeType savedType = basin.grapeType;
        if (savedType == null) {
            player.sendActionBar(
                    Component.text("还没有可以取的葡萄汁")
                            .color(NamedTextColor.RED));
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            bottle.setAmount(bottle.getAmount() - 1);
        }

        ItemStack juice = ItemFactory.createBeer(
                savedType.getBeerType(), BeerQuality.HIGH);
        player.getInventory().addItem(juice).forEach((i, drop) ->
                player.getWorld().dropItemNaturally(player.getLocation(), drop));
        player.updateInventory();

        basin.juiceCount--;

        int level = GrapeBasinManager.getLevel(block);
        if (basin.juiceCount <= 0) {
            GrapeBasinManager.setLevel(block, 0);
            basin.grapeType = null;
            basin.jumpCount = 0;
        } else {
            GrapeBasinManager.setLevel(block, level / 2);
        }

        player.playSound(block.getLocation(),
                Sound.ITEM_BOTTLE_FILL, 1.0f, 1.0f);

        String message = "获得了" + savedType.getGrapeName() + "汁";
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendActionBar(
                        Component.text(message).color(NamedTextColor.YELLOW));
            }
        }, 1L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getY() == event.getTo().getY()) return;

        Location loc = player.getLocation();
        Block below = loc.clone().subtract(0, 1, 0).getBlock();

        if (below.getType() != Material.COMPOSTER) return;
        if (!GrapeBasinManager.isGrapeBasin(below.getLocation())) return;

        boolean onGround = player.isOnGround();
        Boolean prev = lastOnGround.get(player.getUniqueId());
        lastOnGround.put(player.getUniqueId(), onGround);

        if (prev == null) return;

        if (!prev && onGround) {
            player.playSound(loc, Sound.BLOCK_MUD_PLACE, 0.8f, 1.0f);
        }

        if (prev && !onGround) {
            GrapeBasin basin = GrapeBasinManager.get(below.getLocation());
            if (basin == null) return;

            int level = GrapeBasinManager.getLevel(below);
            if (level < GrapeBasinManager.MAX_LEVEL) return;
            if (level >= GrapeBasinManager.TOP_LEVEL) return;

            basin.jumpCount++;
            player.playSound(below.getLocation(),
                    Sound.BLOCK_COMPOSTER_FILL, 1.0f, 1.0f);

            if (basin.jumpCount >= GrapeBasinManager.NEEDED_JUMPS) {
                GrapeBasinManager.setLevel(below, GrapeBasinManager.TOP_LEVEL);
                basin.juiceCount = GrapeBasinManager.MAX_JUICE;
                basin.jumpCount = 0;

                player.playSound(below.getLocation(),
                        Sound.BLOCK_COMPOSTER_READY, 1.0f, 1.0f);
                below.getWorld().spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER,
                        below.getLocation().add(0.5, 1.3, 0.5),
                        15, 0.4, 0.3, 0.4, 0);
            }
        }
    }
}