package com.xiaomian124.alcohol.give;

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

public class GiveGUI implements InventoryHolder {

    public static final int SIZE = 54;
    private static final int CONTENT_START = 9;
    private static final int PER_PAGE = 36;
    private static final int PREV_SLOT = 45;
    private static final int NEXT_SLOT = 53;

    private final Inventory inventory;
    private final List<GiveableItem> items;
    private final Player player;
    private int page;

    public GiveGUI(Player player) {
        this.player = player;
        this.items = GiveRegistry.getAll();
        this.page = GuiPageCache.getGivePage(player);
        this.inventory = Bukkit.createInventory(this, SIZE,
                Component.text("获取物品").color(NamedTextColor.DARK_GRAY));
        clampPage();
        refresh();
    }

    private void clampPage() {
        int totalPages = (items.size() + PER_PAGE - 1) / PER_PAGE;
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
            if (idx >= items.size()) break;

            int slot = CONTENT_START + i;
            if (slot >= PREV_SLOT) break;

            GiveableItem item = items.get(idx);
            ItemStack display = item.create();
            display.setAmount(1);

            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                List<Component> lore = meta.lore() == null
                        ? new ArrayList<>() : new ArrayList<>(meta.lore());
                lore.add(Component.empty());
                lore.add(ItemFactory.plainName("ID：" + item.id,
                        NamedTextColor.DARK_GRAY));
                meta.lore(lore);
                display.setItemMeta(meta);
            }

            inventory.setItem(slot, display);
        }

        int totalPages = (items.size() + PER_PAGE - 1) / PER_PAGE;
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
        GuiPageCache.setGivePage(player, p);
        refresh();
    }

    public int getPage() { return page; }

    public GiveableItem getItemAtSlot(int slot) {
        if (slot < CONTENT_START || slot >= PREV_SLOT) return null;
        int index = page * PER_PAGE + (slot - CONTENT_START);
        if (index < 0 || index >= items.size()) return null;
        return items.get(index);
    }

    public boolean hasPrev() { return page > 0; }
    public boolean hasNext() {
        int totalPages = (items.size() + PER_PAGE - 1) / PER_PAGE;
        return page < totalPages - 1;
    }

    @Override
    public Inventory getInventory() { return inventory; }
}