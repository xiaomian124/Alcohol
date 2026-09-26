package com.xiaomian124.alcohol.gui;

import com.xiaomian124.alcohol.recipe.MachineType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public final class GuiPageCache {

    private GuiPageCache() {}

    private static final NamespacedKey RECIPE_KEY =
            new NamespacedKey("alcohol", "recipe_page");
    private static final NamespacedKey GIVE_KEY =
            new NamespacedKey("alcohol", "give_page");

    private static NamespacedKey machineKey(MachineType machine) {
        return new NamespacedKey("alcohol",
                "machine_page_" + machine.name().toLowerCase());
    }

    public static int getRecipePage(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(RECIPE_KEY, PersistentDataType.INTEGER, 0);
    }

    public static void setRecipePage(Player player, int page) {
        player.getPersistentDataContainer().set(
                RECIPE_KEY, PersistentDataType.INTEGER, Math.max(0, page));
    }

    public static int getGivePage(Player player) {
        return player.getPersistentDataContainer()
                .getOrDefault(GIVE_KEY, PersistentDataType.INTEGER, 0);
    }

    public static void setGivePage(Player player, int page) {
        player.getPersistentDataContainer().set(
                GIVE_KEY, PersistentDataType.INTEGER, Math.max(0, page));
    }

    public static int getMachinePage(Player player, MachineType machine) {
        if (player == null || machine == null) return 0;
        return player.getPersistentDataContainer()
                .getOrDefault(machineKey(machine), PersistentDataType.INTEGER, 0);
    }

    public static void setMachinePage(Player player, MachineType machine, int page) {
        if (player == null || machine == null) return;
        player.getPersistentDataContainer().set(
                machineKey(machine), PersistentDataType.INTEGER, Math.max(0, page));
    }
}