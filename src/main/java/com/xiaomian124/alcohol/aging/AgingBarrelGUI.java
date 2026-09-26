package com.xiaomian124.alcohol.aging;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.BeerQuality;
import com.xiaomian124.alcohol.BeerType;
import com.xiaomian124.alcohol.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AgingBarrelGUI implements InventoryHolder {

    public static final int SIZE = 45;

    public static final int WINE_INPUT_SLOT    = 10;
    public static final int HEAD_1_SLOT        = 12;
    public static final int WINE_STORAGE_SLOT  = 14;
    public static final int WINE_OUTPUT_SLOT   = 16;
    public static final int HEAD_2_SLOT        = 25;
    public static final int INGREDIENT_1_SLOT  = 29;
    public static final int INGREDIENT_2_SLOT  = 30;
    public static final int INGREDIENT_3_SLOT  = 31;
    public static final int GLASS_INPUT_SLOT   = 34;

    public static final int AGING_TICKS = 6000;
    public static final int WINE_PER_POUR = 25;
    public static final int WINE_MAX = 100;
    public static final int MAX_OUTPUT = 64;

    private final AlcoholPlugin plugin;
    private final Block block;
    private final Inventory inventory;

    private BeerType storageType = null;
    private int storagePercent = 0;
    private int agingRemaining = 0;

    public AgingBarrelGUI(AlcoholPlugin plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
        this.inventory = Bukkit.createInventory(this, SIZE,
                Component.text("陈酿桶").color(NamedTextColor.DARK_GRAY));
        fillBackground();
        updateHeads();
        updateStorageDisplay();
        restoreFromManager();
    }

    private void fillBackground() {
        ItemStack filler = makePane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, filler);
        inventory.setItem(WINE_INPUT_SLOT, null);
        inventory.setItem(WINE_OUTPUT_SLOT, null);
        inventory.setItem(INGREDIENT_1_SLOT, null);
        inventory.setItem(INGREDIENT_2_SLOT, null);
        inventory.setItem(INGREDIENT_3_SLOT, null);
        inventory.setItem(GLASS_INPUT_SLOT, null);
    }

    private static ItemStack makePane(Material mat, String name) {
        ItemStack pane = new ItemStack(mat);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(ItemFactory.plainName(name, NamedTextColor.GRAY));
        pane.setItemMeta(meta);
        return pane;
    }

    private void syncToManager() {
        AgingBarrelManager.saveState(block.getLocation(),
                getContents(), storageType, storagePercent, agingRemaining);
    }

    private void restoreFromManager() {
        AgingBarrelManager.SavedState state =
                AgingBarrelManager.loadState(block.getLocation());
        if (state == null) return;
        setContents(state.items);
        this.storageType = state.storageType;
        this.storagePercent = state.storagePercent;
        this.agingRemaining = state.agingRemaining;
        updateHeads();
        updateStorageDisplay();
    }

    public void tick() {
        if (block.getType() != Material.BARREL) return;
        try {
            tickWineInput();
            tickAging();
            updateHeads();
            updateStorageDisplay();
        } finally {
            syncToManager();
        }
    }

    private void tickWineInput() {
        ItemStack input = inventory.getItem(WINE_INPUT_SLOT);
        if (input == null || input.getType() == Material.AIR) return;
        if (!ItemFactory.isBeer(input)) return;

        BeerType wineType = ItemFactory.getBeerType(input);
        if (wineType == null) return;

        if (storageType != null && storageType != wineType) return;

        if (storagePercent >= WINE_MAX) return;

        storageType = wineType;

        int newAmount = input.getAmount() - 1;
        if (newAmount <= 0) {
            inventory.setItem(WINE_INPUT_SLOT, null);
        } else {
            ItemStack newItem = input.clone();
            newItem.setAmount(newAmount);
            inventory.setItem(WINE_INPUT_SLOT, newItem);
        }

        storagePercent = Math.min(WINE_MAX, storagePercent + WINE_PER_POUR);

        Player viewer = getViewer();
        if (viewer != null) {
            ItemStack bottle = new ItemStack(Material.GLASS_BOTTLE);
            viewer.getInventory().addItem(bottle).forEach((i, drop) ->
                    viewer.getWorld().dropItemNaturally(viewer.getLocation(), drop));
            viewer.updateInventory();
        }
    }

    private void tickAging() {
        if (!canStartAging()) {
            agingRemaining = 0;
            return;
        }

        if (agingRemaining <= 0) {
            agingRemaining = AGING_TICKS;
            return;
        }

        agingRemaining--;
        if (agingRemaining <= 0) {
            completeAging();
        }
    }

    private boolean canStartAging() {
        if (storageType == null || storagePercent <= 0) return false;

        ItemStack glass = inventory.getItem(GLASS_INPUT_SLOT);
        if (glass == null || glass.getType() != Material.GLASS_BOTTLE) return false;

        ItemStack[] ings = new ItemStack[]{
                inventory.getItem(INGREDIENT_1_SLOT),
                inventory.getItem(INGREDIENT_2_SLOT),
                inventory.getItem(INGREDIENT_3_SLOT)
        };

        AgingRecipe recipe = findRecipe(storageType, ings);
        if (recipe == null) return false;
        if (storagePercent < recipe.wineCostPercent()) return false;
        if (!outputHasSpace()) return false;

        return true;
    }

    private void completeAging() {
        ItemStack[] ings = new ItemStack[]{
                inventory.getItem(INGREDIENT_1_SLOT),
                inventory.getItem(INGREDIENT_2_SLOT),
                inventory.getItem(INGREDIENT_3_SLOT)
        };

        AgingRecipe recipe = findRecipe(storageType, ings);
        if (recipe == null) return;

        storagePercent -= recipe.wineCostPercent();
        if (storagePercent < 0) storagePercent = 0;

        ItemStack glass = inventory.getItem(GLASS_INPUT_SLOT);
        if (glass != null && glass.getType() == Material.GLASS_BOTTLE) {
            glass.setAmount(glass.getAmount() - 1);
            if (glass.getAmount() <= 0) inventory.setItem(GLASS_INPUT_SLOT, null);
        }

        Player viewer = getViewer();

        for (int slot : new int[]{INGREDIENT_1_SLOT, INGREDIENT_2_SLOT, INGREDIENT_3_SLOT}) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;

            if (isBottleItem(item.getType())) {
                int newAmount = item.getAmount() - 1;

                if (newAmount <= 0) {
                    inventory.setItem(slot, new ItemStack(Material.GLASS_BOTTLE));
                } else {
                    item.setAmount(newAmount);
                    if (viewer != null) {
                        viewer.getInventory()
                                .addItem(new ItemStack(Material.GLASS_BOTTLE))
                                .forEach((i, drop) ->
                                        viewer.getWorld().dropItemNaturally(
                                                viewer.getLocation(), drop));
                        viewer.updateInventory();
                    }
                }
            } else {
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() <= 0) inventory.setItem(slot, null);
            }
        }

        ItemStack result = ItemFactory.createBeer(recipe.outputWine(), BeerQuality.HIGH);
        ItemStack existing = inventory.getItem(WINE_OUTPUT_SLOT);

        if (existing == null || existing.getType() == Material.AIR) {
            inventory.setItem(WINE_OUTPUT_SLOT, result);
        } else if (ItemFactory.isBeer(existing)
                && existing.getAmount() < MAX_OUTPUT
                && ItemFactory.getBeerType(existing) == ItemFactory.getBeerType(result)
                && ItemFactory.getBeerQuality(existing) == ItemFactory.getBeerQuality(result)) {
            existing.setAmount(existing.getAmount() + 1);
        }

        if (storagePercent <= 0) storageType = null;
    }

    private static boolean isBottleItem(Material m) {
        return m == Material.HONEY_BOTTLE
                || m == Material.POTION
                || m == Material.DRAGON_BREATH;
    }

    private AgingRecipe findRecipe(BeerType inputWine, ItemStack[] ings) {
        for (AgingRecipe r : AgingBarrelManager.getRecipes()) {
            if (r.inputWine() != inputWine) continue;
            if (!r.matchesIngredients(ings)) continue;
            return r;
        }
        return null;
    }

    private boolean outputHasSpace() {
        ItemStack cur = inventory.getItem(WINE_OUTPUT_SLOT);
        if (cur == null || cur.getType() == Material.AIR) return true;
        if (!ItemFactory.isBeer(cur)) return false;
        return cur.getAmount() < MAX_OUTPUT;
    }

    private void updateStorageDisplay() {
        ItemStack pane;
        ItemMeta meta;

        if (storagePercent <= 0 || storageType == null) {
            pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            meta = pane.getItemMeta();
            meta.displayName(ItemFactory.plainName("酒量储存为空", NamedTextColor.RED));
        } else {
            pane = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
            meta = pane.getItemMeta();
            meta.displayName(ItemFactory.plainName(
                    storageType.getDisplayName() + "：" + storagePercent + "%",
                    NamedTextColor.GOLD));
        }
        pane.setItemMeta(meta);
        inventory.setItem(WINE_STORAGE_SLOT, pane);
    }

    private void updateHeads() {
        inventory.setItem(HEAD_1_SLOT, ItemFactory.createTimerHeadIdle());

        if (agingRemaining > 0) {
            inventory.setItem(HEAD_2_SLOT, ItemFactory.createTimerHead2(agingRemaining));
        } else {
            inventory.setItem(HEAD_2_SLOT, ItemFactory.createTimerHeadIdle2());
        }
    }

    private AgingRecipe findCurrentRecipe() {
        ItemStack[] ings = new ItemStack[]{
                inventory.getItem(INGREDIENT_1_SLOT),
                inventory.getItem(INGREDIENT_2_SLOT),
                inventory.getItem(INGREDIENT_3_SLOT)
        };
        for (AgingRecipe r : AgingBarrelManager.getRecipes()) {
            if (r.inputWine() != storageType) continue;
            if (!r.matchesIngredients(ings)) continue;
            return r;
        }
        return null;
    }

    private boolean hasGlassBottle() {
        ItemStack glass = inventory.getItem(GLASS_INPUT_SLOT);
        return glass != null && glass.getType() == Material.GLASS_BOTTLE;
    }

    private Player getViewer() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getOpenInventory().getTopInventory().getHolder() == this) {
                return p;
            }
        }
        return null;
    }

    public Block getBlock() { return block; }
    public BeerType getStorageType() { return storageType; }
    public int getStoragePercent() { return storagePercent; }
    public int getAgingRemaining() { return agingRemaining; }

    public void setStorageState(BeerType type, int percent) {
        this.storageType = type;
        this.storagePercent = Math.max(0, Math.min(WINE_MAX, percent));
    }

    public void setAgingRemaining(int ticks) {
        this.agingRemaining = Math.max(0, ticks);
    }

    public boolean isInputSlot(int slot) {
        return slot == WINE_INPUT_SLOT
                || slot == INGREDIENT_1_SLOT
                || slot == INGREDIENT_2_SLOT
                || slot == INGREDIENT_3_SLOT
                || slot == GLASS_INPUT_SLOT;
    }

    public boolean isOutputSlot(int slot) {
        return slot == WINE_OUTPUT_SLOT;
    }

    public boolean isHeadSlot(int slot) {
        return slot == HEAD_1_SLOT || slot == HEAD_2_SLOT;
    }

    public boolean isStorageDisplaySlot(int slot) {
        return slot == WINE_STORAGE_SLOT;
    }

    public ItemStack[] getContents() {
        ItemStack[] arr = new ItemStack[SIZE];
        for (int i = 0; i < SIZE; i++) arr[i] = inventory.getItem(i);
        return arr;
    }

    public void setContents(ItemStack[] arr) {
        if (arr == null) return;
        for (int i = 0; i < arr.length && i < SIZE; i++) {
            if (isHeadSlot(i) || isStorageDisplaySlot(i)) continue;
            inventory.setItem(i, arr[i]);
        }
    }

    @Override
    public Inventory getInventory() { return inventory; }
}