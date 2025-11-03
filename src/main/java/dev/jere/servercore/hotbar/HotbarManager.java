package dev.jere.servercore.hotbar;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.util.Heads;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HotbarManager {
    private final ServerCorePlugin plugin;

    public static final String T_MENU = "menu", T_SETTINGS = "settings", T_FLY = "fly";
    private final String ICON1 = "f4cc1a3cc1f1f508d57f075444ef98a32956543d4d6acdfb5a12dd0bafcd1e09";
    private final String ICON2 = "3e0c40e6bd8492d7b89c84b303bdb5caa9a2c22db55fa6da3fb8c05620e6f04c";
    private final String ICON3 = "f0dd5b0dcbf63683bcab9d8f1a9e4b9a2e9fe8d6ef1d5c00f9ba9ddfb6690a2d";

    public HotbarManager(ServerCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void giveHotbarItems(Player p) {
        var inv = p.getInventory();
        var menu = Heads.customHead("§bMenú del Servidor", ICON1);
        var set = Heads.customHead("§eAjustes", ICON2);
        var fly = Heads.customHead("§dFly ON/OFF", ICON3);

        tag(menu, T_MENU, "MAIN");
        tag(set, T_SETTINGS, "SET");
        tag(fly, T_FLY, "TOGGLE");

        inv.setItem(0, menu);
        inv.setItem(1, set);
        inv.setItem(2, fly);
        p.updateInventory();
    }

    private void tag(ItemStack item, String type, String id) {
        ItemMeta meta = item.getItemMeta();
        NamespacedKey kt = plugin.keyType, ki = plugin.keyId;
        meta.getPersistentDataContainer().set(kt, PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(ki, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
    }
}
