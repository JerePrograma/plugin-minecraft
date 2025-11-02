package dev.jere.servercore.commands;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.model.Profile;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class FlyCmd implements CommandExecutor {
    private final ServerCorePlugin plugin;
    private final ProfileService profiles;

    public FlyCmd(ServerCorePlugin plugin, ProfileService profiles) {
        this.plugin = plugin;
        this.profiles = profiles;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) {
            s.sendMessage("Solo jugadores.");
            return true;
        }
        if (!p.hasPermission("servercore.fly")) {
            p.sendMessage("§cSin permiso.");
            return true;
        }
        var pf = profiles.get(p.getUniqueId());
        boolean next = !p.getAllowFlight();
        p.setAllowFlight(next);
        if (!next && p.isFlying()) p.setFlying(false);
        pf.canFly = next;
        profiles.save(pf);
        p.sendMessage("Vuelo: " + (next ? "§aON" : "§cOFF"));
        return true;
    }
}
