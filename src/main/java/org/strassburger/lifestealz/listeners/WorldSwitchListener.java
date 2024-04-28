package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.storage.PlayerData;

import java.util.List;

public class WorldSwitchListener implements Listener {
    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        List<String> worldWhitelist = LifeStealZ.getInstance().getConfig().getStringList("worlds");

        if (worldWhitelist.contains(player.getLocation().getWorld().getName())) {
            PlayerData playerData = LifeStealZ.getInstance().getPlayerDataStorage().load(player.getUniqueId());
            LifeStealZ.setMaxHealth(player, playerData.getMaxhp());

            if (!worldWhitelist.contains(event.getFrom().getName())) player.setHealth(playerData.getMaxhp());
        } else {
            LifeStealZ.setMaxHealth(player, 20.0);
        }
    }
}
