package com.xiaomian124.alcohol.give;

import com.xiaomian124.alcohol.BeerQuality;
import com.xiaomian124.alcohol.BeerType;
import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.grape.GrapeType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GiveRegistry {

    private GiveRegistry() {}

    private static final List<GiveableItem> ITEMS = new ArrayList<>();
    private static final Map<String, GiveableItem> BY_ID = new HashMap<>();

    public static void init() {
        ITEMS.clear();
        BY_ID.clear();

        register("cooking_pot",   "item.cooking_pot.name",   ItemFactory::createCookingPot);
        register("brewing_box",   "item.brewing_box.name",   ItemFactory::createBrewingBox);
        register("apple_press",   "item.apple_press.name",   ItemFactory::createApplePress);
        register("aging_barrel",  "item.aging_barrel.name",  ItemFactory::createAgingBarrel);
        register("grape_basin",   "item.grape_basin.name",   ItemFactory::createGrapeBasin);
        register("music_player",  "item.music_player.name",  ItemFactory::createMusicPlayer);

        register("harvest_shears", "item.harvest_shears.name", ItemFactory::createHarvestShears);

        register("dry_wheat",      "item.dry_wheat.name",      ItemFactory::createDryWheat);
        register("barley",         "item.barley.name",         ItemFactory::createBarley);
        register("dried_barley",   "item.dried_barley.name",   ItemFactory::createDriedBarley);
        register("oats",           "item.oats.name",           ItemFactory::createOats);
        register("dried_oats",     "item.dried_oats.name",     ItemFactory::createDriedOats);
        register("corn_seed",      "item.corn_seed.name",      ItemFactory::createCornSeed);
        register("corn",           "item.corn.name",           ItemFactory::createCorn);
        register("corn_kernels",   "item.corn_kernels.name",   ItemFactory::createCornKernels);
        register("dried_corn",     "item.dried_corn.name",     ItemFactory::createDriedCorn);
        register("wild_nettle",    "item.wild_nettle.name",    ItemFactory::createWildNettle);
        register("hops",           "item.hops.name",           ItemFactory::createHops);
        register("yeast",          "item.yeast.name",          ItemFactory::createYeast);
        register("apple_mash",     "item.apple_mash.name",     ItemFactory::createAppleMash);
        register("apple_juice",    "item.apple_juice.name",    ItemFactory::createAppleJuice);
        register("cherry",         "item.cherry.name",         ItemFactory::createCherry);
        register("rotten_cherry",  "item.rotten_cherry.name",  ItemFactory::createRottenCherry);

        for (GrapeType type : GrapeType.values()) {
            final GrapeType t = type;
            register(t.name().toLowerCase() + "_seed", t.seedNameKey(),
                    p -> ItemFactory.createGrapeSeed(p, t));
            register(t.name().toLowerCase() + "_grape", t.grapeNameKey(),
                    p -> ItemFactory.createGrape(p, t));
        }

        for (BeerType type : BeerType.values()) {
            if (type == BeerType.APPLE_JUICE) continue;

            final BeerType t = type;
            register(t.name().toLowerCase(), t.getNameKey(),
                    p -> ItemFactory.createBeer(p, t, BeerQuality.HIGH));
        }
    }

    private static void register(String id, String langKey,
                                 java.util.function.Function<Player, ItemStack> factory) {
        GiveableItem item = new GiveableItem(id.toLowerCase(), langKey, factory);
        ITEMS.add(item);
        BY_ID.put(item.id, item);
    }

    public static List<GiveableItem> getAll() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static GiveableItem byId(String id) {
        if (id == null) return null;
        return BY_ID.get(id.toLowerCase());
    }

    public static List<String> allIds() {
        List<String> list = new ArrayList<>();
        for (GiveableItem item : ITEMS) list.add(item.id);
        return list;
    }
}
