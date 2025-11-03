// dev/jere/servercore/commands/MsjCmd.java
package dev.jere.servercore.commands;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MsjCmd implements CommandExecutor {
    private final ServerCorePlugin plugin;
    private final ProfileService profiles;

    public MsjCmd(ServerCorePlugin plugin, ProfileService profiles) {
        this.plugin = plugin; this.profiles = profiles;
    }

    private String cfg(String path, String def) {
        return ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString(path, def));
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player from) || a.length < 2) {
            s.sendMessage("§7Uso: §f/msj <jugador> <mensaje>");
            return true;
        }
        Player to = Bukkit.getPlayerExact(a[0]);
        if (to == null) { from.sendMessage("§cJugador offline."); return true; }

        var pfFrom = profiles.get(from.getUniqueId());
        var pfTo   = profiles.get(to.getUniqueId());
        if (pfFrom == null || pfTo == null) { from.sendMessage("§cPerfil no disponible."); return true; }

        if (!pfFrom.allowPM) { from.sendMessage("§cTenés privados desactivados."); return true; }
        if (!pfTo.allowPM)   { from.sendMessage(cfg("messages.pm_off","&cEse jugador tiene privados desactivados.")); return true; }

        String body = String.join(" ", Arrays.copyOfRange(a, 1, a.length));
        String toPrefix   = cfg("pm.blocked_prefix_from", "&7[De &b{from}&7] &f").replace("{from}", from.getName());
        String fromPrefix = cfg("pm.blocked_prefix_to",   "&7[Para &b{to}&7] &f").replace("{to}",   to.getName());

        to.sendMessage(toPrefix + body);
        from.sendMessage(fromPrefix + body);
        return true;
    }
}
