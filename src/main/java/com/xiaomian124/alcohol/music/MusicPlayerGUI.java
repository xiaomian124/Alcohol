package com.xiaomian124.alcohol.music;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MusicPlayerGUI implements InventoryHolder {

    public static final int SIZE = 45;

    public static final int DISC_SLOT   = 13;
    public static final int PREV_SLOT   = 29;
    public static final int PLAY_SLOT   = 31;
    public static final int NEXT_SLOT   = 33;

    private final AlcoholPlugin plugin;
    private final Location location;
    private final Inventory inventory;

    public boolean showingLoopMode = false;

    public MusicPlayerGUI(AlcoholPlugin plugin, Location loc) {
        this.plugin = plugin;
        this.location = loc.clone();
        this.inventory = Bukkit.createInventory(this, SIZE,
                Component.text("吧台音乐播放器").color(NamedTextColor.DARK_GRAY));
        refresh();
    }

    public void refresh() {
        MusicPlayer mp = MusicPlayerManager.getOrCreate(location);

        ItemStack black = pane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, black);

        ItemStack gray = pane(Material.GRAY_STAINED_GLASS_PANE, " ");
        int[] graySlots = {3, 4, 5, 12, 14, 21, 22, 23};
        for (int s : graySlots) inventory.setItem(s, gray);

        inventory.setItem(DISC_SLOT, buildDiscItem(mp));

        inventory.setItem(PREV_SLOT, ItemFactory.createPrevHead());
        inventory.setItem(PLAY_SLOT, ItemFactory.createPlayPauseHead(mp.playing));
        inventory.setItem(NEXT_SLOT, ItemFactory.createNextHead());
    }

    private ItemStack buildDiscItem(MusicPlayer mp) {
        MusicDisc disc = MusicPlayerManager.DISCS.isEmpty()
                ? null : MusicPlayerManager.DISCS.get(mp.currentIndex);
        Material icon = disc != null ? disc.icon : Material.MUSIC_DISC_13;

        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        String title = MusicPlayerManager.getTitle(mp);
        String author = MusicPlayerManager.getAuthor(mp);

        if (showingLoopMode) {
            String mode = mp.singleLoop ? "单曲" : "列表";
            meta.displayName(ItemFactory.plainName(title + " | " + mode,
                    NamedTextColor.GRAY));
        } else {
            meta.displayName(ItemFactory.plainName(title, NamedTextColor.YELLOW));
        }

        List<Component> lore = List.of(
                ItemFactory.plainName(author, NamedTextColor.GRAY),
                Component.empty(),
                ItemFactory.plainName("点击切换循环模式", NamedTextColor.DARK_GRAY)
        );
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack pane(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ItemFactory.plainName(name, NamedTextColor.GRAY));
        item.setItemMeta(meta);
        return item;
    }

    public Location getLocation() { return location; }
    public Inventory getInventory() { return inventory; }
}