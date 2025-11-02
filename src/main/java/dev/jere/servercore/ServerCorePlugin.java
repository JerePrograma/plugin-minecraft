package dev.jere.servercore;

import dev.jere.servercore.data.ProfileService;
import dev.jere.servercore.gui.MenuFactory;
import dev.jere.servercore.hooks.LuckPermsHook;
import dev.jere.servercore.hotbar.HotbarManager;
import dev.jere.servercore.listeners.*;
import dev.jere.servercore.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerCorePlugin extends JavaPlugin {

    private ProfileService profiles;
    private HotbarManager hotbar;
    private LuckPermsHook luckPerms;
    private MenuFactory menus;

    public NamespacedKey keyType, keyId; // para marcar items especiales

    @Override
    public void onEnable() {
        saveDefaultConfig(); // crea config.yml
        this.keyType = new NamespacedKey(this, "type");
        this.keyId = new NamespacedKey(this, "id");

        this.profiles = new ProfileService(this);
        this.luckPerms = new LuckPermsHook();
        this.menus = new MenuFactory(this, profiles, luckPerms);
        this.hotbar = new HotbarManager(this);

        // Listeners
        var pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitListener(this, profiles, hotbar), this);
        pm.registerEvents(new InteractListener(this, menus, profiles, hotbar), this);
        pm.registerEvents(new InventoryProtectionListener(), this);
        pm.registerEvents(new CommandGateListener(this, profiles), this);

        // Comandos
        getCommand("fly").setExecutor(new FlyCmd(this, profiles));
        getCommand("coins").setExecutor(new CoinsCmd(profiles));
        getCommand("msg").setExecutor(new MsgCmd(profiles));

        getLogger().info("ServerCore listo.");
        // dar hotbar a conectados tras /reload
        Bukkit.getOnlinePlayers().forEach(hotbar::giveHotbarItems);
    }

    @Override
    public void onDisable() {
        profiles.flushAll();
    }

    public ProfileService profiles() {
        return profiles;
    }

    public LuckPermsHook luckPerms() {
        return luckPerms;
    }

    public MenuFactory menus() {
        return menus;
    }
}
