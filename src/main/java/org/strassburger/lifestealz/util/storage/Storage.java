package org.strassburger.lifestealz.util.storage;

import org.bukkit.plugin.Plugin;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.List;
import java.util.UUID;

public abstract class Storage {

    // Private field to store the Plugin instance
    private final LifeStealZ plugin;

    // Constructor to initialize the Plugin instance
    public Storage(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    // Getter method for the Plugin instance (if needed)
    protected LifeStealZ getPlugin() {
        return plugin;
    }

    /**
     * Initializes the storage system.
     */
    public abstract void init();

    /**
     * Saves the player data to the storage system.
     *
     * @param playerData The player data to save.
     */
    public abstract void save(PlayerData playerData);

    /**
     * Loads the player data from the storage system.
     *
     * @param uuid The UUID of the player to load.
     * @return The player data of the player.
     */
    public abstract PlayerData load(String uuid);

    /**
     * Loads the player data from the storage system.
     *
     * @param uuid The UUID of the player to load.
     * @return The player data of the player.
     */
    public abstract PlayerData load(UUID uuid);

    /**
     * Get a list of all eliminated players.
     */
    public abstract List<UUID> getEliminatedPlayers();

    /**
     * Export the player data to a file.
     *
     * @param fileName The name of the file to export to.
     * @return The path to the exported file.
     */
    public abstract String export(String fileName);

    /**
     * Import the player data from a file.
     *
     * @param fileName The name of the file to import from.
     */
    public abstract void importData(String fileName);

    /**
     * Get all player names
     */
    public abstract List<String> getPlayerNames();
}