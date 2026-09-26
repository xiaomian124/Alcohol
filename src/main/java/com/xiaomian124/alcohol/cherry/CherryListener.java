package com.xiaomian124.alcohol.cherry;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;

public class CherryListener implements Listener {

    private final AlcoholPlugin plugin;

    public CherryListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    private static boolean isGroundBlock(Material m) {
        return m == Material.GRASS_BLOCK
                || m == Material.DIRT
                || m == Material.MYCELIUM
                || m == Material.COARSE_DIRT
                || m == Material.PODZOL
                || m == Material.ROOTED_DIRT;
    }

    private static Location parseKey(String k) {
        String[] parts = k.split(":");
        if (parts.length != 4) return null;
        World w = Bukkit.getWorld(parts[0]);
        if (w == null) return null;
        try {
            return new Location(w,
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) { return null; }
    }

    private static void makeLeavesPersistent(Block block) {
        if (block.getBlockData() instanceof Leaves leaves) {
            leaves.setPersistent(true);
            block.setBlockData(leaves, false);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        EquipmentSlot handSlot = event.getHand();
        if (handSlot == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        Player player = event.getPlayer();
        ItemStack hand = event.getItem();

        if (hand != null) {
            if (ItemFactory.isCherry(hand)) {
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    Block clicked = event.getClickedBlock();
                    BlockFace face = event.getBlockFace();
                    if (clicked != null
                            && face == BlockFace.UP
                            && isGroundBlock(clicked.getType())
                            && clicked.getRelative(BlockFace.UP).getType() == Material.AIR) {
                        event.setCancelled(true);
                        Block above = clicked.getRelative(BlockFace.UP);
                        above.setType(Material.OAK_SAPLING, false);
                        CherryManager.registerSapling(above.getLocation());

                        if (player.getGameMode() != GameMode.CREATIVE) {
                            hand.setAmount(hand.getAmount() - 1);
                        }
                        player.playSound(above.getLocation(),
                                Sound.ITEM_CROP_PLANT, 1.0f, 1.0f);
                        return;
                    }
                    event.setCancelled(true);
                    return;
                }
                return;
            }

            if (ItemFactory.isRottenCherry(hand)) {
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                }
                return;
            }
        }

        if (action != Action.RIGHT_CLICK_BLOCK) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        if (clicked.getType() == Material.JUNGLE_LEAVES) {
            CherryTree tree = CherryManager.getTreeByLeaf(clicked.getLocation());
            if (tree == null) return;

            event.setCancelled(true);
            CherryManager.dropCherryFromHarvest(clicked);

            clicked.setType(Material.OAK_LEAVES, false);
            makeLeavesPersistent(clicked);

            player.playSound(clicked.getLocation(),
                    Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (ItemFactory.isCherry(item)) return;

        if (ItemFactory.isRottenCherry(item)) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.POISON,
                    20 * 10,
                    0,
                    true, true, true));
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        Location saplingLoc = event.getLocation();
        if (saplingLoc == null) return;

        org.bukkit.TreeType species = event.getSpecies();
        if (species != org.bukkit.TreeType.TREE
                && species != org.bukkit.TreeType.BIG_TREE
                && species != org.bukkit.TreeType.SWAMP) {
            return;
        }

        if (!CherryManager.isSapling(saplingLoc)) return;

        CherryManager.removeSapling(saplingLoc);

        CherryTree tree = new CherryTree(saplingLoc);
        for (BlockState state : event.getBlocks()) {
            Material type = state.getType();
            String k = CherryManager.key(state.getLocation());

            if (type.name().endsWith("_LOG") || type.name().endsWith("_WOOD")) {
                tree.logs.add(k);
            } else if (type == Material.OAK_LEAVES
                    || type == Material.JUNGLE_LEAVES) {
                tree.leaves.add(k);
                if (state.getBlockData() instanceof Leaves leaves) {
                    leaves.setPersistent(true);
                    state.setBlockData(leaves);
                }
            }
        }
        CherryManager.registerTree(tree);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        CherryTree treeByLog = CherryManager.getTreeByLog(loc);
        if (treeByLog != null) {
            for (String k : new HashSet<>(treeByLog.leaves)) {
                Location leafLoc = parseKey(k);
                if (leafLoc == null) continue;
                Block leafBlock = leafLoc.getBlock();
                if (leafBlock.getType() == Material.OAK_LEAVES
                        || leafBlock.getType() == Material.JUNGLE_LEAVES) {
                    boolean matured = leafBlock.getType() == Material.JUNGLE_LEAVES;
                    CherryManager.dropCherryFromBreak(leafBlock, matured);
                    leafBlock.setType(Material.AIR, false);
                }
            }
            CherryManager.unregisterTree(treeByLog);
            return;
        }

        CherryTree treeByLeaf = CherryManager.getTreeByLeaf(loc);
        if (treeByLeaf != null) {
            Material type = block.getType();
            if (type == Material.OAK_LEAVES || type == Material.JUNGLE_LEAVES) {
                boolean matured = type == Material.JUNGLE_LEAVES;
                event.setDropItems(false);
                CherryManager.dropCherryFromBreak(block, matured);
                treeByLeaf.leaves.remove(CherryManager.key(loc));
            }
        }

        if (block.getType() == Material.OAK_SAPLING
                && CherryManager.isSapling(loc)) {
            CherryManager.removeSapling(loc);
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(
                    block.getLocation().add(0.5, 0.5, 0.5),
                    ItemFactory.createCherry());
        }
    }
}