package org.strassburger.lifestealz.util.geysermc;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.UUID;

public class GeyserManager {
    private final FloodgateApi geyserApi = FloodgateApi.getInstance();
    private final GeyserPlayerFile geyserPlayerFile = LifeStealZ.getInstance().getGeyserPlayerFile();

    public boolean isBedrockPlayer(Player player) {
        return geyserApi.isFloodgatePlayer(player.getUniqueId());
    }

    public UUID getOfflineBedrockPlayerUniqueId(String playerName) {
        return geyserPlayerFile.getPlayerUUID(playerName);
    }

    public String getOfflineBedrockPlayerName(UUID playerUniqueId) {
        return geyserPlayerFile.getPlayerName(playerUniqueId);
    }
}