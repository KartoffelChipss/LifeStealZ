package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.PlayerDataStorage;

import java.util.List;

public class PlayerJoinListener implements Listener {

    private final LifeStealZ plugin;

    public PlayerJoinListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataStorage playerDataStorage = plugin.getPlayerDataStorage();

        List<String> worldWhitelisted = plugin.getConfig().getStringList("worlds");

        if (!worldWhitelisted.contains(player.getLocation().getWorld().getName())) {
            if ((player.hasPermission("lifestealz.admin.*") || player.isOp()) && !plugin.getConfig().getBoolean("suppressWhitelistMessage", false)) {
                player.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "unwhitelistedWorld",
                        "\n<red><b><grey>></grey> World Whitelist</b></red>\n\n<gray>You are currently playing on world <red><click:COPY_TO_CLIPBOARD:'&world&'><hover:show_text:'&7Copy to clipboard'>&world&</hover></click></red>.\nThis world is not whitelisted. LSZ won't activate here.\n</gray>\n<red><u><click:open_url:'https://lsz.strassburger.dev/configuration/whitelist'>Documentation</click></u></red>   <red><u><click:open_url:'https://strassburger.org/discord'>Support Discord</click></u></red>   <u><hover:show_text:'<gray>To ignore: \nSet 'supressWhitelistMessage' to <b>true</b> in the config file.</gray>'><red>Hide Message</red></hover></u>\n",
                        new MessageUtils.Replaceable("&world&", player.getLocation().getWorld().getName())
                ));
            }

            return;
        }

        PlayerData playerData = playerDataStorage.load(player.getUniqueId());

        if (playerData == null) {
            PlayerData newPlayerData = new PlayerData(player.getName(), player.getUniqueId());
            playerDataStorage.save(newPlayerData);
            playerData = newPlayerData;
        }

        LifeStealZ.setMaxHealth(player, playerData.getMaxhp());

        if (player.isOp() && plugin.getConfig().getBoolean("checkForUpdates") && plugin.getVersionChecker().NEW_VERSION_AVAILABLE) {
            player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.newVersionAvailable", "&7A new version of LifeStealZ is available!\\n&c<click:OPEN_URL:https://modrinth.com/plugin/lifestealz/versions>https://modrinth.com/plugin/lifestealz/versions</click>"));
        }
    }
}
