// dev/jere/servercore/listeners/InteractListener.java
package dev.jere.servercore.listeners;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.gui.MenuFactory;
import dev.jere.servercore.hotbar.HotbarManager;
import dev.jere.servercore.model.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class InteractListener implements Listener {
    private final ServerCorePlugin plugin;
    private final MenuFactory menus;
    private final ProfileService profiles;

    public InteractListener(ServerCorePlugin plugin, MenuFactory menus, ProfileService profiles, HotbarManager hotbar) {
        this.plugin = plugin;
        this.menus = menus;
        this.profiles = profiles;
    }

    // Click en aire/bloque: solo RIGHT_CLICK con mano principal u offhand
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onUse(PlayerInteractEvent e) {
        final Action a = e.getAction();
        if (a != Action.RIGHT_CLICK_AIR && a != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack it = itemFromHand(e.getPlayer(), e.getHand(), e.getItem());
        if (!isProfileMain(it)) return;

        // Bloquear interacción por completo y abrir menú
        e.setCancelled(true);
        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);
        openProfile(e.getPlayer());
    }

    // Intento de colocar la skull: cancelar siempre si es el ítem del perfil
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlace(BlockPlaceEvent e) {
        final ItemStack it = e.getItemInHand();
        if (!isProfileMain(it)) return;

        e.setCancelled(true);
        openProfile(e.getPlayer());
    }

    // Click derecho a entidad (mano principal u offhand)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        final ItemStack it = itemFromHand(e.getPlayer(), e.getHand(), null);
        if (!isProfileMain(it)) return;

        e.setCancelled(true);
        openProfile(e.getPlayer());
    }

    // Click derecho "preciso" a entidad (armor stands, etc.), mano principal u offhand
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        final ItemStack it = itemFromHand(e.getPlayer(), e.getHand(), null);
        if (!isProfileMain(it)) return;

        e.setCancelled(true);
        openProfile(e.getPlayer());
    }

    // ===== helpers =====
    private ItemStack itemFromHand(Player player, EquipmentSlot hand, ItemStack fallback) {
        if (hand == null || hand == EquipmentSlot.HAND) {
            if (fallback != null) {
                return fallback;
            }
            return player.getInventory().getItemInMainHand();
        }
        if (hand == EquipmentSlot.OFF_HAND) {
            return player.getInventory().getItemInOffHand();
        }
        return fallback;
    }

    private boolean isProfileMain(ItemStack it) {
        if (it == null || it.getItemMeta() == null) return false;
        var c = it.getItemMeta().getPersistentDataContainer();
        final String type = c.get(plugin.keyType, PersistentDataType.STRING);
        final String id   = c.get(plugin.keyId,   PersistentDataType.STRING);
        return HotbarManager.T_PROFILE.equals(type) && "MAIN".equals(id);
    }

    private void openProfile(Player p) {
        // Abrir en el siguiente tick para evitar conflictos con la cancelación del evento
        final Profile pf = profiles.get(p.getUniqueId());
        Bukkit.getScheduler().runTask(plugin, () -> menus.openProfileMenu(p, pf));
    }
}
