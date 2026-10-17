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
import org.bukkit.entity.Player;
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

    private static void name(ItemStack item, Player player, String langKey,
                             NamedTextColor color) {
        name(item, lang(langKey), color);
    }

    private static void name(ItemStack item, String text, NamedTextColor color) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(plainName(text, color));
        item.setItemMeta(meta);
    }

    private static void lore(ItemStack item, Player player, String langKey) {
        lore(item, lang(langKey));
    }

    private static void lore(ItemStack item, String text) {
        ItemMeta meta = item.getItemMeta();
        List<Component> old = meta.lore();
        List<Component> list = (old == null) ? new ArrayList<>() : new ArrayList<>(old);
        list.add(plainName(text, NamedTextColor.GRAY));
        meta.lore(list);
        item.setItemMeta(meta);
    }

    public static String lang(Player player, String key) {
        return LanguageManager.getRaw(player, key);
    }

    public static String lang(String key) {
        return LanguageManager.getRaw(null, key);
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

    public static ItemStack createPrevHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta,
                UUID.nameUUIDFromBytes("alcohol:prev".getBytes(StandardCharsets.UTF_8)),
                PREV_HEAD_TEXTURE);
        meta.displayName(plainName(lang("gui.prev_page"), NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    @Deprecated
    public static ItemStack createPrevHead() {
        return createPrevHead(null);
    }

    public static ItemStack createNextHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta,
                UUID.nameUUIDFromBytes("alcohol:next".getBytes(StandardCharsets.UTF_8)),
                NEXT_HEAD_TEXTURE);
        meta.displayName(plainName(lang("gui.next_page"), NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    @Deprecated
    public static ItemStack createNextHead() {
        return createNextHead(null);
    }

    public static ItemStack createPlayPauseHead(Player player, boolean playing) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, PLAY_PAUSE_HEAD_UUID, PLAY_PAUSE_HEAD_TEXTURE);
        meta.displayName(plainName(
                lang(playing ? "gui.pause" : "gui.play"),
                NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    @Deprecated
    public static ItemStack createPlayPauseHead(boolean playing) {
        return createPlayPauseHead(null, playing);
    }

    public static ItemStack createDryWheat(Player player) {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, player, "item.dry_wheat.name", NamedTextColor.WHITE);
        lore(item, player, "item.dry_wheat.lore");
        mark(item, ItemKeys.DRY_WHEAT);
        return item;
    }

    @Deprecated
    public static ItemStack createDryWheat() {
        return createDryWheat(null);
    }

    public static ItemStack createBarley(Player player) {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, player, "item.barley.name", NamedTextColor.WHITE);
        lore(item, player, "item.barley.lore");
        mark(item, ItemKeys.BARLEY);
        return item;
    }

    @Deprecated
    public static ItemStack createBarley() {
        return createBarley(null);
    }

    public static ItemStack createDriedBarley(Player player) {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, player, "item.dried_barley.name", NamedTextColor.WHITE);
        lore(item, player, "item.dried_barley.lore");
        mark(item, ItemKeys.DRIED_BARLEY);
        return item;
    }

    @Deprecated
    public static ItemStack createDriedBarley() {
        return createDriedBarley(null);
    }

    public static ItemStack createOats(Player player) {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, player, "item.oats.name", NamedTextColor.WHITE);
        lore(item, player, "item.oats.lore");
        mark(item, ItemKeys.OATS);
        return item;
    }

    @Deprecated
    public static ItemStack createOats() {
        return createOats(null);
    }

    public static ItemStack createDriedOats(Player player) {
        ItemStack item = new ItemStack(Material.WHEAT);
        name(item, player, "item.dried_oats.name", NamedTextColor.WHITE);
        lore(item, player, "item.dried_oats.lore");
        mark(item, ItemKeys.DRIED_OATS);
        return item;
    }

    @Deprecated
    public static ItemStack createDriedOats() {
        return createDriedOats(null);
    }

    public static ItemStack createCornSeed(Player player) {
        ItemStack item = new ItemStack(Material.PUMPKIN_SEEDS);
        name(item, player, "item.corn_seed.name", NamedTextColor.WHITE);
        lore(item, player, "item.corn_seed.lore");
        mark(item, ItemKeys.CORN_SEED);
        return item;
    }

    @Deprecated
    public static ItemStack createCornSeed() {
        return createCornSeed(null);
    }

    public static ItemStack createCorn(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        applySkin(meta, CORN_UUID, CORN_TEXTURE);
        meta.displayName(plainName(lang("item.corn.name"), NamedTextColor.WHITE));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.lore(List.of(
                plainName(lang("item.corn.lore_1"), NamedTextColor.GRAY),
                plainName(lang("item.corn.lore_2"), NamedTextColor.GRAY)
        ));
        meta.getPersistentDataContainer().set(
                ItemKeys.CORN, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);

        applyFood(item, 3, 3.5f);
        return item;
    }

    @Deprecated
    public static ItemStack createCorn() {
        return createCorn(null);
    }

    public static ItemStack createCornKernels(Player player) {
        ItemStack item = new ItemStack(Material.BEETROOT_SEEDS);
        name(item, player, "item.corn_kernels.name", NamedTextColor.WHITE);
        lore(item, player, "item.corn_kernels.lore");
        mark(item, ItemKeys.CORN_KERNELS);
        return item;
    }

    @Deprecated
    public static ItemStack createCornKernels() {
        return createCornKernels(null);
    }

    public static ItemStack createDriedCorn(Player player) {
        ItemStack item = new ItemStack(Material.BEETROOT_SEEDS);
        name(item, player, "item.dried_corn.name", NamedTextColor.WHITE);
        lore(item, player, "item.dried_corn.lore");
        mark(item, ItemKeys.DRIED_CORN);
        return item;
    }

    @Deprecated
    public static ItemStack createDriedCorn() {
        return createDriedCorn(null);
    }

    public static ItemStack createWildNettle(Player player) {
        ItemStack item = new ItemStack(Material.FERN);
        name(item, player, "item.wild_nettle.name", NamedTextColor.WHITE);
        lore(item, player, "item.wild_nettle.lore");
        mark(item, ItemKeys.WILD_NETTLE);
        return item;
    }

    @Deprecated
    public static ItemStack createWildNettle() {
        return createWildNettle(null);
    }

    public static ItemStack createHops(Player player) {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS);
        name(item, player, "item.hops.name", NamedTextColor.WHITE);
        lore(item, player, "item.hops.lore");
        mark(item, ItemKeys.HOPS);
        return item;
    }

    @Deprecated
    public static ItemStack createHops() {
        return createHops(null);
    }

    public static ItemStack createYeast(Player player) {
        ItemStack item = new ItemStack(Material.SUGAR);
        name(item, player, "item.yeast.name", NamedTextColor.WHITE);
        lore(item, player, "item.yeast.lore");
        mark(item, ItemKeys.YEAST);
        return item;
    }

    @Deprecated
    public static ItemStack createYeast() {
        return createYeast(null);
    }

    public static ItemStack createHarvestShears(Player player) {
        ItemStack item = new ItemStack(Material.SHEARS);
        name(item, player, "item.harvest_shears.name", NamedTextColor.YELLOW);
        lore(item, player, "item.harvest_shears.lore");
        mark(item, ItemKeys.HARVEST_SHEARS);
        return item;
    }

    @Deprecated
    public static ItemStack createHarvestShears() {
        return createHarvestShears(null);
    }

    public static ItemStack createCookingPot(Player player) {
        ItemStack item = new ItemStack(Material.CAULDRON);
        name(item, player, "item.cooking_pot.name", NamedTextColor.YELLOW);
        lore(item, player, "item.cooking_pot.lore");
        mark(item, ItemKeys.COOKING_POT);
        return item;
    }

    @Deprecated
    public static ItemStack createCookingPot() {
        return createCookingPot(null);
    }

    public static ItemStack createBrewingBox(Player player) {
        ItemStack item = new ItemStack(Material.SMOKER);
        name(item, player, "item.brewing_box.name", NamedTextColor.YELLOW);
        lore(item, player, "item.brewing_box.lore");
        mark(item, ItemKeys.BREWING_BOX);
        return item;
    }

    @Deprecated
    public static ItemStack createBrewingBox() {
        return createBrewingBox(null);
    }

    public static ItemStack createApplePress(Player player) {
        ItemStack item = new ItemStack(Material.LOOM);
        name(item, player, "item.apple_press.name", NamedTextColor.YELLOW);
        lore(item, player, "item.apple_press.lore");
        mark(item, ItemKeys.APPLE_PRESS);
        return item;
    }

    @Deprecated
    public static ItemStack createApplePress() {
        return createApplePress(null);
    }

    public static ItemStack createAppleMash(Player player) {
        ItemStack item = new ItemStack(Material.BROWN_DYE);
        name(item, player, "item.apple_mash.name", NamedTextColor.YELLOW);
        lore(item, player, "item.apple_mash.lore");
        mark(item, ItemKeys.APPLE_MASH);
        return item;
    }

    @Deprecated
    public static ItemStack createAppleMash() {
        return createAppleMash(null);
    }

    public static ItemStack createAppleJuice(Player player) {
        ItemStack item = createBeer(BeerType.APPLE_JUICE, BeerQuality.HIGH);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
                ItemKeys.APPLE_JUICE, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    @Deprecated
    public static ItemStack createAppleJuice() {
        return createAppleJuice(null);
    }

    public static boolean isAppleMash(ItemStack item) {
        return isCustom(item, ItemKeys.APPLE_MASH);
    }

    public static boolean isAppleJuice(ItemStack item) {
        return isCustom(item, ItemKeys.APPLE_JUICE);
    }

    public static ItemStack createAgingBarrel(Player player) {
        ItemStack item = new ItemStack(Material.BARREL);
        name(item, player, "item.aging_barrel.name", NamedTextColor.YELLOW);
        lore(item, player, "item.aging_barrel.lore");
        mark(item, ItemKeys.AGING_BARREL);
        return item;
    }

    @Deprecated
    public static ItemStack createAgingBarrel() {
        return createAgingBarrel(null);
    }

    public static ItemStack createGrapeBasin(Player player) {
        ItemStack item = new ItemStack(Material.COMPOSTER);
        name(item, player, "item.grape_basin.name", NamedTextColor.YELLOW);
        lore(item, player, "item.grape_basin.lore");
        mark(item, ItemKeys.GRAPE_BASIN);
        return item;
    }

    @Deprecated
    public static ItemStack createGrapeBasin() {
        return createGrapeBasin(null);
    }

    public static ItemStack createMusicPlayer(Player player) {
        ItemStack item = new ItemStack(Material.JUKEBOX);
        name(item, player, "item.music_player.name", NamedTextColor.YELLOW);
        lore(item, player, "item.music_player.lore");
        mark(item, ItemKeys.MUSIC_PLAYER);
        return item;
    }

    @Deprecated
    public static ItemStack createMusicPlayer() {
        return createMusicPlayer(null);
    }

    public static ItemStack createGrapeSeed(Player player, GrapeType type) {
        ItemStack item = new ItemStack(type.getSeedMaterial());
        name(item, lang(type.seedNameKey()), NamedTextColor.WHITE);
        lore(item, lang(type.loreKey()));
        mark(item, type.getSeedKey());
        return item;
    }

    public static org.bukkit.profile.PlayerProfile cornHeadProfile() {
        PlayerProfile profile = Bukkit.createPlayerProfile(CORN_UUID);
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(extractTextureUrl(CORN_TEXTURE)));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        profile.setTextures(textures);
        return profile;
    }

    @Deprecated
    public static ItemStack createGrapeSeed(GrapeType type) {
        return createGrapeSeed(null, type);
    }

    public static ItemStack createGrape(Player player, GrapeType type) {
        ItemStack item = new ItemStack(type.getGrapeMaterial());
        name(item, lang(type.grapeNameKey()), NamedTextColor.WHITE);
        lore(item, lang(type.loreKey()));
        lore(item, lang("item.grape.lore_1"));
        lore(item, lang("item.grape.lore_2"));
        mark(item, type.getGrapeKey());

        applyFood(item, 2, 1.0f);

        return item;
    }

    @Deprecated
    public static ItemStack createGrape(GrapeType type) {
        return createGrape(null, type);
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

    public static ItemStack createCherry(Player player) {
        ItemStack item = new ItemStack(Material.SWEET_BERRIES);
        name(item, player, "item.cherry.name", NamedTextColor.WHITE);
        lore(item, player, "item.cherry.lore_1");
        lore(item, player, "item.cherry.lore_2");
        lore(item, player, "item.cherry.lore_3");
        mark(item, ItemKeys.CHERRY);

        applyFood(item, 2, 1.4f);

        return item;
    }

    @Deprecated
    public static ItemStack createCherry() {
        return createCherry(null);
    }

    public static ItemStack createRottenCherry(Player player) {
        ItemStack item = new ItemStack(Material.SWEET_BERRIES);
        name(item, player, "item.rotten_cherry.name", NamedTextColor.WHITE);
        lore(item, player, "item.rotten_cherry.lore_1");
        lore(item, player, "item.rotten_cherry.lore_2");
        mark(item, ItemKeys.ROTTEN_CHERRY);

        applyFood(item, 1, 1.0f);

        return item;
    }

    @Deprecated
    public static ItemStack createRottenCherry() {
        return createRottenCherry(null);
    }

    public static boolean isCherry(ItemStack item) {
        return isCustom(item, ItemKeys.CHERRY);
    }

    public static boolean isRottenCherry(ItemStack item) {
        return isCustom(item, ItemKeys.ROTTEN_CHERRY);
    }

    public static ItemStack createWheatBeer(Player player) {
        return createBeer(player, BeerType.WHEAT_BEER, BeerQuality.HIGH);
    }

    @Deprecated
    public static ItemStack createWheatBeer() {
        return createWheatBeer((Player) null);
    }

    public static ItemStack createWheatBeer(Player player, BeerQuality quality) {
        return createBeer(player, BeerType.WHEAT_BEER, quality);
    }

    @Deprecated
    public static ItemStack createWheatBeer(BeerQuality quality) {
        return createWheatBeer(null, quality);
    }

    public static ItemStack createBeer(Player player, BeerType type, BeerQuality quality) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (item.getType() == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
            applyBeerSkin(skullMeta, type);
        } else if (item.getType() == Material.POTION && meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(type.getPotionColor());
            potionMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            potionMeta.setMaxStackSize(64);
        }

        meta.displayName(plainName(lang(type.getNameKey()), NamedTextColor.YELLOW));

        List<Component> loreList = new ArrayList<>();
        loreList.add(plainName(lang(type.getLoreKey()), NamedTextColor.GRAY));

        if (type.hasQuality()) {
            loreList.add(plainName(lang("lore.quality_prefix"), NamedTextColor.GRAY)
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

    @Deprecated
    public static ItemStack createBeer(BeerType type, BeerQuality quality) {
        return createBeer(null, type, quality);
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

    public static ItemStack createTimerHead(Player player, int remainingTicks) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, TIMER_HEAD_UUID, TIMER_HEAD_TEXTURE);
        int seconds = (remainingTicks + 19) / 20;
        meta.displayName(plainName(seconds + "s", NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    @Deprecated
    public static ItemStack createTimerHead(int remainingTicks) {
        return createTimerHead(null, remainingTicks);
    }

    public static ItemStack createTimerHeadIdle(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, TIMER_HEAD_UUID, TIMER_HEAD_TEXTURE);
        meta.displayName(plainName(lang("gui.view_recipe"), NamedTextColor.WHITE));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    @Deprecated
    public static ItemStack createTimerHeadIdle() {
        return createTimerHeadIdle(null);
    }

    public static ItemStack createTimerHead2(Player player, int remainingTicks) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, TIMER_HEAD_2_UUID, TIMER_HEAD_2_TEXTURE);
        int seconds = (remainingTicks + 19) / 20;
        meta.displayName(plainName(seconds + "s", NamedTextColor.YELLOW));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    @Deprecated
    public static ItemStack createTimerHead2(int remainingTicks) {
        return createTimerHead2(null, remainingTicks);
    }

    public static ItemStack createTimerHeadIdle2(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        applySkin(meta, TIMER_HEAD_2_UUID, TIMER_HEAD_2_TEXTURE);
        meta.displayName(plainName(lang("gui.view_recipe"), NamedTextColor.WHITE));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        head.setItemMeta(meta);
        return head;
    }

    @Deprecated
    public static ItemStack createTimerHeadIdle2() {
        return createTimerHeadIdle2(null);
    }

    public static boolean isCustom(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(key, PersistentDataType.BYTE);
    }

    public static java.util.Set<NamespacedKey> markersOf(ItemStack item) {
        java.util.Set<NamespacedKey> found = new java.util.LinkedHashSet<>();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return found;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        for (NamespacedKey key : ItemKeys.allMarkers()) {
            if (key != null && pdc.has(key, PersistentDataType.BYTE)) found.add(key);
        }
        return found;
    }
}