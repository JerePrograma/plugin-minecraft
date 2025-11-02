package dev.jere.servercore.commands;

import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.model.Profile;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class MsgCmd implements CommandExecutor {
    private final ProfileService profiles;

    public MsgCmd(ProfileService profiles) {
        this.profiles = profiles;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player from) || a.length < 2) {
            s.sendMessage("/msg <jugador> <mensaje>");
            return true;
        }
        Player to = Bukkit.getPlayerExact(a[0]);
        if (to == null) {
            from.sendMessage("Jugador offline.");
            return true;
        }
        if (!profiles.get(to.getUniqueId()).allowPM) {
            from.sendMessage("§cEse jugador tiene privados desactivados.");
            return true;
        }
        String msg = String.join(" ", java.util.Arrays.copyOfRange(a, 1, a.length));
        to.sendMessage("§7[De §b" + from.getName() + "§7] §f" + msg);
        from.sendMessage("§7[Para §b" + to.getName() + "§7] §f" + msg);
        return true;
    }
}
