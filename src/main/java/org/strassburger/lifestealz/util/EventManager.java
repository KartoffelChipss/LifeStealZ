package org.strassburger.lifestealz.util;

import org.bukkit.event.Listener;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.listeners.*;

public final class EventManager {
    private final LifeStealZ plugin;

    public EventManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers all listeners
     */
    public void registerListeners() {
        registerListener(new PlayerJoinListener(plugin));
        registerListener(new PlayerLoginListener(plugin));
        registerListener(new EntityDamageByEntityListener(plugin));
        registerListener(new EntityResurrectListener(plugin));
        registerListener(new InteractionListener(plugin));
        registerListener(new InventoryCloseListener(plugin));
        registerListener(new InventoryClickListener(plugin));
        registerListener(new PlayerDeathListener(plugin));
        registerListener(new WorldSwitchListener(plugin));
        registerListener(new PlayerItemPickupListener(plugin));
        registerListener(new InteractionEntityEventListener(plugin));
        registerListener(new PrepareItemCraft());
        registerListener(new PrepareGrindstone());
    }

    /**
     * Registers a listener
     *
     * @param listener The listener to register
     */
    private void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
