package com.xiaomian124.alcohol.give;

import com.xiaomian124.alcohol.BeerQuality;
import com.xiaomian124.alcohol.BeerType;
import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.grape.GrapeType;
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

        register("cooking_pot",   "烹饪锅",        ItemFactory::createCookingPot);
        register("brewing_box",   "酿造炉",        ItemFactory::createBrewingBox);
        register("apple_press",   "苹果压榨器",    ItemFactory::createApplePress);
        register("aging_barrel",  "陈酿桶",        ItemFactory::createAgingBarrel);
        register("grape_basin",   "葡萄藤盆",      ItemFactory::createGrapeBasin);
        register("music_player",  "吧台音乐播放器", ItemFactory::createMusicPlayer);

        register("harvest_shears", "收割剪刀", ItemFactory::createHarvestShears);

        register("dry_wheat",      "烘干的小麦",     ItemFactory::createDryWheat);
        register("barley",         "大麦",           ItemFactory::createBarley);
        register("dried_barley",   "烘干的大麦",     ItemFactory::createDriedBarley);
        register("oats",           "燕麦",           ItemFactory::createOats);
        register("dried_oats",     "烘干的燕麦",     ItemFactory::createDriedOats);
        register("corn_seed",      "玉米种子",       ItemFactory::createCornSeed);
        register("corn",           "玉米",           ItemFactory::createCorn);
        register("corn_kernels",   "玉米粒",         ItemFactory::createCornKernels);
        register("dried_corn",     "烘干的玉米粒",   ItemFactory::createDriedCorn);
        register("wild_nettle",    "野生荨麻",       ItemFactory::createWildNettle);
        register("hops",           "啤酒花",         ItemFactory::createHops);
        register("yeast",          "酵母",           ItemFactory::createYeast);
        register("apple_mash",     "苹果泥",         ItemFactory::createAppleMash);
        register("apple_juice",    "苹果汁",         ItemFactory::createAppleJuice);
        register("cherry",         "樱桃",           ItemFactory::createCherry);
        register("rotten_cherry",  "腐烂的樱桃",     ItemFactory::createRottenCherry);

        for (GrapeType type : GrapeType.values()) {
            final GrapeType t = type;
            register(t.name().toLowerCase() + "_seed", t.getSeedName(),
                    () -> ItemFactory.createGrapeSeed(t));
            register(t.name().toLowerCase() + "_grape", t.getGrapeName(),
                    () -> ItemFactory.createGrape(t));
        }

        for (BeerType type : BeerType.values()) {
            if (type == BeerType.APPLE_JUICE) continue;

            final BeerType t = type;
            register(t.name().toLowerCase(), t.getDisplayName(),
                    () -> ItemFactory.createBeer(t, BeerQuality.HIGH));
        }
    }

    private static void register(String id, String displayName,
                                 java.util.function.Supplier<ItemStack> factory) {
        GiveableItem item = new GiveableItem(id.toLowerCase(), displayName, factory);
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