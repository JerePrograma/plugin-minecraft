package dev.jere.servercore.data;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.model.ServerInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServerInventoryService implements Listener {
    public static final String TITLE = "\u00a7aInventario del Servidor";

    private final ServerCorePlugin plugin;
    private final File dir;
    private final Map<UUID, Inventory> cache = new HashMap<>();

    public ServerInventoryService(ServerCorePlugin plugin) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), "inventories");
        if (!dir.exists() && !dir.mkdirs()) {
            plugin.getLogger().warning("No se pudo crear carpeta de inventarios: " + dir.getAbsolutePath());
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        Inventory inv = cache.computeIfAbsent(player.getUniqueId(), this::load);
        // ensure holder keeps UUID for close event
        if (!(inv.getHolder() instanceof ServerInventoryHolder)) {
            ItemStack[] contents = inv.getContents();
            inv = Bukkit.createInventory(new ServerInventoryHolder(player.getUniqueId()), inv.getSize(), TITLE);
            inv.setContents(contents);
            cache.put(player.getUniqueId(), inv);
        }
        player.openInventory(inv);
    }

    private Inventory load(UUID uuid) {
        Inventory inv = Bukkit.createInventory(new ServerInventoryHolder(uuid), 27, TITLE);
        File file = file(uuid);
        if (!file.exists()) {
            return inv;
        }
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        List<?> stored = y.getList("contents");
        if (stored == null) {
            return inv;
        }
        ItemStack[] contents = new ItemStack[inv.getSize()];
        for (int i = 0; i < Math.min(stored.size(), inv.getSize()); i++) {
            Object obj = stored.get(i);
            if (obj instanceof ItemStack stack) {
                contents[i] = stack;
            }
        }
        inv.setContents(contents);
        return inv;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ServerInventoryHolder holder)) {
            return;
        }
        UUID uuid = holder.getOwner();
        Inventory inv = event.getInventory();
        cache.put(uuid, inv);
        save(uuid, inv);
    }

    public void saveAll() {
        cache.forEach(this::save);
    }

    private void save(UUID uuid, Inventory inventory) {
        File file = file(uuid);
        YamlConfiguration y = new YamlConfiguration();
        y.set("contents", inventory.getContents());
        try {
            y.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar inventario de " + uuid + ": " + e.getMessage());
        }
    }

    private File file(UUID uuid) {
        return new File(dir, uuid + ".yml");
    }
}
