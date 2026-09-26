package com.xiaomian124.alcohol;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaomian124.alcohol.grape.GrapeType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public final class ItemFactory {

    private static final String TIMER_HEAD_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE1NDQ1ZGExNmZhYjY3ZmNkODI3ZjcxYmFlOWMxZDJmOTBjNzNlYjJjMWJkMWVmOGQ4Mzk2Y2Q4ZTgifX19";
    private static final UUID TIMER_HEAD_UUID =
            UUID.nameUUIDFromBytes("alcohol:timer_head".getBytes(StandardCharsets.UTF_8));

    private static final String TIMER_HEAD_2_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGE4MDFlMmFkYzU5YWFjNjZkOGY1OWRiYzM1ZmFiYWMyY2Y1YjYxMWJlN2IyMTc1OTQxODU1NTEzN2ZmNDJhMiJ9fX0=";
    private static final UUID TIMER_HEAD_2_UUID =
            UUID.nameUUIDFromBytes("alcohol:timer_head_2".getBytes(StandardCharsets.UTF_8));

    private static final String CORN_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNhNmIwOTljZDQwMWUzYTBkNjRkOWExNmY0NmNkMGM1Y2E1ZjdlNDVlNmE2OWMyN2QyZTQ3Mzc3NWIyNWZlIn19fQ==";
    private static final UUID CORN_UUID =
            UUID.nameUUIDFromBytes("alcohol:corn".getBytes(StandardCharsets.UTF_8));

    private static final String PREV_HEAD_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM5MjYyM2NhZmRmNzQ1YmZjYjMxZDA1ZGJjZDI0N2Q1NzFkODk3ZjIyYWNlMzhlZTU0ZWVkM2ZiOTYzNiJ9fX0=";
    private static final String NEXT_HEAD_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJjYTkwZWJhZmRmNTdkZjRjMzgwY2YyNjU1YWE3YjRlYzZhNGJkYmQxNTUxNmViZmRlNDMyN2ExZTI3In19fQ==";

    private static final String PLAY_PAUSE_HEAD_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAzODRjMDc0YTQxNmEwZGMyMmExOGJhMjM0NTc1YmExMzFhNGVhMjhmYzU5MDVlYTNkNGFiODRmODQxZmMzMyJ9fX0=";
    private static final UUID PLAY_PAUSE_HEAD_UUID =
            UUID.nameUUIDFromBytes("alcohol:play_pause".getBytes(StandardCharsets.UTF_8));

    private ItemFactory() {}

    public static Component plainName(String text, NamedTextColor color) {
        return Component.text(text).color(color)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static void mark(ItemStack item, NamespacedKey key) {
        if (key == null) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
    }

    private static void name(ItemStack item, String text, NamedTextColor color) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(plainName(text, color));
        item.setItemMeta(meta);
    }

    private static void lore(ItemStack item, String text) {
        ItemMeta meta = item.getItemMeta();
        List<Component> old = meta.lore();
        List<Component> list = (old == null) ? new ArrayList<>() : new ArrayList<>(old);
        list.add(plainName(text, NamedTextColor.GRAY));
        meta.lore(list);
        item.setItemMeta(meta);
    }

    public static void applyBeerSkin(SkullMeta meta, BeerType type) {
        applySkin(meta, type.getSkinUUID(), type.getTexture());
    }

    private static void applySkin(SkullMeta meta, UUID uuid, String base64Texture) {
        if (base64Texture == null || base64Texture.isEmpty()) return;
        PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(extractTextureUrl(base64Texture)));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
    }

    private static String extractTextureUrl(String base64) {
        String padded = base64;
        int mod = padded.length() % 4;
        if (mod == 2) padded += "==";
        else if (mod == 3) padded += "=";
        String decoded = new String(
                Base64.getDecoder().decode(padded), StandardCharsets.UTF_8);
        JsonObject obj = JsonParser.parseString(decoded).getAsJsonObject();
        return obj.getAsJsonObject("textures").getAsJsonObject("SKIN")
                .get("url").getAsString();
    }

    public static void applyFood(ItemStack item, int nutrition, float wantedSaturation) {
        try {
            if (nutrition <= 0) nutrition = 1;

            float modifier = wantedSaturation / (nutrition * 2f);

            item.setData(
                    DataComponentTypes.FOOD,
                    FoodProperties.food()
                            .nutrition(nutrition)
                            .saturation(modifier)
                            .build()
            );

            item.setData(
                    DataComponentTypes.CONSUMABLE,
                    Consumable.consumable()
                            .animation(ItemUseAnimation.EAT)
                            .consumeSeconds(1.6f)
                            .build()
            );

            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack createPrevHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta,
                UUID.nameUUIDFromBytes("alcohol:prev".getBytes(StandardCharsets.UTF_8)),
                PREV_HEAD_TEXTURE);
        meta.displayName(plainName("上一页", NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createNextHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta,
                UUID.nameUUIDFromBytes("alcohol:next".getBytes(StandardCharsets.UTF_8)),
                NEXT_HEAD_TEXTURE);
        meta.displayName(plainName("下一页", NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createPlayPauseHead(boolean playing) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, PLAY_PAUSE_HEAD_UUID, PLAY_PAUSE_HEAD_TEXTURE);
        meta.displayName(plainName(playing ? "暂停" : "播放", NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createDryWheat() {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, "烘干的小麦", NamedTextColor.WHITE);
        lore(item, "烘干后的小麦，散发着独特的香味");
        mark(item, ItemKeys.DRY_WHEAT);
        return item;
    }

    public static ItemStack createBarley() {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, "大麦", NamedTextColor.WHITE);
        lore(item, "一种古老的禾本科谷物，广泛用于食品");
        mark(item, ItemKeys.BARLEY);
        return item;
    }

    public static ItemStack createDriedBarley() {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, "烘干的大麦", NamedTextColor.WHITE);
        lore(item, "烘干后的大麦，散发着独特的香味");
        mark(item, ItemKeys.DRIED_BARLEY);
        return item;
    }

    public static ItemStack createOats() {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, "燕麦", NamedTextColor.WHITE);
        lore(item, "一种禾本科植物结出的谷物，广泛种植于温带地区");
        mark(item, ItemKeys.OATS);
        return item;
    }

    public static ItemStack createDriedOats() {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, "烘干的燕麦", NamedTextColor.WHITE);
        lore(item, "烘干后的燕麦，散发着独特的香味");
        mark(item, ItemKeys.DRIED_OATS);
        return item;
    }

    public static ItemStack createCornSeed() {
        ItemStack item = new ItemStack(Material.PUMPKIN_SEEDS);
        name(item, "玉米种子", NamedTextColor.WHITE);
        lore(item, "种在耕地上可以长出玉米");
        mark(item, ItemKeys.CORN_SEED);
        return item;
    }

    public static ItemStack createCorn() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        applySkin(meta, CORN_UUID, CORN_TEXTURE);
        meta.displayName(plainName("玉米", NamedTextColor.WHITE));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.lore(List.of(
                plainName("金黄色的玉米棒，可以合成玉米粒", NamedTextColor.GRAY),
                plainName("食用可恢复 3.5 点饥饿值", NamedTextColor.GRAY)
        ));
        meta.getPersistentDataContainer().set(
                ItemKeys.CORN, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);

        applyFood(item, 3, 3.5f);
        return item;
    }

    public static ItemStack createCornKernels() {
        ItemStack item = new ItemStack(Material.BEETROOT_SEEDS);
        name(item, "玉米粒", NamedTextColor.WHITE);
        lore(item, "从玉米上剥下的金黄色颗粒");
        mark(item, ItemKeys.CORN_KERNELS);
        return item;
    }

    public static ItemStack createDriedCorn() {
        ItemStack item = new ItemStack(Material.BEETROOT_SEEDS);
        name(item, "烘干的玉米粒", NamedTextColor.WHITE);
        lore(item, "烘干后的玉米粒，散发着甜香");
        mark(item, ItemKeys.DRIED_CORN);
        return item;
    }

    public static ItemStack createWildNettle() {
        ItemStack item = new ItemStack(Material.FERN);
        name(item, "野生荨麻", NamedTextColor.WHITE);
        lore(item, "一种靠地下根茎和种子自然繁殖，耐寒又耐旱");
        mark(item, ItemKeys.WILD_NETTLE);
        return item;
    }

    public static ItemStack createHops() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS);
        name(item, "啤酒花", NamedTextColor.WHITE);
        lore(item, "一种多年生缠绕草本植物，可用于酿造啤酒");
        mark(item, ItemKeys.HOPS);
        return item;
    }

    public static ItemStack createYeast() {
        ItemStack item = new ItemStack(Material.SUGAR);
        name(item, "酵母", NamedTextColor.WHITE);
        lore(item, "能发酵糖类的各种单细胞真菌，可用于酿造生产");
        mark(item, ItemKeys.YEAST);
        return item;
    }

    public static ItemStack createHarvestShears() {
        ItemStack item = new ItemStack(Material.SHEARS);
        name(item, "收割剪刀", NamedTextColor.YELLOW);
        lore(item, "可以采集一些农作物");
        mark(item, ItemKeys.HARVEST_SHEARS);
        return item;
    }

    public static ItemStack createCookingPot() {
        ItemStack item = new ItemStack(Material.CAULDRON);
        name(item, "烹饪锅", NamedTextColor.YELLOW);
        lore(item, "试试你的拿手菜吧");
        mark(item, ItemKeys.COOKING_POT);
        return item;
    }

    public static ItemStack createBrewingBox() {
        ItemStack item = new ItemStack(Material.SMOKER);
        name(item, "酿造炉", NamedTextColor.YELLOW);
        lore(item, "尝试酿造好喝啤酒");
        mark(item, ItemKeys.BREWING_BOX);
        return item;
    }

    public static ItemStack createApplePress() {
        ItemStack item = new ItemStack(Material.LOOM);
        name(item, "苹果压榨器", NamedTextColor.YELLOW);
        lore(item, "把苹果压榨成苹果泥和苹果汁");
        mark(item, ItemKeys.APPLE_PRESS);
        return item;
    }

    public static ItemStack createAppleMash() {
        ItemStack item = new ItemStack(Material.BROWN_DYE);
        name(item, "苹果泥", NamedTextColor.YELLOW);
        lore(item, "压碎的苹果果肉");
        mark(item, ItemKeys.APPLE_MASH);
        return item;
    }

    public static ItemStack createAppleJuice() {
        ItemStack item = createBeer(BeerType.APPLE_JUICE, BeerQuality.HIGH);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
                ItemKeys.APPLE_JUICE, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isAppleMash(ItemStack item) {
        return isCustom(item, ItemKeys.APPLE_MASH);
    }

    public static boolean isAppleJuice(ItemStack item) {
        return isCustom(item, ItemKeys.APPLE_JUICE);
    }

    public static ItemStack createAgingBarrel() {
        ItemStack item = new ItemStack(Material.BARREL);
        name(item, "陈酿桶", NamedTextColor.YELLOW);
        lore(item, "把酒放入桶中慢慢陈酿");
        mark(item, ItemKeys.AGING_BARREL);
        return item;
    }

    public static ItemStack createGrapeBasin() {
        ItemStack item = new ItemStack(Material.COMPOSTER);
        name(item, "葡萄藤盆", NamedTextColor.YELLOW);
        lore(item, "脚踩榨取葡萄汁，保持葡萄汁的纯净度和口感");
        mark(item, ItemKeys.GRAPE_BASIN);
        return item;
    }

    public static ItemStack createMusicPlayer() {
        ItemStack item = new ItemStack(Material.JUKEBOX);
        name(item, "吧台音乐播放器", NamedTextColor.YELLOW);
        lore(item, "在你的酒吧里播放一些音乐");
        mark(item, ItemKeys.MUSIC_PLAYER);
        return item;
    }

    public static ItemStack createGrapeSeed(GrapeType type) {
        ItemStack item = new ItemStack(type.getSeedMaterial());
        name(item, type.getSeedName(), NamedTextColor.WHITE);
        lore(item, type.getLore());
        mark(item, type.getSeedKey());
        return item;
    }

    public static ItemStack createGrape(GrapeType type) {
        ItemStack item = new ItemStack(type.getGrapeMaterial());
        name(item, type.getGrapeName(), NamedTextColor.WHITE);
        lore(item, type.getLore());
        lore(item, "食用可恢复 2 点饥饿值");
        lore(item, "有几率获得葡萄种子");
        mark(item, type.getGrapeKey());

        applyFood(item, 2, 1.0f);

        return item;
    }

    public static GrapeType getGrapeSeedType(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        for (GrapeType t : GrapeType.values()) {
            if (t.getSeedKey() != null && pdc.has(t.getSeedKey(), PersistentDataType.BYTE)) {
                return t;
            }
        }
        return null;
    }

    public static GrapeType getGrapeType(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        for (GrapeType t : GrapeType.values()) {
            if (t.getGrapeKey() != null && pdc.has(t.getGrapeKey(), PersistentDataType.BYTE)) {
                return t;
            }
        }
        return null;
    }

    public static ItemStack createCherry() {
        ItemStack item = new ItemStack(Material.SWEET_BERRIES);
        name(item, "樱桃", NamedTextColor.WHITE);
        lore(item, "鲜红饱满的果实，可用于陈酿酒");
        lore(item, "可右键种植到地面");
        lore(item, "食用可恢复 2 点饥饿值");
        mark(item, ItemKeys.CHERRY);

        applyFood(item, 2, 1.4f);

        return item;
    }

    public static ItemStack createRottenCherry() {
        ItemStack item = new ItemStack(Material.SWEET_BERRIES);
        name(item, "腐烂的樱桃", NamedTextColor.WHITE);
        lore(item, "已经腐坏的樱桃，散发出一股恶臭");
        lore(item, "食用可恢复 1 点饥饿值");
        mark(item, ItemKeys.ROTTEN_CHERRY);

        applyFood(item, 1, 1.0f);

        return item;
    }

    public static boolean isCherry(ItemStack item) {
        return isCustom(item, ItemKeys.CHERRY);
    }

    public static boolean isRottenCherry(ItemStack item) {
        return isCustom(item, ItemKeys.ROTTEN_CHERRY);
    }

    public static ItemStack createWheatBeer() {
        return createBeer(BeerType.WHEAT_BEER, BeerQuality.HIGH);
    }

    public static ItemStack createWheatBeer(BeerQuality quality) {
        return createBeer(BeerType.WHEAT_BEER, quality);
    }

    public static ItemStack createBeer(BeerType type, BeerQuality quality) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (item.getType() == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
            applyBeerSkin(skullMeta, type);
        } else if (item.getType() == Material.POTION && meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(type.getPotionColor());
            potionMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            potionMeta.setMaxStackSize(64);
        }

        meta.displayName(plainName(type.getDisplayName(), NamedTextColor.YELLOW));

        List<Component> loreList = new ArrayList<>();
        loreList.add(plainName(type.getLoreLine(), NamedTextColor.GRAY));

        if (type.hasQuality()) {
            loreList.add(plainName("品质：", NamedTextColor.GRAY)
                    .append(Component.text(quality.getLabel())
                            .color(quality.getColor())
                            .decoration(TextDecoration.ITALIC, false)));
        }

        loreList.add(Component.empty());
        loreList.addAll(BeerEffects.getEffectLines(type));

        meta.lore(loreList);

        var pdc = meta.getPersistentDataContainer();
        pdc.set(ItemKeys.BEER, PersistentDataType.BYTE, (byte) 1);
        pdc.set(ItemKeys.BEER_TYPE, PersistentDataType.STRING, type.name());
        if (type.hasQuality()) {
            pdc.set(ItemKeys.BEER_QUALITY, PersistentDataType.STRING, quality.name());
        }

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isBeer(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(ItemKeys.BEER, PersistentDataType.BYTE);
    }

    public static BeerType getBeerType(ItemStack item) {
        if (!isBeer(item)) return null;
        String name = item.getItemMeta().getPersistentDataContainer()
                .get(ItemKeys.BEER_TYPE, PersistentDataType.STRING);
        if (name == null) return BeerType.WHEAT_BEER;
        try { return BeerType.valueOf(name); }
        catch (IllegalArgumentException e) { return BeerType.WHEAT_BEER; }
    }

    public static BeerQuality getBeerQuality(ItemStack item) {
        if (!isBeer(item)) return BeerQuality.HIGH;
        String name = item.getItemMeta().getPersistentDataContainer()
                .get(ItemKeys.BEER_QUALITY, PersistentDataType.STRING);
        if (name == null) return BeerQuality.HIGH;
        try { return BeerQuality.valueOf(name); }
        catch (IllegalArgumentException e) { return BeerQuality.HIGH; }
    }

    public static ItemStack createTimerHead(int remainingTicks) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, TIMER_HEAD_UUID, TIMER_HEAD_TEXTURE);
        int seconds = (remainingTicks + 19) / 20;
        meta.displayName(plainName(seconds + "s", NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createTimerHeadIdle() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, TIMER_HEAD_UUID, TIMER_HEAD_TEXTURE);
        meta.displayName(plainName("查看配方", NamedTextColor.WHITE));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createTimerHead2(int remainingTicks) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, TIMER_HEAD_2_UUID, TIMER_HEAD_2_TEXTURE);
        int seconds = (remainingTicks + 19) / 20;
        meta.displayName(plainName(seconds + "s", NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack createTimerHeadIdle2() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, TIMER_HEAD_2_UUID, TIMER_HEAD_2_TEXTURE);
        meta.displayName(plainName("查看配方", NamedTextColor.WHITE));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    public static boolean isCustom(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(key, PersistentDataType.BYTE);
    }
}