package org.strassburger.lifestealz.util;

import org.bukkit.event.Listener;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.listeners.*;
import org.strassburger.lifestealz.listeners.revivebeacon.ReviveBeaconBreakListener;
import org.strassburger.lifestealz.listeners.revivebeacon.ReviveBeaconInteractListener;
import org.strassburger.lifestealz.listeners.revivebeacon.ReviveBeaconPlaceListener;

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
        registerListener(new PlayerItemPickupListener(plugin));
        registerListener(new InteractionEntityEventListener(plugin));
        registerListener(new PrepareItemCraft());
        registerListener(new PrepareGrindstone());
        registerListener(new PlayerDropItemListener());

        // Revive Beacon Listeners
        registerListener(new ReviveBeaconPlaceListener(plugin));
        registerListener(new ReviveBeaconBreakListener(plugin));
        registerListener(new ReviveBeaconInteractListener(plugin));
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
