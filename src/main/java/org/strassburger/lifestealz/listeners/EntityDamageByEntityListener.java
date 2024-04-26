package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.strassburger.lifestealz.LifeStealZ;

public class EntityDamageByEntityListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damagedEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();

        boolean preventCrystalPVP = LifeStealZ.getInstance().getConfig().getBoolean("preventCrystalPVP");

        if ((damagedEntity instanceof Player) && damagerEntity.getType() == EntityType.ENDER_CRYSTAL && preventCrystalPVP) event.setCancelled(true);
    }
}
