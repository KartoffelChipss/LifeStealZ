package org.strassburger.lifestealz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.WhitelistManager;
import org.strassburger.lifestealz.storage.PlayerData;
import org.strassburger.lifestealz.storage.Storage;

public final class PlayerLoginListener implements Listener {
    private final LifeStealZ plugin;

    public PlayerLoginListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Storage storage = plugin.getStorage();

        if (!WhitelistManager.isWorldWhitelisted(player)) return;

        PlayerData playerData = loadOrCreatePlayerData(player, storage);

        if (shouldKickPlayer(playerData)) {
            kickPlayer(event);
        }
    }

    private PlayerData loadOrCreatePlayerData(Player player, Storage storage) {
        PlayerData playerData = plugin.getStorage().load(player.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(player.getName(), player.getUniqueId());
            playerData.setFirstJoin(System.currentTimeMillis());
            storage.save(playerData);
            plugin.getGracePeriodManager().startGracePeriod(player);
            plugin.getOfflinePlayerCache().addItem(player.getName());
        }
        return playerData;
    }

    private boolean shouldKickPlayer(PlayerData playerData) {
        boolean disabledBanOnDeath = plugin.getConfig().getBoolean("disablePlayerBanOnElimination");
        double minHearts = plugin.getConfig().getInt("minHearts") * 2;
        return playerData.getMaxHealth() <= minHearts && !disabledBanOnDeath;
    }

    private void kickPlayer(PlayerLoginEvent event) {
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        Component kickmsg = MessageUtils.getAndFormatMsg(false, "eliminatedJoin", "&cYou don't have any hearts left!");
        event.kickMessage(kickmsg);
    }
}