package org.strassburger.lifestealz.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Utility class for creating custom player skull items in Minecraft.
 * This class provides a method to generate a player skull with a custom texture
 * specified by a URL.
 */
public class HeadUtil {

    /**
     * Creates a custom player skull item with the specified texture URL.
     *
     * @param url The URL of the texture to apply to the skull. The URL should point to
     *            a valid skin texture, or the default skull will be returned if the URL
     *            is null or invalid.
     * @return An {@link ItemStack} of type PLAYER_HEAD with the custom texture applied.
     */
    public static ItemStack createCustomSkull(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (url == null || url.isEmpty()) return skull;

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "CustomSkull");

        PlayerTextures textures = profile.getTextures();
        try {
            URL textureUrl = new URL(url);
            textures.setSkin(textureUrl);
            profile.setTextures(textures);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        skullMeta.setOwnerProfile(profile);
        skull.setItemMeta(skullMeta);

        return skull;
    }
}
