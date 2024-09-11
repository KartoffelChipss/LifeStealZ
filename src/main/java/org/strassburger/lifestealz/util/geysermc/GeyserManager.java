package org.strassburger.lifestealz.util.geysermc;

import org.bukkit.entity.Player;
import org.geysermc.geyser.api.GeyserApi;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.UUID;

public class GeyserManager {
    private GeyserApi geyserApi = GeyserApi.api();
    private GeyserPlayerFile geyserPlayerFile = LifeStealZ.getInstance().getGeyserPlayerFile();

    public boolean isBedrockPlayer(Player player) {
        if(geyserApi.isBedrockPlayer(player.getUniqueId()))
            return true;
         else
            return false;
    }

    public UUID getOfflineBedrockPlayerUniqueId(String playerName) {
        return geyserPlayerFile.getPlayerUUID(playerName);
    }

    public String getOfflineBedrockPlayerName(UUID playerUniqueId) {
        return geyserPlayerFile.getPlayerName(playerUniqueId);
    }

}
