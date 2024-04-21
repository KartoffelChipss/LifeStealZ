package org.strassburger.lifestealz.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.storage.PlayerData;
import org.strassburger.lifestealz.util.storage.PlayerDataStorage;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataStorage playerDataStorage = LifeStealZ.getInstance().getPlayerDataStorage();

        PlayerData playerData = playerDataStorage.load(player.getUniqueId());

        if (playerData == null) {
            PlayerData newPlayerData = new PlayerData(player.getName(), player.getUniqueId());
            playerDataStorage.save(newPlayerData);
            playerData = newPlayerData;
        }

        setMaxHealth(player, playerData.getMaxhp());
    }

    private void setMaxHealth(Player player, double maxHealth) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(maxHealth);
            player.setHealthScale(maxHealth);
        }
    }
}
