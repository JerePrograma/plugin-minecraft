package dev.jere.servercore.gui;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.data.ServerInventoryService;
import dev.jere.servercore.hooks.LuckPermsHook;
import dev.jere.servercore.model.Profile;
import dev.jere.servercore.util.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MenuFactory implements Listener {
    private final ServerCorePlugin plugin;
    private final ProfileService profiles;
    private final LuckPermsHook lp;
    private final ServerInventoryService inventories;

    private final String ICON_INV = "b1370acbb4ef68b0cbf5bfc0d6b5c1db61b31b5f7f5bcab044a73a4c50b27df0";
    private final String ICON_PROFILE = "0c3a1fbb79f02f4ce0dfafad9dca7eac191f85632d436a3d79042f2f20c62185";
    private final String ICON_RANK = "4c8f4c02bc7c1a25ed2089e16d731544ca06979cd13e341fa9a316bed112fb4b";

    public MenuFactory(ServerCorePlugin plugin, ProfileService profiles, LuckPermsHook lp, ServerInventoryService inventories) {
        this.plugin = plugin;
        this.profiles = profiles;
        this.lp = lp;
        this.inventories = inventories;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMain(Player p, Profile pf) {
        Inventory inv = Bukkit.createInventory(p, 27, "§bMenú del Servidor");
        inv.setItem(10, named(Heads.customHead("§aInventario del Servidor", ICON_INV), "§aInventario del Servidor", "§7Almacén virtual personal."));
        inv.setItem(13, named(Heads.customHead("§bPerfil", ICON_PROFILE), "§bPerfil", "§7Consulta tu información básica."));
        String rank = lp.primaryGroup(p);
        if (rank == null) rank = pf.localRank;
        ItemStack rankItem = Heads.customHead("§dRango: §f" + rank, ICON_RANK);
        inv.setItem(16, rankItem);

        ItemStack coins = new ItemStack(Material.GOLD_NUGGET);
        coins = named(coins, "§6Monedas: §e" + pf.coins, "§7Gestiona con /coins.");
        inv.setItem(22, coins);
        p.openInventory(inv);
    }

    public void openSettings(Player p, Profile pf) {
        Inventory inv = Bukkit.createInventory(p, 27, "§eAjustes");
        inv.setItem(11, named(toggleItem(pf.lobbyHidePlayers), "§aJugadores Lobby: " + (pf.lobbyHidePlayers ? "§cOcultos" : "§aVisibles"),
                pf.lobbyHidePlayers ? "§7Vuelve a mostrarlos en un click." : "§7Oculta jugadores molestos."));
        inv.setItem(15, named(toggleItem(!pf.allowPM), "§aPrivados: " + (pf.allowPM ? "§aActivados" : "§cDesactivados"),
                pf.allowPM ? "§7Recibir mensajes privados." : "§7Bloquea mensajes entrantes."));
        p.openInventory(inv);
    }

    private ItemStack toggleItem(boolean off) {
        return new ItemStack(off ? Material.REDSTONE : Material.EMERALD);
    }

    private ItemStack named(ItemStack it, String name, String... lore) {
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(name);
        if (lore != null && lore.length > 0) {
            m.setLore(Arrays.asList(lore));
        }
        it.setItemMeta(m);
        return it;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle() == null) return;
        String title = e.getView().getTitle();
        if (!title.contains("Menú del Servidor") && !title.contains("Ajustes")) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        var p = (Player) e.getWhoClicked();
        var pf = profiles.get(p.getUniqueId());

        if (title.contains("Menú del Servidor")) {
            switch (e.getRawSlot()) {
                case 10 -> {
                    p.closeInventory();
                    Bukkit.getScheduler().runTask(plugin, () -> inventories.open(p));
                }
                case 13 -> openProfile(p, pf);
                default -> {
                }
            }
        } else {
            if (e.getRawSlot() == 11) {
                pf.lobbyHidePlayers = !pf.lobbyHidePlayers;
                profiles.save(pf);
                Bukkit.getOnlinePlayers().forEach(other -> {
                    if (pf.lobbyHidePlayers) p.hidePlayer(plugin, other);
                    else p.showPlayer(plugin, other);
                });
                p.sendMessage("§7Lobby jugadores: " + (pf.lobbyHidePlayers ? "§cOcultos" : "§aVisibles"));
                reopenSettings(p, pf);
            } else if (e.getRawSlot() == 15) {
                pf.allowPM = !pf.allowPM;
                profiles.save(pf);
                p.sendMessage("§7Privados: " + (pf.allowPM ? "§aActivados" : "§cDesactivados"));
                reopenSettings(p, pf);
            }
        }
    }

    private void openProfile(Player p, Profile pf) {
        Inventory inv = Bukkit.createInventory(p, 27, "§bPerfil");
        inv.setItem(11, named(new ItemStack(Material.PLAYER_HEAD), "§bNombre: §f" + p.getName(), "§7UUID: " + p.getUniqueId()));
        inv.setItem(13, named(new ItemStack(Material.GOLD_NUGGET), "§6Monedas: §e" + pf.coins, "§7Puedes cambiarlas con /coins."));
        String rank = lp.primaryGroup(p);
        if (rank == null) rank = pf.localRank;
        inv.setItem(15, named(new ItemStack(Material.NAME_TAG), "§dRango: §f" + rank, "§7Fuente: " + (lp.primaryGroup(p) != null ? "LuckPerms" : "Local")));
        inv.setItem(22, named(new ItemStack(Material.ELYTRA), "§dFly: " + (pf.canFly ? "§aON" : "§cOFF"), "§7Togglea con la cabeza del hotbar."));
        p.openInventory(inv);
    }

    private void reopenSettings(Player p, Profile pf) {
        Bukkit.getScheduler().runTask(plugin, () -> openSettings(p, pf));
    }
}
