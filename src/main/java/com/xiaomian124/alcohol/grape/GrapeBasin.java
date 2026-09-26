package com.xiaomian124.alcohol.grape;

import org.bukkit.Location;

public class GrapeBasin {

    public Location location;
    public GrapeType grapeType;
    public int juiceCount;
    public int jumpCount;

    public GrapeBasin(Location loc) {
        this.location = loc.clone();
    }
}