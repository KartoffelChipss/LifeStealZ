package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.WhitelistManager;

import java.util.List;

public class EntityResurrectListener implements Listener {
    private final LifeStealZ plugin;

    public EntityResurrectListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityRessurect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!WhitelistManager.isWorldWhitelisted(player)) return;

        if (plugin.getConfig().getBoolean("preventTotems")) event.setCancelled(true);
    }
}
