package dev.jere.servercore.listeners;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.model.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Locale;

public class CommandGateListener implements Listener {
    private final ProfileService profiles;

    public CommandGateListener(ServerCorePlugin plugin, ProfileService profiles) {
        this.profiles = profiles;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPreprocess(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage().toLowerCase();
        if (msg.startsWith("/msj ") || msg.startsWith("/msg ") || msg.startsWith("/tell ") || msg.startsWith("/w ")) {
            var pf = profiles.get(e.getPlayer().getUniqueId());
            if (!pf.allowPM) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("§cTenés privados desactivados.");
            }
        }
    }

}
