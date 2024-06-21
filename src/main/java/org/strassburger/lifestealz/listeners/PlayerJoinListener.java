package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.Replaceable;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.PlayerDataStorage;

import java.util.List;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataStorage playerDataStorage = LifeStealZ.getInstance().getPlayerDataStorage();

        List<String> worldWhitelisted = LifeStealZ.getInstance().getConfig().getStringList("worlds");
        if (!worldWhitelisted.contains(player.getLocation().getWorld().getName())) return;

        PlayerData playerData = playerDataStorage.load(player.getUniqueId());

        if (playerData == null) {
            PlayerData newPlayerData = new PlayerData(player.getName(), player.getUniqueId());
            playerDataStorage.save(newPlayerData);
            playerData = newPlayerData;
        }

        LifeStealZ.setMaxHealth(player, playerData.getMaxhp());

        if (player.isOp() && LifeStealZ.getInstance().getConfig().getBoolean("checkForUpdates") && LifeStealZ.getInstance().getVersionChecker().NEW_VERSION_AVAILABLE) {
            player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.newVersionAvailable", "&7A new version of LifeStealZ is available!\\n&c<click:OPEN_URL:https://modrinth.com/plugin/lifestealz/versions>https://modrinth.com/plugin/lifestealz/versions</click>"));
        }
        if (player.hasPermission("lifestealz.admin.*")) {
            if (!worldWhitelisted.contains(player.getLocation().getWorld().getName())) {
                if (!LifeStealZ.getInstance().getConfig().getBoolean("suppressWhitelistMessage", false)) {
                    player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.unwhitedlistWorld", "<dark_gray><b>World Whitelist</b></dark_gray>\n<i><gray>You are currently playing on: &world&.\nThis world is not a whitelisted world, LSZ won't activate here.\n</gray></i>\n<red><u><b><click:open_url:'https://lsz.strassburger.dev/configuration/whitelist'>Documentation</click></b></u></red>   <b><u><hover:show_text:'<i><gray>To ignore: \nSet 'supressWhitelistMessage' to <b>true</b></gray></i>'><red>Hide Message</red></hover></b>", new Replaceable("&world&", player.getLocation().getWorld().getName())));
                }
                return;
            }
        }
    }
}
