package dev.jere.servercore.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class Tags {
    private Tags() {
    }

    public static void tag(ItemStack item, NamespacedKey keyType, NamespacedKey keyId, String type, String id) {
        ItemMeta m = item.getItemMeta();
        m.getPersistentDataContainer().set(keyType, PersistentDataType.STRING, type);
        m.getPersistentDataContainer().set(keyId, PersistentDataType.STRING, id);
        item.setItemMeta(m);
    }

    public static boolean has(ItemStack item, NamespacedKey keyType, NamespacedKey keyId, String type, String id) {
        if (item == null || !item.hasItemMeta()) return false;
        var c = item.getItemMeta().getPersistentDataContainer();
        String t = c.get(keyType, PersistentDataType.STRING);
        String i = c.get(keyId, PersistentDataType.STRING);
        return type.equals(t) && id.equals(i);
    }
}
