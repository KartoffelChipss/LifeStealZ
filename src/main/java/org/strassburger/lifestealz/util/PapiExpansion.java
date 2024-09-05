package org.strassburger.lifestealz.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.storage.PlayerData;

public class PapiExpansion extends PlaceholderExpansion {
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
        return LifeStealZ.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player.getPlayer() == null) return null;

        switch (identifier) {
            case "name": {
                return player.getName();
            }
            case "hearts": {
                PlayerData playerData = LifeStealZ.getInstance().getStorage().load(player.getUniqueId());
                return String.valueOf((int) playerData.getMaxHealth() / 2);
            }
            case "revived": {
                PlayerData playerData = LifeStealZ.getInstance().getStorage().load(player.getUniqueId());
                return String.valueOf(playerData.getHasbeenRevived());
            }
            case "health": {
                return String.valueOf((int) (player.getPlayer().getHealth() / 2));
            }
            case "maxhearts": {
                return String.valueOf(LifeStealZ.getInstance().getConfig().getInt("maxHearts"));
            }
            case "maxrevives": {
                return String.valueOf(LifeStealZ.getInstance().getConfig().getInt("maxRevives"));
            }
            case "craftedhearts": {
                PlayerData playerData = LifeStealZ.getInstance().getStorage().load(player.getUniqueId());
                return String.valueOf(playerData.getCraftedHearts());
            }
            case "craftedrevives": {
                PlayerData playerData = LifeStealZ.getInstance().getStorage().load(player.getUniqueId());
                return String.valueOf(playerData.getCraftedRevives());
            }
        }

        return null;
    }
}
