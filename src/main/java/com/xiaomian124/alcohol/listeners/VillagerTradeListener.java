package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class VillagerTradeListener implements Listener {

    private static final int CHERRY_PRICE = 8;

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;

        if (villager.getProfession() != Villager.Profession.FARMER) return;

        for (MerchantRecipe recipe : villager.getRecipes()) {
            if (ItemFactory.isCherry(recipe.getResult())) {
                return;
            }
        }

        MerchantRecipe cherryTrade = createCherryTrade();

        List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());
        recipes.add(cherryTrade);
        villager.setRecipes(recipes);
    }

    private static MerchantRecipe createCherryTrade() {
        ItemStack cherry = ItemFactory.createCherry(null);
        cherry.setAmount(1);

        MerchantRecipe recipe = new MerchantRecipe(cherry, 999999);

        recipe.addIngredient(new ItemStack(Material.EMERALD, CHERRY_PRICE));

        recipe.setExperienceReward(true);
        recipe.setVillagerExperience(5);

        return recipe;
    }
}