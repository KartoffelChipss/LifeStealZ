package org.strassburger.lifestealz.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.MessageUtils;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.PlayerDataStorage;

import java.util.List;

public class PlayerLoginListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerDataStorage playerDataStorage = LifeStealZ.getInstance().getPlayerDataStorage();

        PlayerData playerData = LifeStealZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());

        List<String> worldWhitelisted = LifeStealZ.getInstance().getConfig().getStringList("worlds");
        if (!worldWhitelisted.contains(player.getLocation().getWorld().getName())) return;

        if (playerData == null) {
            PlayerData newPlayerData = new PlayerData(player.getName(), player.getUniqueId());
            playerDataStorage.save(newPlayerData);
            playerData = newPlayerData;
        }

        boolean disabledBanOnDeath = LifeStealZ.getInstance().getConfig().getBoolean("disablePlayerBanOnElimination");
        double minHearts = LifeStealZ.getInstance().getConfig().getInt("minHearts") * 2;
        if (playerData.getMaxhp() <= minHearts && !disabledBanOnDeath) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            Component kickmsg = MessageUtils.getAndFormatMsg(false, "eliminatedJoin", "&cYou don't have any hearts left!");
            event.kickMessage(kickmsg);
        }

    }
}
