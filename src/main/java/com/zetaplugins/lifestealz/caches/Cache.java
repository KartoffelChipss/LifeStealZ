package com.zetaplugins.lifestealz.caches;

import com.zetaplugins.lifestealz.LifeStealZ;

import java.util.HashSet;
import java.util.Set;

public abstract class Cache<T> {
    private final Set<T> cachedData;
    private final LifeStealZ plugin;

    public Cache(LifeStealZ plugin) {
        this.plugin = plugin;
        this.cachedData = new HashSet<>();
        reloadCache();
    }

    /**
     * Reload the cache from the database
     */
    public abstract void reloadCache();

    /**
     * Get a set of all cached data
     */
    public Set<T> getCachedData() {
        return new HashSet<>(cachedData);
    }

    /**
     * Add an item to the cache
     * @param item The item to add
     */
    public void addItem(T item) {
        cachedData.add(item);
    }

    /**
     * Remove an item from the cache
     * @param item The item to remove
     */
    public void removeItem(T item) {
        cachedData.remove(item);
    }

    /**
     * Add all items to the cache
     * @param items The items to add
     */
    public void addAllItems(Set<T> items) {
        cachedData.addAll(items);
    }

    /**
     * Clear the cache
     */
    public void clearCache() {
        cachedData.clear();
    }

    protected LifeStealZ getPlugin() {
        return plugin;
    }
}
