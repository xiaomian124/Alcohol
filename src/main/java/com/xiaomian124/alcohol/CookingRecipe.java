package com.xiaomian124.alcohol;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CookingRecipe(
        List<ItemStack> inputs,
        ItemStack output,
        int cookTicks
) {

    public boolean matches(List<ItemStack> actualInputs) {
        List<ItemStack> actual = new ArrayList<>();
        for (ItemStack s : actualInputs) {
            if (s != null && s.getType() != Material.AIR) actual.add(s);
        }
        if (actual.size() != inputs.size()) return false;

        boolean[] used = new boolean[actual.size()];
        for (ItemStack req : inputs) {
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