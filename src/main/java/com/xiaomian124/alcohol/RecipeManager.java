package com.xiaomian124.alcohol;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmokingRecipe;

import java.util.List;

public final class RecipeManager {

    public static NamespacedKey KEY_DRY_WHEAT;
    public static NamespacedKey KEY_HARVEST_SHEARS;
    public static NamespacedKey KEY_COOKING_POT;
    public static NamespacedKey KEY_BREWING_BOX;
    public static NamespacedKey KEY_CORN_KERNELS;
    public static NamespacedKey KEY_APPLE_PRESS;
    public static NamespacedKey KEY_AGING_BARREL;
    public static NamespacedKey KEY_GRAPE_BASIN;

    public static List<NamespacedKey> getAllKeys() {
        return List.of(
                KEY_DRY_WHEAT,
                KEY_HARVEST_SHEARS,
                KEY_COOKING_POT,
                KEY_BREWING_BOX,
                KEY_CORN_KERNELS,
                KEY_APPLE_PRESS,
                KEY_AGING_BARREL,
                KEY_GRAPE_BASIN
        );
    }

    private RecipeManager() {}

    public static void register(AlcoholPlugin plugin) {

        KEY_DRY_WHEAT = new NamespacedKey(plugin, "dry_wheat_smoking");
        Bukkit.removeRecipe(KEY_DRY_WHEAT);
        Bukkit.addRecipe(new SmokingRecipe(
                KEY_DRY_WHEAT,
                ItemFactory.createDryWheat(null),
                Material.WHEAT,
                0.35f,
                200));

        NamespacedKey cornSmeltKey = new NamespacedKey(plugin, "corn_kernels_smoking");
        Bukkit.removeRecipe(cornSmeltKey);
        Bukkit.addRecipe(new SmokingRecipe(
                cornSmeltKey,
                ItemFactory.createDriedCorn(null),
                Material.BEETROOT_SEEDS,
                0.35f,
                200));

        KEY_HARVEST_SHEARS = new NamespacedKey(plugin, "harvest_shears");
        Bukkit.removeRecipe(KEY_HARVEST_SHEARS);
        ShapedRecipe shears = new ShapedRecipe(
                KEY_HARVEST_SHEARS, ItemFactory.createHarvestShears(null));
        shears.shape("i i", "i i", " i ");
        shears.setIngredient('i', Material.IRON_INGOT);
        Bukkit.addRecipe(shears);

        KEY_COOKING_POT = new NamespacedKey(plugin, "cooking_pot");
        Bukkit.removeRecipe(KEY_COOKING_POT);
        ShapedRecipe pot = new ShapedRecipe(
                KEY_COOKING_POT, ItemFactory.createCookingPot(null));
        pot.shape("   ", "iwi", "iii");
        pot.setIngredient('i', Material.IRON_INGOT);
        pot.setIngredient('w', Material.WOODEN_SHOVEL);
        Bukkit.addRecipe(pot);

        KEY_BREWING_BOX = new NamespacedKey(plugin, "brewing_box");
        Bukkit.removeRecipe(KEY_BREWING_BOX);
        ShapedRecipe box = new ShapedRecipe(
                KEY_BREWING_BOX, ItemFactory.createBrewingBox(null));
        box.shape("   ", "i i", "isi");
        box.setIngredient('i', Material.IRON_INGOT);
        box.setIngredient('s', Material.SMOKER);
        Bukkit.addRecipe(box);

        KEY_CORN_KERNELS = new NamespacedKey(plugin, "corn_kernels");
        Bukkit.removeRecipe(KEY_CORN_KERNELS);
        ItemStack kernelsResult = ItemFactory.createCornKernels(null);
        kernelsResult.setAmount(5);
        ShapelessRecipe cornKernels = new ShapelessRecipe(
                KEY_CORN_KERNELS, kernelsResult);
        cornKernels.addIngredient(1, Material.PLAYER_HEAD);
        Bukkit.addRecipe(cornKernels);

        KEY_APPLE_PRESS = new NamespacedKey(plugin, "apple_press");
        Bukkit.removeRecipe(KEY_APPLE_PRESS);
        ShapedRecipe applePress = new ShapedRecipe(
                KEY_APPLE_PRESS, ItemFactory.createApplePress(null));
        applePress.shape("   ", "iii", "ili");
        applePress.setIngredient('i', Material.IRON_INGOT);
        applePress.setIngredient('l', Material.LOOM);
        Bukkit.addRecipe(applePress);

        KEY_AGING_BARREL = new NamespacedKey(plugin, "aging_barrel");
        Bukkit.removeRecipe(KEY_AGING_BARREL);
        ShapedRecipe agingBarrel = new ShapedRecipe(
                KEY_AGING_BARREL, ItemFactory.createAgingBarrel(null));
        agingBarrel.shape("   ", "ibi", "iii");
        agingBarrel.setIngredient('i', Material.IRON_INGOT);
        agingBarrel.setIngredient('b', Material.BARREL);
        Bukkit.addRecipe(agingBarrel);

        KEY_GRAPE_BASIN = new NamespacedKey(plugin, "grape_basin");
        Bukkit.removeRecipe(KEY_GRAPE_BASIN);
        ShapedRecipe grapeBasin = new ShapedRecipe(
                KEY_GRAPE_BASIN, ItemFactory.createGrapeBasin(null));
        grapeBasin.shape("   ", "ibi", "iii");
        grapeBasin.setIngredient('i', Material.IRON_INGOT);
        grapeBasin.setIngredient('b', Material.COMPOSTER);
        Bukkit.addRecipe(grapeBasin);

        NamespacedKey musicPlayerKey = new NamespacedKey(plugin, "music_player");
        Bukkit.removeRecipe(musicPlayerKey);
        ShapedRecipe musicPlayer = new ShapedRecipe(
                musicPlayerKey, ItemFactory.createMusicPlayer(null));
        musicPlayer.shape("   ", "iji", "iii");
        musicPlayer.setIngredient('i', Material.IRON_INGOT);
        musicPlayer.setIngredient('j', Material.JUKEBOX);
        Bukkit.addRecipe(musicPlayer);

        plugin.getLogger().info("All recipes registered!");
    }

