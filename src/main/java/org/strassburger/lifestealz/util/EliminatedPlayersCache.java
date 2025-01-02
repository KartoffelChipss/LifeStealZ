package org.strassburger.lifestealz.util;

import org.strassburger.lifestealz.LifeStealZ;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EliminatedPlayersCache {
    private final Set<String> eliminatedPlayers;
    private final LifeStealZ plugin;

    /**
     * A cache for eliminated players to avoid unnecessary database queries on tab completion
     */
    public EliminatedPlayersCache(LifeStealZ plugin) {
        this.plugin = plugin;
        this.eliminatedPlayers = Collections.synchronizedSet(new HashSet<>());
        reloadCache();
    }

    /**
     * Reload the cache from the database
     */
    public void reloadCache() {
        synchronized (eliminatedPlayers) {
            eliminatedPlayers.clear();
            eliminatedPlayers.addAll(plugin.getStorage().getEliminatedPlayerNames());
        }
    }

    /**
     * Get a set of all eliminated players
     * @return A set of all eliminated players
     */
    public Set<String> getEliminatedPlayers() {
        synchronized (eliminatedPlayers) {
            return new HashSet<>(eliminatedPlayers);
        }
    }

    /**
     * Add a player to the cache
     * @param username The username of the player to add
     */
    public void addEliminatedPlayer(String username) {
        synchronized (eliminatedPlayers) {
            eliminatedPlayers.add(username);
        }
    }

    /**
     * Remove a player from the cache
     * @param username The username of the player to remove
     */
    public void removeEliminatedPlayer(String username) {
        synchronized (eliminatedPlayers) {
            eliminatedPlayers.remove(username);
        }
    }
}
