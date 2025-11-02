package dev.jere.servercore.hooks;

import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LuckPermsHook {
    private final boolean present = Bukkit.getPluginManager().getPlugin("LuckPerms") != null;

    public String primaryGroup(Player p) {
        if (!present) return null;
        try {
            var user = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId());
            if (user == null) return null;
            var data = user.getPrimaryGroup();
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
