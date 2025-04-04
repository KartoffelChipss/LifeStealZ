package org.strassburger.lifestealz.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.List;

public final class WhitelistManager {

    // For use in commands
    public boolean isWorldWhitelisted(CommandSender sender) {
        if (!(sender instanceof Player)) return true; // Console is always allowed

        Player player = (Player) sender;
        return isWorldWhitelisted(player);
    }

    // For use in listeners
    public static boolean isWorldWhitelisted(Player player) {
        boolean whiteListEnabled = LifeStealZ.getInstance().getConfig().getBoolean("enableWhitelist", false);
        List<String> worldWhitelist = LifeStealZ.getInstance().getConfig().getStringList("worlds");

        if (!whiteListEnabled || worldWhitelist.isEmpty()) return true;

        return worldWhitelist.contains(player.getLocation().getWorld().getName());
    }
}