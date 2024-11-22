package org.strassburger.lifestealz.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.storage.PlayerData;

public class PapiExpansion extends PlaceholderExpansion {
    private final LifeStealZ plugin;

    public PapiExpansion(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Kartoffelchipss";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lifestealz";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player.getPlayer() == null) return null;

        switch (identifier) {
            case "name": {
                return player.getName();
            }
            case "hearts": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return String.valueOf((int) playerData.getMaxHealth() / 2);
            }
            case "revived": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return String.valueOf(playerData.getHasbeenRevived());
            }
            case "health": {
                return String.valueOf((int) (player.getPlayer().getHealth() / 2));
            }
            case "maxhearts": {
                return String.valueOf(plugin.getConfig().getInt("maxHearts"));
            }
            case "maxrevives": {
                return String.valueOf(plugin.getConfig().getInt("maxRevives"));
            }
            case "craftedhearts": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return String.valueOf(playerData.getCraftedHearts());
            }
            case "craftedrevives": {
                PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
                return String.valueOf(playerData.getCraftedRevives());
            }
        }

        return null;
    }
}
