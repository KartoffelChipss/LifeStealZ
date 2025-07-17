package com.zetaplugins.lifestealz.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 * Utility class to manage the maximum number of hearts a player can have.
 */
public final class MaxHeartsManager {
    private MaxHeartsManager() {}

    /**
     * Returns the maximum number of hearts a player can have.
     * @param player the player to check
     * @param config the LifeStealZ main configuration
     * @return the maximum number of hearts the player can have
     */
    public static double getMaxHearts(Player player, FileConfiguration config) {
        final double configMaxHearts = config.getInt("maxHearts") * 2;

        int highestFound = -1;

        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String perm = permInfo.getPermission();
            if (perm.startsWith("lifestealz.maxhearts.")) {
                try {
                    String numberPart = perm.substring("lifestealz.maxhearts.".length());
                    int hearts = Integer.parseInt(numberPart) * 2;
                    if (hearts > highestFound) highestFound = hearts;
                } catch (NumberFormatException ignored) {}
            }
        }

        return highestFound == -1 ? configMaxHearts : highestFound;
    }
}
