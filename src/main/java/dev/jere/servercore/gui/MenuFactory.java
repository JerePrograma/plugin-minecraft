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
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class MenuFactory implements Listener {
    private final ServerCorePlugin plugin;
    private final ProfileService profiles;
    private final LuckPermsHook lp;
    private static final String PROFILE_ICON = "0c3a1fbb79f02f4ce0dfafad9dca7eac191f85632d436a3d79042f2f20c62185";
    private static final String SETTINGS_ICON = "3e0c40e6bd8492d7b89c84b303bdb5caa9a2c22db55fa6da3fb8c05620e6f04c";

    private static final String PROFILE_MENU_TYPE = "profile-menu";
    private static final String PROFILE_SETTINGS_TYPE = "profile-settings";
    private static final String ID_INFO = "info";
    private static final String ID_SETTINGS = "settings";
    private static final String ID_FLY = "fly";
    private static final String ID_VISIBILITY = "visibility";
    private static final String ID_MESSAGES = "messages";

    public MenuFactory(ServerCorePlugin plugin, ProfileService profiles, LuckPermsHook lp) {
        this.plugin = plugin;
        this.profiles = profiles;
        this.lp = lp;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openProfileMenu(Player p, Profile pf) {
        Inventory inv = Bukkit.createInventory(p, 27, "§bPerfil");

        ItemStack info = Heads.customHead("§bInformación", PROFILE_ICON);
        info = named(info, "§bInformación", "§7Click derecho: ver tus datos", "§7Nick, rango y monedas.");
        tag(info, PROFILE_MENU_TYPE, ID_INFO);
        inv.setItem(11, info);

        ItemStack settings = Heads.customHead("§eAjustes", SETTINGS_ICON);
        settings = named(settings, "§eAjustes", "§7Click izquierdo: abrir ajustes", "§7Click derecho: mostrar opciones disponibles.");
        tag(settings, PROFILE_MENU_TYPE, ID_SETTINGS);
        inv.setItem(15, settings);

        p.openInventory(inv);
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
        boolean isProfileMenu = title.contains("Perfil");
        boolean isSettingsMenu = title.contains("Ajustes de Usuario");
        if (!isProfileMenu && !isSettingsMenu) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;
        e.setCancelled(true);
        var p = (Player) e.getWhoClicked();
        var pf = profiles.get(p.getUniqueId());

        ItemMeta meta = e.getCurrentItem().getItemMeta();
        var container = meta.getPersistentDataContainer();
        String type = container.get(plugin.keyType, PersistentDataType.STRING);
        String id = container.get(plugin.keyId, PersistentDataType.STRING);
        if (type == null || id == null) return;

        if (isProfileMenu && PROFILE_MENU_TYPE.equals(type)) {
            if (ID_INFO.equals(id) && e.getClick().isRightClick()) {
                showProfileInfo(p, pf);
                p.closeInventory();
            } else if (ID_SETTINGS.equals(id)) {
                if (e.getClick().isRightClick()) {
                    p.sendMessage("§7Ajustes disponibles: §dFly§7, §aVisibilidad de jugadores§7 y §bMensajes privados§7.");
                }
                if (e.getClick().isLeftClick()) {
                    openUserSettings(p, pf);
                }
            }
        } else if (isSettingsMenu && PROFILE_SETTINGS_TYPE.equals(type) && e.getClick().isLeftClick()) {
            switch (id) {
                case ID_FLY -> {
                    boolean next = !pf.canFly;
                    pf.canFly = next;
                    profiles.save(pf);
                    p.setAllowFlight(next);
                    if (!next && p.isFlying()) {
                        p.setFlying(false);
                    }
                    p.sendMessage("§7Fly: " + (next ? "§aActivado" : "§cDesactivado"));
                    reopenUserSettings(p, pf);
                }
                case ID_VISIBILITY -> {
                    pf.lobbyHidePlayers = !pf.lobbyHidePlayers;
                    profiles.save(pf);
                    Bukkit.getOnlinePlayers().forEach(other -> {
                        if (pf.lobbyHidePlayers) {
                            p.hidePlayer(plugin, other);
                        } else {
                            p.showPlayer(plugin, other);
                        }
                    });
                    p.sendMessage("§7Jugadores: " + (pf.lobbyHidePlayers ? "§cOcultos" : "§aVisibles"));
                    reopenUserSettings(p, pf);
                }
                case ID_MESSAGES -> {
                    pf.allowPM = !pf.allowPM;
                    profiles.save(pf);
                    p.sendMessage("§7Mensajes privados: " + (pf.allowPM ? "§aActivados" : "§cDesactivados"));
                    reopenUserSettings(p, pf);
                }
            }
        }
    }

    private void openUserSettings(Player p, Profile pf) {
        Inventory inv = Bukkit.createInventory(p, 27, "§eAjustes de Usuario");

        ItemStack fly = named(new ItemStack(Material.FEATHER),
                "§dFly: " + (pf.canFly ? "§aActivado" : "§cDesactivado"),
                "§7Clic izquierdo para alternar.");
        tag(fly, PROFILE_SETTINGS_TYPE, ID_FLY);
        inv.setItem(11, fly);

        ItemStack visibility = named(new ItemStack(Material.ENDER_EYE),
                "§aVisibilidad: " + (pf.lobbyHidePlayers ? "§cOcultos" : "§aVisibles"),
                "§7Clic izquierdo para alternar.");
        tag(visibility, PROFILE_SETTINGS_TYPE, ID_VISIBILITY);
        inv.setItem(13, visibility);

        ItemStack messages = named(new ItemStack(Material.PAPER),
                "§bMensajes: " + (pf.allowPM ? "§aActivados" : "§cDesactivados"),
                "§7Clic izquierdo para alternar.");
        tag(messages, PROFILE_SETTINGS_TYPE, ID_MESSAGES);
        inv.setItem(15, messages);

        p.openInventory(inv);
    }

    private void reopenUserSettings(Player p, Profile pf) {
        Bukkit.getScheduler().runTask(plugin, () -> openUserSettings(p, pf));
    }

    private void showProfileInfo(Player p, Profile pf) {
        String rank = lp.primaryGroup(p);
        if (rank == null) {
            rank = pf.localRank;
        }
        p.sendMessage("§8§m------------------------------");
        p.sendMessage("§bNick: §f" + p.getName());
        p.sendMessage("§dRango: §f" + rank);
        p.sendMessage("§6Monedas: §e" + pf.coins);
        p.sendMessage("§8§m------------------------------");
    }

    private void tag(ItemStack item, String type, String id) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.keyType, PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(plugin.keyId, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
    }
}
