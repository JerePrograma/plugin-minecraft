package dev.jere.servercore.listeners;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.model.Profile;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandGateListener implements Listener {
    private final ProfileService profiles;

    public CommandGateListener(ServerCorePlugin plugin, ProfileService profiles) {
        this.profiles = profiles;
    }

    @EventHandler
    public void onPreprocess(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage().toLowerCase();
        if (msg.startsWith("/msg ") || msg.startsWith("/tell ") || msg.startsWith("/w ")) {
            var pf = profiles.get(e.getPlayer().getUniqueId());
            if (!pf.allowPM) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("§cTenés privados desactivados.");
            }
        }
    }
}
