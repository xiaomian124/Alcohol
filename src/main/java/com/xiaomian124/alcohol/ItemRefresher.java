package com.xiaomian124.alcohol;

import com.xiaomian124.alcohol.grape.GrapeType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class ItemRefresher {

    private ItemRefresher() {}

    private static final Map<NamespacedKey, Supplier<ItemStack>> REGISTRY = new HashMap<>();
    private static final Set<NamespacedKey> MACHINES = new HashSet<>();
    private static boolean ready = false;

    private static synchronized void ensureReady() {
        if (ready) return;
        ready = true;

        put(ItemKeys.DRY_WHEAT,       ItemFactory::createDryWheat);
        put(ItemKeys.BARLEY,          ItemFactory::createBarley);
        put(ItemKeys.DRIED_BARLEY,    ItemFactory::createDriedBarley);
        put(ItemKeys.OATS,            ItemFactory::createOats);
        put(ItemKeys.DRIED_OATS,      ItemFactory::createDriedOats);
        put(ItemKeys.CORN_SEED,       ItemFactory::createCornSeed);
        put(ItemKeys.CORN,            ItemFactory::createCorn);
        put(ItemKeys.CORN_KERNELS,    ItemFactory::createCornKernels);
        put(ItemKeys.DRIED_CORN,      ItemFactory::createDriedCorn);
        put(ItemKeys.WILD_NETTLE,     ItemFactory::createWildNettle);
        put(ItemKeys.HOPS,            ItemFactory::createHops);
        put(ItemKeys.YEAST,           ItemFactory::createYeast);
        put(ItemKeys.APPLE_MASH,      ItemFactory::createAppleMash);
        put(ItemKeys.CHERRY,          ItemFactory::createCherry);
        put(ItemKeys.ROTTEN_CHERRY,   ItemFactory::createRottenCherry);

        putMachine(ItemKeys.COOKING_POT,   ItemFactory::createCookingPot);
        putMachine(ItemKeys.BREWING_BOX,   ItemFactory::createBrewingBox);
        putMachine(ItemKeys.APPLE_PRESS,   ItemFactory::createApplePress);
        putMachine(ItemKeys.AGING_BARREL,  ItemFactory::createAgingBarrel);
        putMachine(ItemKeys.GRAPE_BASIN,   ItemFactory::createGrapeBasin);
        putMachine(ItemKeys.MUSIC_PLAYER,  ItemFactory::createMusicPlayer);
        putMachine(ItemKeys.HARVEST_SHEARS, ItemFactory::createHarvestShears);

        for (GrapeType t : GrapeType.values()) {
            NamespacedKey seedKey = t.getSeedKey();
            if (seedKey != null) REGISTRY.put(seedKey, () -> ItemFactory.createGrapeSeed(t));
            NamespacedKey grapeKey = t.getGrapeKey();
            if (grapeKey != null) REGISTRY.put(grapeKey, () -> ItemFactory.createGrape(t));
        }
    }

    private static void put(NamespacedKey key, Supplier<ItemStack> factory) {
        if (key != null) REGISTRY.put(key, factory);
    }

    private static void putMachine(NamespacedKey key, Supplier<ItemStack> factory) {
        put(key, factory);
        if (key != null) MACHINES.add(key);
    }

    public static ItemStack refresh(ItemStack old) {
        if (old == null || old.getAmount() <= 0 || !old.hasItemMeta()) return null;
        ensureReady();

        Set<NamespacedKey> markers = ItemFactory.markersOf(old);
        if (markers.isEmpty()) return null;

        for (NamespacedKey key : markers) {
            Supplier<ItemStack> factory = REGISTRY.get(key);
            if (factory == null) continue;

            ItemStack fresh;
            try {
                fresh = factory.get();
            } catch (Exception e) {
                return null;
            }
            if (fresh == null || fresh.getType() != old.getType()) return null;

            fresh.setAmount(old.getAmount());
            return fresh;
        }
        return null;
    }

    public static boolean isRefreshable(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ensureReady();
        for (NamespacedKey key : ItemFactory.markersOf(item)) {
            if (REGISTRY.containsKey(key)) return true;
        }
        return false;
    }

    public static boolean isMachine(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ensureReady();
        for (NamespacedKey key : ItemFactory.markersOf(item)) {
            if (MACHINES.contains(key)) return true;
        }
        return false;
    }

    public static int refreshContents(ItemStack[] contents, boolean skipMachines) {
        if (contents == null) return 0;
        int count = 0;
        for (int i = 0; i < contents.length; i++) {
            ItemStack old = contents[i];
            if (old == null) continue;
            if (skipMachines && isMachine(old)) continue;
            ItemStack fresh = refresh(old);
            if (fresh == null) continue;
            contents[i] = fresh;
            count++;
        }
        return count;
    }

    public static int refreshPlayer(Player player) {
        int count = 0;
        count += refreshContents(player.getInventory().getContents(), false);
        count += refreshContents(player.getEnderChest().getContents(), false);
        player.updateInventory();
        return count;
    }
}
