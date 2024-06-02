package org.strassburger.lifestealz.util.storage;

import java.util.List;
import java.util.UUID;

public interface PlayerDataStorage {
    /**
     * Initializes the storage system.
     */
    void init();

    /**
     * Saves the player data to the storage system.
     *
     * @param playerData The player data to save.
     */
    void save(PlayerData playerData);

    /**
     * Loads the player data from the storage system.
     *
     * @param uuid The UUID of the player to load.
     * @return The player data of the player.
     */
    PlayerData load(String uuid);

    /**
     * Loads the player data from the storage system.
     *
     * @param uuid The UUID of the player to load.
     * @return The player data of the player.
     */
    PlayerData load(UUID uuid);

    /**
     * Get a list of all eliminated players.
     */
    List<UUID> getEliminatedPlayers();

    /**
     * Export the player data to a file.
     *
     * @param fileName The name of the file to export to.
     * @return The path to the exported file.
     */
    String export(String fileName);

    /**
     * Import the player data from a file.
     *
     * @param fileName The name of the file to import from.
     */
    void importData(String fileName);
}
