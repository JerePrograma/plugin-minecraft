package dev.jere.servercore.model;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class ServerInventoryHolder implements InventoryHolder {
    private final UUID owner;

    public ServerInventoryHolder(UUID owner) {
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }

    @Override
    public Inventory getInventory() {
        return null; // Bukkit manejará el inventario asociado
    }
}
