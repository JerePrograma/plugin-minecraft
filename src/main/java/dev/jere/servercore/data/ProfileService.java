package dev.jere.servercore.data;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.model.Profile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProfileService {
    private final ServerCorePlugin plugin;
    private final Map<UUID, Profile> cache = new HashMap<>();
    private final File dir;

    public ProfileService(ServerCorePlugin plugin) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), "data");
        if (!dir.exists() && !dir.mkdirs()) {
            plugin.getLogger().warning("No se pudo crear la carpeta de datos: " + dir.getAbsolutePath());
        }
    }

    public Profile get(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::load);
    }

    private Profile load(UUID uuid) {
        File f = new File(dir, uuid + ".yml");
        Profile p = new Profile(uuid);
        if (!f.exists()) return p;

        YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
        p.lobbyHidePlayers = y.getBoolean("lobbyHidePlayers", false);
        p.allowPM = y.getBoolean("allowPM", true);
        p.canFly = y.getBoolean("canFly", false);
        p.coins = y.getLong("coins", 0L);
        p.localRank = y.getString("localRank", "DEFAULT");
        return p;
    }

    public void save(Profile p) {
        File f = new File(dir, p.uuid + ".yml");
        YamlConfiguration y = new YamlConfiguration();
        y.set("lobbyHidePlayers", p.lobbyHidePlayers);
        y.set("allowPM", p.allowPM);
        y.set("canFly", p.canFly);
        y.set("coins", p.coins);
        y.set("localRank", p.localRank);
        try {
            y.save(f);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar perfil " + p.uuid + ": " + e.getMessage());
        }
    }

    public void flushAll() {
        cache.values().forEach(this::save);
    }
}
