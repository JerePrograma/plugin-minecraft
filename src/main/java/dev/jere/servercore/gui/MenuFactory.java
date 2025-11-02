package dev.jere.servercore.gui;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
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

public class MenuFactory implements Listener {
    private final ServerCorePlugin plugin;
    private final ProfileService profiles;
    private final LuckPermsHook lp;

    private final String ICON_INV = "https://textures.minecraft.net/texture/8b5f...";
    private final String ICON_PROFILE = "https://textures.minecraft.net/texture/1a2b...";
    private final String ICON_RANK = "https://textures.minecraft.net/texture/9c3d...";

    public MenuFactory(ServerCorePlugin plugin, ProfileService profiles, LuckPermsHook lp) {
        this.plugin = plugin;
        this.profiles = profiles;
        this.lp = lp;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMain(Player p, Profile pf) {
        Inventory inv = Bukkit.createInventory(p, 27, "§bMenú del Servidor");
        inv.setItem(10, named(Heads.customHead("§aInventario del Servidor", ICON_INV), "§aInventario del Servidor"));
        inv.setItem(13, named(Heads.customHead("§bPerfil", ICON_PROFILE), "§bPerfil"));
        String rank = lp.primaryGroup(p);
        if (rank == null) rank = pf.localRank;
        ItemStack rankItem = new ItemStack(Material.NAME_TAG);
        rankItem = named(rankItem, "§dRango: §f" + rank);
        inv.setItem(16, rankItem);

        ItemStack coins = new ItemStack(Material.GOLD_NUGGET);
        coins = named(coins, "§6Monedas: §e" + pf.coins);
        inv.setItem(22, coins);
        p.openInventory(inv);
    }

    public void openSettings(Player p, Profile pf) {
        Inventory inv = Bukkit.createInventory(p, 27, "§eAjustes");
        inv.setItem(11, named(toggleItem(pf.lobbyHidePlayers), "§aJugadores Lobby: " + (pf.lobbyHidePlayers ? "§cOcultos" : "§aVisibles")));
        inv.setItem(15, named(toggleItem(!pf.allowPM), "§aPrivados: " + (pf.allowPM ? "§aActivados" : "§cDesactivados")));
        p.openInventory(inv);
    }

    private ItemStack toggleItem(boolean off) {
        return new ItemStack(off ? Material.REDSTONE : Material.EMERALD);
    }

    private ItemStack named(ItemStack it, String name) {
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(name);
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
                case 10 -> p.sendMessage("§7[Aún] Inventario del servidor: acá podés listar warps/kits/colas."); // TODO
                case 13 -> openProfile(p, pf);
                default -> {
                }
            }
        } else {
            if (e.getRawSlot() == 11) {
                pf.lobbyHidePlayers = !pf.lobbyHidePlayers;
                profiles.save(pf);
                p.closeInventory();
                Bukkit.getOnlinePlayers().forEach(other -> {
                    if (pf.lobbyHidePlayers) p.hidePlayer(plugin, other);
                    else p.showPlayer(plugin, other);
                });
                p.sendMessage("§7Lobby jugadores: " + (pf.lobbyHidePlayers ? "§cOcultos" : "§aVisibles"));
            } else if (e.getRawSlot() == 15) {
                pf.allowPM = !pf.allowPM;
                profiles.save(pf);
                p.closeInventory();
                p.sendMessage("§7Privados: " + (pf.allowPM ? "§aActivados" : "§cDesactivados"));
            }
        }
    }

    private void openProfile(Player p, Profile pf) {
        Inventory inv = Bukkit.createInventory(p, 27, "§bPerfil");
        inv.setItem(11, named(new ItemStack(Material.PLAYER_HEAD), "§bNombre: §f" + p.getName()));
        inv.setItem(13, named(new ItemStack(Material.GOLD_NUGGET), "§6Monedas: §e" + pf.coins));
        inv.setItem(15, named(new ItemStack(Material.ELYTRA), "§dFly: " + (pf.canFly ? "§aON" : "§cOFF")));
        p.openInventory(inv);
    }
}
