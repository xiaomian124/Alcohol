package com.xiaomian124.alcohol.aging;

import com.xiaomian124.alcohol.BeerType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record AgingRecipe(
        BeerType inputWine,
        List<ItemStack> ingredients,
        BeerType outputWine,
        int wineCostPercent
) {

    public boolean matchesIngredients(ItemStack[] slots) {
        if (slots.length != ingredients.size()) return false;
        boolean[] used = new boolean[slots.length];

        for (ItemStack req : ingredients) {
            if (req == null || req.getType() == Material.AIR) continue;
            boolean found = false;
            for (int i = 0; i < slots.length; i++) {
                if (used[i]) continue;
                if (similar(req, slots[i])) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        for (int i = 0; i < slots.length; i++) {
            if (used[i]) continue;
            if (slots[i] != null && slots[i].getType() != Material.AIR) return false;
        }
        return true;
    }

    private static boolean similar(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getType() != b.getType()) return false;
        var an = a.hasItemMeta() ? a.getItemMeta().displayName() : null;
        var bn = b.hasItemMeta() ? b.getItemMeta().displayName() : null;
        return java.util.Objects.equals(an, bn);
    }
}