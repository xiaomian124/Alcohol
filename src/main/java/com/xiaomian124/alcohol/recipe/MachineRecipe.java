package com.xiaomian124.alcohol.recipe;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public record MachineRecipe(String name, Map<Integer, ItemStack> slots) {}