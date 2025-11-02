package dev.jere.servercore.listeners;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.hotbar.HotbarManager;
import dev.jere.servercore.model.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class JoinQuitListener implements Listener {
    private final ServerCorePlugin plugin;
    private final ProfileService profiles;
    private final HotbarManager hotbar;

    public JoinQuitListener(ServerCorePlugin plugin, ProfileService profiles, HotbarManager hotbar) {
        this.plugin = plugin;
        this.profiles = profiles;
        this.hotbar = hotbar;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Profile pf = profiles.get(p.getUniqueId());
        hotbar.giveHotbarItems(p);
        applyVisibility(p, pf.lobbyHidePlayers);
        p.setAllowFlight(pf.canFly);
        p.setFlying(pf.canFly && p.isOnGround());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTask(plugin, () -> hotbar.giveHotbarItems(e.getPlayer()));
    }

    private void applyVisibility(Player p, boolean hide) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(p)) continue;
            if (hide) p.hidePlayer(plugin, other);
            else p.showPlayer(plugin, other);
        }
    }
}
