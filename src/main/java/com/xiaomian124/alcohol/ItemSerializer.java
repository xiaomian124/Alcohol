package com.xiaomian124.alcohol;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public final class ItemSerializer {

    private ItemSerializer() {}

    public static String toBase64(ItemStack item) {
        if (item == null) return null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOut = new BukkitObjectOutputStream(out)) {
            dataOut.writeObject(item);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    public static ItemStack fromBase64(String base64) {
        if (base64 == null || base64.isEmpty()) return null;
        try (ByteArrayInputStream in = new ByteArrayInputStream(
                Base64.getDecoder().decode(base64));
             BukkitObjectInputStream dataIn = new BukkitObjectInputStream(in)) {
            return (ItemStack) dataIn.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}