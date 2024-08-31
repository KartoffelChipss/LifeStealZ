package org.strassburger.lifestealz.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.List;

public class WhitelistManager {

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

        if (!worldWhitelist.contains(player.getLocation().getWorld().getName())) {
            player.sendMessage(MessageUtils.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"));
            return false;
        }
        return true;
    }
}