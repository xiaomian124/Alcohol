package com.xiaomian124.alcohol.aging;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.BeerQuality;
import com.xiaomian124.alcohol.BeerType;
import com.xiaomian124.alcohol.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AgingBarrelManager {

    private AgingBarrelManager() {}

    private static final Set<String> REGISTERED = new HashSet<>();
    private static final Map<String, AgingBarrelGUI> BARRELS = new HashMap<>();
    private static final Map<String, SavedState> STATES = new HashMap<>();

    private static final List<AgingRecipe> RECIPES = new ArrayList<>();

    public static List<AgingRecipe> getRecipes() {
        return RECIPES;
    }

    public static void addRecipe(AgingRecipe recipe) {
        RECIPES.add(recipe);
    }

    // 注册配方 26
    public static void initRecipes() {
        RECIPES.clear();

        // 1. 蜂蜜酒：苹果汁 + 蜂蜜瓶 + 糖 - 1%
        addRecipe(R(BeerType.APPLE_JUICE, I(Material.HONEY_BOTTLE), I(Material.SUGAR), null,
                BeerType.HONEY_WINE, 1));

        // 2. 苹果风味酒：苹果汁 + 糖 - 20%
        addRecipe(R(BeerType.APPLE_JUICE, I(Material.SUGAR), null, null,
                BeerType.APPLE_FLAVOR_WINE, 20));

        // 3. 苹果酒：苹果汁 + 苹果汁 - 15%
        addRecipe(R(BeerType.APPLE_JUICE, I(BeerType.APPLE_JUICE), null, null,
                BeerType.APPLE_WINE, 15));

        // 4. 活力之酒：白葡萄汁 + 糖 + 荧石粉 - 5%
        addRecipe(R(BeerType.WHITE_GRAPE_JUICE, I(Material.SUGAR), I(Material.GLOWSTONE_DUST), null,
                BeerType.VITALITY_WINE, 5));

        // 5. 阳光葡萄酒：白葡萄汁 + 发光浆果 - 10%
        addRecipe(R(BeerType.WHITE_GRAPE_JUICE, I(Material.GLOW_BERRIES), null, null,
                BeerType.SUNSHINE_WINE, 10));

        // 6. 世纪白葡萄酒：白葡萄汁 + 蜂蜜瓶 + 甜浆果 - 15%
        addRecipe(R(BeerType.WHITE_GRAPE_JUICE, I(Material.HONEY_BOTTLE), I(Material.SWEET_BERRIES), null,
                BeerType.CENTURY_WHITE_WINE, 15));

        // 7. Pinot Noir：红葡萄汁 + 甜浆果 - 12%
        addRecipe(R(BeerType.RED_GRAPE_JUICE, I(Material.SWEET_BERRIES), null, null,
                BeerType.PINOT_NOIR, 12));

        // 8. 风味红葡萄酒：红葡萄汁 + 糖 - 10%
        addRecipe(R(BeerType.RED_GRAPE_JUICE, I(Material.SUGAR), null, null,
                BeerType.FLAVOR_RED_WINE, 10));

        // 9. Merlot：红葡萄汁 + 可可豆 + 糖 - 8%
        addRecipe(R(BeerType.RED_GRAPE_JUICE, I(Material.COCOA_BEANS), I(Material.SUGAR), null,
                BeerType.MERLOT, 8));

        // 10. 樱桃风味酒：红葡萄汁 + 樱桃 - 15%
        addRecipe(R(BeerType.RED_GRAPE_JUICE, ItemFactory.createCherry(), null, null,
                BeerType.CHERRY_WINE, 15));

        // 11. Scuba Diving：红葡萄汁 + 糖 + 羽毛 + 烈焰粉 - 25%
        addRecipe(R(BeerType.RED_GRAPE_JUICE, I(Material.SUGAR), I(Material.FEATHER), I(Material.BLAZE_POWDER),
                BeerType.SCUBA_DIVING, 25));

        // 12. 热带苦力怕：热带草原白葡萄汁 + 火药 + 燧石 - 10%
        addRecipe(R(BeerType.SAVANNA_WHITE_GRAPE_JUICE, I(Material.GUNPOWDER), I(Material.FLINT), null,
                BeerType.TROPICAL_CREEPER_WINE, 10));

        // 13. 热带海味：热带草原白葡萄汁 + 海带 + 海草 - 14%
        addRecipe(R(BeerType.SAVANNA_WHITE_GRAPE_JUICE, I(Material.KELP), I(Material.SEAGRASS), null,
                BeerType.TROPICAL_SEA_WINE, 14));

        // 14. Lambrusco：热带草原红葡萄汁 + 蜂蜜瓶 + 樱桃 - 14%
        addRecipe(R(BeerType.SAVANNA_RED_GRAPE_JUICE, I(Material.HONEY_BOTTLE), ItemFactory.createCherry(), null,
                BeerType.LAMBRUSCO, 14));

        // 15. Spider特调：热带草原红葡萄汁 + 发酵蛛眼 - 10%
        addRecipe(R(BeerType.SAVANNA_RED_GRAPE_JUICE, I(Material.FERMENTED_SPIDER_EYE), null, null,
                BeerType.SPIDER_SPECIAL_WINE, 10));

        // 16. 冰霜白：针叶林白葡萄汁 + 冰 + 雪球 - 10%
        addRecipe(R(BeerType.TAIGA_WHITE_GRAPE_JUICE, I(Material.ICE), I(Material.SNOWBALL), null,
                BeerType.FROST_WHITE_WINE, 10));

        // 17. 神盾白：针叶林白葡萄汁 + 糖 + 金苹果 + 铁锭 - 7%
        addRecipe(R(BeerType.TAIGA_WHITE_GRAPE_JUICE, I(Material.SUGAR), I(Material.GOLDEN_APPLE), I(Material.IRON_INGOT),
                BeerType.AEGIS_WHITE_WINE, 7));

        // 18. 烈焰行者：针叶林红葡萄汁 + 蜂蜜瓶 + 樱桃 - 6%
        addRecipe(R(BeerType.TAIGA_RED_GRAPE_JUICE, I(Material.HONEY_BOTTLE), ItemFactory.createCherry(), null,
                BeerType.BLAZE_WALKER_WINE, 6));

        // 19. 紫颂果：针叶林红葡萄汁 + 紫颂果 - 8%
        addRecipe(R(BeerType.TAIGA_RED_GRAPE_JUICE, I(Material.CHORUS_FRUIT), null, null,
                BeerType.CHORUS_WINE, 8));

        // 20. 不详果实：丛林白葡萄汁 + 不详旗帜 - 18%
        addRecipe(R(BeerType.JUNGLE_WHITE_GRAPE_JUICE, I(Material.CROSSBOW), null, null,
                BeerType.OMEN_WINE, 18));

        // 21. Bounce白：丛林白葡萄汁 + 兔子腿 + 糖 - 12%
        addRecipe(R(BeerType.JUNGLE_WHITE_GRAPE_JUICE, I(Material.RABBIT_FOOT), I(Material.SUGAR), null,
                BeerType.BOUNCE_WHITE_WINE, 12));

        // 22. 磁吸之酒：丛林红葡萄汁 + 铁锭 + 燧石 - 14%
        addRecipe(R(BeerType.JUNGLE_RED_GRAPE_JUICE, I(Material.IRON_INGOT), I(Material.FLINT), null,
                BeerType.MAGNETIC_WINE, 14));

        // 23. Health红：丛林红葡萄汁 + 糖 + 闪烁的西瓜片 - 10%
        addRecipe(R(BeerType.JUNGLE_RED_GRAPE_JUICE, I(Material.SUGAR), I(Material.GLISTERING_MELON_SLICE), null,
                BeerType.HEALTH_RED_WINE, 10));

        // 24. 斯柏德红：丛林红葡萄汁 + 蜂蜜瓶 + 蜘蛛眼 - 12%
        addRecipe(R(BeerType.JUNGLE_RED_GRAPE_JUICE, I(Material.HONEY_BOTTLE), I(Material.SPIDER_EYE), null,
                BeerType.SPIDER_RED_WINE, 12));

        // 25. 猫娘特调红：红葡萄汁 + 风味红葡萄酒 + Merlot + 熟鳕鱼 - 28%
        addRecipe(R(BeerType.RED_GRAPE_JUICE,
                B(BeerType.FLAVOR_RED_WINE), B(BeerType.MERLOT), I(Material.COOKED_COD),
                BeerType.CATGIRL_RED_WINE, 28));

        // 26. 耄耋特调：白葡萄汁 + 世纪白葡萄酒 + 活力之酒 + 不死图腾 - 28%
        addRecipe(R(BeerType.WHITE_GRAPE_JUICE,
                B(BeerType.CENTURY_WHITE_WINE), B(BeerType.VITALITY_WINE), I(Material.TOTEM_OF_UNDYING),
                BeerType.ELDER_WINE, 28));
    }

    private static ItemStack I(Material m) { return new ItemStack(m); }
    private static ItemStack I(BeerType type) { return ItemFactory.createBeer(type, BeerQuality.HIGH); }
    private static ItemStack B(BeerType type) { return ItemFactory.createBeer(type, BeerQuality.HIGH); }

    private static AgingRecipe R(BeerType input, ItemStack a, ItemStack b, ItemStack c,
                                 BeerType output, int cost) {
        List<ItemStack> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        list.add(c);
        return new AgingRecipe(input, list, output, cost);
    }

    public static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static void register(Location loc) { REGISTERED.add(key(loc)); }

    public static void unregister(Location loc) {
        String k = key(loc);
        REGISTERED.remove(k);
        BARRELS.remove(k);
        STATES.remove(k);
    }

    public static boolean isAgingBarrel(Location loc) {
        return REGISTERED.contains(key(loc));
    }

    public static Set<String> getRegisteredKeys() {
        return new HashSet<>(REGISTERED);
    }

    public static Location parseLocation(String k) {
        String[] parts = k.split(":");
        if (parts.length != 4) return null;
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;
        try {
            return new Location(world,
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) { return null; }
    }

    public static AgingBarrelGUI getOrCreate(AlcoholPlugin plugin, Block block) {
        return BARRELS.computeIfAbsent(key(block.getLocation()),
                k -> new AgingBarrelGUI(plugin, block));
    }

    public static AgingBarrelGUI get(Location loc) { return BARRELS.get(key(loc)); }

    public static void saveState(Location loc, ItemStack[] items,
                                 BeerType storageType, int storagePercent,
                                 int agingRemaining) {
        STATES.put(key(loc), new SavedState(items, storageType, storagePercent, agingRemaining));
    }

    public static SavedState loadState(Location loc) { return STATES.get(key(loc)); }

    public static void tickAll() {
        Iterator<AgingBarrelGUI> it = BARRELS.values().iterator();
        while (it.hasNext()) {
            AgingBarrelGUI gui = it.next();
            Block block = gui.getBlock();
            if (block.getType() != Material.BARREL) { it.remove(); continue; }
            gui.tick();
        }
    }

    public static class SavedState {
        public final ItemStack[] items;
        public final BeerType storageType;
        public final int storagePercent;
        public final int agingRemaining;

        public SavedState(ItemStack[] items, BeerType storageType,
                          int storagePercent, int agingRemaining) {
            this.items = items;
            this.storageType = storageType;
            this.storagePercent = storagePercent;
            this.agingRemaining = agingRemaining;
        }
    }
}