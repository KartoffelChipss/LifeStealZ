package com.zetaplugins.lifestealz.caches;

import com.zetaplugins.lifestealz.LifeStealZ;

import java.util.HashSet;
import java.util.Set;

public final class OfflinePlayerCache extends Cache<String> {

    /**
     * A cache for offline players to avoid unnecessary database queries on tab completion
     */
    public OfflinePlayerCache(LifeStealZ plugin) {
        super(plugin);
    }

    /**
     * Reload the cache from the database
     */
    @Override
    public void reloadCache() {
        clearCache();
        Set<String> offlinePlayerNames = new HashSet<>(getPlugin().getStorage().getPlayerNames());
        addAllItems(offlinePlayerNames);
    }
}
