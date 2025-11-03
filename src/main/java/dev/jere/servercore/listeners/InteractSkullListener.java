// dev/jere/servercore/listeners/InteractSkullListener.java
package dev.jere.servercore.listeners;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.gui.MenuFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class InteractSkullListener implements Listener {
    private final ServerCorePlugin plugin;
    private final MenuFactory menus;
    private final ProfileService profiles;

    public InteractSkullListener(ServerCorePlugin plugin, MenuFactory menus, ProfileService profiles) {
        this.plugin = plugin;
        this.menus = menus;
        this.profiles = profiles;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onUse(PlayerInteractEvent e) {
        var it = e.getItem();
        if (it == null || !it.hasItemMeta()) return;
        var c = it.getItemMeta().getPersistentDataContainer();
        String type = c.get(plugin.keyType, PersistentDataType.STRING);
        String id = c.get(plugin.keyId, PersistentDataType.STRING);
        if (!"profile".equals(type) || !"MAIN".equals(id)) return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        menus.openProfileMenu(p, profiles.get(p.getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        var it = e.getItemInHand();
        if (it == null || !it.hasItemMeta()) return;
        var c = it.getItemMeta().getPersistentDataContainer();
        String type = c.get(plugin.keyType, PersistentDataType.STRING);
        String id = c.get(plugin.keyId, PersistentDataType.STRING);
        if (!"profile".equals(type) || !"MAIN".equals(id)) return;

        e.setCancelled(true);
        var p = e.getPlayer();
        menus.openProfileMenu(p, profiles.get(p.getUniqueId()));
    }
}
