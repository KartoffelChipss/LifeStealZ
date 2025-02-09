package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.WhitelistManager;
import org.strassburger.lifestealz.util.geysermc.GeyserManager;
import org.strassburger.lifestealz.util.geysermc.GeyserPlayerFile;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.Storage;

public class PlayerJoinListener implements Listener {

    private final LifeStealZ plugin;

    private final GeyserManager geyserManager;
    private final GeyserPlayerFile geyserPlayerFile;

    public PlayerJoinListener(LifeStealZ plugin) {
        this.plugin = plugin;
        this.geyserManager = plugin.getGeyserManager();
        this.geyserPlayerFile = plugin.getGeyserPlayerFile();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Storage storage = plugin.getStorage();

        if(plugin.hasGeyser()) {
            if(geyserManager.isBedrockPlayer(player)) {
                geyserPlayerFile.savePlayer(player.getUniqueId(), player.getName());
            }
        }

        if (!WhitelistManager.isWorldWhitelisted(player)) {
            handleUnwhitelistedWorld(player);
            return;
        }

        PlayerData playerData = loadOrCreatePlayerData(player, storage);
        LifeStealZ.setMaxHealth(player, playerData.getMaxHealth());

        notifyOpAboutUpdate(player);
    }

    private void handleUnwhitelistedWorld(Player player) {
        if ((player.hasPermission("lifestealz.admin.*") || player.isOp()) && !plugin.getConfig().getBoolean("suppressWhitelistMessage", false)) {
            player.sendMessage(MessageUtils.getAndFormatMsg(
                    false,
                    "unwhitelistedWorld",
                    "\n<red><b><grey>></grey> World Whitelist</b></red>\n\n<gray>You are currently playing on world <red><click:COPY_TO_CLIPBOARD:'&world&'><hover:show_text:'&7Copy to clipboard'>&world&</hover></click></red>.\nThis world is not whitelisted. LSZ won't activate here.\n</gray>\n<red><u><click:open_url:'https://lsz.strassburger.dev/configuration/whitelist'>Documentation</click></u></red>   <red><u><click:open_url:'https://strassburger.org/discord'>Support Discord</click></u></red>   <u><hover:show_text:'<gray>To ignore: \nSet 'supressWhitelistMessage' to <b>true</b> in the config file.</gray>'><red>Hide Message</red></hover></u>\n",
                    new MessageUtils.Replaceable("&world&", player.getLocation().getWorld().getName())
            ));
        }
    }

    private PlayerData loadOrCreatePlayerData(Player player, Storage storage) {
        PlayerData playerData = storage.load(player.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(player.getName(), player.getUniqueId());
            storage.save(playerData);
            storage.save(playerData);
        }
        return playerData;
    }

    private void notifyOpAboutUpdate(Player player) {
        if (player.isOp() && plugin.getConfig().getBoolean("checkForUpdates") && plugin.getVersionChecker().NEW_VERSION_AVAILABLE) {
            player.sendMessage(MessageUtils.getAndFormatMsg(true, "messages.newVersionAvailable", "&7A new version of LifeStealZ is available!\\n&c<click:OPEN_URL:https://modrinth.com/plugin/lifestealz/versions>https://modrinth.com/plugin/lifestealz/versions</click>"));
        }
    }
}