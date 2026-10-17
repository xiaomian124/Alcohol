package com.xiaomian124.alcohol.recipe;

import com.xiaomian124.alcohol.*;
import com.xiaomian124.alcohol.grape.GrapeType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RecipeData {

    private RecipeData() {}

    public static List<MachineRecipe> getRecipes(Player player, MachineType type) {
        return switch (type) {
            case COOKING_POT   -> cookingPotRecipes(player);
            case BREWING_BOX   -> brewingBoxRecipes(player);
            case APPLE_PRESS   -> applePressRecipes(player);
            case AGING_BARREL  -> agingBarrelRecipes(player);
            case GRAPE_BASIN   -> grapeBasinRecipes(player);
            case MUSIC_PLAYER  -> new ArrayList<>();
        };
    }

    public static Map<Integer, ItemStack> getCraftingRecipe(Player player, MachineType type) {
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

    private static List<MachineRecipe> cookingPotRecipes(Player player) {
        List<MachineRecipe> list = new ArrayList<>();
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(10, new ItemStack(Material.SUGAR));
        m.put(11, new ItemStack(Material.WATER_BUCKET));
        m.put(12, new ItemStack(Material.WHEAT));
        ItemStack yeast = ItemFactory.createYeast(player);
        m.put(16, yeast);
        list.add(new MachineRecipe("item.yeast.name", m));
        return list;
    }

    private static List<MachineRecipe> brewingBoxRecipes(Player player) {
        List<MachineRecipe> list = new ArrayList<>();

        list.add(beerRecipe(player, ItemFactory.createDryWheat(player), ItemFactory.createHops(player),
                BeerType.WHEAT_BEER));
        list.add(beerRecipe(player, ItemFactory.createDriedBarley(player), ItemFactory.createHops(player),
                BeerType.BARLEY_BEER));
        list.add(beerRecipe(player, ItemFactory.createHops(player), ItemFactory.createHops(player),
                BeerType.HOPS_BEER));
        list.add(beerRecipe(player, ItemFactory.createWildNettle(player), ItemFactory.createHops(player),
                BeerType.NETTLE_BEER));
        list.add(beerRecipe(player, ItemFactory.createDriedOats(player), ItemFactory.createHops(player),
                BeerType.OATS_BEER));
        list.add(beerRecipe(player, new ItemStack(Material.COCOA_BEANS), ItemFactory.createHops(player),
                BeerType.COCOA_BEER));

        list.add(whiskyRecipe(player, ItemFactory.createDriedBarley(player), ItemFactory.createDriedBarley(player),
                BeerType.BELGRAVIA));
        list.add(whiskyRecipe(player, ItemFactory.createDryWheat(player), ItemFactory.createDriedBarley(player),
                BeerType.ISLAY));
        list.add(whiskyRecipe(player, ItemFactory.createDriedCorn(player), ItemFactory.createDriedCorn(player),
                BeerType.BOURBON));
        list.add(whiskyRecipe(player, ItemFactory.createDryWheat(player), ItemFactory.createDryWheat(player),
                BeerType.MACALLAN));
        list.add(whiskyRecipe(player, ItemFactory.createDryWheat(player), ItemFactory.createDriedCorn(player),
                BeerType.GLENFIDDICH));
        list.add(whiskyRecipe(player, ItemFactory.createDriedOats(player), ItemFactory.createDriedCorn(player),
                BeerType.BAINS));
        list.add(whiskyRecipe(player, ItemFactory.createDryWheat(player), ItemFactory.createDriedOats(player),
                BeerType.SUNTORY));
        list.add(whiskyRecipe(player, ItemFactory.createDriedOats(player), ItemFactory.createDriedOats(player),
                BeerType.BUFFALO_TRACE));
        list.add(whiskyRecipe(player, ItemFactory.createDriedBarley(player), ItemFactory.createDriedOats(player),
                BeerType.JIM_BEAM));

        return list;
    }

    private static MachineRecipe beerRecipe(Player player, ItemStack a, ItemStack b, BeerType out) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(10, a);
        m.put(11, b);
        m.put(12, ItemFactory.createYeast(player));
        m.put(16, ItemFactory.createBeer(player, out, BeerQuality.HIGH));
        m.put(29, new ItemStack(Material.WATER_BUCKET));
        m.put(32, new ItemStack(Material.COAL, 3));
        return new MachineRecipe(out.getNameKey(), m);
    }

    private static MachineRecipe whiskyRecipe(Player player, ItemStack a, ItemStack b, BeerType out) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(10, a);
        m.put(11, b);
        m.put(12, ItemFactory.createYeast(player));
        m.put(16, ItemFactory.createBeer(player, out, BeerQuality.HIGH));
        m.put(29, new ItemStack(Material.WATER_BUCKET));
        m.put(32, new ItemStack(Material.COAL, 3));
        return new MachineRecipe(out.getNameKey(), m);
    }

    private static List<MachineRecipe> applePressRecipes(Player player) {
        List<MachineRecipe> list = new ArrayList<>();

        Map<Integer, ItemStack> m1 = new LinkedHashMap<>();
        m1.put(19, new ItemStack(Material.APPLE));
        ItemStack mash = ItemFactory.createAppleMash(player);
        m1.put(23, mash);
        list.add(new MachineRecipe("item.apple_mash.name", m1));

        Map<Integer, ItemStack> m2 = new LinkedHashMap<>();
        m2.put(23, ItemFactory.createAppleMash(player));
        m2.put(34, new ItemStack(Material.GLASS_BOTTLE));
        ItemStack juice = ItemFactory.createAppleJuice(player);
        m2.put(16, juice);
        list.add(new MachineRecipe("item.apple_juice.name", m2));

        return list;
    }

    private static List<MachineRecipe> agingBarrelRecipes(Player player) {
        List<MachineRecipe> list = new ArrayList<>();

        list.add(agingRecipe(player, BeerType.APPLE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), new ItemStack(Material.SUGAR), null,
                BeerType.HONEY_WINE));
        list.add(agingRecipe(player, BeerType.APPLE_JUICE,
                new ItemStack(Material.SUGAR), null, null,
                BeerType.APPLE_FLAVOR_WINE));
        list.add(agingRecipe(player, BeerType.APPLE_JUICE,
                ItemFactory.createAppleJuice(player), null, null,
                BeerType.APPLE_WINE));
        list.add(agingRecipe(player, BeerType.WHITE_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), new ItemStack(Material.GLOWSTONE_DUST), null,
                BeerType.VITALITY_WINE));
        list.add(agingRecipe(player, BeerType.WHITE_GRAPE_JUICE,
                new ItemStack(Material.GLOW_BERRIES), null, null,
                BeerType.SUNSHINE_WINE));
        list.add(agingRecipe(player, BeerType.WHITE_GRAPE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), new ItemStack(Material.SWEET_BERRIES), null,
                BeerType.CENTURY_WHITE_WINE));
        list.add(agingRecipe(player, BeerType.RED_GRAPE_JUICE,
                new ItemStack(Material.SWEET_BERRIES), null, null,
                BeerType.PINOT_NOIR));
        list.add(agingRecipe(player, BeerType.RED_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), null, null,
                BeerType.FLAVOR_RED_WINE));
        list.add(agingRecipe(player, BeerType.RED_GRAPE_JUICE,
                new ItemStack(Material.COCOA_BEANS), new ItemStack(Material.SUGAR), null,
                BeerType.MERLOT));
        list.add(agingRecipe(player, BeerType.RED_GRAPE_JUICE,
                ItemFactory.createCherry(player), null, null,
                BeerType.CHERRY_WINE));
        list.add(agingRecipe(player, BeerType.RED_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), new ItemStack(Material.FEATHER), new ItemStack(Material.BLAZE_POWDER),
                BeerType.SCUBA_DIVING));
        list.add(agingRecipe(player, BeerType.SAVANNA_WHITE_GRAPE_JUICE,
                new ItemStack(Material.GUNPOWDER), new ItemStack(Material.FLINT), null,
                BeerType.TROPICAL_CREEPER_WINE));
        list.add(agingRecipe(player, BeerType.SAVANNA_WHITE_GRAPE_JUICE,
                new ItemStack(Material.KELP), new ItemStack(Material.SEAGRASS), null,
                BeerType.TROPICAL_SEA_WINE));
        list.add(agingRecipe(player, BeerType.SAVANNA_RED_GRAPE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), ItemFactory.createCherry(player), null,
                BeerType.LAMBRUSCO));
        list.add(agingRecipe(player, BeerType.SAVANNA_RED_GRAPE_JUICE,
                new ItemStack(Material.FERMENTED_SPIDER_EYE), null, null,
                BeerType.SPIDER_SPECIAL_WINE));
        list.add(agingRecipe(player, BeerType.TAIGA_WHITE_GRAPE_JUICE,
                new ItemStack(Material.ICE), new ItemStack(Material.SNOWBALL), null,
                BeerType.FROST_WHITE_WINE));
        list.add(agingRecipe(player, BeerType.TAIGA_WHITE_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.IRON_INGOT),
                BeerType.AEGIS_WHITE_WINE));
        list.add(agingRecipe(player, BeerType.TAIGA_RED_GRAPE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), ItemFactory.createCherry(player), null,
                BeerType.BLAZE_WALKER_WINE));
        list.add(agingRecipe(player, BeerType.TAIGA_RED_GRAPE_JUICE,
                new ItemStack(Material.CHORUS_FRUIT), null, null,
                BeerType.CHORUS_WINE));
        list.add(agingRecipe(player, BeerType.JUNGLE_WHITE_GRAPE_JUICE,
                new ItemStack(Material.WHITE_BANNER), null, null,
                BeerType.OMEN_WINE));
        list.add(agingRecipe(player, BeerType.JUNGLE_WHITE_GRAPE_JUICE,
                new ItemStack(Material.RABBIT_FOOT), new ItemStack(Material.SUGAR), null,
                BeerType.BOUNCE_WHITE_WINE));
        list.add(agingRecipe(player, BeerType.JUNGLE_RED_GRAPE_JUICE,
                new ItemStack(Material.IRON_INGOT), new ItemStack(Material.FLINT), null,
                BeerType.MAGNETIC_WINE));
        list.add(agingRecipe(player, BeerType.JUNGLE_RED_GRAPE_JUICE,
                new ItemStack(Material.SUGAR), new ItemStack(Material.GLISTERING_MELON_SLICE), null,
                BeerType.HEALTH_RED_WINE));
        list.add(agingRecipe(player, BeerType.JUNGLE_RED_GRAPE_JUICE,
                new ItemStack(Material.HONEY_BOTTLE), new ItemStack(Material.SPIDER_EYE), null,
                BeerType.SPIDER_RED_WINE));
        list.add(agingRecipe(player, BeerType.RED_GRAPE_JUICE,
                ItemFactory.createBeer(player, BeerType.FLAVOR_RED_WINE, BeerQuality.HIGH),
                ItemFactory.createBeer(player, BeerType.MERLOT, BeerQuality.HIGH),
                new ItemStack(Material.COOKED_COD),
                BeerType.CATGIRL_RED_WINE));
        list.add(agingRecipe(player, BeerType.WHITE_GRAPE_JUICE,
                ItemFactory.createBeer(player, BeerType.CENTURY_WHITE_WINE, BeerQuality.HIGH),
                ItemFactory.createBeer(player, BeerType.VITALITY_WINE, BeerQuality.HIGH),
                new ItemStack(Material.TOTEM_OF_UNDYING),
                BeerType.ELDER_WINE));

        return list;
    }

    private static MachineRecipe agingRecipe(Player player, BeerType wineInput,
                                             ItemStack ing1, ItemStack ing2, ItemStack ing3,
                                             BeerType output) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(10, ItemFactory.createBeer(player, wineInput, BeerQuality.HIGH));
        m.put(29, ing1);
        if (ing2 != null) m.put(30, ing2);
        if (ing3 != null) m.put(31, ing3);
        m.put(34, new ItemStack(Material.GLASS_BOTTLE));
        m.put(16, ItemFactory.createBeer(player, output, BeerQuality.HIGH));
        return new MachineRecipe(output.getNameKey(), m);
    }

    private static List<MachineRecipe> grapeBasinRecipes(Player player) {
        List<MachineRecipe> list = new ArrayList<>();

        list.add(grapeBasinRecipe(player, GrapeType.RED, BeerType.RED_GRAPE_JUICE));
        list.add(grapeBasinRecipe(player, GrapeType.WHITE, BeerType.WHITE_GRAPE_JUICE));
        list.add(grapeBasinRecipe(player, GrapeType.SAVANNA_RED, BeerType.SAVANNA_RED_GRAPE_JUICE));
        list.add(grapeBasinRecipe(player, GrapeType.SAVANNA_WHITE, BeerType.SAVANNA_WHITE_GRAPE_JUICE));
        list.add(grapeBasinRecipe(player, GrapeType.JUNGLE_RED, BeerType.JUNGLE_RED_GRAPE_JUICE));
        list.add(grapeBasinRecipe(player, GrapeType.JUNGLE_WHITE, BeerType.JUNGLE_WHITE_GRAPE_JUICE));
        list.add(grapeBasinRecipe(player, GrapeType.TAIGA_RED, BeerType.TAIGA_RED_GRAPE_JUICE));
        list.add(grapeBasinRecipe(player, GrapeType.TAIGA_WHITE, BeerType.TAIGA_WHITE_GRAPE_JUICE));

        return list;
    }

    private static MachineRecipe grapeBasinRecipe(Player player, GrapeType grape, BeerType juice) {
        Map<Integer, ItemStack> m = new LinkedHashMap<>();
        m.put(13, ItemFactory.createGrape(player, grape));
        m.put(21, new ItemStack(Material.GLASS_BOTTLE));
        ItemStack out = ItemFactory.createBeer(player, juice, BeerQuality.HIGH);
        m.put(31, out);
        return new MachineRecipe(juice.getNameKey(), m);
    }
}
