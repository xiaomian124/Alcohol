package com.xiaomian124.alcohol.grape;

import org.bukkit.Location;
import org.bukkit.Material;

public class GrapePlant {

    public Location anchorLocation;
    public GrapeType type;
    public int growthStage = 0;
    public int harvestCount = 0;
    public int growTicks = 0;
    public int growThreshold = 0;

    public Material originalFenceMaterial;
    public boolean originalFenceWaterlogged = false;

    public GrapePlant(Location anchor, GrapeType type) {
        this.anchorLocation = anchor.clone();
        this.type = type;
    }
}