package dev.jere.servercore.listeners;

import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryProtectionListener implements Listener {
    @EventHandler
    public void onMove(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getSlot() < 9) { // slots hotbar
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null &&
                    e.getCurrentItem().getItemMeta().getPersistentDataContainer().getKeys().size() > 0) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        var meta = e.getItemDrop().getItemStack().getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().getKeys().size() > 0) e.setCancelled(true);
    }
}
