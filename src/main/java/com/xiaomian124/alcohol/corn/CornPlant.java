package com.xiaomian124.alcohol.corn;

import org.bukkit.Location;

public class CornPlant {

    public Location baseLocation;

    public int stage = 1;
    public int harvestedTimes = 0;
    public int stageTicks = 0;
    public int stageThreshold = 0;

    public CornPlant(Location baseLocation) {
        this.baseLocation = baseLocation.clone();
    }
}