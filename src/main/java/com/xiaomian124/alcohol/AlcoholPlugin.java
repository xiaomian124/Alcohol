package com.xiaomian124.alcohol;

import com.xiaomian124.alcohol.aging.AgingBarrelListener;
import com.xiaomian124.alcohol.aging.AgingBarrelManager;
import com.xiaomian124.alcohol.apple.ApplePressListener;
import com.xiaomian124.alcohol.apple.ApplePressManager;
import com.xiaomian124.alcohol.brewing.BrewingBoxListener;
import com.xiaomian124.alcohol.brewing.BrewingBoxManager;
import com.xiaomian124.alcohol.cherry.CherryListener;
import com.xiaomian124.alcohol.cherry.CherryManager;
import com.xiaomian124.alcohol.cooking.CookingPotListener;
import com.xiaomian124.alcohol.cooking.CookingPotManager;
import com.xiaomian124.alcohol.corn.CornListener;
import com.xiaomian124.alcohol.corn.CornManager;
import com.xiaomian124.alcohol.grape.GrapeBasinListener;
import com.xiaomian124.alcohol.grape.GrapeListener;
import com.xiaomian124.alcohol.grape.GrapeManager;
import com.xiaomian124.alcohol.listeners.*;
import com.xiaomian124.alcohol.music.MusicPlayerListener;
import com.xiaomian124.alcohol.music.MusicPlayerManager;
import com.xiaomian124.alcohol.recipe.RecipeCommand;
import com.xiaomian124.alcohol.recipe.RecipeGUIListener;
import com.xiaomian124.alcohol.give.GiveGUIListener;
import com.xiaomian124.alcohol.give.GiveRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AlcoholPlugin extends JavaPlugin {

    private static AlcoholPlugin instance;
    private StorageManager storage;

    @Override
    public void onEnable() {
        instance = this;

        ItemKeys.init(this);
        GiveRegistry.init();
        CustomItemProtectionListener.init();

        RecipeManager.register(this);
        AgingBarrelManager.initRecipes();

        getServer().getPluginManager().registerEvents(new HopsListener(), this);
        getServer().getPluginManager().registerEvents(new SmeltingListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new CookingPotListener(this), this);
        getServer().getPluginManager().registerEvents(new BrewingBoxListener(this), this);
        getServer().getPluginManager().registerEvents(new ApplePressListener(this), this);
        getServer().getPluginManager().registerEvents(new AgingBarrelListener(this), this);
        getServer().getPluginManager().registerEvents(new CornListener(), this);
        getServer().getPluginManager().registerEvents(new CherryListener(this), this);
        getServer().getPluginManager().registerEvents(new GrapeListener(this), this);
        getServer().getPluginManager().registerEvents(new GrapeBasinListener(this), this);
        getServer().getPluginManager().registerEvents(new WheatBeerListener(this), this);
        getServer().getPluginManager().registerEvents(new SpecialEffectListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomItemProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new RecipeUnlockListener(this), this);
        getServer().getPluginManager().registerEvents(new RecipeGUIListener(), this);
        getServer().getPluginManager().registerEvents(new MusicPlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GiveGUIListener(), this);
        getServer().getPluginManager().registerEvents(new MilkListener(), this);

        if (getCommand("alcohol") != null) {
            RecipeCommand cmd = new RecipeCommand(this);
            getCommand("alcohol").setExecutor(cmd);
            getCommand("alcohol").setTabCompleter(cmd);
        }

        EffectStorage.init(this);
        storage = new StorageManager(this);
        storage.loadAll();
        EffectStorage.loadAll();
        MusicPlayerManager.loadDiscs();

        for (Player p : getServer().getOnlinePlayers()) {
            p.discoverRecipes(RecipeManager.getAllKeys());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                CookingPotManager.tickAll();
                BrewingBoxManager.tickAll();
                ApplePressManager.tickAll();
                AgingBarrelManager.tickAll();
                CornManager.tickAll();
                CherryManager.tickAll();
                GrapeManager.tickAll();
                SpecialEffectManager.tickAll();
                DrinkEffectDisplay.tickAll();
                MusicPlayerManager.tickAll();
            }
        }.runTaskTimer(this, 0L, 1L);

        // 5m
        new BukkitRunnable() {
            @Override
            public void run() {
                storage.saveAll();
            }
        }.runTaskTimer(this, 6000L, 6000L);

        // 5m
        new BukkitRunnable() {
            @Override
            public void run() {
                EffectStorage.saveAll();
            }
        }.runTaskTimer(this, 6000L, 6000L);

        getLogger().info("Alcohol 插件已启用");
    }

    @Override
    public void onDisable() {
        if (storage != null) storage.saveAll();

        EffectStorage.saveAll();

        try {
            for (com.xiaomian124.alcohol.music.MusicPlayer mp
                    : MusicPlayerManager.getAll()) {
                if (mp.songPlayer != null) {
                    mp.songPlayer.setPlaying(false);
                    mp.songPlayer.destroy();
                }
            }
        } catch (Exception ignored) {}

        getLogger().info("Alcohol 插件已禁用");
    }

    public static AlcoholPlugin getInstance() {
        return instance;
    }

    public StorageManager getStorageManager() {
        return storage;
    }
}