package com.xiaomian124.alcohol.recipe;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xiaomian124.alcohol.SpecialEffectManager;
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

    public static final String PERM_RECIPE = "alcohol.recipe";
    public static final String PERM_GIVE   = "alcohol.give";
    public static final String PERM_RELOAD = "alcohol.reload";
    public static final String PERM_KILL   = "alcohol.kill";

    private static final List<String> SUB_COMMANDS_ALL =
            List.of("recipe", "give", "reload", "kill", "help");
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
            sender.sendMessage(Component.text("只有玩家可以使用这个命令")
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
                player.sendMessage(Component.text("你没有权限使用这个命令")
                        .color(NamedTextColor.RED));
                return true;
            }
            return handleRecipe(player, args);
        }

        if (sub.equals("give")) {
            if (!player.hasPermission(PERM_GIVE)) {
                player.sendMessage(Component.text("你没有权限使用这个命令")
                        .color(NamedTextColor.RED));
                return true;
            }
            return handleGive(player, args);
        }

        if (sub.equals("reload")) {
            if (!player.hasPermission(PERM_RELOAD)) {
                player.sendMessage(Component.text("你没有权限使用这个命令")
                        .color(NamedTextColor.RED));
                return true;
            }
            return handleReload(player);
        }

        if (sub.equals("kill")) {
            if (!player.hasPermission(PERM_KILL)) {
                player.sendMessage(Component.text("你没有权限使用这个命令")
                        .color(NamedTextColor.RED));
                return true;
            }
            return handleKill(player, args);
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
            player.sendMessage(Component.text("未知机器：" + args[1])
                    .color(NamedTextColor.RED));
            sendRecipeUsage(player);
            return true;
        }

        boolean wantsCraft = args.length >= 3 && args[2].equalsIgnoreCase("craft");

        if (type == MachineType.MUSIC_PLAYER) {
            if (wantsCraft) {
                MachineRecipeGUI.open(player, type, MachineRecipeGUI.Mode.CRAFTING);
            } else {
                player.sendMessage(Component.text(
                                "用法：/alcohol recipe MusicPlayer craft")
                        .color(NamedTextColor.RED));
                player.sendMessage(Component.text(
                                "说明：此功能方块只提供查看其合成方式")
                        .color(NamedTextColor.RED));
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
            player.sendMessage(Component.text(
                            "用法：/alcohol give <玩家> <物品> <数量>")
                    .color(NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage(Component.text("玩家不在线：" + args[1])
                    .color(NamedTextColor.RED));
            return true;
        }

        GiveableItem item = GiveRegistry.byId(args[2]);
        if (item == null) {
            player.sendMessage(Component.text("未知物品：" + args[2])
                    .color(NamedTextColor.RED));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("数量必须是正整数")
                    .color(NamedTextColor.RED));
            return true;
        }

        ItemStack stack = item.create();
        stack.setAmount(Math.min(amount, stack.getMaxStackSize()));

        boolean dropped = false;
        int remaining = amount;
        while (remaining > 0) {
            int give = Math.min(remaining, stack.getMaxStackSize());
            ItemStack copy = item.create();
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

        Component giverMsg = Component.text("已将 ")
                .color(NamedTextColor.GOLD)
                .append(Component.text(amount).color(NamedTextColor.YELLOW))
                .append(Component.text(" 个 [").color(NamedTextColor.GOLD))
                .append(Component.text(item.displayName).color(NamedTextColor.YELLOW))
                .append(Component.text("] 给予 ").color(NamedTextColor.GOLD))
                .append(Component.text(target.getName()).color(NamedTextColor.YELLOW));
        player.sendMessage(giverMsg);

        Component targetMsg = Component.text("你被给予了 ")
                .color(NamedTextColor.GREEN)
                .append(Component.text(amount).color(NamedTextColor.YELLOW))
                .append(Component.text(" 个 [").color(NamedTextColor.GREEN))
                .append(Component.text(item.displayName).color(NamedTextColor.YELLOW))
                .append(Component.text("] ！").color(NamedTextColor.GREEN));
        target.sendMessage(targetMsg);

        if (dropped) {
            player.sendMessage(Component.text("目标玩家背包已满，部分物品已掉落在地上。")
                    .color(NamedTextColor.YELLOW));
            target.sendMessage(Component.text("你的背包已满，部分物品已掉落在地上。")
                    .color(NamedTextColor.YELLOW));
        }

        return true;
    }

    private boolean handleReload(Player player) {
        player.sendMessage(Component.text("§e🔄 §3正在重新加载 Alcohol 插件")
                .color(NamedTextColor.YELLOW));

        try {
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

            player.sendMessage(Component.text("§a✔ §3Alcohol 插件已重新加载"));
            player.sendMessage(Component.text("§7- 已加载 "
                            + MusicPlayerManager.DISCS.size() + " 首nbs音乐")
                    .color(NamedTextColor.GRAY));
        } catch (Exception e) {
            player.sendMessage(Component.text("§c✖ §3Alcohol 插件重新加载失败：" + e.getMessage())
                    .color(NamedTextColor.RED));
            plugin.getLogger().warning("重载失败：" + e.getMessage());
        }

        return true;
    }

    private boolean handleKill(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("用法：/alcohol kill <CatGirl|MaoDie>")
                    .color(NamedTextColor.RED));
            player.sendMessage(Component.text("类型：CatGirl, MaoDie")
                    .color(NamedTextColor.RED));
            return true;
        }

        String type = args[1].toLowerCase();

        int removed;
        if (type.equals("catgirl")) {
            removed = removeEntities(true);
        } else if (type.equals("maodie") || type.equals("maoDie".toLowerCase())) {
            removed = removeEntities(false);
        } else {
            player.sendMessage(Component.text("未知类型：" + args[1])
                    .color(NamedTextColor.RED));
            player.sendMessage(Component.text("可用类型：CatGirl, MaoDie")
                    .color(NamedTextColor.RED));
            return true;
        }

        if (removed > 0) {
            player.sendMessage(Component.text("已清除 "
                            + removed + " 个残留实体")
                    .color(NamedTextColor.YELLOW));
            plugin.getLogger().info(player.getName()
                    + " 清除了 " + removed + " 个 " + args[1]);
        } else {
            player.sendMessage(Component.text("没有找到残留实体")
                    .color(NamedTextColor.RED));
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

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("§7==== §3Alcohol插件帮助列表 §7===="));
        player.sendMessage(Component.text("§3/alcohol recipe [<type>|<type> craft]"));
        player.sendMessage(Component.text("§7- 打开合成配方GUI"));
        if (player.hasPermission(PERM_GIVE)) {
            player.sendMessage(Component.text("§3/alcohol give [<player> <item> <amount>]"));
            player.sendMessage(Component.text("§7- 获取该插件的物品"));
        }
        if (player.hasPermission(PERM_KILL)) {
            player.sendMessage(Component.text("§3/alcohol kill <CatGirl|MaoDie>"));
            player.sendMessage(Component.text("§7- 清除残留实体"));
        }
        if (player.hasPermission(PERM_RELOAD)) {
            player.sendMessage(Component.text("§3/alcohol reload"));
            player.sendMessage(Component.text("§7- 重新加载插件"));
        }
        player.sendMessage(Component.text("§3/alcohol help"));
        player.sendMessage(Component.text("§7- 显示此帮助列表"));
        player.sendMessage(Component.text("§7========================"));
    }

    private void sendRecipeUsage(Player player) {
        player.sendMessage(Component.text("用法：/alcohol recipe <类型>")
                .color(NamedTextColor.RED));
        player.sendMessage(Component.text("类型：" + String.join(", ", MACHINE_NAMES))
                .color(NamedTextColor.RED));
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