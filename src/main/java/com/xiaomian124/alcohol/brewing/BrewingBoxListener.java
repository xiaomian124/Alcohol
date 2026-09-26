package com.xiaomian124.alcohol.brewing;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import com.xiaomian124.alcohol.recipe.MachineRecipeGUI;
import com.xiaomian124.alcohol.recipe.MachineType;
import org.bukkit.entity.Player;

public class BrewingBoxListener implements Listener {

    private final AlcoholPlugin plugin;

    public BrewingBoxListener(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.SMOKER) return;

        BlockState state = block.getState();
        if (!(state instanceof TileState tileState)) return;
        if (!tileState.getPersistentDataContainer()
                .has(ItemKeys.BREWING_BOX, PersistentDataType.BYTE)) return;

        Player player = event.getPlayer();
        BrewingBoxGUI gui = BrewingBoxManager.getOrCreate(plugin, block);

        if (player.isSneaking()) {
            event.setCancelled(true);
            gui.vent();
            return;
        }

        event.setCancelled(true);
        player.openInventory(gui.getInventory());
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.SMOKER) return;

        BlockState state = block.getState();
        if (!(state instanceof TileState tileState)) return;
        if (!tileState.getPersistentDataContainer()
                .has(ItemKeys.BREWING_BOX, PersistentDataType.BYTE)) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() != Material.GLASS_BOTTLE) {
            player.sendActionBar(
                    Component.text("需要手持玻璃瓶").color(NamedTextColor.RED));
            return;
        }

        BrewingBoxGUI gui = BrewingBoxManager.getOrCreate(plugin, block);
        if (!gui.isBrewed()) return;

        event.setCancelled(true);

        ItemStack beer = gui.collectBrew();
        if (beer == null) return;

        hand.setAmount(hand.getAmount() - 1);
        player.getInventory().addItem(beer);

        player.playSound(player.getLocation(),
                Sound.ITEM_BOTTLE_FILL, 1.0f, 1.0f);

        Component beerName = (beer.getItemMeta() != null
                && beer.getItemMeta().displayName() != null)
                ? beer.getItemMeta().displayName()
                : Component.text("酒").color(NamedTextColor.YELLOW);

        player.sendActionBar(
                Component.text("你酿造了一瓶")
                        .color(NamedTextColor.YELLOW).append(beerName));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BrewingBoxGUI gui)) return;

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= BrewingBoxGUI.SIZE) return;

        if (slot == BrewingBoxGUI.TIMER_SLOT) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player p) {
                MachineRecipeGUI.open(p, MachineType.BREWING_BOX, MachineRecipeGUI.Mode.RECIPES);
            }
            return;
        }

        if (gui.isInputSlot(slot)
                || slot == BrewingBoxGUI.WATER_SLOT
                || slot == BrewingBoxGUI.FUEL_SLOT) {
            return;
        }

        event.setCancelled(true);
    }
}