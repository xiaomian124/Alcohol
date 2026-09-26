package com.xiaomian124.alcohol.recipe;

import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.gui.GuiPageCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecipeViewerGUI implements InventoryHolder {

    public static final int SIZE = 54;

    private static final int CONTENT_START = 9;
    private static final int PER_PAGE = 36;
    private static final int PREV_SLOT = 45;
    private static final int NEXT_SLOT = 53;

    private final Inventory inventory;
    private final List<MachineType> machines;
    private final Player player;
    private int page;

    public RecipeViewerGUI(Player player) {
        this.player = player;
        this.machines = new ArrayList<>(List.of(MachineType.values()));
        this.page = GuiPageCache.getRecipePage(player);
        this.inventory = Bukkit.createInventory(this, SIZE,
                Component.text("配方查看").color(NamedTextColor.DARK_GRAY));
        clampPage();
        refresh();
    }

    private void clampPage() {
        int totalPages = (machines.size() + PER_PAGE - 1) / PER_PAGE;
        if (totalPages == 0) totalPages = 1;
        if (page >= totalPages) page = totalPages - 1;
        if (page < 0) page = 0;
    }

    public void refresh() {
        ItemStack black = pane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, black);

        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE; i++) {
            int idx = start + i;
            if (idx >= machines.size()) break;

            int slot = CONTENT_START + i;
            if (slot >= PREV_SLOT) break;

            MachineType machine = machines.get(idx);
            ItemStack icon = new ItemStack(machine.icon);
            ItemMeta meta = icon.getItemMeta();
            meta.displayName(ItemFactory.plainName(
                    machine.displayName, NamedTextColor.GOLD));

            if (machine == MachineType.MUSIC_PLAYER) {
                meta.lore(List.of(
                        ItemFactory.plainName("右键：查看合成方式",
                                NamedTextColor.GRAY)
                ));
            } else {
                meta.lore(List.of(
                        ItemFactory.plainName("左键：查看可制作物品",
                                NamedTextColor.GRAY),
                        ItemFactory.plainName("右键：查看合成方式",
                                NamedTextColor.GRAY)
                ));
            }
            icon.setItemMeta(meta);
            inventory.setItem(slot, icon);
        }

        int totalPages = (machines.size() + PER_PAGE - 1) / PER_PAGE;
        if (page > 0) inventory.setItem(PREV_SLOT, ItemFactory.createPrevHead());
        if (page < totalPages - 1) inventory.setItem(NEXT_SLOT, ItemFactory.createNextHead());
    }

    private static ItemStack pane(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ItemFactory.plainName(name, NamedTextColor.GRAY));
        item.setItemMeta(meta);
        return item;
    }

    public void setPage(int p) {
        this.page = p;
        GuiPageCache.setRecipePage(player, p);
        refresh();
    }

    public int getPage() { return page; }

    public MachineType getMachineAtSlot(int slot) {
        if (slot < CONTENT_START || slot >= PREV_SLOT) return null;
        int index = page * PER_PAGE + (slot - CONTENT_START);
        if (index < 0 || index >= machines.size()) return null;
        return machines.get(index);
    }

    public boolean hasPrev() { return page > 0; }
    public boolean hasNext() {
        int totalPages = (machines.size() + PER_PAGE - 1) / PER_PAGE;
        return page < totalPages - 1;
    }

    @Override
    public Inventory getInventory() { return inventory; }
}