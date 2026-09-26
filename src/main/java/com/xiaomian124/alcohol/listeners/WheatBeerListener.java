package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.BeerEffects;
import com.xiaomian124.alcohol.BeerQuality;
import com.xiaomian124.alcohol.BeerType;
import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

public class WheatBeerListener implements Listener {

    private final AlcoholPlugin plugin;

    public static final NamespacedKey COOLDOWN_KEY =
            new NamespacedKey("alcohol", "drink_cooldown");

    private static final long COOLDOWN_MS = 60_000L;

    private static final BlockFace[] YAW_FACES = {
            BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST,
            BlockFace.WEST_SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST,
            BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH,
            BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST,
            BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_SOUTH_EAST
    };

    public WheatBeerListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (ItemFactory.isBeer(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        ItemStack hand = event.getItem();
        if (hand == null) return;

        if (!ItemFactory.isBeer(hand)) return;

        Action action = event.getAction();
        BeerType type = ItemFactory.getBeerType(hand);
        if (type == null) return;

        Player player = event.getPlayer();

        if (player.isSneaking() && action == Action.RIGHT_CLICK_BLOCK) {
            Block clicked = event.getClickedBlock();
            if (clicked == null) return;

            event.setCancelled(true);

            if (!type.isPlaceable()) return;
            if (event.getBlockFace() != BlockFace.UP) return;

            Block target = clicked.getRelative(BlockFace.UP);
            if (target.getType() != Material.AIR) return;

            placeBeer(player, slot, target);
            return;
        }

        if (hand.getType() == Material.POTION) {
            return;
        }

        if (hand.getType() == Material.PLAYER_HEAD) {
            if (action == Action.RIGHT_CLICK_AIR
                    || action == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                drinkBeer(player, slot, hand);
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!ItemFactory.isBeer(item)) return;
        if (item.getType() != Material.POTION) return;

        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        long cooldownEnd = player.getPersistentDataContainer()
                .getOrDefault(COOLDOWN_KEY, PersistentDataType.LONG, 0L);

        BeerType type = ItemFactory.getBeerType(item);
        boolean isJuice = type != null && type.isJuice();

        if (!isJuice && now < cooldownEnd) {
            event.setCancelled(true);
            long remainSec = (cooldownEnd - now + 999) / 1000;
            player.sendActionBar(
                    Component.text("你一下子喝不了那么多，再等 "
                                    + remainSec + " 秒吧")
                            .color(NamedTextColor.RED));
            player.playSound(player.getLocation(),
                    Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
            return;
        }

        if (!isJuice && player.getFoodLevel() >= 20) {
            event.setCancelled(true);
            player.sendActionBar(
                    Component.text("你现在喝不下了")
                            .color(NamedTextColor.RED));
            player.playSound(player.getLocation(),
                    Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
            return;
        }

        if (!isJuice && type != null) {
            BeerEffects.apply(player, type);
        }

        if (!isJuice) {
            int newFood = Math.min(20, player.getFoodLevel() + 3);
            player.setFoodLevel(newFood);
            player.setSaturation(Math.min(newFood, player.getSaturation() + 1.5f));
            player.getPersistentDataContainer().set(
                    COOLDOWN_KEY, PersistentDataType.LONG, now + COOLDOWN_MS);
        }

        player.playSound(player.getLocation(),
                Sound.ENTITY_GENERIC_DRINK, 1.0f, 1.0f);
    }

    private void drinkBeer(Player player, EquipmentSlot slot, ItemStack beer) {
        long now = System.currentTimeMillis();
        long cooldownEnd = player.getPersistentDataContainer()
                .getOrDefault(COOLDOWN_KEY, PersistentDataType.LONG, 0L);

        BeerType type = ItemFactory.getBeerType(beer);
        boolean isJuice = type != null && type.isJuice();

        if (!isJuice && now < cooldownEnd) {
            long remainSec = (cooldownEnd - now + 999) / 1000;
            player.sendActionBar(
                    Component.text("你一下子喝不了那么多，再等 "
                                    + remainSec + " 秒吧")
                            .color(NamedTextColor.RED));
            player.playSound(player.getLocation(),
                    Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
            return;
        }

        if (!isJuice && player.getFoodLevel() >= 20) {
            player.sendActionBar(
                    Component.text("你现在喝不下了")
                            .color(NamedTextColor.RED));
            player.playSound(player.getLocation(),
                    Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
            return;
        }

        if (!isJuice && type != null) {
            BeerEffects.apply(player, type);
        }

        if (!isJuice) {
            int newFood = Math.min(20, player.getFoodLevel() + 3);
            player.setFoodLevel(newFood);
            player.setSaturation(Math.min(newFood, player.getSaturation() + 1.5f));
        }

        consumeOne(player, slot, beer);

        if (!isJuice) {
            player.getPersistentDataContainer().set(
                    COOLDOWN_KEY, PersistentDataType.LONG, now + COOLDOWN_MS);
        }

        player.playSound(player.getLocation(),
                Sound.ENTITY_GENERIC_DRINK, 1.0f, 1.0f);
    }

    private void placeBeer(Player player, EquipmentSlot slot, Block target) {
        ItemStack hand = getHandItem(player, slot);
        if (hand == null || hand.getAmount() <= 0) return;

        BeerType type = ItemFactory.getBeerType(hand);
        BeerQuality quality = ItemFactory.getBeerQuality(hand);
        if (type == null || !type.isPlaceable()) return;

        consumeOne(player, slot, hand);

        target.setType(Material.PLAYER_HEAD, true);

        BlockState state = target.getState();
        if (!(state instanceof Skull skull)) return;

        ItemStack tempSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta tempMeta = (SkullMeta) tempSkull.getItemMeta();
        ItemFactory.applyBeerSkin(tempMeta, type);
        skull.setOwnerProfile(tempMeta.getOwnerProfile());

        float yaw = player.getLocation().getYaw() + 180f;
        int index = Math.round(((yaw % 360 + 360) % 360) / 22.5f) % 16;
        skull.setRotation(YAW_FACES[index]);

        var pdc = skull.getPersistentDataContainer();
        pdc.set(ItemKeys.BEER, PersistentDataType.BYTE, (byte) 1);
        pdc.set(ItemKeys.BEER_TYPE, PersistentDataType.STRING, type.name());
        pdc.set(ItemKeys.BEER_QUALITY, PersistentDataType.STRING, quality.name());

        skull.update(true, true);

        player.playSound(target.getLocation(),
                Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
    }

    private static ItemStack getHandItem(Player p, EquipmentSlot slot) {
        return slot == EquipmentSlot.HAND
                ? p.getInventory().getItemInMainHand()
                : p.getInventory().getItemInOffHand();
    }

    private void consumeOne(Player player, EquipmentSlot slot, ItemStack hand) {
        int newAmount = hand.getAmount() - 1;
        ItemStack newHand = null;
        if (newAmount > 0) {
            newHand = hand.clone();
            newHand.setAmount(newAmount);
        }

        if (slot == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(newHand);
        } else {
            player.getInventory().setItemInOffHand(newHand);
        }
        player.updateInventory();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.PLAYER_HEAD) return;

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        var pdc = skull.getPersistentDataContainer();
        if (!pdc.has(ItemKeys.BEER, PersistentDataType.BYTE)) return;

        event.setDropItems(false);

        String typeName = pdc.getOrDefault(ItemKeys.BEER_TYPE,
                PersistentDataType.STRING, BeerType.WHEAT_BEER.name());
        String qualityName = pdc.getOrDefault(ItemKeys.BEER_QUALITY,
                PersistentDataType.STRING, BeerQuality.HIGH.name());

        BeerType type;
        try { type = BeerType.valueOf(typeName); }
        catch (IllegalArgumentException e) { type = BeerType.WHEAT_BEER; }

        BeerQuality quality;
        try { quality = BeerQuality.valueOf(qualityName); }
        catch (IllegalArgumentException e) { quality = BeerQuality.HIGH; }

        block.getWorld().dropItemNaturally(
                block.getLocation().add(0.5, 0.5, 0.5),
                ItemFactory.createBeer(type, quality));
    }
}