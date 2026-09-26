package com.xiaomian124.alcohol.cooking;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.CookingRecipe;
import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.RecipeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CookingPotGUI implements InventoryHolder {

    public static final int SIZE = 45;
    public static final int[] INPUT_SLOTS = {10, 11, 12, 19, 20, 21};
    public static final int TIMER_SLOT     = 14;
    public static final int OUTPUT_SLOT    = 16;
    public static final int HEAT_IND_SLOT  = 32;
    public static final int WATER_IND_SLOT = 34;

    public static final int MAX_OUTPUT = 64;

    private static final int SOUND_COOLDOWN = 20;

    private final AlcoholPlugin plugin;
    private final Block block;
    private final Inventory inventory;

    private int remainingTicks = 0;
    private CookingRecipe activeRecipe = null;

    private boolean lastTickHadHeat = false;
    private int crackleSoundCooldown = 0;

    private final List<ItemStack> outputQueue = new ArrayList<>();
    private int lastDisplayedCount = 0;

    public CookingPotGUI(AlcoholPlugin plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
        this.inventory = Bukkit.createInventory(
                this, SIZE,
                Component.text("烹饪锅").color(NamedTextColor.DARK_GRAY));
        fillBackground();
        refreshOutputSlot();
        updateTimerSlot();
        updateHeatIndicator();
        updateWaterIndicator();
        restoreFromManager();
    }

    private void fillBackground() {
        ItemStack filler = makePane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, filler);
        for (int s : INPUT_SLOTS) inventory.setItem(s, null);
        inventory.setItem(OUTPUT_SLOT, null);
    }

    private static ItemStack makePane(Material mat, String name) {
        ItemStack pane = new ItemStack(mat);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(ItemFactory.plainName(name, NamedTextColor.GRAY));
        pane.setItemMeta(meta);
        return pane;
    }

    private static ItemStack makePane(Material mat, String name, NamedTextColor color) {
        ItemStack pane = new ItemStack(mat);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(ItemFactory.plainName(name, color));
        pane.setItemMeta(meta);
        return pane;
    }

    private void syncToManager() {
        ItemStack[] items = new ItemStack[SIZE];
        for (int i = 0; i < SIZE; i++) items[i] = inventory.getItem(i);
        CookingPotManager.saveContents(block.getLocation(), items);
    }

    private void restoreFromManager() {
        ItemStack[] saved = CookingPotManager.loadContents(block.getLocation());
        if (saved == null) return;
        for (int i = 0; i < saved.length && i < SIZE; i++) {
            if (i == OUTPUT_SLOT) continue;
            if (saved[i] != null && saved[i].getType() != Material.AIR) {
                inventory.setItem(i, saved[i]);
            }
        }
    }

    public void tick() {
        try {
            tickInternal();
        } finally {
            syncToManager();
        }
    }

    private void tickInternal() {
        if (block.getType() != Material.CAULDRON
                && block.getType() != Material.WATER_CAULDRON) return;

        boolean hasHeatNow = hasHeat();
        if (activeRecipe != null && lastTickHadHeat && !hasHeatNow) {
            block.getWorld().playSound(block.getLocation(),
                    Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f);
        }
        lastTickHadHeat = hasHeatNow;

        List<ItemStack> inputs = new ArrayList<>();
        for (int s : INPUT_SLOTS) {
            ItemStack item = inventory.getItem(s);
            if (item != null && item.getType() != Material.AIR) inputs.add(item);
        }

        CookingRecipe matched = RecipeManager.findCookingRecipe(inputs);

        if (matched == null) {
            remainingTicks = 0;
            activeRecipe = null;
            refreshIndicators();
            return;
        }

        if (!hasHeatNow || !hasWater()) {
            refreshIndicators();
            return;
        }

        if (activeRecipe == null && outputQueue.size() >= MAX_OUTPUT) {
            refreshIndicators();
            return;
        }

        if (activeRecipe == null || !activeRecipe.equals(matched)) {
            activeRecipe = matched;
            remainingTicks = matched.cookTicks();
        }

        remainingTicks--;
        if (remainingTicks <= 0) {
            completeCooking(matched);
            remainingTicks = 0;
            activeRecipe = null;
            refreshIndicators();
            return;
        }

        spawnCookingParticles();
        playCrackleSound();
        refreshIndicators();
    }

    private void completeCooking(CookingRecipe recipe) {
        int currentWater = CookingPotManager.getWater(block.getLocation());
        if (currentWater > 0) {
            int newWater = currentWater - 1;
            CookingPotManager.setWater(block.getLocation(), newWater);
            CookingPotManager.updateCauldronBlock(block, newWater);
        }

        consumeOneSet(recipe);
        outputQueue.add(recipe.output().clone());
        refreshOutputSlot();
    }

    private void consumeOneSet(CookingRecipe recipe) {
        boolean[] used = new boolean[INPUT_SLOTS.length];
        for (ItemStack req : recipe.inputs()) {
            for (int i = 0; i < INPUT_SLOTS.length; i++) {
                if (used[i]) continue;
                int slot = INPUT_SLOTS[i];
                ItemStack item = inventory.getItem(slot);
                if (item == null || item.getType() == Material.AIR) continue;
                if (similarItem(req, item)) {
                    item.setAmount(item.getAmount() - 1);
                    if (item.getAmount() <= 0) inventory.setItem(slot, null);
                    used[i] = true;
                    break;
                }
            }
        }
    }

    private static boolean similarItem(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getType() != b.getType()) return false;
        Component an = a.hasItemMeta() ? a.getItemMeta().displayName() : null;
        Component bn = b.hasItemMeta() ? b.getItemMeta().displayName() : null;
        return Objects.equals(an, bn);
    }

    private void refreshIndicators() {
        updateTimerSlot();
        updateHeatIndicator();
        updateWaterIndicator();
    }

    private void updateHeatIndicator() {
        if (hasHeat()) {
            inventory.setItem(HEAT_IND_SLOT,
                    makePane(Material.LIME_STAINED_GLASS_PANE,
                            "热源充足", NamedTextColor.YELLOW));
        } else if (activeRecipe != null) {
            inventory.setItem(HEAT_IND_SLOT,
                    makePane(Material.RED_STAINED_GLASS_PANE,
                            "下方缺少热源", NamedTextColor.RED));
        } else {
            inventory.setItem(HEAT_IND_SLOT,
                    makePane(Material.GRAY_STAINED_GLASS_PANE,
                            "下方缺少热源", NamedTextColor.RED));
        }
    }

    private void updateWaterIndicator() {
        int water = CookingPotManager.getWater(block.getLocation());
        if (water > 0) {
            inventory.setItem(WATER_IND_SLOT,
                    makePane(Material.LIME_STAINED_GLASS_PANE,
                            "储水量 " + water + "/3", NamedTextColor.YELLOW));
        } else if (activeRecipe != null) {
            inventory.setItem(WATER_IND_SLOT,
                    makePane(Material.RED_STAINED_GLASS_PANE,
                            "储水量 0/3", NamedTextColor.RED));
        } else {
            inventory.setItem(WATER_IND_SLOT,
                    makePane(Material.GRAY_STAINED_GLASS_PANE,
                            "储水量 0/3", NamedTextColor.RED));
        }
    }

    private void refreshOutputSlot() {
        if (outputQueue.isEmpty()) {
            lastDisplayedCount = 0;
            inventory.setItem(OUTPUT_SLOT, null);
            return;
        }
        int displayCount = Math.min(MAX_OUTPUT, outputQueue.size());
        lastDisplayedCount = displayCount;

        ItemStack item = outputQueue.get(0).clone();
        item.setAmount(displayCount);
        inventory.setItem(OUTPUT_SLOT, item);
    }

    public void syncFromInventory() {
        ItemStack item = inventory.getItem(OUTPUT_SLOT);
        int newDisplayed = (item == null || item.getType() == Material.AIR)
                ? 0 : item.getAmount();

        int removed = lastDisplayedCount - newDisplayed;
        if (removed <= 0) {
            refreshOutputSlot();
            return;
        }
        for (int i = 0; i < removed && !outputQueue.isEmpty(); i++) {
            outputQueue.remove(0);
        }
        refreshOutputSlot();
    }

    public List<ItemStack> getOutputs() { return new ArrayList<>(outputQueue); }
    public void setOutputs(List<ItemStack> items) {
        outputQueue.clear();
        if (items != null) outputQueue.addAll(items);
        refreshOutputSlot();
    }

    private void updateTimerSlot() {
        if (activeRecipe != null && remainingTicks > 0) {
            inventory.setItem(TIMER_SLOT, ItemFactory.createTimerHead(remainingTicks));
        } else {
            inventory.setItem(TIMER_SLOT, ItemFactory.createTimerHeadIdle());
        }
    }

    private void spawnCookingParticles() {
        Location loc = block.getLocation().add(0.5, 1.2, 0.5);
        block.getWorld().spawnParticle(
                Particle.CAMPFIRE_COSY_SMOKE, loc, 3, 0.2, 0.2, 0.2, 0.01);
    }

    private void playCrackleSound() {
        if (crackleSoundCooldown > 0) { crackleSoundCooldown--; return; }
        crackleSoundCooldown = SOUND_COOLDOWN;
        block.getWorld().playSound(block.getLocation(),
                Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.6f, 1.0f);
    }

    private boolean hasHeat() {
        Block below = block.getRelative(BlockFace.DOWN);
        Material m = below.getType();
        return m == Material.CAMPFIRE || m == Material.SOUL_CAMPFIRE
                || m == Material.FIRE || m == Material.SOUL_FIRE;
    }

    private boolean hasWater() {
        return CookingPotManager.getWater(block.getLocation()) > 0;
    }

    public Block getBlock() { return block; }
    public int getRemainingTicks() { return remainingTicks; }
    public void setRemainingTicks(int ticks) { this.remainingTicks = ticks; }
    public boolean isInputSlot(int slot) {
        for (int s : INPUT_SLOTS) if (s == slot) return true;
        return false;
    }

    @Override
    public Inventory getInventory() { return inventory; }
}