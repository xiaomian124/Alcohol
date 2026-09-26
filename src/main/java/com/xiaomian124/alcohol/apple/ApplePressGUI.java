package com.xiaomian124.alcohol.apple;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ApplePressGUI implements InventoryHolder {

    public static final int SIZE = 45;

    public static final int JUICE_OUTPUT_SLOT = 16;
    public static final int APPLE_INPUT_SLOT  = 19;
    public static final int HEAD_1_SLOT       = 21;
    public static final int MASH_SLOT         = 23;
    public static final int HEAD_2_SLOT       = 25;
    public static final int GLASS_INPUT_SLOT  = 34;

    public static final int PRESS_TICKS = 600;

    public static final int MAX_OUTPUT = 64;

    private final AlcoholPlugin plugin;
    private final Block block;
    private final Inventory inventory;

    private int mashRemaining = 0;
    private int juiceRemaining = 0;

    public ApplePressGUI(AlcoholPlugin plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
        this.inventory = Bukkit.createInventory(this, SIZE,
                Component.text("苹果压榨器").color(NamedTextColor.DARK_GRAY));
        fillBackground();
        updateHeads();
        restoreFromManager();
    }

    private void fillBackground() {
        ItemStack filler = makePane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, filler);
        inventory.setItem(APPLE_INPUT_SLOT, null);
        inventory.setItem(GLASS_INPUT_SLOT, null);
        inventory.setItem(JUICE_OUTPUT_SLOT, null);
        inventory.setItem(MASH_SLOT, null);
    }

    private static ItemStack makePane(Material mat, String name) {
        ItemStack pane = new ItemStack(mat);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(ItemFactory.plainName(name, NamedTextColor.GRAY));
        pane.setItemMeta(meta);
        return pane;
    }

    private void syncToManager() {
        ApplePressManager.saveState(block.getLocation(),
                getContents(), mashRemaining, juiceRemaining);
    }

    private void restoreFromManager() {
        ApplePressManager.SavedState state =
                ApplePressManager.loadState(block.getLocation());
        if (state == null) return;
        setContents(state.items);
        this.mashRemaining = state.mashRemaining;
        this.juiceRemaining = state.juiceRemaining;
        updateHeads();
    }

    public void tick() {
        if (block.getType() != Material.LOOM) return;
        try {
            tickMash();
            tickJuice();
            updateHeads();
        } finally {
            syncToManager();
        }
    }

    private void tickMash() {
        ItemStack apple = inventory.getItem(APPLE_INPUT_SLOT);
        boolean hasApple = apple != null
                && apple.getType() == Material.APPLE
                && apple.getAmount() > 0;

        boolean mashHasSpace = outputHasSpace(MASH_SLOT, ItemFactory::isAppleMash);

        if (!hasApple || !mashHasSpace) {
            mashRemaining = 0;
            return;
        }

        if (mashRemaining <= 0) {
            mashRemaining = PRESS_TICKS;
            return;
        }

        mashRemaining--;
        if (mashRemaining <= 0) {
            completeMash();
        }
    }

    private void completeMash() {
        ItemStack apple = inventory.getItem(APPLE_INPUT_SLOT);
        if (apple != null) {
            apple.setAmount(apple.getAmount() - 1);
            if (apple.getAmount() <= 0) inventory.setItem(APPLE_INPUT_SLOT, null);
        }
        addToOutput(MASH_SLOT, ItemFactory::isAppleMash, ItemFactory::createAppleMash);
    }

    private void tickJuice() {
        ItemStack mash = inventory.getItem(MASH_SLOT);
        ItemStack glass = inventory.getItem(GLASS_INPUT_SLOT);

        boolean hasMash = mash != null
                && ItemFactory.isAppleMash(mash)
                && mash.getAmount() > 0;
        boolean hasGlass = glass != null
                && glass.getType() == Material.GLASS_BOTTLE
                && glass.getAmount() > 0;
        boolean juiceHasSpace = outputHasSpace(JUICE_OUTPUT_SLOT,
                ItemFactory::isAppleJuice);

        if (!hasMash || !hasGlass || !juiceHasSpace) {
            juiceRemaining = 0;
            return;
        }

        if (juiceRemaining <= 0) {
            juiceRemaining = PRESS_TICKS;
            return;
        }

        juiceRemaining--;
        if (juiceRemaining <= 0) {
            completeJuice();
        }
    }

    private void completeJuice() {
        ItemStack mash = inventory.getItem(MASH_SLOT);
        if (mash != null && ItemFactory.isAppleMash(mash)) {
            mash.setAmount(mash.getAmount() - 1);
            if (mash.getAmount() <= 0) inventory.setItem(MASH_SLOT, null);
        }
        ItemStack glass = inventory.getItem(GLASS_INPUT_SLOT);
        if (glass != null && glass.getType() == Material.GLASS_BOTTLE) {
            glass.setAmount(glass.getAmount() - 1);
            if (glass.getAmount() <= 0) inventory.setItem(GLASS_INPUT_SLOT, null);
        }
        addToOutput(JUICE_OUTPUT_SLOT, ItemFactory::isAppleJuice,
                ItemFactory::createAppleJuice);
    }

    private boolean outputHasSpace(int slot,
                                   java.util.function.Predicate<ItemStack> isType) {
        ItemStack cur = inventory.getItem(slot);
        if (cur == null || cur.getType() == Material.AIR) return true;
        if (!isType.test(cur)) return false;
        return cur.getAmount() < MAX_OUTPUT;
    }

    private void addToOutput(int slot,
                             java.util.function.Predicate<ItemStack> isType,
                             java.util.function.Supplier<ItemStack> factory) {
        ItemStack cur = inventory.getItem(slot);
        if (cur == null || cur.getType() == Material.AIR) {
            inventory.setItem(slot, factory.get());
        } else if (isType.test(cur)) {
            if (cur.getAmount() < MAX_OUTPUT) {
                cur.setAmount(cur.getAmount() + 1);
            }
        }
    }

    private void updateHeads() {
        if (mashRemaining > 0) {
            inventory.setItem(HEAD_1_SLOT, ItemFactory.createTimerHead(mashRemaining));
        } else {
            inventory.setItem(HEAD_1_SLOT, ItemFactory.createTimerHeadIdle());
        }
        if (juiceRemaining > 0) {
            inventory.setItem(HEAD_2_SLOT, ItemFactory.createTimerHead2(juiceRemaining));
        } else {
            inventory.setItem(HEAD_2_SLOT, ItemFactory.createTimerHeadIdle2());
        }
    }

    public Block getBlock() { return block; }

    public boolean isInputSlot(int slot) {
        return slot == APPLE_INPUT_SLOT
                || slot == GLASS_INPUT_SLOT
                || slot == MASH_SLOT;
    }

    public boolean isOutputSlot(int slot) {
        return slot == JUICE_OUTPUT_SLOT;
    }

    public boolean isHeadSlot(int slot) {
        return slot == HEAD_1_SLOT || slot == HEAD_2_SLOT;
    }

    public ItemStack[] getContents() {
        ItemStack[] arr = new ItemStack[SIZE];
        for (int i = 0; i < SIZE; i++) arr[i] = inventory.getItem(i);
        return arr;
    }

    public void setContents(ItemStack[] arr) {
        if (arr == null) return;
        for (int i = 0; i < arr.length && i < SIZE; i++) {
            if (isHeadSlot(i)) continue;
            inventory.setItem(i, arr[i]);
        }
    }

    public boolean isMashActive()  { return mashRemaining > 0; }
    public int getMashRemaining()  { return mashRemaining; }
    public boolean isJuiceActive() { return juiceRemaining > 0; }
    public int getJuiceRemaining() { return juiceRemaining; }

    public void setState(int mashRemaining, int juiceRemaining) {
        this.mashRemaining = Math.max(0, mashRemaining);
        this.juiceRemaining = Math.max(0, juiceRemaining);
        updateHeads();
    }

    @Override
    public Inventory getInventory() { return inventory; }
}