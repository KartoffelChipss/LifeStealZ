package org.strassburger.lifestealz.util;

import org.bukkit.event.Listener;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.listeners.PlayerJoinListener;
import org.strassburger.lifestealz.listeners.PlayerLoginListener;

public class EventManager {
    private static final LifeStealZ plugin = LifeStealZ.getInstance();

    private EventManager() {}

    public static void registerListeners() {
        registerListener(new PlayerJoinListener());
        registerListener(new PlayerLoginListener());
    }

    private static void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
