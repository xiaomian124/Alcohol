package com.xiaomian124.alcohol;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BrewingRecipe(
        List<ItemStack> ingredients,
        ItemStack output,
        int fuelCost,
        int targetPressure,
        int brewTicks
) {

    public boolean matches(List<ItemStack> actualIngredients) {
        List<ItemStack> actual = new ArrayList<>();
        for (ItemStack s : actualIngredients) {
            if (s != null && s.getType() != Material.AIR) actual.add(s);
        }
        if (actual.size() != ingredients.size()) return false;

        boolean[] used = new boolean[actual.size()];
        for (ItemStack req : ingredients) {
            boolean found = false;
            for (int i = 0; i < actual.size(); i++) {
                if (!used[i] && similar(req, actual.get(i))) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private static boolean similar(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getType() != b.getType()) return false;
        Component an = a.hasItemMeta() ? a.getItemMeta().displayName() : null;
        Component bn = b.hasItemMeta() ? b.getItemMeta().displayName() : null;
        return Objects.equals(an, bn);
    }
}