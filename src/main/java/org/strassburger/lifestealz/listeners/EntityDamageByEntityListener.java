package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.GracePeriodManager;
import org.strassburger.lifestealz.util.MessageUtils;

public final class EntityDamageByEntityListener implements Listener {
    private final LifeStealZ plugin;

    public EntityDamageByEntityListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damagedEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();

        boolean preventCrystalPVP = plugin.getConfig().getBoolean("preventCrystalPVP");

        if ((damagedEntity instanceof Player) && damagerEntity.getType() == EntityType.END_CRYSTAL && preventCrystalPVP) event.setCancelled(true);

        if (damagedEntity instanceof Player && damagerEntity instanceof Player) {
            Player damagedPlayer = (Player) damagedEntity;
            Player damagerPlayer = (Player) damagerEntity;

            GracePeriodManager gracePeriodManager = plugin.getGracePeriodManager();

            if (gracePeriodManager.isInGracePeriod(damagedPlayer) && !gracePeriodManager.getConfig().damageFromPlayers()) {
                event.setCancelled(true);
                damagerPlayer.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "noDamageInGracePeriod",
                        "&cYou can't damage players during the grace period!"
                ));
                return;
            }

            if (gracePeriodManager.isInGracePeriod(damagerPlayer) && !gracePeriodManager.getConfig().damageToPlayers()) {
                event.setCancelled(true);
                damagerPlayer.sendMessage(MessageUtils.getAndFormatMsg(
                        false,
                        "noDamageInGracePeriod",
                        "&cYou can't damage players during the grace period!"
                ));
                return;
            }
        }
    }
}
