package com.xiaomian124.alcohol.give;

import com.xiaomian124.alcohol.LanguageManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class GiveableItem {

    public final String id;
    public final String langKey;
    public final Function<Player, ItemStack> factory;

    public GiveableItem(String id, String langKey, Function<Player, ItemStack> factory) {
        this.id = id;
        this.langKey = langKey;
        this.factory = factory;
    }

    public String displayName(Player player) {
        return LanguageManager.getRaw(player, langKey);
    }

    @Deprecated
    public String displayName() {
        return LanguageManager.getRaw(null, langKey);
    }

    public ItemStack create(Player player) {
        return factory.apply(player);
    }

    @Deprecated
    public ItemStack create() {
        return factory.apply(null);
    }
}
