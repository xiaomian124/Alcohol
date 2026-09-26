package com.xiaomian124.alcohol.give;

import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class GiveableItem {

    public final String id;
    public final String displayName;
    public final Supplier<ItemStack> factory;

    public GiveableItem(String id, String displayName, Supplier<ItemStack> factory) {
        this.id = id;
        this.displayName = displayName;
        this.factory = factory;
    }

    public ItemStack create() {
        return factory.get();
    }
}