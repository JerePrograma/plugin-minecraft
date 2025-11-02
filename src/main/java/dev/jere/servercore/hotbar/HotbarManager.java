package dev.jere.servercore.hotbar;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.util.Heads;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HotbarManager {
    private final ServerCorePlugin plugin;

    public static final String T_MENU = "menu", T_SETTINGS = "settings", T_FLY = "fly";
    private final String ICON1 = "https://textures.minecraft.net/texture/3f2c9c8b8..."; // TODO
    private final String ICON2 = "https://textures.minecraft.net/texture/7aa0d12c1...";
    private final String ICON3 = "https://textures.minecraft.net/texture/a88b934a0...";

    public HotbarManager(ServerCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void giveHotbarItems(Player p) {
        var inv = p.getInventory();
        var menu = Heads.customHead("§bMenú del Servidor", ICON1);
        var set = Heads.customHead("§eAjustes", ICON2);
        var fly = Heads.customHead("§dFly ON/OFF", ICON3);

        tag(menu.getItemMeta(), T_MENU, "MAIN");
        tag(set.getItemMeta(), T_SETTINGS, "SET");
        tag(fly.getItemMeta(), T_FLY, "TOGGLE");

        inv.setItem(0, menu);
        inv.setItem(1, set);
        inv.setItem(2, fly);
        p.updateInventory();
    }

    private void tag(ItemMeta meta, String type, String id) {
        NamespacedKey kt = plugin.keyType, ki = plugin.keyId;
        meta.getPersistentDataContainer().set(kt, PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(ki, PersistentDataType.STRING, id);
    }
}
