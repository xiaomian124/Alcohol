package com.xiaomian124.alcohol.recipe;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.ItemRefresher;
import com.xiaomian124.alcohol.LanguageManager;
import com.xiaomian124.alcohol.SpecialEffectManager;
import com.xiaomian124.alcohol.aging.AgingBarrelGUI;
import com.xiaomian124.alcohol.aging.AgingBarrelManager;
import com.xiaomian124.alcohol.apple.ApplePressGUI;
import com.xiaomian124.alcohol.apple.ApplePressManager;
import com.xiaomian124.alcohol.brewing.BrewingBoxGUI;
import com.xiaomian124.alcohol.brewing.BrewingBoxManager;
import com.xiaomian124.alcohol.cooking.CookingPotGUI;
import com.xiaomian124.alcohol.cooking.CookingPotManager;
import com.xiaomian124.alcohol.entity.CatGirlManager;
import com.xiaomian124.alcohol.give.GiveGUI;
import com.xiaomian124.alcohol.give.GiveRegistry;
import com.xiaomian124.alcohol.give.GiveableItem;
import com.xiaomian124.alcohol.music.MusicPlayer;
import com.xiaomian124.alcohol.music.MusicPlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeCommand implements CommandExecutor, TabCompleter {

    public static final String PERM_RECIPE  = "alcohol.recipe";
    public static final String PERM_GIVE    = "alcohol.give";
    public static final String PERM_RELOAD  = "alcohol.reload";
    public static final String PERM_KILL    = "alcohol.kill";
    public static final String PERM_REFRESH = "alcohol.refresh";

    private static final List<String> SUB_COMMANDS_ALL =
            List.of("recipe", "give", "reload", "kill", "refresh", "help");
    private static final List<String> REFRESH_ARGS = List.of("all");
    private static final List<String> MACHINE_NAMES;
    private static final List<String> CRAFT_ARG = List.of("craft");
    private static final List<String> AMOUNT_SUGGEST = List.of("1", "5", "16", "32", "64");
    private static final List<String> KILL_TYPES = List.of("CatGirl", "MaoDie");

    static {
        List<String> names = new ArrayList<>();
        for (MachineType t : MachineType.values()) names.add(t.name());
        MACHINE_NAMES = Collections.unmodifiableList(names);
    }

    private final AlcoholPlugin plugin;

    public RecipeCommand(AlcoholPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.")
                    .color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("recipe")) {
            if (!player.hasPermission(PERM_RECIPE)) {
                player.sendMessage(LanguageManager.get(player, "cmd.no_permission"));
                return true;
            }
            return handleRecipe(player, args);
        }

        if (sub.equals("give")) {
            if (!player.hasPermission(PERM_GIVE)) {
                player.sendMessage(LanguageManager.get(player, "cmd.no_permission"));
                return true;
            }
            return handleGive(player, args);
        }

        if (sub.equals("reload")) {
            if (!player.hasPermission(PERM_RELOAD)) {
                player.sendMessage(LanguageManager.get(player, "cmd.no_permission"));
                return true;
            }
            return handleReload(player);
        }

        if (sub.equals("kill")) {
            if (!player.hasPermission(PERM_KILL)) {
                player.sendMessage(LanguageManager.get(player, "cmd.no_permission"));
                return true;
            }
            return handleKill(player, args);
        }

        if (sub.equals("refresh")) {
            if (!player.hasPermission(PERM_REFRESH)) {
                player.sendMessage(LanguageManager.get(player, "cmd.no_permission"));
                return true;
            }
            return handleRefresh(player, args);
        }

        if (sub.equals("help")) {
            sendHelp(player);
            return true;
        }

        sendHelp(player);
        return true;
    }

    private boolean handleRecipe(Player player, String[] args) {
        if (args.length == 1) {
            player.openInventory(new RecipeViewerGUI(player).getInventory());
            return true;
        }

        if (args[1].isEmpty()) {
            sendRecipeUsage(player);
            return true;
        }

        MachineType type = MachineType.byName(args[1]);
        if (type == null) {
            player.sendMessage(LanguageManager.get(player,
                    "cmd.unknown_machine", "name", args[1]));
            sendRecipeUsage(player);
            return true;
        }

        boolean wantsCraft = args.length >= 3 && args[2].equalsIgnoreCase("craft");

        if (type == MachineType.MUSIC_PLAYER) {
            if (wantsCraft) {
                MachineRecipeGUI.open(player, type, MachineRecipeGUI.Mode.CRAFTING);
            } else {
                player.sendMessage(LanguageManager.get(player,
                        "cmd.recipe.music_player_usage"));
                player.sendMessage(LanguageManager.get(player,
                        "cmd.recipe.music_player_note"));
            }
            return true;
        }

        if (wantsCraft) {
            MachineRecipeGUI.open(player, type, MachineRecipeGUI.Mode.CRAFTING);
        } else {
            MachineRecipeGUI.open(player, type, MachineRecipeGUI.Mode.RECIPES);
        }
        return true;
    }

    private boolean handleGive(Player player, String[] args) {
        if (args.length == 1) {
            player.openInventory(new GiveGUI(player).getInventory());
            return true;
        }

        if (args.length < 4) {
            player.sendMessage(LanguageManager.get(player, "cmd.give.usage"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage(LanguageManager.get(player,
                    "cmd.unknown_player", "name", args[1]));
            return true;
        }

        GiveableItem item = GiveRegistry.byId(args[2]);
        if (item == null) {
            player.sendMessage(LanguageManager.get(player,
                    "cmd.unknown_item", "name", args[2]));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(LanguageManager.get(player, "cmd.invalid_amount"));
            return true;
        }

        ItemStack stack = item.create(target);
        stack.setAmount(Math.min(amount, stack.getMaxStackSize()));

        boolean dropped = false;
        int remaining = amount;
        while (remaining > 0) {
            int give = Math.min(remaining, stack.getMaxStackSize());
            ItemStack copy = item.create(target);
            copy.setAmount(give);

            var leftover = target.getInventory().addItem(copy);
            if (!leftover.isEmpty()) {
                dropped = true;
                leftover.values().forEach(drop ->
                        target.getWorld().dropItemNaturally(target.getLocation(), drop));
            }
            remaining -= give;
        }
        target.updateInventory();

        player.sendMessage(LanguageManager.get(player, "cmd.give.success",
                "amount", String.valueOf(amount),
                "item", item.displayName(player),
                "player", target.getName()));

        target.sendMessage(LanguageManager.get(target, "cmd.give.received",
                "amount", String.valueOf(amount),
                "item", item.displayName(target)));

        if (dropped) {
            player.sendMessage(LanguageManager.get(player,
                    "cmd.give.backpack_full_giver"));
            target.sendMessage(LanguageManager.get(target,
                    "cmd.give.backpack_full_target"));
        }

        return true;
    }

    private boolean handleReload(Player player) {
        player.sendMessage(LanguageManager.get(player, "cmd.reload.reloading"));

        try {
            String before = LanguageManager.getDefaultLocale();

            for (MusicPlayer mp : MusicPlayerManager.getAll()) {
                if (mp.songPlayer != null) {
                    mp.songPlayer.setPlaying(false);
                    mp.songPlayer.destroy();
                    mp.songPlayer = null;
                }
                mp.playing = false;
            }

            if (plugin.getStorageManager() != null) {
                plugin.getStorageManager().saveAll();
                plugin.getStorageManager().loadAll();
            }

            MusicPlayerManager.loadDiscs();

            plugin.reloadConfig();
            LanguageManager.init(plugin);

            String after = LanguageManager.getDefaultLocale();

            player.sendMessage(LanguageManager.get(player, "cmd.reload.success"));
            player.sendMessage(LanguageManager.get(player, "cmd.reload.songs_loaded",
                    "count", String.valueOf(MusicPlayerManager.DISCS.size())));

            if (!before.equals(after)) {
                player.sendMessage(LanguageManager.get(player, "cmd.reload.locale_changed",
                        "locale", after));
            } else {
                player.sendMessage(LanguageManager.get(player, "cmd.reload.locale",
                        "locale", after));
            }
        } catch (Exception e) {
            player.sendMessage(LanguageManager.get(player, "cmd.reload.failed",
                    "reason", String.valueOf(e.getMessage())));
            plugin.getLogger().warning("Reload failed: " + e.getMessage());
        }

        return true;
    }

    private boolean handleKill(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(LanguageManager.get(player, "cmd.kill.usage"));
            player.sendMessage(LanguageManager.get(player, "cmd.kill.types"));
            return true;
        }

        String type = args[1].toLowerCase();

        int removed;
        if (type.equals("catgirl")) {
            removed = removeEntities(true);
        } else if (type.equals("maodie")) {
            removed = removeEntities(false);
        } else {
            player.sendMessage(LanguageManager.get(player,
                    "cmd.kill.unknown_type", "name", args[1]));
            player.sendMessage(LanguageManager.get(player,
                    "cmd.kill.available_types"));
            return true;
        }

        if (removed > 0) {
            player.sendMessage(LanguageManager.get(player,
                    "cmd.kill.success", "count", String.valueOf(removed)));
            plugin.getLogger().info(player.getName()
                    + " removed " + removed + " " + args[1]);
        } else {
            player.sendMessage(LanguageManager.get(player, "cmd.kill.none"));
        }
        return true;
    }

    private int removeEntities(boolean catGirl) {
        int count = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity e : world.getEntities()) {
                if (catGirl) {
                    if (e instanceof Mannequin m
                            && m.getPersistentDataContainer().has(
                            CatGirlManager.CATGIRL_KEY,
                            PersistentDataType.BYTE)) {
                        m.remove();
                        count++;
                    }
                } else {
                    if (e instanceof Cat cat
                            && cat.getPersistentDataContainer().has(
                            SpecialEffectManager.ELDER_CAT_KEY,
                            PersistentDataType.BYTE)) {
                        cat.remove();
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            SpecialEffectManager.cleanupReferences();
        }

        return count;
    }

    private boolean handleRefresh(Player player, String[] args) {
        boolean all = args.length >= 2 && args[1].equalsIgnoreCase("all");

        int items = 0;
        int players = 0;

        if (all) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                items += ItemRefresher.refreshPlayer(p);
                players++;
            }
        } else {
            items += ItemRefresher.refreshPlayer(player);
            players = 1;
        }

        int containerItems = refreshMachineContents(player, !all);

        player.sendMessage(LanguageManager.get(player, "cmd.refresh.done",
                "items", String.valueOf(items),
                "containers", String.valueOf(containerItems),
                "players", String.valueOf(players)));

        if (items == 0 && containerItems == 0) {
            player.sendMessage(LanguageManager.get(player, "cmd.refresh.none"));
        }
        return true;
    }

    private int refreshMachineContents(Player viewer, boolean onlyViewer) {
        int count = 0;

        for (CookingPotGUI gui : CookingPotManager.getAll()) {
            if (onlyViewer && !isViewing(viewer, gui)) continue;
            count += ItemRefresher.refreshContents(
                    gui.getInventory().getContents(), true);
        }
        for (ApplePressGUI gui : ApplePressManager.getAll()) {
            if (onlyViewer && !isViewing(viewer, gui)) continue;
            count += ItemRefresher.refreshContents(
                    gui.getInventory().getContents(), true);
        }
        for (AgingBarrelGUI gui : AgingBarrelManager.getAll()) {
            if (onlyViewer && !isViewing(viewer, gui)) continue;
            count += ItemRefresher.refreshContents(
                    gui.getInventory().getContents(), true);
        }
        for (BrewingBoxGUI gui : BrewingBoxManager.getAll()) {
            if (onlyViewer && !isViewing(viewer, gui)) continue;
            count += ItemRefresher.refreshContents(
                    gui.getInventory().getContents(), true);
        }
        return count;
    }

    private static boolean isViewing(Player player, org.bukkit.inventory.InventoryHolder holder) {
        return player.getOpenInventory().getTopInventory().getHolder() == holder;
    }

    private void sendHelp(Player player) {
        player.sendMessage(LanguageManager.get(player, "cmd.help.header"));
        player.sendMessage(LanguageManager.get(player, "cmd.help.recipe.cmd"));
        player.sendMessage(LanguageManager.get(player, "cmd.help.recipe.desc"));
        if (player.hasPermission(PERM_GIVE)) {
            player.sendMessage(LanguageManager.get(player, "cmd.help.give.cmd"));
            player.sendMessage(LanguageManager.get(player, "cmd.help.give.desc"));
        }
        if (player.hasPermission(PERM_KILL)) {
            player.sendMessage(LanguageManager.get(player, "cmd.help.kill.cmd"));
            player.sendMessage(LanguageManager.get(player, "cmd.help.kill.desc"));
        }
        if (player.hasPermission(PERM_RELOAD)) {
            player.sendMessage(LanguageManager.get(player, "cmd.help.reload.cmd"));
            player.sendMessage(LanguageManager.get(player, "cmd.help.reload.desc"));
        }
        if (player.hasPermission(PERM_REFRESH)) {
            player.sendMessage(LanguageManager.get(player, "cmd.help.refresh.cmd"));
            player.sendMessage(LanguageManager.get(player, "cmd.help.refresh.desc"));
        }
        player.sendMessage(LanguageManager.get(player, "cmd.help.help.cmd"));
        player.sendMessage(LanguageManager.get(player, "cmd.help.help.desc"));
        player.sendMessage(LanguageManager.get(player, "cmd.help.footer"));
    }

    private void sendRecipeUsage(Player player) {
        player.sendMessage(LanguageManager.get(player, "cmd.recipe.usage"));
        player.sendMessage(LanguageManager.get(player, "cmd.recipe.types",
                "types", String.join(", ", MACHINE_NAMES)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();

        if (args.length == 1) {
            List<String> subs = new ArrayList<>();
            subs.add("recipe");
            subs.add("help");
            if (player.hasPermission(PERM_GIVE)) subs.add("give");
            if (player.hasPermission(PERM_RELOAD)) subs.add("reload");
            if (player.hasPermission(PERM_KILL)) subs.add("kill");
            if (player.hasPermission(PERM_REFRESH)) subs.add("refresh");

            return StringUtil.copyPartialMatches(args[0], subs, new ArrayList<>());
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("recipe")) {
            if (!player.hasPermission(PERM_RECIPE)) return Collections.emptyList();
            if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], MACHINE_NAMES,
                        new ArrayList<>());
            }
            if (args.length == 3) {
                return StringUtil.copyPartialMatches(args[2], CRAFT_ARG,
                        new ArrayList<>());
            }
            return Collections.emptyList();
        }

        if (sub.equals("give")) {
            if (!player.hasPermission(PERM_GIVE)) return Collections.emptyList();
            if (args.length == 2) {
                List<String> names = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
                return StringUtil.copyPartialMatches(args[1], names, new ArrayList<>());
            }
            if (args.length == 3) {
                return StringUtil.copyPartialMatches(args[2],
                        GiveRegistry.allIds(), new ArrayList<>());
            }
            if (args.length == 4) {
                return StringUtil.copyPartialMatches(args[3], AMOUNT_SUGGEST,
                        new ArrayList<>());
            }
            return Collections.emptyList();
        }

        if (sub.equals("refresh")) {
            if (!player.hasPermission(PERM_REFRESH)) return Collections.emptyList();
            if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], REFRESH_ARGS,
                        new ArrayList<>());
            }
            return Collections.emptyList();
        }

        if (sub.equals("kill")) {
            if (!player.hasPermission(PERM_KILL)) return Collections.emptyList();
            if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], KILL_TYPES,
                        new ArrayList<>());
            }
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }
}