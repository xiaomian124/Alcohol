package com.xiaomian124.alcohol;

import com.xiaomian124.alcohol.entity.CatGirlManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class SpecialEffectManager {

    private SpecialEffectManager() {}

    public static final org.bukkit.NamespacedKey ELDER_CAT_KEY =
            new org.bukkit.NamespacedKey("alcohol", "elder_cat");

    private static final Map<UUID, Map<BeerType, Long>> ACTIVE = new HashMap<>();
    private static final Map<UUID, List<UUID>> ATTACHED = new HashMap<>();
    private static final Map<UUID, Long> CLIMBING = new HashMap<>();
    private static final Map<UUID, Long> JUMP_COOLDOWN = new HashMap<>();
    private static final Map<UUID, Set<Location>> HIDDEN_WEBS = new HashMap<>();

    private static int tickCounter = 0;

    public static int duration(BeerType type) {
        return switch (type) {
            case HONEY_WINE -> 200;
            case TROPICAL_CREEPER_WINE -> 1;
            case TROPICAL_SEA_WINE -> 1200;
            case LAMBRUSCO -> 600;
            case SPIDER_SPECIAL_WINE -> 600;
            case FROST_WHITE_WINE -> 400;
            case AEGIS_WHITE_WINE -> 600;
            case BLAZE_WALKER_WINE -> 800;
            case CHORUS_WINE -> 300;
            case BOUNCE_WHITE_WINE -> 200;
            case MAGNETIC_WINE -> 400;
            case SPIDER_RED_WINE -> 800;
            case CATGIRL_RED_WINE -> 500;
            case ELDER_WINE -> 2400;
            default -> 0;
        };
    }

    public static void apply(Player p, BeerType type) {
        switch (type) {
            case TROPICAL_CREEPER_WINE -> { doExplode(p); return; }
            case HONEY_WINE -> spawnHoneyBees(p);
            case CATGIRL_RED_WINE -> CatGirlManager.spawnCatGirl(p);
            case ELDER_WINE -> spawnCat(p);
            default -> {}
        }

        long end = System.currentTimeMillis() + duration(type) * 50L;
        ACTIVE.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>()).put(type, end);
        DrinkEffectDisplay.recordSpecial(p, getSpecialName(type), duration(type));

        EffectStorage.saveAll();
    }

    public static void onPlayerJump(Player p) {
        // 飞升
        if (hasEffect(p, BeerType.BOUNCE_WHITE_WINE)) {
            Long last = JUMP_COOLDOWN.get(p.getUniqueId());
            long now = System.currentTimeMillis();
            if (last == null || now - last > 200) {
                JUMP_COOLDOWN.put(p.getUniqueId(), now);
                Bukkit.getScheduler().runTaskLater(AlcoholPlugin.getInstance(), () -> {
                    if (!p.isOnline()) return;
                    Vector v = p.getVelocity();
                    p.setVelocity(new Vector(v.getX(), 2.4, v.getZ()));
                }, 1L);
            }
        }
    }

    private static boolean dirty = false;

    public static void markDirty() {
        dirty = true;
    }

    public static void tickAll() {
        long now = System.currentTimeMillis();
        tickCounter++;

        boolean changed = false;

        Iterator<Map.Entry<UUID, Map<BeerType, Long>>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Map<BeerType, Long>> entry = it.next();
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p == null || !p.isOnline()) {
                removeAttached(entry.getKey(), true);
                it.remove();
                changed = true;
                continue;
            }

            Map<BeerType, Long> effects = entry.getValue();
            Iterator<Map.Entry<BeerType, Long>> eit = effects.entrySet().iterator();
            while (eit.hasNext()) {
                Map.Entry<BeerType, Long> e = eit.next();
                BeerType type = e.getKey();
                if (now >= e.getValue()) {
                    onExpire(p, type);
                    eit.remove();
                    changed = true;
                    continue;
                }
                tickEffect(p, type);
            }
            if (effects.isEmpty()) it.remove();
        }

        if (changed) {
            EffectStorage.saveAll();
        }
    }

    private static void tickEffect(Player p, BeerType type) {
        switch (type) {
            case TROPICAL_SEA_WINE -> tickWaterWalk(p);
            case LAMBRUSCO -> tickAutoJump(p);
            case SPIDER_SPECIAL_WINE -> tickClimb(p);
            case FROST_WHITE_WINE -> tickFreeze(p);
            case BLAZE_WALKER_WINE -> tickLavaWalk(p);
            case CHORUS_WINE -> tickTeleport(p);
            case MAGNETIC_WINE -> tickMagnet(p);
            case SPIDER_RED_WINE -> {
                if (tickCounter % 5 == 0) tickWebImmune(p);
            }
            case HONEY_WINE -> tickFollowEntities(p, 1.5);
            case ELDER_WINE -> tickCat(p);
            default -> {}
        }
    }

    private static void onExpire(Player p, BeerType type) {
        switch (type) {
            case HONEY_WINE -> removeAttached(p.getUniqueId(), false);
            case ELDER_WINE -> removeAttached(p.getUniqueId(), true);
            case CATGIRL_RED_WINE -> CatGirlManager.removeCatGirl(p);
            case FROST_WHITE_WINE -> p.setFreezeTicks(0);
            case SPIDER_SPECIAL_WINE -> CLIMBING.remove(p.getUniqueId());
            case BOUNCE_WHITE_WINE -> JUMP_COOLDOWN.remove(p.getUniqueId());
            case SPIDER_RED_WINE -> restoreAllWebs(p);
            default -> {}
        }
    }

    public static void clearAll(Player p) {
        Map<BeerType, Long> effects = ACTIVE.remove(p.getUniqueId());
        if (effects != null) {
            for (BeerType type : effects.keySet()) {
                onExpire(p, type);
            }
        }
        CLIMBING.remove(p.getUniqueId());
        JUMP_COOLDOWN.remove(p.getUniqueId());
        restoreAllWebs(p);

        EffectStorage.saveAll();
    }

    private static void doExplode(Player p) {
        Location loc = p.getLocation();
        loc.getWorld().createExplosion(loc, 3.0f, false, true, p);
    }

    private static void spawnHoneyBees(Player p) {
        World w = p.getWorld();
        List<UUID> uuids = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Bee bee = (Bee) w.spawnEntity(p.getLocation(), EntityType.BEE);
            bee.setAI(false);
            bee.setSilent(true);
            bee.setInvulnerable(true);
            bee.setPersistent(true);
            uuids.add(bee.getUniqueId());
        }
        ATTACHED.put(p.getUniqueId(), uuids);
    }

    private static void spawnCat(Player p) {
        World w = p.getWorld();
        Location spawnLoc = p.getLocation().clone().add(
                p.getLocation().getDirection().multiply(1.5));

        Cat cat = (Cat) w.spawnEntity(spawnLoc, EntityType.CAT);
        cat.setCatType(Cat.Type.RED);
        cat.setOwner(p);
        cat.setSitting(false);
        cat.setAI(true);
        cat.setInvulnerable(true);
        cat.setSilent(false);
        cat.setPersistent(true);
        cat.customName(Component.text("耄耋").color(NamedTextColor.GOLD));
        cat.setCustomNameVisible(true);
        cat.getPersistentDataContainer().set(
                ELDER_CAT_KEY, PersistentDataType.BYTE, (byte) 1);

        w.spawnParticle(Particle.CRIT,
                spawnLoc.clone().add(0, 0.5, 0),
                20, 0.4, 0.4, 0.4, 0.1);
        w.playSound(spawnLoc, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);

        ATTACHED.put(p.getUniqueId(), new ArrayList<>(List.of(cat.getUniqueId())));
    }

    private static void tickWaterWalk(Player p) { walkOnLiquid(p, Material.WATER); }
    private static void tickLavaWalk(Player p)  { walkOnLiquid(p, Material.LAVA); }

    private static void walkOnLiquid(Player p, Material liquid) {
        if (p.isSneaking()) {
            if (!p.hasGravity()) p.setGravity(true);
            return;
        }

        Location loc = p.getLocation();
        Block below = loc.clone().subtract(0, 0.1, 0).getBlock();
        Block feet = loc.getBlock();

        boolean onLiquid = below.getType() == liquid || feet.getType() == liquid;

        if (!onLiquid) {
            if (!p.hasGravity()) p.setGravity(true);
            return;
        }

        if (p.hasGravity()) p.setGravity(false);

        double surfaceY;
        if (feet.getType() == liquid) {
            surfaceY = feet.getY() + 1.0;
        } else {
            surfaceY = below.getY() + 1.0;
        }

        Vector v = p.getVelocity();
        if (Math.abs(v.getY()) > 0.001) {
            p.setVelocity(new Vector(v.getX(), 0, v.getZ()));
        }

        if (loc.getY() < surfaceY - 0.1) {
            Location target = loc.clone();
            target.setY(surfaceY);
            p.teleport(target);
        }
    }

    private static void tickAutoJump(Player p) {
        if (p.isOnGround()) {
            Vector v = p.getVelocity();
            p.setVelocity(new Vector(v.getX(), 0.42, v.getZ()));
        }
    }

    private static void tickClimb(Player p) {
        if (!isWallAhead(p)) return;

        Vector vel = p.getVelocity();
        if (vel.getY() < -0.3) return;

        p.setVelocity(new Vector(vel.getX(), 0.28, vel.getZ()));
        p.setFallDistance(0);
    }

    private static boolean isWallAhead(Player p) {
        Location loc = p.getLocation();
        Vector dir = loc.getDirection().setY(0).normalize();

        for (double off : new double[]{0.3, 0.6, 0.9}) {
            for (double h : new double[]{0.0, 0.5, 1.0, 1.5, 2.0}) {
                Location check = loc.clone()
                        .add(dir.clone().multiply(off))
                        .add(0, h, 0);
                if (check.getBlock().getType().isSolid()) return true;
            }
        }
        return false;
    }

    private static void tickFreeze(Player p) {
        p.setFreezeTicks(Math.max(p.getFreezeTicks(), 140));
        if (tickCounter % 20 == 0) p.damage(1);
    }

    private static void tickTeleport(Player p) {
        if (tickCounter % 3 != 0) return;

        Location loc = p.getLocation();
        double dx = ThreadLocalRandom.current().nextDouble(-8, 8);
        double dy = ThreadLocalRandom.current().nextDouble(-3, 3);
        double dz = ThreadLocalRandom.current().nextDouble(-8, 8);
        Location target = loc.clone().add(dx, dy, dz);

        if (target.getBlock().getType() != Material.AIR) return;
        if (target.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) return;
        if (target.getBlockY() < target.getWorld().getMinHeight() + 1) return;

        p.teleport(target);
        p.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        target.getWorld().spawnParticle(Particle.PORTAL,
                target.clone().add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.5);
    }

    private static void tickMagnet(Player p) {
        Location center = p.getLocation();
        for (Entity e : p.getWorld().getNearbyEntities(center, 20, 20, 20)) {
            if (!(e instanceof Item)) continue;
            Vector dir = center.toVector().subtract(e.getLocation().toVector());
            if (dir.lengthSquared() < 4) continue;
            e.setVelocity(dir.normalize().multiply(0.5));
        }
    }

    private static void tickWebImmune(Player p) {
        Set<Location> hidden = HIDDEN_WEBS.computeIfAbsent(
                p.getUniqueId(), k -> new HashSet<>());

        Location center = p.getLocation();

        Set<Location> nearby = new HashSet<>();
        int radius = 6;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) continue;

                    Location check = center.clone().add(dx, dy, dz);
                    if (check.getBlock().getType() == Material.COBWEB) {
                        nearby.add(check.getBlock().getLocation());
                    }
                }
            }
        }

        for (Location loc : nearby) {
            if (hidden.contains(loc)) continue;

            Block block = loc.getBlock();
            block.setType(Material.AIR, false);

            loc.getWorld().playSound(loc, Sound.BLOCK_COBWEB_BREAK, 1.0f, 1.0f);
            loc.getWorld().spawnParticle(
                    Particle.BLOCK,
                    loc.clone().add(0.5, 0.5, 0.5),
                    15, 0.3, 0.3, 0.3,
                    Material.COBWEB.createBlockData()
            );

            hidden.add(loc);
        }

        double restoreRadiusSq = 7.0 * 7.0;
        Iterator<Location> it = hidden.iterator();
        while (it.hasNext()) {
            Location loc = it.next();

            boolean stillNear = loc.getWorld().equals(center.getWorld())
                    && loc.distanceSquared(center) <= restoreRadiusSq;

            if (stillNear) continue;

            Block block = loc.getBlock();
            if (block.getType() == Material.AIR) {
                block.setType(Material.COBWEB, false);

                loc.getWorld().playSound(loc, Sound.BLOCK_COBWEB_BREAK, 1.0f, 1.0f);
                loc.getWorld().spawnParticle(
                        Particle.BLOCK,
                        loc.clone().add(0.5, 0.5, 0.5),
                        15, 0.3, 0.3, 0.3,
                        Material.COBWEB.createBlockData()
                );
            }
            it.remove();
        }
    }

    private static void restoreAllWebs(Player p) {
        Set<Location> hidden = HIDDEN_WEBS.remove(p.getUniqueId());
        if (hidden == null) return;

        for (Location loc : hidden) {
            Block block = loc.getBlock();
            if (block.getType() == Material.AIR) {
                block.setType(Material.COBWEB, false);
                loc.getWorld().playSound(loc, Sound.BLOCK_COBWEB_BREAK, 1.0f, 1.0f);
                loc.getWorld().spawnParticle(
                        Particle.BLOCK,
                        loc.clone().add(0.5, 0.5, 0.5),
                        15, 0.3, 0.3, 0.3,
                        Material.COBWEB.createBlockData()
                );
            }
        }
    }

    private static void tickFollowEntities(Player p, double radius) {
        List<UUID> uuids = ATTACHED.get(p.getUniqueId());
        if (uuids == null) return;

        int idx = 0;
        int total = uuids.size();
        for (UUID uuid : uuids) {
            Entity e = Bukkit.getEntity(uuid);
            if (e == null) continue;

            double angle = (tickCounter / 20.0) + (idx * (Math.PI * 2 / total));
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;

            Location target = p.getLocation().clone().add(ox, 1.2, oz);
            e.teleport(target);
            e.setVelocity(new Vector(0, 0, 0));
            idx++;
        }
    }

    private static void tickCat(Player p) {
        List<UUID> uuids = ATTACHED.get(p.getUniqueId());
        if (uuids == null || uuids.isEmpty()) return;
        Entity e = Bukkit.getEntity(uuids.get(0));
        if (!(e instanceof Cat cat)) return;
        if (!cat.isValid()) return;

        Location playerLoc = p.getLocation();
        Location catLoc = cat.getLocation();

        if (!cat.getWorld().equals(p.getWorld())) {
            cat.teleport(playerLoc);
            return;
        }

        double dist = catLoc.distance(playerLoc);
        if (dist > 20) {
            Location target = playerLoc.clone().add(
                    playerLoc.getDirection().multiply(-1.5));
            target.setY(playerLoc.getY());
            cat.teleport(target);
        }

        if (tickCounter % 60 == 0) {
            cat.getWorld().playSound(catLoc, Sound.ENTITY_CAT_HISS, 0.8f, 1.0f);
        }
    }

    private static void removeAttached(UUID playerUUID, boolean withEffects) {
        List<UUID> uuids = ATTACHED.remove(playerUUID);
        if (uuids != null) {
            for (UUID uuid : uuids) {
                Entity e = Bukkit.getEntity(uuid);
                if (e == null) continue;
                if (withEffects) {
                    Location loc = e.getLocation();
                    e.getWorld().spawnParticle(Particle.POOF,
                            loc.clone().add(0, 0.5, 0),
                            15, 0.3, 0.3, 0.3, 0.05);
                    e.getWorld().playSound(loc,
                            Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                }
                e.remove();
            }
        }
    }

    public static boolean hasEffect(Player p, BeerType type) {
        Map<BeerType, Long> effects = ACTIVE.get(p.getUniqueId());
        if (effects == null) return false;
        Long end = effects.get(type);
        return end != null && System.currentTimeMillis() < end;
    }

    private static String getSpecialName(BeerType type) {
        return switch (type) {
            case HONEY_WINE -> "蜂蜜";
            case TROPICAL_SEA_WINE -> "水上行者";
            case LAMBRUSCO -> "狂欢";
            case SPIDER_SPECIAL_WINE -> "攀爬";
            case FROST_WHITE_WINE -> "冰冻";
            case AEGIS_WHITE_WINE -> "护盾";
            case BLAZE_WALKER_WINE -> "岩浆行者";
            case CHORUS_WINE -> "瞬移";
            case BOUNCE_WHITE_WINE -> "飞升";
            case MAGNETIC_WINE -> "磁吸";
            case SPIDER_RED_WINE -> "免疫蜘蛛网";
            case CATGIRL_RED_WINE -> "猫娘";
            case ELDER_WINE -> "耄耋";
            default -> type.getDisplayName();
        };
    }

    public static Map<UUID, Map<BeerType, Long>> snapshotActive() {
        Map<UUID, Map<BeerType, Long>> copy = new HashMap<>();
        for (var e : ACTIVE.entrySet()) {
            copy.put(e.getKey(), new HashMap<>(e.getValue()));
        }
        return copy;
    }

    public static Map<UUID, Set<Location>> snapshotHiddenWebs() {
        Map<UUID, Set<Location>> copy = new HashMap<>();
        for (var e : HIDDEN_WEBS.entrySet()) {
            copy.put(e.getKey(), new HashSet<>(e.getValue()));
        }
        return copy;
    }

    public static Map<UUID, List<AttachedEntity>> snapshotAttached() {
        // 返回玩家 → 猫娘/耄耋位置
        Map<UUID, List<AttachedEntity>> result = new HashMap<>();
        for (var entry : ATTACHED.entrySet()) {
            List<AttachedEntity> list = new ArrayList<>();
            for (UUID entUUID : entry.getValue()) {
                Entity e = Bukkit.getEntity(entUUID);
                if (e == null) continue;

                String type = null;
                if (e instanceof Cat cat
                        && cat.getPersistentDataContainer().has(ELDER_CAT_KEY,
                        PersistentDataType.BYTE)) {
                    type = "CAT";
                }
                if (type == null) continue;

                list.add(new AttachedEntity(type, e.getLocation()));
            }
            if (!list.isEmpty()) result.put(entry.getKey(), list);
        }
        return result;
    }

    public static void restoreActive(UUID uuid, Map<BeerType, Long> map) {
        ACTIVE.put(uuid, map);
    }

    public static void restoreHiddenWeb(UUID uuid, Location loc) {
        HIDDEN_WEBS.computeIfAbsent(uuid, k -> new HashSet<>()).add(loc);
    }

    public static Map<BeerType, Long> getEffects(Player p) {
        return ACTIVE.get(p.getUniqueId());
    }

    public static void restoreEntitiesFor(Player p) {
        Map<BeerType, Long> effects = ACTIVE.get(p.getUniqueId());
        if (effects == null) return;

        for (org.bukkit.World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                // 猫娘
                if (e instanceof org.bukkit.entity.Mannequin m
                        && m.getPersistentDataContainer().has(
                        com.xiaomian124.alcohol.entity.CatGirlManager.CATGIRL_KEY,
                        PersistentDataType.BYTE)) {
                    m.remove();
                    continue;
                }
                // 耄耋
                if (e instanceof Cat cat
                        && cat.getPersistentDataContainer().has(
                        ELDER_CAT_KEY, PersistentDataType.BYTE)) {
                    cat.remove();
                }
            }
        }

        ATTACHED.remove(p.getUniqueId());

        if (effects.containsKey(BeerType.CATGIRL_RED_WINE)) {
            com.xiaomian124.alcohol.entity.CatGirlManager.spawnCatGirl(p);
        }
        if (effects.containsKey(BeerType.ELDER_WINE)) {
            spawnCat(p);
        }
    }

    public static void removeAttachedOnQuit(Player p) {
        removeAttached(p.getUniqueId(), false);
    }

    public static void cleanupReferences() {
        java.util.Iterator<Map.Entry<UUID, List<UUID>>> it = ATTACHED.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, List<UUID>> entry = it.next();
            List<UUID> list = entry.getValue();
            list.removeIf(uuid -> {
                Entity e = Bukkit.getEntity(uuid);
                return e == null || !e.isValid();
            });
            if (list.isEmpty()) it.remove();
        }
    }

    public record AttachedEntity(String type, Location location) {}
}