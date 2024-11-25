package org.strassburger.lifestealz.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.customitems.CustomItem;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    public static ItemStack createCustomHead(String url) {
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

    /**
     * Creates a player head item for a given UUID.
     *
     * @param uuid The UUID of the player whose head is to be created.
     * @return An {@link ItemStack} of type PLAYER_HEAD with the player's skin.
     */
    public static ItemStack getPlayerHead(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer == null || offlinePlayer.getName() == null) return new CustomItem(Material.SKELETON_SKULL).setName("&dUnknown").setLore(new ArrayList<>(List.of("&8" + UUID.randomUUID()))).getItemStack();

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(offlinePlayer);
        head.setItemMeta(skullMeta);
        return head;
    }

    /**
     * Gets a skeleton skull instead of a head
     *
     * @param uuid The uuid of the bedrock player
     * @return A skeleton skull
     */
    public static ItemStack getBedrockPlayerHead(UUID uuid) {
        ItemStack head = new ItemStack(Material.SKELETON_SKULL);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        skullMeta.displayName(Component.text("§d" + LifeStealZ.getInstance().getGeyserManager().getOfflineBedrockPlayerName(uuid)));

        List<Component> lines = new ArrayList<>();
        lines.add(MessageUtils.getAndFormatMsg(false, "messages.revivePlayerDesc", "&7Click to revive this player"));
        lines.add(MessageUtils.formatMsg("<dark_gray>" + uuid));
        lines.add(MessageUtils.formatMsg("<dark_gray><i>This player is using the Bedrock Edition of Minecraft.</i>"));

        skullMeta.lore(lines);

        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack getAmbiguousPlayerHead(UUID uuid) {
        if(LifeStealZ.getInstance().hasGeyser() && LifeStealZ.getInstance().getGeyserPlayerFile().isPlayerStored(uuid)) {
            return getBedrockPlayerHead(uuid);
        } else {
            return getPlayerHead(uuid);
        }
    }

}
