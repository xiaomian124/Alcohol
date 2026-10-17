package com.xiaomian124.alcohol.recipe;

import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.LanguageManager;
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

import java.util.List;
import java.util.Map;

public class MachineRecipeGUI implements InventoryHolder {

    public enum Mode { RECIPES, CRAFTING }

    private static final int SIZE = 45;

    private final MachineType machine;
    private final Mode mode;
    private final Inventory inventory;
    private final List<MachineRecipe> recipes;
    private final Player player;

    private int page = 0;

    public static void open(Player player, MachineType machine, Mode mode) {
        player.openInventory(
                new MachineRecipeGUI(machine, mode, player).getInventory());
    }

    public MachineRecipeGUI(MachineType machine, Mode mode, Player player) {
        this.machine = machine;
        this.mode = mode;
        this.player = player;
        this.recipes = RecipeData.getRecipes(player, machine);

        String title = mode == Mode.RECIPES
                ? LanguageManager.getRaw(player, machine.langKey()) + " · "
                  + LanguageManager.getRaw(player, "gui.recipe_mode")
                : LanguageManager.getRaw(player, machine.langKey()) + " · "
                  + LanguageManager.getRaw(player, "gui.craft_mode");
        this.inventory = Bukkit.createInventory(this, SIZE,
                Component.text(title).color(NamedTextColor.DARK_GRAY));

        if (mode == Mode.RECIPES && player != null) {
            this.page = GuiPageCache.getMachinePage(player, machine);
        }

        clampPage();
        refresh();
    }

    private void clampPage() {
        if (mode != Mode.RECIPES) { page = 0; return; }
        if (recipes.isEmpty()) { page = 0; return; }
        if (page >= recipes.size()) page = recipes.size() - 1;
        if (page < 0) page = 0;
    }

    public void refresh() {
        ItemStack black = pane(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, black);

        if (mode == Mode.CRAFTING) {
            Map<Integer, ItemStack> m = RecipeData.getCraftingRecipe(player, machine);
            int[] mapping = {10, 11, 12, 19, 20, 21, 28, 29, 30};
            for (Map.Entry<Integer, ItemStack> e : m.entrySet()) {
                inventory.setItem(mapping[e.getKey()], e.getValue());
            }
            return;
        }

        if (recipes.isEmpty()) return;

        MachineRecipe recipe = recipes.get(page);
        for (Map.Entry<Integer, ItemStack> e : recipe.slots().entrySet()) {
            inventory.setItem(e.getKey(), e.getValue());
        }

        if (page > 0) inventory.setItem(36, ItemFactory.createPrevHead(player));
        if (page < recipes.size() - 1) {
            inventory.setItem(44, ItemFactory.createNextHead(player));
        }

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta meta = info.getItemMeta();
        meta.displayName(ItemFactory.plainName(
                LanguageManager.getRaw(player, recipe.langKey()), NamedTextColor.GOLD));
        meta.lore(List.of(
                ItemFactory.plainName(
                        LanguageManager.getRaw(player, "gui.recipe_index",
                                "page", String.valueOf(page + 1),
                                "total", String.valueOf(recipes.size())),
                        NamedTextColor.GRAY)
        ));
        info.setItemMeta(meta);
        inventory.setItem(40, info);
    }

    private static ItemStack pane(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ItemFactory.plainName(name, NamedTextColor.GRAY));
        item.setItemMeta(meta);
        return item;
    }

    public boolean prev() {
        if (mode != Mode.RECIPES) return false;
        if (page <= 0) return false;
        page--;
        savePage();
        refresh();
        return true;
    }

    public boolean next() {
        if (mode != Mode.RECIPES) return false;
        if (page >= recipes.size() - 1) return false;
        page++;
        savePage();
        refresh();
        return true;
    }

    private void savePage() {
        if (mode == Mode.RECIPES && player != null) {
            GuiPageCache.setMachinePage(player, machine, page);
        }
    }

    public MachineType getMachine() { return machine; }
    public int getPage() { return page; }

    @Override
    public Inventory getInventory() { return inventory; }
}