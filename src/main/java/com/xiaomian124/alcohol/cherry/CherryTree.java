package com.xiaomian124.alcohol.cherry;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class CherryTree {

    public final Set<String> logs = new HashSet<>();
    public final Set<String> leaves = new HashSet<>();

    public Location saplingLocation;

    public CherryTree(Location sapling) {
        this.saplingLocation = sapling == null ? null : sapling.clone();
    }
}