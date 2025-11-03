// dev/jere/servercore/listeners/CommandKillerListener.java
package dev.jere.servercore.listeners;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Locale;
import java.util.Set;

public class CommandKillerListener implements Listener {
    private static final Set<String> PM = Set.of("/msg","/tell","/w","/whisper","/pm","/m","/t","/r","/reply");
    private final ServerCorePlugin plugin;
    private final ProfileService profiles;

    public CommandKillerListener(ServerCorePlugin plugin, ProfileService profiles) {
        this.plugin = plugin; this.profiles = profiles;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCmd(PlayerCommandPreprocessEvent e) {
        String raw = e.getMessage();
        int sp = raw.indexOf(' ');
        String label = (sp == -1 ? raw : raw.substring(0, sp)).toLowerCase(Locale.ROOT);
        if (!PM.contains(label)) return;

        // /r es ambiguo ⇒ cancelar y avisar
        if (label.equals("/r") || label.equals("/reply")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§7Usá §b/msj <jugador> <mensaje>§7.");
            return;
        }

        // Redirigir a /msj si hay args suficientes
        if (sp != -1) {
            String args = raw.substring(sp + 1);
            int sp2 = args.indexOf(' ');
            if (sp2 != -1) {
                String target = args.substring(0, sp2);
                String body   = args.substring(sp2 + 1);
                e.setCancelled(true);
                Bukkit.dispatchCommand(e.getPlayer(), "servercore:msj " + target + " " + body);
                return;
            }
        }
        e.setCancelled(true);
        e.getPlayer().sendMessage("§7Usá §b/msj <jugador> <mensaje>§7.");
    }
}
