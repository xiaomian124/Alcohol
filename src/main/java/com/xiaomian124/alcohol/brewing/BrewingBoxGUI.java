package com.xiaomian124.alcohol.brewing;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.BeerQuality;
import com.xiaomian124.alcohol.BeerType;
import com.xiaomian124.alcohol.BrewingRecipe;
import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.RecipeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BrewingBoxGUI implements InventoryHolder {

    public static final int SIZE = 45;
    public static final int[] INGREDIENT_SLOTS = {10, 11, 12};
    public static final int TIMER_SLOT         = 14;
    public static final int OUTPUT_SLOT        = 16;
    public static final int WATER_IND_SLOT     = 28;
    public static final int WATER_SLOT         = 29;
    public static final int FUEL_IND_SLOT      = 31;
    public static final int FUEL_SLOT          = 32;
    public static final int PRESSURE_SLOT      = 34;

    public static final int MAX_OUTPUT = 64;

    private static final int PRESSURE_MAX            = 100;
    private static final int PRESSURE_INTERVAL       = 5;
    private static final int VENT_TARGET             = 20;
    private static final int VENT_SPEED              = 2;
    private static final int OVER_PRESSURE_THRESHOLD = 200;
    private static final int SOUND_COOLDOWN          = 20;

    private final AlcoholPlugin plugin;
    private final Block block;
    private final Inventory inventory;

    private BrewingRecipe activeRecipe = null;
    private int remainingTicks = 0;
    private int pressure = 0;
    private int pressureAccumulator = 0;
    private int ventingTicks = 0;

    private final List<ItemStack> pendingOutputs = new ArrayList<>();
    private int lastDisplayedCount = 0;

    private int qualityPenalty = 0;
    private int overPressureTicks = 0;

    private int fuelBurnTicks = 0;
    private int fuelConsumed = 0;

    private int ambientSoundCooldown = 0;
    private int extinguishSoundCooldown = 0;

    public BrewingBoxGUI(AlcoholPlugin plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
        this.inventory = Bukkit.createInventory(this, SIZE,
                Component.text("酿造炉").color(NamedTextColor.DARK_GRAY));
        fillBackground();
        refreshOutputSlot();
        updateTimerSlot();
        updateWaterIndicator();
        updateFuelIndicator();
        updatePressureSlot();
    }

    private void fillBackground() {
        ItemStack filler = makePane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, filler);
        for (int s : INGREDIENT_SLOTS) inventory.setItem(s, null);
        inventory.setItem(WATER_SLOT, null);
        inventory.setItem(FUEL_SLOT, null);
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

    public void tick() {
        if (block.getType() != Material.SMOKER) return;

        if (activeRecipe != null) {
            List<ItemStack> current = new ArrayList<>();
            for (int s : INGREDIENT_SLOTS) {
                ItemStack item = inventory.getItem(s);
                if (item != null && item.getType() != Material.AIR) current.add(item);
            }
            if (!activeRecipe.matches(current)) {
                cancelTask();
                refreshOutputSlot();
                updateTimerSlot();
                updateWaterIndicator();
                updateFuelIndicator();
                updatePressureSlot();
                return;
            }
        }

        updateWaterIndicator();
        updateFuelIndicator();
        updatePressureSlot();

        if (ventingTicks > 0) {
            ventingTicks--;
            spawnVentParticles();
            playExtinguishSound();
            if (pressure > VENT_TARGET) {
                pressure = Math.max(VENT_TARGET, pressure - VENT_SPEED);
            }
            if (ventingTicks == 0 || pressure <= VENT_TARGET) {
                pressure = VENT_TARGET;
                ventingTicks = 0;
                pressureAccumulator = 0;
                overPressureTicks = 0;
            }
            updatePressureSlot();
            updateTimerSlot();
            return;
        }

        if (activeRecipe == null) {
            tryStartBrewing();
            updateTimerSlot();
            return;
        }

        if (!hasWater()) {
            updateTimerSlot();
            return;
        }

        if (pressure >= PRESSURE_MAX) {
            spawnAngryVillagerParticles();
            playAmbientSound();
            overPressureTicks++;
            if (overPressureTicks >= OVER_PRESSURE_THRESHOLD) {
                overPressureTicks = 0;
                qualityPenalty++;
                if (qualityPenalty > BeerQuality.values().length) {
                    qualityPenalty = BeerQuality.values().length;
                }
                block.getWorld().playSound(block.getLocation(),
                        Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f);
                startVenting();
            }
            updateTimerSlot();
            return;
        }

        if (activeRecipe.fuelCost() > 0
                && fuelConsumed < activeRecipe.fuelCost()) {
            if (fuelBurnTicks > 0) {
                fuelBurnTicks--;
            } else {
                if (!tryConsumeNextFuel()) {
                    updateTimerSlot();
                    return;
                }
                fuelBurnTicks--;
            }
        }

        pressureAccumulator++;
        if (pressureAccumulator >= PRESSURE_INTERVAL) {
            pressureAccumulator = 0;
            if (pressure < PRESSURE_MAX) pressure++;
        }
        if (pressure >= PRESSURE_MAX) {
            spawnAngryVillagerParticles();
            playAmbientSound();
            updateTimerSlot();
            return;
        }

        remainingTicks--;
        if (remainingTicks <= 0) {
            completeBrewing();
        }
        updateTimerSlot();
    }

    private boolean tryConsumeNextFuel() {
        if (activeRecipe == null) return false;
        ItemStack fuel = inventory.getItem(FUEL_SLOT);
        if (fuel == null || !isFuel(fuel)) return false;

        fuel.setAmount(fuel.getAmount() - 1);
        if (fuel.getAmount() <= 0) inventory.setItem(FUEL_SLOT, null);

        fuelConsumed++;
        fuelBurnTicks = Math.max(1, activeRecipe.brewTicks() / activeRecipe.fuelCost());
        return true;
    }

    private void tryStartBrewing() {
        if (pendingOutputs.size() >= MAX_OUTPUT) return;

        List<ItemStack> ingredients = new ArrayList<>();
        for (int s : INGREDIENT_SLOTS) {
            ItemStack item = inventory.getItem(s);
            if (item != null && item.getType() != Material.AIR) ingredients.add(item);
        }
        if (ingredients.isEmpty()) return;

        BrewingRecipe recipe = RecipeManager.findBrewingRecipe(ingredients);
        if (recipe == null) return;
        if (!hasWater()) return;

        ItemStack fuel = inventory.getItem(FUEL_SLOT);
        if (fuel == null || !isFuel(fuel)) return;

        activeRecipe = recipe;
        remainingTicks = recipe.brewTicks();
        pressure = 0;
        pressureAccumulator = 0;
        qualityPenalty = 0;
        overPressureTicks = 0;
        fuelBurnTicks = 0;
        fuelConsumed = 0;
    }

    private void completeBrewing() {
        ItemStack water = inventory.getItem(WATER_SLOT);
        if (water != null && water.getType() == Material.WATER_BUCKET) {
            inventory.setItem(WATER_SLOT, new ItemStack(Material.BUCKET));
        }

        consumeOneSet(activeRecipe);

        BeerType type = ItemFactory.getBeerType(activeRecipe.output());
        if (type == null) type = BeerType.WHEAT_BEER;
        BeerQuality quality = BeerQuality.fromPenalty(qualityPenalty);
        pendingOutputs.add(ItemFactory.createBeer(type, quality));

        activeRecipe = null;
        remainingTicks = 0;
        pressure = 0;
        pressureAccumulator = 0;
        qualityPenalty = 0;
        overPressureTicks = 0;
        fuelBurnTicks = 0;
        fuelConsumed = 0;

        refreshOutputSlot();
    }

    private void consumeOneSet(BrewingRecipe recipe) {
        boolean[] used = new boolean[INGREDIENT_SLOTS.length];
        for (ItemStack req : recipe.ingredients()) {
            for (int i = 0; i < INGREDIENT_SLOTS.length; i++) {
                if (used[i]) continue;
                int slot = INGREDIENT_SLOTS[i];
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

    private void refreshOutputSlot() {
        if (pendingOutputs.isEmpty()) {
            lastDisplayedCount = 0;
            inventory.setItem(OUTPUT_SLOT, null);
            return;
        }

        ItemStack first = pendingOutputs.get(0);
        BeerType firstType = ItemFactory.getBeerType(first);
        BeerQuality firstQuality = ItemFactory.getBeerQuality(first);
        if (firstType == null) firstType = BeerType.WHEAT_BEER;
        if (firstQuality == null) firstQuality = BeerQuality.HIGH;

        int displayCount = Math.min(MAX_OUTPUT, pendingOutputs.size());
        lastDisplayedCount = displayCount;

        ItemStack brown = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
        brown.setAmount(displayCount);
        ItemMeta meta = brown.getItemMeta();

        Component name = Component.text(firstType.getDisplayName())
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(" - ")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false))
                .append(Component.text(firstQuality.getLabel())
                        .color(firstQuality.getColor())
                        .decoration(TextDecoration.ITALIC, false));
        meta.displayName(name);

        brown.setItemMeta(meta);
        inventory.setItem(OUTPUT_SLOT, brown);
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

        for (int i = 0; i < removed && !pendingOutputs.isEmpty(); i++) {
            pendingOutputs.remove(0);
        }
        refreshOutputSlot();
    }

    public ItemStack collectBrew() {
        if (pendingOutputs.isEmpty()) return null;
        ItemStack result = pendingOutputs.remove(0).clone();
        refreshOutputSlot();
        updateTimerSlot();
        updatePressureSlot();
        return result;
    }

    public void vent() {
        if (ventingTicks > 0) return;
        if (pressure < PRESSURE_MAX) return;
        startVenting();
    }

    private void startVenting() {
        ventingTicks = (PRESSURE_MAX - VENT_TARGET) / VENT_SPEED;
        if (ventingTicks <= 0) ventingTicks = 1;
    }

    private void cancelTask() {
        activeRecipe = null;
        remainingTicks = 0;
        pressure = 0;
        pressureAccumulator = 0;
        qualityPenalty = 0;
        overPressureTicks = 0;
        ventingTicks = 0;
        fuelBurnTicks = 0;
        fuelConsumed = 0;
    }

    private void updateWaterIndicator() {
        if (hasWater()) {
            inventory.setItem(WATER_IND_SLOT,
                    makePane(Material.LIME_STAINED_GLASS_PANE,
                            "水量充足", NamedTextColor.YELLOW));
        } else if (activeRecipe != null) {
            inventory.setItem(WATER_IND_SLOT,
                    makePane(Material.RED_STAINED_GLASS_PANE,
                            "缺少水量", NamedTextColor.RED));
        } else {
            inventory.setItem(WATER_IND_SLOT,
                    makePane(Material.GRAY_STAINED_GLASS_PANE,
                            "缺少水量", NamedTextColor.RED));
        }
    }

    private void updateFuelIndicator() {
        boolean hasFuel = fuelBurnTicks > 0;
        if (!hasFuel) {
            ItemStack fuel = inventory.getItem(FUEL_SLOT);
            hasFuel = fuel != null && isFuel(fuel);
        }
        if (hasFuel) {
            inventory.setItem(FUEL_IND_SLOT,
                    makePane(Material.LIME_STAINED_GLASS_PANE,
                            "燃料充足", NamedTextColor.YELLOW));
        } else if (activeRecipe != null) {
            inventory.setItem(FUEL_IND_SLOT,
                    makePane(Material.RED_STAINED_GLASS_PANE,
                            "缺少燃料", NamedTextColor.RED));
        } else {
            inventory.setItem(FUEL_IND_SLOT,
                    makePane(Material.GRAY_STAINED_GLASS_PANE,
                            "缺少燃料", NamedTextColor.RED));
        }
    }

    private void updateTimerSlot() {
        if (activeRecipe != null && remainingTicks > 0) {
            inventory.setItem(TIMER_SLOT, ItemFactory.createTimerHead(remainingTicks));
        } else {
            inventory.setItem(TIMER_SLOT, ItemFactory.createTimerHeadIdle());
        }
    }

    private void updatePressureSlot() {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        NamedTextColor color = pressure >= PRESSURE_MAX
                ? NamedTextColor.RED : NamedTextColor.YELLOW;
        meta.displayName(ItemFactory.plainName("气压 " + pressure + "%", color));
        clock.setItemMeta(meta);
        inventory.setItem(PRESSURE_SLOT, clock);
    }

    private void spawnAngryVillagerParticles() {
        Location loc = block.getLocation().add(0.5, 1.2, 0.5);
        block.getWorld().spawnParticle(Particle.ANGRY_VILLAGER,
                loc, 3, 0.3, 0.3, 0.3, 0);
    }

    private void spawnVentParticles() {
        Location loc = block.getLocation().add(0.5, 1.2, 0.5);
        block.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                loc, 5, 0.2, 0.2, 0.2, 0.01);
    }

    private void playAmbientSound() {
        if (ambientSoundCooldown > 0) { ambientSoundCooldown--; return; }
        ambientSoundCooldown = SOUND_COOLDOWN;
        block.getWorld().playSound(block.getLocation(),
                Sound.BLOCK_FIRE_AMBIENT, 0.8f, 1.0f);
    }

    private void playExtinguishSound() {
        if (extinguishSoundCooldown > 0) { extinguishSoundCooldown--; return; }
        extinguishSoundCooldown = SOUND_COOLDOWN;
        block.getWorld().playSound(block.getLocation(),
                Sound.BLOCK_FIRE_EXTINGUISH, 0.8f, 1.0f);
    }

    private boolean hasWater() {
        ItemStack water = inventory.getItem(WATER_SLOT);
        return water != null && water.getType() == Material.WATER_BUCKET;
    }

    private static boolean isFuel(ItemStack item) {
        return item.getType() == Material.COAL || item.getType() == Material.CHARCOAL;
    }

    public boolean isBrewing() { return activeRecipe != null; }
    public boolean isBrewed() { return !pendingOutputs.isEmpty(); }
    public int getPendingCount() { return pendingOutputs.size(); }
    public List<ItemStack> getPendingOutputs() { return new ArrayList<>(pendingOutputs); }
    public ItemStack getPendingOutput() {
        return pendingOutputs.isEmpty() ? null : pendingOutputs.get(0);
    }
    public void setPendingOutput(ItemStack item) {
        pendingOutputs.clear();
        if (item != null) pendingOutputs.add(item);
        refreshOutputSlot();
    }
    public void setPendingOutputs(List<ItemStack> items) {
        pendingOutputs.clear();
        if (items != null) pendingOutputs.addAll(items);
        refreshOutputSlot();
    }
    public int getPressure() { return pressure; }
    public int getRemainingTicks() { return remainingTicks; }
    public void setRemainingTicks(int ticks) { this.remainingTicks = ticks; }
    public void setPressure(int p) { this.pressure = Math.max(0, Math.min(PRESSURE_MAX, p)); }
    public boolean isInputSlot(int slot) {
        for (int s : INGREDIENT_SLOTS) if (s == slot) return true;
        return false;
    }
    public Block getBlock() { return block; }

    @Override
    public Inventory getInventory() { return inventory; }
}