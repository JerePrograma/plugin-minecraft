package dev.jere.servercore.hotbar;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.util.Heads;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HotbarManager {
    private final ServerCorePlugin plugin;

    public static final String T_PROFILE = "profile";
    private static final String PROFILE_ICON = "0c3a1fbb79f02f4ce0dfafad9dca7eac191f85632d436a3d79042f2f20c62185";

    public HotbarManager(ServerCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void giveHotbarItems(Player p) {
        var inv = p.getInventory();
        clearHotbarItems(inv);

        var profile = Heads.customHead("§bPerfil", PROFILE_ICON);
        tag(profile, T_PROFILE, "MAIN");

        inv.setItem(0, profile);
        p.updateInventory();
    }

    private void clearHotbarItems(PlayerInventory inv) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack existing = inv.getItem(slot);
            if (existing == null) continue;
            ItemMeta meta = existing.getItemMeta();
            if (meta == null) continue;
            var container = meta.getPersistentDataContainer();
            if (container.has(plugin.keyType, PersistentDataType.STRING)) {
                inv.clear(slot);
            }
        }
    }

    private void tag(ItemStack item, String type, String id) {
        ItemMeta meta = item.getItemMeta();
        NamespacedKey kt = plugin.keyType, ki = plugin.keyId;
        meta.getPersistentDataContainer().set(kt, PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(ki, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
    }
}
