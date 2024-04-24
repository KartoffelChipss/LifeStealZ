package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.List;

public class EntityResurrectListener implements Listener {
    @EventHandler
    public void onEntityRessurect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        List<String> worldWhitelist = LifeStealZ.getInstance().getConfig().getStringList("worlds");
        if (!worldWhitelist.contains(player.getLocation().getWorld().getName())) return;

        if (LifeStealZ.getInstance().getConfig().getBoolean("preventTotems")) event.setCancelled(true);
    }
}
