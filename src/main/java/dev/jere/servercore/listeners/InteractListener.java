package dev.jere.servercore.listeners;

import dev.jere.servercore.ServerCorePlugin;
import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.gui.MenuFactory;
import dev.jere.servercore.hotbar.HotbarManager;
import dev.jere.servercore.model.Profile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

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
        e.setCancelled(true);
        Player p = e.getPlayer();
        Profile pf = profiles.get(p.getUniqueId());
        switch (type) {
            case HotbarManager.T_MENU -> menus.openMain(p, pf);
            case HotbarManager.T_SETTINGS -> menus.openSettings(p, pf);
            case HotbarManager.T_FLY -> {
                boolean next = !p.getAllowFlight();
                p.setAllowFlight(next);
                if (!next && p.isFlying()) p.setFlying(false);
                pf.canFly = next;
                profiles.save(pf);

                // acción visual en barra
                p.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        new TextComponent(next ? "✈ Vuelo: ON" : "✈ Vuelo: OFF")
                );
                p.playSound(p.getLocation(), next ? org.bukkit.Sound.ENTITY_PLAYER_LEVELUP : org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 0.7f, next ? 1.2f : 0.6f);
            }
        }
    }
}
