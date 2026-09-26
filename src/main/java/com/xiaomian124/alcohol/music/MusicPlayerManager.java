package com.xiaomian124.alcohol.music;

import com.xiaomian124.alcohol.AlcoholPlugin;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class MusicPlayerManager {

    private MusicPlayerManager() {}

    public static final List<MusicDisc> DISCS = new ArrayList<>();

    public static int BROADCAST_RANGE = 16;

    private static final Material[] ICONS = {
            Material.MUSIC_DISC_13, Material.MUSIC_DISC_CAT,
            Material.MUSIC_DISC_BLOCKS, Material.MUSIC_DISC_CHIRP,
            Material.MUSIC_DISC_FAR, Material.MUSIC_DISC_MALL,
            Material.MUSIC_DISC_MELLOHI, Material.MUSIC_DISC_STAL,
            Material.MUSIC_DISC_STRAD, Material.MUSIC_DISC_WARD,
            Material.MUSIC_DISC_11, Material.MUSIC_DISC_WAIT,
            Material.MUSIC_DISC_OTHERSIDE, Material.MUSIC_DISC_5,
            Material.MUSIC_DISC_PIGSTEP, Material.MUSIC_DISC_RELIC
    };

    public static void loadDiscs() {
        DISCS.clear();
        releaseBuiltinSongs();

        File songsDir = new File(AlcoholPlugin.getInstance().getDataFolder(), "songs");
        if (!songsDir.exists()) songsDir.mkdirs();

        File[] files = songsDir.listFiles((dir, name) -> name.endsWith(".nbs"));
        if (files == null || files.length == 0) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("没有找到任何nbs文件！请把nbs歌曲放入 "
                            + songsDir.getAbsolutePath());
            return;
        }

        Arrays.sort(files, Comparator.comparing(File::getName));

        int index = 0;
        for (File f : files) {
            String fileName = f.getName().substring(0, f.getName().length() - 4);
            Material icon = ICONS[index % ICONS.length];
            DISCS.add(new MusicDisc(fileName, fileName, icon));
            index++;
        }

        AlcoholPlugin.getInstance().getLogger()
                .info("已加载 " + DISCS.size() + " 首nbs音乐");
    }

    private static void releaseBuiltinSongs() {
        try {
            var codeSource = AlcoholPlugin.class.getProtectionDomain().getCodeSource();
            if (codeSource == null) return;

            File jarFile = new File(URLDecoder.decode(
                    codeSource.getLocation().getFile(), "UTF-8"));
            if (!jarFile.isFile()) return;

            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (!name.startsWith("songs/") || !name.endsWith(".nbs")) continue;
                    if (entry.isDirectory()) continue;

                    File outFile = new File(
                            AlcoholPlugin.getInstance().getDataFolder(), name);
                    if (outFile.exists() && outFile.length() == entry.getSize()) continue;

                    outFile.getParentFile().mkdirs();
                    try (InputStream in = jar.getInputStream(entry);
                         OutputStream out = new FileOutputStream(outFile)) {
                        in.transferTo(out);
                    }
                }
            }
        } catch (Exception e) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("释放内置nbs歌曲失败: " + e.getMessage());
        }
    }

    private static final Map<String, MusicPlayer> PLAYERS = new HashMap<>();
    private static int tickCounter = 0;

    public static String key(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }

    public static MusicPlayer getOrCreate(Location loc) {
        return PLAYERS.computeIfAbsent(key(loc), k -> new MusicPlayer(loc));
    }

    public static MusicPlayer get(Location loc) {
        return PLAYERS.get(key(loc));
    }

    public static void remove(Location loc) {
        MusicPlayer p = PLAYERS.remove(key(loc));
        if (p != null && p.songPlayer != null) {
            p.songPlayer.setPlaying(false);
            p.songPlayer.destroy();
        }
    }

    public static Song loadSong(MusicDisc disc) {
        try {
            File file = new File(AlcoholPlugin.getInstance().getDataFolder(),
                    "songs/" + disc.fileName + ".nbs");
            if (!file.exists()) {
                AlcoholPlugin.getInstance().getLogger()
                        .warning("找不到nbs歌曲文件: " + file.getAbsolutePath());
                return null;
            }
            return NBSDecoder.parse(file);
        } catch (Exception e) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("加载nbs歌曲失败 " + disc.fileName + ": " + e.getMessage());
            return null;
        }
    }

    public static void play(MusicPlayer mp, int index) {
        if (DISCS.isEmpty()) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("播放失败：nbs歌曲列表为空");
            return;
        }

        index = ((index % DISCS.size()) + DISCS.size()) % DISCS.size();

        if (mp.songPlayer != null) {
            mp.songPlayer.setPlaying(false);
            mp.songPlayer.destroy();
            mp.songPlayer = null;
        }

        MusicDisc disc = DISCS.get(index);
        Song song = loadSong(disc);
        if (song == null) {
            AlcoholPlugin.getInstance().getLogger()
                    .warning("播放失败：无法加载 " + disc.fileName + ".nbs");
            return;
        }

        RadioSongPlayer player = new RadioSongPlayer(song);
        updateListeners(mp, player);
        player.setPlaying(true);

        mp.songPlayer = player;
        mp.currentSong = song;
        mp.currentIndex = index;
        mp.playing = true;
        mp.pausedTick = -1;

        notifyListeners(mp, false);
    }

    private static void notifyListeners(MusicPlayer mp, boolean resuming) {
        if (mp.location == null) return;
        World world = mp.location.getWorld();
        if (world == null) return;

        Location center = mp.location.clone().add(0.5, 0.5, 0.5);
        double rangeSq = (double) BROADCAST_RANGE * BROADCAST_RANGE;

        String title = getTitle(mp);
        String author = getAuthor(mp);

        String text = resuming
                ? "正在继续播放音乐：" + title + " - " + author
                : "正在播放音乐：" + title + " - " + author;

        Component msg = Component.text(text).color(NamedTextColor.YELLOW);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getWorld().equals(world)) continue;
            if (p.getLocation().distanceSquared(center) > rangeSq) continue;
            p.sendActionBar(msg);
        }
    }

    private static void updateListeners(MusicPlayer mp, SongPlayer player) {
        if (player == null) return;

        Location center = mp.location.clone().add(0.5, 0.5, 0.5);
        double rangeSq = (double) BROADCAST_RANGE * BROADCAST_RANGE;
        Set<java.util.UUID> listeners = player.getPlayerUUIDs();

        String title = getTitle(mp);
        String author = getAuthor(mp);
        Component playMsg = Component.text(
                        "正在播放音乐：" + title + " - " + author)
                .color(NamedTextColor.YELLOW);

        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean sameWorld = p.getWorld().equals(center.getWorld());
            boolean inRange = sameWorld
                    && p.getLocation().distanceSquared(center) <= rangeSq;
            boolean alreadyAdded = listeners.contains(p.getUniqueId());

            if (inRange && !alreadyAdded) {
                player.addPlayer(p);
                p.sendActionBar(playMsg);
            } else if (!inRange && alreadyAdded) {
                player.removePlayer(p);
            }
        }
    }

    public static void pause(MusicPlayer mp) {
        if (mp.songPlayer == null) return;
        mp.pausedTick = mp.songPlayer.getTick();
        mp.songPlayer.setPlaying(false);
        mp.playing = false;

        notifyPaused(mp);
    }

    private static void notifyPaused(MusicPlayer mp) {
        if (mp.location == null) return;
        World world = mp.location.getWorld();
        if (world == null) return;

        Location center = mp.location.clone().add(0.5, 0.5, 0.5);
        double rangeSq = (double) BROADCAST_RANGE * BROADCAST_RANGE;

        Component msg = Component.text("音乐已暂停").color(NamedTextColor.YELLOW);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getWorld().equals(world)) continue;
            if (p.getLocation().distanceSquared(center) > rangeSq) continue;
            p.sendActionBar(msg);
        }
    }

    public static void resume(MusicPlayer mp) {
        if (mp.songPlayer == null) {
            play(mp, mp.currentIndex);
            return;
        }
        if (mp.pausedTick >= 0) {
            mp.songPlayer.setTick(mp.pausedTick);
        }
        mp.songPlayer.setPlaying(true);
        mp.playing = true;
        mp.pausedTick = -1;

        notifyListeners(mp, true);
    }

    public static void next(MusicPlayer mp) {
        play(mp, mp.currentIndex + 1);
    }

    public static void prev(MusicPlayer mp) {
        play(mp, mp.currentIndex - 1);
    }

    public static void toggleLoop(MusicPlayer mp) {
        mp.singleLoop = !mp.singleLoop;
    }

    public static String getAuthor(MusicPlayer mp) {
        if (mp.currentSong == null) return "Alcohol Plugin";
        String author = mp.currentSong.getAuthor();
        if (author == null || author.isEmpty()
                || author.equalsIgnoreCase("Unknown")) {
            return "Alcohol Plugin";
        }
        return author;
    }

    public static String getTitle(MusicPlayer mp) {
        if (mp.currentSong == null) {
            MusicDisc d = DISCS.isEmpty() ? null : DISCS.get(mp.currentIndex);
            return d != null ? d.displayName : "未知歌曲";
        }
        String title = mp.currentSong.getTitle();
        if (title == null || title.isEmpty()
                || title.equalsIgnoreCase("Unknown Title")) {
            MusicDisc d = DISCS.get(mp.currentIndex);
            return d.displayName;
        }
        return title;
    }

    public static void tickAll() {
        tickCounter++;

        Iterator<Map.Entry<String, MusicPlayer>> it = PLAYERS.entrySet().iterator();
        while (it.hasNext()) {
            MusicPlayer mp = it.next().getValue();

            Block block = mp.location.getBlock();
            if (block.getType() != Material.JUKEBOX) {
                if (mp.songPlayer != null) {
                    mp.songPlayer.setPlaying(false);
                    mp.songPlayer.destroy();
                }
                it.remove();
                continue;
            }

            if (!mp.playing || mp.songPlayer == null || mp.currentSong == null) {
                continue;
            }

            if (tickCounter % 20 == 0) {
                updateListeners(mp, mp.songPlayer);
            }

            if (tickCounter % 5 == 0) {
                spawnMusicParticles(mp);
            }

            if (!mp.songPlayer.isPlaying()
                    || mp.songPlayer.getTick() >= mp.currentSong.getLength()) {
                if (mp.singleLoop) {
                    play(mp, mp.currentIndex);
                } else {
                    play(mp, mp.currentIndex + 1);
                }
            }
        }
    }

    private static void spawnMusicParticles(MusicPlayer mp) {
        if (mp.location == null) return;
        World world = mp.location.getWorld();
        if (world == null) return;

        world.spawnParticle(
                Particle.NOTE,
                mp.location.clone().add(0.5, 1.2, 0.5),
                1,
                0.3, 0.1, 0.3,
                0.5
        );
    }

    public static Set<String> getKeys() {
        return new HashSet<>(PLAYERS.keySet());
    }

    public static Location parseLocation(String k) {
        String[] parts = k.split(":");
        if (parts.length != 4) return null;
        World w = Bukkit.getWorld(parts[0]);
        if (w == null) return null;
        try {
            return new Location(w,
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static List<MusicPlayer> getAll() {
        return new ArrayList<>(PLAYERS.values());
    }
}