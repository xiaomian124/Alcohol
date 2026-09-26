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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class HopsListener implements Listener {

    /** 丛林树叶 → 啤酒花 */
    private static final double HOPS_CHANCE = 0.15;

    /** 橡树树叶 → 红/白葡萄种子 */
    private static final double OAK_RED_GRAPE_CHANCE   = 0.15;
    private static final double OAK_WHITE_GRAPE_CHANCE = 0.15;

    /** 金合欢树叶 → 热带草原红/白葡萄种子 */
    private static final double ACACIA_RED_GRAPE_CHANCE   = 0.15;
    private static final double ACACIA_WHITE_GRAPE_CHANCE = 0.15;

    /** 丛林树叶 → 丛林红/白葡萄种子 */
    private static final double JUNGLE_RED_GRAPE_CHANCE   = 0.15;
    private static final double JUNGLE_WHITE_GRAPE_CHANCE = 0.15;

    /** 云杉树叶 → 针叶林红/白葡萄种子 */
    private static final double SPRUCE_RED_GRAPE_CHANCE   = 0.15;
    private static final double SPRUCE_WHITE_GRAPE_CHANCE = 0.15;

    /** 成熟小麦 → 大麦/燕麦 */
    private static final double BARLEY_CHANCE = 0.20;
    private static final double OATS_CHANCE   = 0.15;

    /** 矮草丛 → 荨麻/玉米种子 */
    private static final double NETTLE_CHANCE    = 0.15;
    private static final double CORN_SEED_CHANCE = 0.15;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        if (!ItemFactory.isCustom(tool, ItemKeys.HARVEST_SHEARS)) return;

        Material type = block.getType();

        if (type == Material.JUNGLE_LEAVES) {
            event.setDropItems(false);
            double r = ThreadLocalRandom.current().nextDouble();

            if (r < HOPS_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createHops());
            } else if (r < HOPS_CHANCE + JUNGLE_RED_GRAPE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(GrapeType.JUNGLE_RED));
            } else if (r < HOPS_CHANCE + JUNGLE_RED_GRAPE_CHANCE + JUNGLE_WHITE_GRAPE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(GrapeType.JUNGLE_WHITE));
            }
            return;
        }

        if (type == Material.OAK_LEAVES) {
            event.setDropItems(false);
            double r = ThreadLocalRandom.current().nextDouble();

            if (r < OAK_RED_GRAPE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(GrapeType.RED));
            } else if (r < OAK_RED_GRAPE_CHANCE + OAK_WHITE_GRAPE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(GrapeType.WHITE));
            }
            return;
        }

        if (type == Material.ACACIA_LEAVES) {
            event.setDropItems(false);
            double r = ThreadLocalRandom.current().nextDouble();

            if (r < ACACIA_RED_GRAPE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(GrapeType.SAVANNA_RED));
            } else if (r < ACACIA_RED_GRAPE_CHANCE + ACACIA_WHITE_GRAPE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(GrapeType.SAVANNA_WHITE));
            }
            return;
        }

        if (type == Material.SPRUCE_LEAVES) {
            event.setDropItems(false);
            double r = ThreadLocalRandom.current().nextDouble();

            if (r < SPRUCE_RED_GRAPE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(GrapeType.TAIGA_RED));
            } else if (r < SPRUCE_RED_GRAPE_CHANCE + SPRUCE_WHITE_GRAPE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createGrapeSeed(GrapeType.TAIGA_WHITE));
            }
            return;
        }

        if (type == Material.WHEAT) {
            BlockData data = block.getBlockData();
            if (!(data instanceof Ageable age)) return;
            if (age.getAge() < age.getMaximumAge()) return;

            double r = ThreadLocalRandom.current().nextDouble();
            if (r < BARLEY_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createBarley());
            } else if (r < BARLEY_CHANCE + OATS_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createOats());
            }
            return;
        }

        if (type == Material.SHORT_GRASS) {
            event.setDropItems(false);
            double r = ThreadLocalRandom.current().nextDouble();
            if (r < NETTLE_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createWildNettle());
            } else if (r < NETTLE_CHANCE + CORN_SEED_CHANCE) {
                block.getWorld().dropItemNaturally(
                        block.getLocation().add(0.5, 0.5, 0.5),
                        ItemFactory.createCornSeed());
            }
            return;
        }

        event.setDropItems(false);
    }
}