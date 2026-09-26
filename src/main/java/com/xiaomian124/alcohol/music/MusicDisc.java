package com.xiaomian124.alcohol.music;

import org.bukkit.Material;

public class MusicDisc {

    public final String fileName;
    public final String displayName;
    public final Material icon;

    public MusicDisc(String fileName, String displayName, Material icon) {
        this.fileName = fileName;
        this.displayName = displayName;
        this.icon = icon;
    }
}