package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.geysermc.GeyserManager;
import org.strassburger.lifestealz.util.geysermc.GeyserPlayerFile;
import org.strassburger.lifestealz.storage.PlayerData;
import org.strassburger.lifestealz.storage.Storage;

public final class PlayerJoinListener implements Listener {

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

        PlayerData playerData = loadOrCreatePlayerData(player, storage);
        LifeStealZ.setMaxHealth(player, playerData.getMaxHealth());

        notifyOpAboutUpdate(player);
    }

    private PlayerData loadOrCreatePlayerData(Player player, Storage storage) {
        PlayerData playerData = storage.load(player.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(player.getName(), player.getUniqueId());
            storage.save(playerData);
            plugin.getOfflinePlayerCache().addItem(player.getName());
        }
        return playerData;
    }

    private void notifyOpAboutUpdate(Player player) {
        if (player.isOp() && plugin.getConfig().getBoolean("checkForUpdates") && plugin.getVersionChecker().isNewVersionAvailable()) {
            player.sendMessage(MessageUtils.getAndFormatMsg(true, "newVersionAvailable", "&7A new version of LifeStealZ is available!\\n&c<click:OPEN_URL:https://modrinth.com/plugin/lifestealz/versions>https://modrinth.com/plugin/lifestealz/versions</click>"));
        }
    }
}