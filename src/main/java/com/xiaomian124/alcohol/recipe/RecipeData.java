package com.xiaomian124.alcohol.recipe;

import com.xiaomian124.alcohol.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RecipeData {

    private RecipeData() {}

    public static List<MachineRecipe> getRecipes(MachineType type) {
        return switch (type) {
            case COOKING_POT   -> cookingPotRecipes();
            case BREWING_BOX   -> brewingBoxRecipes();
            case APPLE_PRESS   -> applePressRecipes();
            case AGING_BARREL  -> agingBarrelRecipes();
            case GRAPE_BASIN   -> grapeBasinRecipes();
            case MUSIC_PLAYER  -> new ArrayList<>();
        };
    }

    public static Map<Integer, ItemStack> getCraftingRecipe(MachineType type) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        switch (type) {
            case COOKING_POT -> {
                m.put(3, new ItemStack(Material.IRON_INGOT));
                m.put(4, new ItemStack(Material.WOODEN_SHOVEL));
                m.put(5, new ItemStack(Material.IRON_INGOT));
                m.put(6, new ItemStack(Material.IRON_INGOT));
                m.put(7, new ItemStack(Material.IRON_INGOT));
                m.put(8, new ItemStack(Material.IRON_INGOT));
            }
            case BREWING_BOX -> {
                m.put(3, new ItemStack(Material.IRON_INGOT));
                m.put(5, new ItemStack(Material.IRON_INGOT));
                m.put(6, new ItemStack(Material.IRON_INGOT));
                m.put(7, new ItemStack(Material.SMOKER));
                m.put(8, new ItemStack(Material.IRON_INGOT));
            }
            case APPLE_PRESS -> {
                m.put(3, new ItemStack(Material.IRON_INGOT));
                m.put(4, new ItemStack(Material.IRON_INGOT));
                m.put(5, new ItemStack(Material.IRON_INGOT));
                m.put(6, new ItemStack(Material.IRON_INGOT));
                m.put(7, new ItemStack(Material.LOOM));
                m.put(8, new ItemStack(Material.IRON_INGOT));
            }
            case AGING_BARREL -> {
                m.put(3, new ItemStack(Material.IRON_INGOT));
                m.put(4, new ItemStack(Material.BARREL));
                m.put(5, new ItemStack(Material.IRON_INGOT));
                m.put(6, new ItemStack(Material.IRON_INGOT));
                m.put(7, new ItemStack(Material.IRON_INGOT));
                m.put(8, new ItemStack(Material.IRON_INGOT));
            }
            case GRAPE_BASIN -> {
                m.put(3, new ItemStack(Material.IRON_INGOT));
                m.put(4, new ItemStack(Material.COMPOSTER));
                m.put(5, new ItemStack(Material.IRON_INGOT));
                m.put(6, new ItemStack(Material.IRON_INGOT));
                m.put(7, new ItemStack(Material.IRON_INGOT));
                m.put(8, new ItemStack(Material.IRON_INGOT));
            }
            case MUSIC_PLAYER -> {
                m.put(3, new ItemStack(Material.IRON_INGOT));
                m.put(4, new ItemStack(Material.JUKEBOX));
                m.put(5, new ItemStack(Material.IRON_INGOT));
                m.put(6, new ItemStack(Material.IRON_INGOT));
                m.put(7, new ItemStack(Material.IRON_INGOT));
                m.put(8, new ItemStack(Material.IRON_INGOT));
            }
        }
        return m;
    }

    private static List<MachineRecipe> cookingPotRecipes() {
        List<MachineRecipe> list = new ArrayList<>();
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(10, new ItemStack(Material.SUGAR));
        m.put(11, new ItemStack(Material.WATER_BUCKET));
        m.put(12, new ItemStack(Material.WHEAT));
        m.put(16, ItemFactory.createYeast());
        list.add(new MachineRecipe("酵母", m));
        return list;
    }

    private static List<MachineRecipe> brewingBoxRecipes() {
        List<MachineRecipe> list = new ArrayList<>();

        list.add(beerRecipe("小麦啤酒",
                ItemFactory.createDryWheat(), ItemFactory.createHops(),
                BeerType.WHEAT_BEER));
        list.add(beerRecipe("大麦啤酒",
                ItemFactory.createDriedBarley(), ItemFactory.createHops(),
                BeerType.BARLEY_BEER));
        list.add(beerRecipe("酒花啤酒",
                ItemFactory.createHops(), ItemFactory.createHops(),
                BeerType.HOPS_BEER));
        list.add(beerRecipe("荨麻啤酒",
                ItemFactory.createWildNettle(), ItemFactory.createHops(),
                BeerType.NETTLE_BEER));
        list.add(beerRecipe("燕麦啤酒",
                ItemFactory.createDriedOats(), ItemFactory.createHops(),
                BeerType.OATS_BEER));
        list.add(beerRecipe("可可啤酒",
                new ItemStack(Material.COCOA_BEANS), ItemFactory.createHops(),
                BeerType.COCOA_BEER));

        list.add(whiskyRecipe("Belgravia威士忌",
                ItemFactory.createDriedBarley(), ItemFactory.createDriedBarley(),
                BeerType.BELGRAVIA));
        list.add(whiskyRecipe("Islay威士忌",
                ItemFactory.createDryWheat(), ItemFactory.createDriedBarley(),
                BeerType.ISLAY));
        list.add(whiskyRecipe("Bourbon威士忌",
                ItemFactory.createDriedCorn(), ItemFactory.createDriedCorn(),
                BeerType.BOURBON));
        list.add(whiskyRecipe("Macallan威士忌",
                ItemFactory.createDryWheat(), ItemFactory.createDryWheat(),
                BeerType.MACALLAN));
        list.add(whiskyRecipe("Glenfiddich威士忌",
                ItemFactory.createDryWheat(), ItemFactory.createDriedCorn(),
                BeerType.GLENFIDDICH));
        list.add(whiskyRecipe("Bain's威士忌",
                ItemFactory.createDriedOats(), ItemFactory.createDriedCorn(),
                BeerType.BAINS));
        list.add(whiskyRecipe("Suntory威士忌",
                ItemFactory.createDryWheat(), ItemFactory.createDriedOats(),
                BeerType.SUNTORY));
        list.add(whiskyRecipe("Buffalo Trace威士忌",
                ItemFactory.createDriedOats(), ItemFactory.createDriedOats(),
                BeerType.BUFFALO_TRACE));
        list.add(whiskyRecipe("Jim Beam威士忌",
                ItemFactory.createDriedBarley(), ItemFactory.createDriedOats(),
                BeerType.JIM_BEAM));

        return list;
    }

    private static MachineRecipe beerRecipe(String name, ItemStack a, ItemStack b, BeerType out) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(10, a);
        m.put(11, b);
        m.put(12, ItemFactory.createYeast());
        m.put(16, ItemFactory.createBeer(out, BeerQuality.HIGH));
        m.put(29, new ItemStack(Material.WATER_BUCKET));
        m.put(32, new ItemStack(Material.COAL, 3));
        return new MachineRecipe(name, m);
    }

    private static MachineRecipe whiskyRecipe(String name, ItemStack a, ItemStack b, BeerType out) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(10, a);
        m.put(11, b);
        m.put(12, ItemFactory.createYeast());
        m.put(16, ItemFactory.createBeer(out, BeerQuality.HIGH));
        m.put(29, new ItemStack(Material.WATER_BUCKET));
        m.put(32, new ItemStack(Material.COAL, 3));
        return new MachineRecipe(name, m);
    }

    private static List<MachineRecipe> applePressRecipes() {
        List<MachineRecipe> list = new ArrayList<>();

        Map<Integer, ItemStack> m1 = new LinkedHashMap<>();
        m1.put(19, new ItemStack(Material.APPLE));
        m1.put(23, ItemFactory.createAppleMash());
        list.add(new MachineRecipe("苹果泥", m1));

        Map<Integer, ItemStack> m2 = new LinkedHashMap<>();
        m2.put(23, ItemFactory.createAppleMash());
        m2.put(34, new ItemStack(Material.GLASS_BOTTLE));
        m2.put(16, ItemFactory.createAppleJuice());
        list.add(new MachineRecipe("苹果汁", m2));

        return list;
    }

    private static List<MachineRecipe> agingBarrelRecipes() {
        List<MachineRecipe> list = new ArrayList<>();

        list.add(agingRecipe("蜂蜜酒", BeerType.APPLE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), new ItemStack(Material.SUGAR), null,
                BeerType.HONEY_WINE));
        list.add(agingRecipe("苹果风味酒", BeerType.APPLE_JUICE,
                new ItemStack(Material.SUGAR), null, null,
                BeerType.APPLE_FLAVOR_WINE));
        list.add(agingRecipe("苹果酒", BeerType.APPLE_JUICE,
                ItemFactory.createAppleJuice(), null, null,
                BeerType.APPLE_WINE));
        list.add(agingRecipe("活力之酒", BeerType.WHITE_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), new ItemStack(Material.GLOWSTONE_DUST), null,
                BeerType.VITALITY_WINE));
        list.add(agingRecipe("阳光葡萄酒", BeerType.WHITE_GRAPE_JUICE,
                new ItemStack(Material.GLOW_BERRIES), null, null,
                BeerType.SUNSHINE_WINE));
        list.add(agingRecipe("世纪白葡萄酒", BeerType.WHITE_GRAPE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), new ItemStack(Material.SWEET_BERRIES), null,
                BeerType.CENTURY_WHITE_WINE));
        list.add(agingRecipe("Pinot Noir", BeerType.RED_GRAPE_JUICE,
                new ItemStack(Material.SWEET_BERRIES), null, null,
                BeerType.PINOT_NOIR));
        list.add(agingRecipe("风味红葡萄酒", BeerType.RED_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), null, null,
                BeerType.FLAVOR_RED_WINE));
        list.add(agingRecipe("Merlot红葡萄酒", BeerType.RED_GRAPE_JUICE,
                new ItemStack(Material.COCOA_BEANS), new ItemStack(Material.SUGAR), null,
                BeerType.MERLOT));
        list.add(agingRecipe("樱桃风味酒", BeerType.RED_GRAPE_JUICE,
                ItemFactory.createCherry(), null, null,
                BeerType.CHERRY_WINE));
        list.add(agingRecipe("Scuba Diving", BeerType.RED_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), new ItemStack(Material.FEATHER), new ItemStack(Material.BLAZE_POWDER),
                BeerType.SCUBA_DIVING));
        list.add(agingRecipe("热带苦力怕", BeerType.SAVANNA_WHITE_GRAPE_JUICE,
                new ItemStack(Material.GUNPOWDER), new ItemStack(Material.FLINT), null,
                BeerType.TROPICAL_CREEPER_WINE));
        list.add(agingRecipe("热带海味", BeerType.SAVANNA_WHITE_GRAPE_JUICE,
                new ItemStack(Material.KELP), new ItemStack(Material.SEAGRASS), null,
                BeerType.TROPICAL_SEA_WINE));
        list.add(agingRecipe("Lambrusco", BeerType.SAVANNA_RED_GRAPE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), ItemFactory.createCherry(), null,
                BeerType.LAMBRUSCO));
        list.add(agingRecipe("Spider特调", BeerType.SAVANNA_RED_GRAPE_JUICE,
                new ItemStack(Material.FERMENTED_SPIDER_EYE), null, null,
                BeerType.SPIDER_SPECIAL_WINE));
        list.add(agingRecipe("冰霜白", BeerType.TAIGA_WHITE_GRAPE_JUICE,
                new ItemStack(Material.ICE), new ItemStack(Material.SNOWBALL), null,
                BeerType.FROST_WHITE_WINE));
        list.add(agingRecipe("神盾白", BeerType.TAIGA_WHITE_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.IRON_INGOT),
                BeerType.AEGIS_WHITE_WINE));
        list.add(agingRecipe("烈焰行者", BeerType.TAIGA_RED_GRAPE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), ItemFactory.createCherry(), null,
                BeerType.BLAZE_WALKER_WINE));
        list.add(agingRecipe("紫颂果风味", BeerType.TAIGA_RED_GRAPE_JUICE,
                new ItemStack(Material.CHORUS_FRUIT), null, null,
                BeerType.CHORUS_WINE));
        list.add(agingRecipe("不详果实", BeerType.JUNGLE_WHITE_GRAPE_JUICE,
                new ItemStack(Material.WHITE_BANNER), null, null,
                BeerType.OMEN_WINE));
        list.add(agingRecipe("Bounce白", BeerType.JUNGLE_WHITE_GRAPE_JUICE,
                new ItemStack(Material.RABBIT_FOOT), new ItemStack(Material.SUGAR), null,
                BeerType.BOUNCE_WHITE_WINE));
        list.add(agingRecipe("磁吸之酒", BeerType.JUNGLE_RED_GRAPE_JUICE,
                new ItemStack(Material.IRON_INGOT), new ItemStack(Material.FLINT), null,
                BeerType.MAGNETIC_WINE));
        list.add(agingRecipe("Health红", BeerType.JUNGLE_RED_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), new ItemStack(Material.GLISTERING_MELON_SLICE), null,
                BeerType.HEALTH_RED_WINE));
        list.add(agingRecipe("斯柏德红", BeerType.JUNGLE_RED_GRAPE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), new ItemStack(Material.SPIDER_EYE), null,
                BeerType.SPIDER_RED_WINE));
        list.add(agingRecipe("猫娘特调红", BeerType.RED_GRAPE_JUICE,
                ItemFactory.createBeer(BeerType.FLAVOR_RED_WINE, BeerQuality.HIGH),
                ItemFactory.createBeer(BeerType.MERLOT, BeerQuality.HIGH),
                new ItemStack(Material.COOKED_COD),
                BeerType.CATGIRL_RED_WINE));
        list.add(agingRecipe("耄耋特调", BeerType.WHITE_GRAPE_JUICE,
                ItemFactory.createBeer(BeerType.CENTURY_WHITE_WINE, BeerQuality.HIGH),
                ItemFactory.createBeer(BeerType.VITALITY_WINE, BeerQuality.HIGH),
                new ItemStack(Material.TOTEM_OF_UNDYING),
                BeerType.ELDER_WINE));

        return list;
    }

    private static MachineRecipe agingRecipe(String name, BeerType wineInput,
                                             ItemStack ing1, ItemStack ing2, ItemStack ing3,
                                             BeerType output) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(10, ItemFactory.createBeer(wineInput, BeerQuality.HIGH));
        m.put(29, ing1);
        if (ing2 != null) m.put(30, ing2);
        if (ing3 != null) m.put(31, ing3);
        m.put(34, new ItemStack(Material.GLASS_BOTTLE));
        m.put(16, ItemFactory.createBeer(output, BeerQuality.HIGH));
        return new MachineRecipe(name, m);
    }

    private static List<MachineRecipe> grapeBasinRecipes() {
        List<MachineRecipe> list = new ArrayList<>();

        list.add(grapeBasinRecipe("红葡萄汁",
                com.xiaomian124.alcohol.grape.GrapeType.RED,
                BeerType.RED_GRAPE_JUICE));
        list.add(grapeBasinRecipe("白葡萄汁",
                com.xiaomian124.alcohol.grape.GrapeType.WHITE,
                BeerType.WHITE_GRAPE_JUICE));
        list.add(grapeBasinRecipe("热带草原红葡萄汁",
                com.xiaomian124.alcohol.grape.GrapeType.SAVANNA_RED,
                BeerType.SAVANNA_RED_GRAPE_JUICE));
        list.add(grapeBasinRecipe("热带草原白葡萄汁",
                com.xiaomian124.alcohol.grape.GrapeType.SAVANNA_WHITE,
                BeerType.SAVANNA_WHITE_GRAPE_JUICE));
        list.add(grapeBasinRecipe("丛林红葡萄汁",
                com.xiaomian124.alcohol.grape.GrapeType.JUNGLE_RED,
                BeerType.JUNGLE_RED_GRAPE_JUICE));
        list.add(grapeBasinRecipe("丛林白葡萄汁",
                com.xiaomian124.alcohol.grape.GrapeType.JUNGLE_WHITE,
                BeerType.JUNGLE_WHITE_GRAPE_JUICE));
        list.add(grapeBasinRecipe("针叶林红葡萄汁",
                com.xiaomian124.alcohol.grape.GrapeType.TAIGA_RED,
                BeerType.TAIGA_RED_GRAPE_JUICE));
        list.add(grapeBasinRecipe("针叶林白葡萄汁",
                com.xiaomian124.alcohol.grape.GrapeType.TAIGA_WHITE,
                BeerType.TAIGA_WHITE_GRAPE_JUICE));

        return list;
    }

    private static MachineRecipe grapeBasinRecipe(String name,
                                                  com.xiaomian124.alcohol.grape.GrapeType grape,
                                                  BeerType juice) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(13, ItemFactory.createGrape(grape));
        m.put(21, new ItemStack(Material.GLASS_BOTTLE));
        m.put(31, ItemFactory.createBeer(juice, BeerQuality.HIGH));
        return new MachineRecipe(name, m);
    }
}