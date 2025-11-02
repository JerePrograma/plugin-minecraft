package dev.jere.servercore.commands;

import dev.jere.servercore.data.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CoinsCmd implements CommandExecutor {
    private final ProfileService profiles;

    public CoinsCmd(ProfileService profiles) {
        this.profiles = profiles;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (a.length == 0 && s instanceof Player p) {
            var pf = profiles.get(p.getUniqueId());
            p.sendMessage("§6Monedas: §e" + pf.coins);
            return true;
        }
        if (!s.hasPermission("servercore.coins")) {
            s.sendMessage("§cSin permiso.");
            return true;
        }
        if (a.length != 3) {
            s.sendMessage("/coins [get|set|add] <player> <amount>");
            return true;
        }
        String sub = a[0];
        Player tgt = Bukkit.getPlayerExact(a[1]);
        if (tgt == null) {
            s.sendMessage("Jugador offline.");
            return true;
        }
        var pf = profiles.get(tgt.getUniqueId());
        long amt;
        try {
            amt = Long.parseLong(a[2]);
        } catch (Exception e) {
            s.sendMessage("amount inválido");
            return true;
        }
        switch (sub.toLowerCase()) {
            case "get" -> s.sendMessage(tgt.getName() + ": " + pf.coins);
            case "set" -> {
                pf.coins = amt;
                profiles.save(pf);
                s.sendMessage("Set OK");
            }
            case "add" -> {
                pf.coins += amt;
                profiles.save(pf);
                s.sendMessage("Add OK");
            }
            default -> s.sendMessage("subcomando inválido");
        }
        return true;
    }
}
