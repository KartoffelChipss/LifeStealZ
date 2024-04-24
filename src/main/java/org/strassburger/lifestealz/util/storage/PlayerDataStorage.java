package org.strassburger.lifestealz.util.storage;

import java.util.List;
import java.util.UUID;

public interface PlayerDataStorage {
    void init();

    void save(PlayerData playerData);

    PlayerData load(String uuid);

    PlayerData load(UUID uuid);

    List<UUID> getEliminatedPlayers();
}
