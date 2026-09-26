package com.xiaomian124.alcohol.music;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import org.bukkit.Location;

public class MusicPlayer {

    public final Location location;
    public int currentIndex = 0;
    public boolean playing = false;
    public boolean singleLoop = false;
    public short pausedTick = -1;

    public SongPlayer songPlayer;
    public Song currentSong;

    public MusicPlayer(Location loc) {
        this.location = loc.clone();
    }
}