    private static List<CookingRecipe> cookingRecipes = null;

    private static List<CookingRecipe> getCookingRecipes() {
        if (cookingRecipes == null) {
            cookingRecipes = List.of(
                    new CookingRecipe(
                            List.of(
                                    new ItemStack(Material.SUGAR),
                                    new ItemStack(Material.WATER_BUCKET),
                                    new ItemStack(Material.WHEAT)
                            ),
                            ItemFactory.createYeast(null),
                            600
                    )
            );
        }
        return cookingRecipes;
    }

    public static CookingRecipe findCookingRecipe(List<ItemStack> inputs) {
        for (CookingRecipe r : getCookingRecipes()) {
            if (r.matches(inputs)) return r;
        }
        return null;
    }

    private static List<BrewingRecipe> brewingRecipes = null;

    private static List<BrewingRecipe> getBrewingRecipes() {
        if (brewingRecipes == null) {
            brewingRecipes = List.of(
                    // 啤酒
                    makeBeerRecipe(BeerType.WHEAT_BEER,
                            ItemFactory.createDryWheat(null),
                            ItemFactory.createHops(null)),
                    makeBeerRecipe(BeerType.BARLEY_BEER,
                            ItemFactory.createDriedBarley(null),
                            ItemFactory.createHops(null)),
                    makeBeerRecipe(BeerType.HOPS_BEER,
                            ItemFactory.createHops(null),
                            ItemFactory.createHops(null)),
                    makeBeerRecipe(BeerType.NETTLE_BEER,
                            ItemFactory.createWildNettle(null),
                            ItemFactory.createHops(null)),
                    makeBeerRecipe(BeerType.OATS_BEER,
                            ItemFactory.createDriedOats(null),
                            ItemFactory.createHops(null)),
                    makeBeerRecipe(BeerType.COCOA_BEER,
                            new ItemStack(Material.COCOA_BEANS),
                            ItemFactory.createHops(null)),

                    // 威士忌
                    makeWhiskyRecipe(BeerType.BELGRAVIA,
                            ItemFactory.createDriedBarley(null),
                            ItemFactory.createDriedBarley(null)),
                    makeWhiskyRecipe(BeerType.ISLAY,
                            ItemFactory.createDryWheat(null),
                            ItemFactory.createDriedBarley(null)),
                    makeWhiskyRecipe(BeerType.BOURBON,
                            ItemFactory.createDriedCorn(null),
                            ItemFactory.createDriedCorn(null)),
                    makeWhiskyRecipe(BeerType.MACALLAN,
                            ItemFactory.createDryWheat(null),
                            ItemFactory.createDryWheat(null)),
                    makeWhiskyRecipe(BeerType.GLENFIDDICH,
                            ItemFactory.createDryWheat(null),
                            ItemFactory.createDriedCorn(null)),
                    makeWhiskyRecipe(BeerType.BAINS,
                            ItemFactory.createDriedOats(null),
                            ItemFactory.createDriedCorn(null)),
                    makeWhiskyRecipe(BeerType.SUNTORY,
                            ItemFactory.createDryWheat(null),
                            ItemFactory.createDriedOats(null)),
                    makeWhiskyRecipe(BeerType.BUFFALO_TRACE,
                            ItemFactory.createDriedOats(null),
                            ItemFactory.createDriedOats(null)),
                    makeWhiskyRecipe(BeerType.JIM_BEAM,
                            ItemFactory.createDriedBarley(null),
                            ItemFactory.createDriedOats(null))
            );
        }
        return brewingRecipes;
    }

    private static BrewingRecipe makeBeerRecipe(BeerType type,
                                                ItemStack main1, ItemStack main2) {
        return new BrewingRecipe(
                List.of(main1, main2, ItemFactory.createYeast(null)),
                ItemFactory.createBeer(null, type, BeerQuality.HIGH),
                3, 100, 1200);
    }

    private static BrewingRecipe makeWhiskyRecipe(BeerType type,
                                                  ItemStack main1, ItemStack main2) {
        return new BrewingRecipe(
                List.of(main1, main2, ItemFactory.createYeast(null)),
                ItemFactory.createBeer(null, type, BeerQuality.HIGH),
                3, 100, 1200);
    }

    public static BrewingRecipe findBrewingRecipe(List<ItemStack> inputs) {
        for (BrewingRecipe r : getBrewingRecipes()) {
            if (r.matches(inputs)) return r;
        }
        return null;
    }
}