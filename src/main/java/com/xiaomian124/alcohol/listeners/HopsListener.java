package com.xiaomian124.alcohol.listeners;

import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import com.xiaomian124.alcohol.grape.GrapeType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class HopsListener implements Listener {

    private static final double HOPS_CHANCE = 0.15;

    private static final double OAK_RED_GRAPE_CHANCE   = 0.15;
    private static final double OAK_WHITE_GRAPE_CHANCE = 0.15;
    private static final double CHERRY_CHANCE          = 0.10;

    private static final double ACACIA_RED_GRAPE_CHANCE   = 0.15;
    private static final double ACACIA_WHITE_GRAPE_CHANCE = 0.15;

    private static final double JUNGLE_RED_GRAPE_CHANCE   = 0.15;
    private static final double JUNGLE_WHITE_GRAPE_CHANCE = 0.15;

    private static final double SPRUCE_RED_GRAPE_CHANCE   = 0.15;
    private static final double SPRUCE_WHITE_GRAPE_CHANCE = 0.15;

    private static final double BARLEY_CHANCE    = 0.20;
    private static final double OATS_CHANCE      = 0.15;

    private static final double NETTLE_CHANCE    = 0.15;
    private static final double CORN_SEED_CHANCE = 0.15;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!ItemFactory.isCustom(tool, ItemKeys.HARVEST_SHEARS)) return;

        Material type = block.getType();

        if (type == Material.JUNGLE_LEAVES) {
            double r = ThreadLocalRandom.current().nextDouble();
            if (r < HOPS_CHANCE) {
                drop(block, ItemFactory.createHops(player));
            } else if (r < HOPS_CHANCE + JUNGLE_RED_GRAPE_CHANCE) {
                drop(block, ItemFactory.createGrapeSeed(player, GrapeType.JUNGLE_RED));
            } else if (r < HOPS_CHANCE + JUNGLE_RED_GRAPE_CHANCE + JUNGLE_WHITE_GRAPE_CHANCE) {
                drop(block, ItemFactory.createGrapeSeed(player, GrapeType.JUNGLE_WHITE));
            }
            return;
        }

        if (type == Material.OAK_LEAVES) {
            double r = ThreadLocalRandom.current().nextDouble();
            if (r < OAK_RED_GRAPE_CHANCE) {
                drop(block, ItemFactory.createGrapeSeed(player, GrapeType.RED));
            } else if (r < OAK_RED_GRAPE_CHANCE + OAK_WHITE_GRAPE_CHANCE) {
                drop(block, ItemFactory.createGrapeSeed(player, GrapeType.WHITE));
            } else if (r < OAK_RED_GRAPE_CHANCE + OAK_WHITE_GRAPE_CHANCE + CHERRY_CHANCE) {
                drop(block, ItemFactory.createCherry(player));
            }
            return;
        }

        if (type == Material.ACACIA_LEAVES) {
            double r = ThreadLocalRandom.current().nextDouble();
            if (r < ACACIA_RED_GRAPE_CHANCE) {
                drop(block, ItemFactory.createGrapeSeed(player, GrapeType.SAVANNA_RED));
            } else if (r < ACACIA_RED_GRAPE_CHANCE + ACACIA_WHITE_GRAPE_CHANCE) {
                drop(block, ItemFactory.createGrapeSeed(player, GrapeType.SAVANNA_WHITE));
            }
            return;
        }

        if (type == Material.SPRUCE_LEAVES) {
            double r = ThreadLocalRandom.current().nextDouble();
            if (r < SPRUCE_RED_GRAPE_CHANCE) {
                drop(block, ItemFactory.createGrapeSeed(player, GrapeType.TAIGA_RED));
            } else if (r < SPRUCE_RED_GRAPE_CHANCE + SPRUCE_WHITE_GRAPE_CHANCE) {
                drop(block, ItemFactory.createGrapeSeed(player, GrapeType.TAIGA_WHITE));
            }
            return;
        }

        if (type == Material.WHEAT) {
            BlockData data = block.getBlockData();
            if (!(data instanceof Ageable age)) return;
            if (age.getAge() < age.getMaximumAge()) return;

            double r = ThreadLocalRandom.current().nextDouble();
            if (r < BARLEY_CHANCE) {
                drop(block, ItemFactory.createBarley(player));
            } else if (r < BARLEY_CHANCE + OATS_CHANCE) {
                drop(block, ItemFactory.createOats(player));
            }
            return;
        }

        if (type == Material.SHORT_GRASS) {
            double r = ThreadLocalRandom.current().nextDouble();
            if (r < NETTLE_CHANCE) {
                drop(block, ItemFactory.createWildNettle(player));
            } else if (r < NETTLE_CHANCE + CORN_SEED_CHANCE) {
                drop(block, ItemFactory.createCornSeed(player));
            }
        }
    }

    private static void drop(Block block, ItemStack item) {
        block.getWorld().dropItemNaturally(
                block.getLocation().add(0.5, 0.5, 0.5), item);
    }
}