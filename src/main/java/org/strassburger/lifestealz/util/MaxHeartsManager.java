package org.strassburger.lifestealz.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public final class MaxHeartsManager {
    private MaxHeartsManager() {}

    public static double getMaxHearts(Player player, FileConfiguration config) {
        final double configMaxHearts = config.getInt("maxHearts") * 2;

        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String perm = permInfo.getPermission();
            if (perm.startsWith("lifestealz.maxhearts.")) {
                try {
                    String numberPart = perm.substring("lifestealz.maxhearts.".length());
                    return Integer.parseInt(numberPart) * 2;
                } catch (NumberFormatException e) {
                    return configMaxHearts;
                }
            }
        }

        return configMaxHearts;
    }
}
