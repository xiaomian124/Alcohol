package com.xiaomian124.alcohol.grape;

import com.xiaomian124.alcohol.BeerType;
import com.xiaomian124.alcohol.ItemFactory;
import com.xiaomian124.alcohol.ItemKeys;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public enum GrapeType {

    RED("red",
            Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES,
            Material.COCOA_BEANS,
            GrowthMode.GROUND, TreeType.OAK,
            BeerType.RED_GRAPE_JUICE,
            "RED_GRAPE_SEED", "RED_GRAPE"),

    WHITE("white",
            Material.SWEET_BERRY_BUSH, Material.TORCHFLOWER_SEEDS,
            Material.COCOA_BEANS,
            GrowthMode.GROUND, TreeType.OAK,
            BeerType.WHITE_GRAPE_JUICE,
            "WHITE_GRAPE_SEED", "WHITE_GRAPE"),

    SAVANNA_RED("savanna_red",
            Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES,
            Material.COCOA_BEANS,
            GrowthMode.GROUND, TreeType.ACACIA,
            BeerType.SAVANNA_RED_GRAPE_JUICE,
            "SAVANNA_RED_GRAPE_SEED", "SAVANNA_RED_GRAPE"),

    SAVANNA_WHITE("savanna_white",
            Material.SWEET_BERRY_BUSH, Material.TORCHFLOWER_SEEDS,
            Material.COCOA_BEANS,
            GrowthMode.GROUND, TreeType.ACACIA,
            BeerType.SAVANNA_WHITE_GRAPE_JUICE,
            "SAVANNA_WHITE_GRAPE_SEED", "SAVANNA_WHITE_GRAPE"),

    JUNGLE_RED("jungle_red",
            Material.VINE, Material.SWEET_BERRIES,
            Material.COCOA_BEANS,
            GrowthMode.VINE, TreeType.JUNGLE,
            BeerType.JUNGLE_RED_GRAPE_JUICE,
            "JUNGLE_RED_GRAPE_SEED", "JUNGLE_RED_GRAPE"),

    JUNGLE_WHITE("jungle_white",
            Material.VINE, Material.TORCHFLOWER_SEEDS,
            Material.COCOA_BEANS,
            GrowthMode.VINE, TreeType.JUNGLE,
            BeerType.JUNGLE_WHITE_GRAPE_JUICE,
            "JUNGLE_WHITE_GRAPE_SEED", "JUNGLE_WHITE_GRAPE"),

    TAIGA_RED("taiga_red",
            Material.DARK_OAK_LEAVES, Material.SWEET_BERRIES,
            Material.COCOA_BEANS,
            GrowthMode.FENCE, TreeType.SPRUCE,
            BeerType.TAIGA_RED_GRAPE_JUICE,
            "TAIGA_RED_GRAPE_SEED", "TAIGA_RED_GRAPE"),

    TAIGA_WHITE("taiga_white",
            Material.DARK_OAK_LEAVES, Material.TORCHFLOWER_SEEDS,
            Material.COCOA_BEANS,
            GrowthMode.FENCE, TreeType.SPRUCE,
            BeerType.TAIGA_WHITE_GRAPE_JUICE,
            "TAIGA_WHITE_GRAPE_SEED", "TAIGA_WHITE_GRAPE");

    public enum GrowthMode { GROUND, VINE, FENCE }
    public enum TreeType { OAK, ACACIA, JUNGLE, SPRUCE }

    private final String langBase;
    private final Material plantMaterial;
    private final Material grapeMaterial;
    private final Material seedMaterial;
    private final GrowthMode growthMode;
    private final TreeType treeType;
    private final BeerType beerType;
    private final String seedKeyName;
    private final String grapeKeyName;

    GrapeType(String langBase,
              Material plantMaterial, Material grapeMaterial,
              Material seedMaterial, GrowthMode growthMode,
              TreeType treeType, BeerType beerType,
              String seedKeyName, String grapeKeyName) {
        this.langBase = langBase;
        this.plantMaterial = plantMaterial;
        this.grapeMaterial = grapeMaterial;
        this.seedMaterial = seedMaterial;
        this.growthMode = growthMode;
        this.treeType = treeType;
        this.beerType = beerType;
        this.seedKeyName = seedKeyName;
        this.grapeKeyName = grapeKeyName;
    }

    public String langBase() { return "grape." + langBase; }

    public String grapeNameKey() { return langBase() + ".name"; }
    public String seedNameKey()  { return langBase() + ".seed_name"; }
    public String loreKey()      { return langBase() + ".lore"; }

    public String getGrapeName() { return ItemFactory.lang(grapeNameKey()); }
    public String getSeedName()  { return ItemFactory.lang(seedNameKey()); }
    public String getLore()      { return ItemFactory.lang(loreKey()); }

    public Material getGrapeMaterial() { return grapeMaterial; }
    public Material getSeedMaterial()  { return seedMaterial; }
    public Material getPlantMaterial() { return plantMaterial; }
    public GrowthMode getGrowthMode() { return growthMode; }
    public TreeType getTreeType()     { return treeType; }
    public BeerType getBeerType()     { return beerType; }

    public NamespacedKey getSeedKey()  { return getKey(seedKeyName); }
    public NamespacedKey getGrapeKey() { return getKey(grapeKeyName); }

    private static NamespacedKey getKey(String fieldName) {
        try {
            return (NamespacedKey) ItemKeys.class.getField(fieldName).get(null);
        } catch (Exception e) { return null; }
    }

    public static GrapeType fromSeedKey(NamespacedKey key) {
        if (key == null) return null;
        for (GrapeType t : values()) if (key.equals(t.getSeedKey())) return t;
        return null;
    }

    public static GrapeType fromGrapeKey(NamespacedKey key) {
        if (key == null) return null;
        for (GrapeType t : values()) if (key.equals(t.getGrapeKey())) return t;
        return null;
    }

    public static GrapeType fromBeerType(BeerType beer) {
        if (beer == null) return null;
        for (GrapeType t : values()) if (t.getBeerType() == beer) return t;
        return null;
    }
}
