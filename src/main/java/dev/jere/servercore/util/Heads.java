// src/main/java/dev/jere/servercore/util/Heads.java
package dev.jere.servercore.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class Heads {

    /**
     * @param name       Texto a mostrar en la cabeza (display name)
     * @param textureRef Puede ser:
     *                   - Hash: "3f2c9c8b8..." (se completa a https://textures.minecraft.net/texture/<hash>)
     *                   - URL completa: "https://textures.minecraft.net/texture/3f2c9c8b8..."
     */
    public static ItemStack customHead(String name, String textureRef) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        profile.getTextures().setSkin(toTextureUrl(textureRef)); // <-- URL, no URI

        meta.setOwnerProfile(profile); // correcto en Spigot
        meta.setDisplayName(name);
        head.setItemMeta(meta);
        return head;
    }

    private static URL toTextureUrl(String ref) {
        try {
            if (ref == null || ref.isBlank()) {
                throw new MalformedURLException("Vacío");
            }
            if (ref.startsWith("http://") || ref.startsWith("https://")) {
                return new URL(ref);
            }
            // Se asume hash de textures.minecraft.net
            return new URL("https://textures.minecraft.net/texture/" + ref);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Texture URL/hash inválido: " + ref, e);
        }
    }
}
