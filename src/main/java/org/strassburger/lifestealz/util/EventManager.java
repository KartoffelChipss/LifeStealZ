package org.strassburger.lifestealz.util;

import org.bukkit.event.Listener;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.listeners.*;

public class EventManager {
    private static final LifeStealZ plugin = LifeStealZ.getInstance();

    private EventManager() {}

    /**
     * Registers all listeners
     */
    public static void registerListeners() {
        registerListener(new PlayerJoinListener());
        registerListener(new PlayerLoginListener());
        registerListener(new EntityDamageByEntityListener());
        registerListener(new EntityResurrectListener());
        registerListener(new InteractionListener());
        registerListener(new InventoryCloseListener());
        registerListener(new InventoryClickListener());
        registerListener(new PlayerDeathListener(plugin));
        registerListener(new WorldSwitchListener());
        registerListener(new PlayerItemPickupListener());
        registerListener(new InteractionEntityEventListener());
    }

    /**
     * Registers a listener
     *
     * @param listener The listener to register
     */
    private static void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
