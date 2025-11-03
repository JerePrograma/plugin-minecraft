package dev.jere.servercore.listeners;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.gui.MenuFactory;
import dev.jere.servercore.hotbar.HotbarManager;
import dev.jere.servercore.model.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.event.block.Action;

public class InteractListener implements Listener {
    private final ServerCorePlugin plugin;
    private final MenuFactory menus;
    private final ProfileService profiles;

    public InteractListener(ServerCorePlugin plugin, MenuFactory menus, ProfileService profiles, HotbarManager hotbar) {
        this.plugin = plugin; this.menus = menus; this.profiles = profiles;
    }

    @EventHandler(ignoreCancelled = true) public void onUse(PlayerInteractEvent e){
        if (e.getHand()!= EquipmentSlot.HAND) return;
        ItemStack it = e.getItem();
        if (it==null || it.getItemMeta()==null) return;
        var pdc = it.getItemMeta().getPersistentDataContainer();
        String type = pdc.get(plugin.keyType, PersistentDataType.STRING);
        if (type==null) return;
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        e.setCancelled(true);
        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);
        Player p = e.getPlayer();
        Profile pf = profiles.get(p.getUniqueId());
        switch (type) {
            case HotbarManager.T_PROFILE -> menus.openProfileMenu(p, pf);
        }
    }
}
