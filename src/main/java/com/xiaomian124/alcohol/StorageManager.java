package com.xiaomian124.alcohol;

import com.xiaomian124.alcohol.aging.AgingBarrelGUI;
import com.xiaomian124.alcohol.aging.AgingBarrelManager;
import com.xiaomian124.alcohol.apple.ApplePressGUI;
import com.xiaomian124.alcohol.apple.ApplePressManager;
import com.xiaomian124.alcohol.brewing.BrewingBoxGUI;
import com.xiaomian124.alcohol.brewing.BrewingBoxManager;
import com.xiaomian124.alcohol.cherry.CherryManager;
import com.xiaomian124.alcohol.cherry.CherryTree;
import com.xiaomian124.alcohol.cooking.CookingPotGUI;
import com.xiaomian124.alcohol.cooking.CookingPotManager;
import com.xiaomian124.alcohol.corn.CornManager;
import com.xiaomian124.alcohol.corn.CornPlant;
import com.xiaomian124.alcohol.grape.GrapeBasin;
import com.xiaomian124.alcohol.grape.GrapeBasinManager;
import com.xiaomian124.alcohol.grape.GrapeManager;
import com.xiaomian124.alcohol.grape.GrapePlant;
import com.xiaomian124.alcohol.grape.GrapeType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StorageManager {

    private final AlcoholPlugin plugin;
    private final File file;

    public StorageManager(AlcoholPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
    }

    public void saveAll() {
        YamlConfiguration config = new YamlConfiguration();

        List<Map<String, Object>> pots = collectPots();
        List<Map<String, Object>> boxes = collectBoxes();
        List<Map<String, Object>> presses = collectApplePresses();
        List<Map<String, Object>> barrels = collectAgingBarrels();
        List<Map<String, Object>> basins = collectGrapeBasins();

        List<Map<String, Object>> cornPlants = collectCornPlants();
        List<Map<String, Object>> grapePlants = collectGrapePlants();
        List<Map<String, Object>> cherrySaplings = collectCherrySaplings();
        List<Map<String, Object>> cherryTrees = collectCherryTrees();

        config.set("cooking_pots", pots);
        config.set("brewing_boxes", boxes);
        config.set("apple_presses", presses);
        config.set("aging_barrels", barrels);
        config.set("grape_basins", basins);

        config.set("corn_plants", cornPlants);
        config.set("grape_plants", grapePlants);
        config.set("cherry_saplings", cherrySaplings);
        config.set("cherry_trees", cherryTrees);

        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            config.save(file);
            plugin.getLogger().info("已保存数据到 data.yml");
            // plugin.getLogger().info("已保存 " + pots.size() + " 烹饪锅、"
            //         + boxes.size() + " 酿造炉、"
            //         + presses.size() + " 苹果压榨器、"
            //         + barrels.size() + " 陈酿桶、"
            //         + basins.size() + " 葡萄藤盆、"
            //         + cornPlants.size() + " 玉米植株、"
            //         + grapePlants.size() + " 葡萄丛、"
            //         + cherryTrees.size() + " 樱桃树");
        } catch (IOException e) {
            plugin.getLogger().severe("保存 data.yml 失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> collectPots() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String key : CookingPotManager.getRegisteredKeys()) {
            Location loc = CookingPotManager.parseLocation(key);
            if (loc == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("world", loc.getWorld().getName());
            m.put("x", loc.getBlockX());
            m.put("y", loc.getBlockY());
            m.put("z", loc.getBlockZ());
            m.put("water", CookingPotManager.getWater(loc));

            CookingPotGUI gui = CookingPotManager.get(loc);
            if (gui != null) {
                Inventory inv = gui.getInventory();
                m.put("remaining", gui.getRemainingTicks());

                Map<String, String> inputs = new LinkedHashMap<>();
                for (int i = 0; i < CookingPotGUI.INPUT_SLOTS.length; i++) {
                    ItemStack item = inv.getItem(CookingPotGUI.INPUT_SLOTS[i]);
                    if (item != null && item.getType() != Material.AIR) {
                        inputs.put(String.valueOf(i), ItemSerializer.toBase64(item));
                    }
                }
                if (!inputs.isEmpty()) m.put("inputs", inputs);

                List<String> outputs = new ArrayList<>();
                for (ItemStack item : gui.getOutputs()) {
                    String s = ItemSerializer.toBase64(item);
                    if (s != null) outputs.add(s);
                }
                if (!outputs.isEmpty()) m.put("outputs", outputs);
            }
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> collectBoxes() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BrewingBoxGUI gui : BrewingBoxManager.getAll()) {
            Location loc = gui.getBlock().getLocation();
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("world", loc.getWorld().getName());
            m.put("x", loc.getBlockX());
            m.put("y", loc.getBlockY());
            m.put("z", loc.getBlockZ());
            m.put("pressure", gui.getPressure());
            m.put("remaining", gui.getRemainingTicks());

            Inventory inv = gui.getInventory();

            Map<String, String> ing = new LinkedHashMap<>();
            for (int i = 0; i < BrewingBoxGUI.INGREDIENT_SLOTS.length; i++) {
                ItemStack item = inv.getItem(BrewingBoxGUI.INGREDIENT_SLOTS[i]);
                if (item != null && item.getType() != Material.AIR) {
                    ing.put(String.valueOf(i), ItemSerializer.toBase64(item));
                }
            }
            if (!ing.isEmpty()) m.put("ingredients", ing);

            ItemStack water = inv.getItem(BrewingBoxGUI.WATER_SLOT);
            if (water != null && water.getType() != Material.AIR)
                m.put("water", ItemSerializer.toBase64(water));

            ItemStack fuel = inv.getItem(BrewingBoxGUI.FUEL_SLOT);
            if (fuel != null && fuel.getType() != Material.AIR)
                m.put("fuel", ItemSerializer.toBase64(fuel));

            List<String> pending = new ArrayList<>();
            for (ItemStack item : gui.getPendingOutputs()) {
                String s = ItemSerializer.toBase64(item);
                if (s != null) pending.add(s);
            }
            if (!pending.isEmpty()) m.put("pending_outputs", pending);

            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> collectApplePresses() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String key : ApplePressManager.getRegisteredKeys()) {
            Location loc = ApplePressManager.parseLocation(key);
            if (loc == null) continue;

            ApplePressGUI gui = ApplePressManager.get(loc);
            if (gui == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("world", loc.getWorld().getName());
            m.put("x", loc.getBlockX());
            m.put("y", loc.getBlockY());
            m.put("z", loc.getBlockZ());
            m.put("mash_remaining", gui.getMashRemaining());
            m.put("juice_remaining", gui.getJuiceRemaining());

            Map<String, String> items = new LinkedHashMap<>();
            ItemStack[] contents = gui.getContents();
            for (int i = 0; i < contents.length; i++) {
                if (gui.isHeadSlot(i)) continue;
                ItemStack c = contents[i];
                if (c != null && c.getType() != Material.AIR) {
                    items.put(String.valueOf(i), ItemSerializer.toBase64(c));
                }
            }
            if (!items.isEmpty()) m.put("items", items);

            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> collectAgingBarrels() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String key : AgingBarrelManager.getRegisteredKeys()) {
            Location loc = AgingBarrelManager.parseLocation(key);
            if (loc == null) continue;

            AgingBarrelGUI gui = AgingBarrelManager.get(loc);
            if (gui == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("world", loc.getWorld().getName());
            m.put("x", loc.getBlockX());
            m.put("y", loc.getBlockY());
            m.put("z", loc.getBlockZ());
            m.put("storage_type", gui.getStorageType() == null
                    ? null : gui.getStorageType().name());
            m.put("storage_percent", gui.getStoragePercent());
            m.put("aging_remaining", gui.getAgingRemaining());

            Map<String, String> items = new LinkedHashMap<>();
            ItemStack[] contents = gui.getContents();
            for (int i = 0; i < contents.length; i++) {
                if (gui.isHeadSlot(i) || gui.isStorageDisplaySlot(i)) continue;
                ItemStack c = contents[i];
                if (c != null && c.getType() != Material.AIR) {
                    items.put(String.valueOf(i), ItemSerializer.toBase64(c));
                }
            }
            if (!items.isEmpty()) m.put("items", items);

            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> collectGrapeBasins() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String key : GrapeBasinManager.getRegisteredKeys()) {
            Location loc = GrapeBasinManager.parseLocation(key);
            if (loc == null) continue;

            GrapeBasin basin = GrapeBasinManager.get(loc);
            if (basin == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("world", loc.getWorld().getName());
            m.put("x", loc.getBlockX());
            m.put("y", loc.getBlockY());
            m.put("z", loc.getBlockZ());
            m.put("grape_type", basin.grapeType == null
                    ? null : basin.grapeType.name());
            m.put("juice_count", basin.juiceCount);
            m.put("jump_count", basin.jumpCount);
            m.put("level", GrapeBasinManager.getLevel(loc.getBlock()));
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> collectCornPlants() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CornPlant plant : CornManager.getAllPlants()) {
            Location loc = plant.baseLocation;
            if (loc == null || loc.getWorld() == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("world", loc.getWorld().getName());
            m.put("x", loc.getBlockX());
            m.put("y", loc.getBlockY());
            m.put("z", loc.getBlockZ());
            m.put("stage", plant.stage);
            m.put("harvested", plant.harvestedTimes);
            m.put("stageTicks", plant.stageTicks);
            m.put("stageThreshold", plant.stageThreshold);
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> collectGrapePlants() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (GrapePlant plant : GrapeManager.PLANTS.values()) {
            Location loc = plant.anchorLocation;
            if (loc == null || loc.getWorld() == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("world", loc.getWorld().getName());
            m.put("x", loc.getBlockX());
            m.put("y", loc.getBlockY());
            m.put("z", loc.getBlockZ());
            m.put("type", plant.type.name());
            m.put("growthStage", plant.growthStage);
            m.put("harvestCount", plant.harvestCount);
            m.put("growTicks", plant.growTicks);
            m.put("growThreshold", plant.growThreshold);
            m.put("fenceMat", plant.originalFenceMaterial == null
                    ? null : plant.originalFenceMaterial.name());
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> collectCherrySaplings() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String k : CherryManager.getAllSaplingKeys()) {
            Location loc = CherryManager.parseKey(k);
            if (loc == null || loc.getWorld() == null) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("world", loc.getWorld().getName());
            m.put("x", loc.getBlockX());
            m.put("y", loc.getBlockY());
            m.put("z", loc.getBlockZ());
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> collectCherryTrees() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CherryTree tree : CherryManager.getAllTrees()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("logs", new ArrayList<>(tree.logs));
            m.put("leaves", new ArrayList<>(tree.leaves));
            list.add(m);
        }
        return list;
    }

    public void loadAll() {
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<Map<?, ?>> pots = config.getMapList("cooking_pots");
        for (Map<?, ?> raw : pots) loadPot(raw);

        List<Map<?, ?>> boxes = config.getMapList("brewing_boxes");
        for (Map<?, ?> raw : boxes) loadBox(raw);

        List<Map<?, ?>> presses = config.getMapList("apple_presses");
        for (Map<?, ?> raw : presses) loadApplePress(raw);

        List<Map<?, ?>> barrels = config.getMapList("aging_barrels");
        for (Map<?, ?> raw : barrels) loadAgingBarrel(raw);

        List<Map<?, ?>> basins = config.getMapList("grape_basins");
        for (Map<?, ?> raw : basins) loadGrapeBasin(raw);

        List<Map<?, ?>> cornPlants = config.getMapList("corn_plants");
        for (Map<?, ?> raw : cornPlants) loadCornPlant(raw);

        List<Map<?, ?>> grapePlants = config.getMapList("grape_plants");
        for (Map<?, ?> raw : grapePlants) loadGrapePlant(raw);

        List<Map<?, ?>> cherrySaplings = config.getMapList("cherry_saplings");
        for (Map<?, ?> raw : cherrySaplings) loadCherrySapling(raw);

        List<Map<?, ?>> cherryTrees = config.getMapList("cherry_trees");
        for (Map<?, ?> raw : cherryTrees) loadCherryTree(raw);

        plugin.getLogger().info("已加载 data.yml 数据");
        // plugin.getLogger().info("已加载 " + pots.size() + " 烹饪锅、"
        //         + boxes.size() + " 酿造炉、"
        //         + presses.size() + " 苹果压榨器、"
        //         + barrels.size() + " 陈酿桶、"
        //         + basins.size() + " 葡萄藤盆、"
        //         + cornPlants.size() + " 玉米植株、"
        //         + grapePlants.size() + " 葡萄丛、"
        //         + cherryTrees.size() + " 樱桃树");
    }

    private Location readLocation(Map<?, ?> raw) {
        Object w = raw.get("world");
        if (!(w instanceof String worldName)) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        try {
            int x = ((Number) raw.get("x")).intValue();
            int y = ((Number) raw.get("y")).intValue();
            int z = ((Number) raw.get("z")).intValue();
            return new Location(world, x, y, z);
        } catch (Exception e) {
            return null;
        }
    }

    private void loadPot(Map<?, ?> raw) {
        Location loc = readLocation(raw);
        if (loc == null) return;

        Block block = loc.getBlock();
        if (block.getType() != Material.CAULDRON
                && block.getType() != Material.WATER_CAULDRON) return;

        CookingPotManager.register(loc);

        Object waterObj = raw.get("water");
        if (waterObj instanceof Number n) {
            CookingPotManager.setWater(loc, n.intValue());
        }

        Object inputsObj = raw.get("inputs");
        Object outputsObj = raw.get("outputs");
        Object oldOutputObj = raw.get("output");
        Object remainingObj = raw.get("remaining");

        boolean hasContent = (inputsObj instanceof Map<?, ?> m && !m.isEmpty())
                || outputsObj instanceof List<?> l && !l.isEmpty()
                || oldOutputObj instanceof String
                || (remainingObj instanceof Number n2 && n2.intValue() > 0);
        if (!hasContent) return;

        CookingPotGUI gui = CookingPotManager.getOrCreate(plugin, block);

        if (remainingObj instanceof Number n) {
            gui.setRemainingTicks(n.intValue());
        }

        Inventory inv = gui.getInventory();

        if (inputsObj instanceof Map<?, ?> inputs) {
            for (Map.Entry<?, ?> e : inputs.entrySet()) {
                try {
                    int idx = Integer.parseInt(e.getKey().toString());
                    if (idx < 0 || idx >= CookingPotGUI.INPUT_SLOTS.length) continue;
                    ItemStack item = ItemSerializer.fromBase64(e.getValue().toString());
                    if (item != null) inv.setItem(CookingPotGUI.INPUT_SLOTS[idx], item);
                } catch (NumberFormatException ignored) {}
            }
        }

        List<ItemStack> outputs = new ArrayList<>();
        if (outputsObj instanceof List<?> list) {
            for (Object o : list) {
                ItemStack item = ItemSerializer.fromBase64(o.toString());
                if (item != null) outputs.add(item);
            }
        } else if (oldOutputObj instanceof String s) {
            ItemStack item = ItemSerializer.fromBase64(s);
            if (item != null) outputs.add(item);
        }
        if (!outputs.isEmpty()) gui.setOutputs(outputs);

        int water = CookingPotManager.getWater(loc);
        CookingPotManager.updateCauldronBlock(block, water);
    }

    private void loadBox(Map<?, ?> raw) {
        Location loc = readLocation(raw);
        if (loc == null) return;

        Block block = loc.getBlock();
        if (block.getType() != Material.SMOKER) return;

        BrewingBoxGUI gui = BrewingBoxManager.getOrCreate(plugin, block);

        Object pressureObj = raw.get("pressure");
        if (pressureObj instanceof Number n) gui.setPressure(n.intValue());

        Object remainingObj = raw.get("remaining");
        if (remainingObj instanceof Number n) gui.setRemainingTicks(n.intValue());

        Inventory inv = gui.getInventory();

        Object ingObj = raw.get("ingredients");
        if (ingObj instanceof Map<?, ?> ing) {
            for (Map.Entry<?, ?> e : ing.entrySet()) {
                try {
                    int idx = Integer.parseInt(e.getKey().toString());
                    if (idx < 0 || idx >= BrewingBoxGUI.INGREDIENT_SLOTS.length) continue;
                    ItemStack item = ItemSerializer.fromBase64(e.getValue().toString());
                    if (item != null) inv.setItem(BrewingBoxGUI.INGREDIENT_SLOTS[idx], item);
                } catch (NumberFormatException ignored) {}
            }
        }

        Object waterObj = raw.get("water");
        if (waterObj instanceof String s) {
            ItemStack item = ItemSerializer.fromBase64(s);
            if (item != null) inv.setItem(BrewingBoxGUI.WATER_SLOT, item);
        }

        Object fuelObj = raw.get("fuel");
        if (fuelObj instanceof String s) {
            ItemStack item = ItemSerializer.fromBase64(s);
            if (item != null) inv.setItem(BrewingBoxGUI.FUEL_SLOT, item);
        }

        List<ItemStack> pendings = new ArrayList<>();
        Object pendingListObj = raw.get("pending_outputs");
        if (pendingListObj instanceof List<?> list) {
            for (Object o : list) {
                ItemStack item = ItemSerializer.fromBase64(o.toString());
                if (item != null) pendings.add(item);
            }
        } else {
            Object oldPendingObj = raw.get("pending_output");
            if (oldPendingObj instanceof String s) {
                ItemStack item = ItemSerializer.fromBase64(s);
                if (item != null) pendings.add(item);
            }
        }
        if (!pendings.isEmpty()) gui.setPendingOutputs(pendings);
    }

    private void loadApplePress(Map<?, ?> raw) {
        Location loc = readLocation(raw);
        if (loc == null) return;

        Block block = loc.getBlock();
        if (block.getType() != Material.LOOM) return;

        ApplePressManager.register(loc);
        ApplePressGUI gui = ApplePressManager.getOrCreate(plugin, block);

        int mashRemaining = raw.get("mash_remaining") instanceof Number n ? n.intValue() : 0;
        int juiceRemaining = raw.get("juice_remaining") instanceof Number n ? n.intValue() : 0;

        Object itemsObj = raw.get("items");
        if (itemsObj instanceof Map<?, ?> itemsMap) {
            for (Map.Entry<?, ?> e : itemsMap.entrySet()) {
                try {
                    int idx = Integer.parseInt(e.getKey().toString());
                    if (idx < 0 || idx >= ApplePressGUI.SIZE) continue;
                    if (gui.isHeadSlot(idx)) continue;
                    ItemStack item = ItemSerializer.fromBase64(e.getValue().toString());
                    if (item != null) gui.getInventory().setItem(idx, item);
                } catch (NumberFormatException ignored) {}
            }
        }

        gui.setState(mashRemaining, juiceRemaining);
    }

    private void loadAgingBarrel(Map<?, ?> raw) {
        Location loc = readLocation(raw);
        if (loc == null) return;

        Block block = loc.getBlock();
        if (block.getType() != Material.BARREL) return;

        AgingBarrelManager.register(loc);
        AgingBarrelGUI gui = AgingBarrelManager.getOrCreate(plugin, block);

        Object typeName = raw.get("storage_type");
        BeerType storageType = null;
        if (typeName instanceof String s && !s.isEmpty()) {
            try { storageType = BeerType.valueOf(s); }
            catch (IllegalArgumentException ignored) {}
        }

        int storagePercent = raw.get("storage_percent") instanceof Number n
                ? n.intValue() : 0;
        int agingRemaining = raw.get("aging_remaining") instanceof Number n
                ? n.intValue() : 0;

        Object itemsObj = raw.get("items");
        if (itemsObj instanceof Map<?, ?> itemsMap) {
            for (Map.Entry<?, ?> e : itemsMap.entrySet()) {
                try {
                    int idx = Integer.parseInt(e.getKey().toString());
                    if (idx < 0 || idx >= AgingBarrelGUI.SIZE) continue;
                    if (gui.isHeadSlot(idx) || gui.isStorageDisplaySlot(idx)) continue;
                    ItemStack item = ItemSerializer.fromBase64(e.getValue().toString());
                    if (item != null) gui.getInventory().setItem(idx, item);
                } catch (NumberFormatException ignored) {}
            }
        }

        gui.setStorageState(storageType, storagePercent);
        gui.setAgingRemaining(agingRemaining);
    }

    private void loadGrapeBasin(Map<?, ?> raw) {
        Location loc = readLocation(raw);
        if (loc == null) return;

        Block block = loc.getBlock();
        if (block.getType() != Material.COMPOSTER) return;

        GrapeBasinManager.register(loc);

        Object typeName = raw.get("grape_type");
        GrapeType type = null;
        if (typeName instanceof String s && !s.isEmpty()) {
            try { type = GrapeType.valueOf(s); }
            catch (IllegalArgumentException ignored) {}
        }

        int juiceCount = raw.get("juice_count") instanceof Number n ? n.intValue() : 0;
        int jumpCount = raw.get("jump_count") instanceof Number n ? n.intValue() : 0;
        int level = raw.get("level") instanceof Number n ? n.intValue() : 0;

        GrapeBasinManager.restoreState(loc, type, juiceCount, jumpCount);
        GrapeBasinManager.setLevel(block, level);
    }

    private void loadCornPlant(Map<?, ?> raw) {
        Location loc = readLocation(raw);
        if (loc == null) return;

        CornPlant plant = new CornPlant(loc);
        plant.stage = raw.get("stage") instanceof Number n ? n.intValue() : 1;
        plant.harvestedTimes = raw.get("harvested") instanceof Number n ? n.intValue() : 0;
        plant.stageTicks = raw.get("stageTicks") instanceof Number n ? n.intValue() : 0;
        plant.stageThreshold = raw.get("stageThreshold") instanceof Number n ? n.intValue() : 0;

        CornManager.restorePlant(plant);
    }

    private void loadGrapePlant(Map<?, ?> raw) {
        Location loc = readLocation(raw);
        if (loc == null) return;

        Object typeName = raw.get("type");
        if (!(typeName instanceof String s)) return;

        GrapeType type;
        try { type = GrapeType.valueOf(s); }
        catch (IllegalArgumentException e) { return; }

        GrapePlant plant = new GrapePlant(loc, type);
        plant.growthStage = raw.get("growthStage") instanceof Number n ? n.intValue() : 0;
        plant.harvestCount = raw.get("harvestCount") instanceof Number n ? n.intValue() : 0;
        plant.growTicks = raw.get("growTicks") instanceof Number n ? n.intValue() : 0;
        plant.growThreshold = raw.get("growThreshold") instanceof Number n ? n.intValue() : 0;

        Object fm = raw.get("fenceMat");
        if (fm instanceof String fmn) {
            try { plant.originalFenceMaterial = Material.valueOf(fmn); }
            catch (IllegalArgumentException ignored) {}
        }

        GrapeManager.restorePlant(plant);
    }

    private void loadCherrySapling(Map<?, ?> raw) {
        Location loc = readLocation(raw);
        if (loc == null) return;
        CherryManager.restoreSapling(loc);
    }

    private void loadCherryTree(Map<?, ?> raw) {
        Object logsObj = raw.get("logs");
        Object leavesObj = raw.get("leaves");
        if (!(logsObj instanceof List<?> logs)) return;
        if (!(leavesObj instanceof List<?> leaves)) return;

        CherryTree tree = new CherryTree(null);
        for (Object o : logs) tree.logs.add(o.toString());
        for (Object o : leaves) tree.leaves.add(o.toString());

        CherryManager.restoreTree(tree);
    }
}