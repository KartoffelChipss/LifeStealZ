package org.strassburger.lifestealz.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElytraDisableListener implements Listener {

    private final LifeStealZ plugin;
    private Map<UUID, Long> lastPVPTime = new HashMap<>();

    public ElytraDisableListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        boolean preventElytra = plugin.getConfig().getBoolean("preventElytraOnPvP");
        if (preventElytra && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            long currentTime = System.currentTimeMillis();
            lastPVPTime.put(victim.getUniqueId(), currentTime);
            lastPVPTime.put(attacker.getUniqueId(), currentTime);
        }
    }

    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        boolean preventElytra = plugin.getConfig().getBoolean("preventElytraOnPvP");
        if (preventElytra && event.isGliding() && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID uuid = player.getUniqueId();
            long currentTime = System.currentTimeMillis();

            if (lastPVPTime.containsKey(uuid) && (currentTime - lastPVPTime.get(uuid)) < 15000) {
                event.setCancelled(true);
                player.sendMessage("§cElytra is disabled for 15 seconds after PVP!");
            }
        }
    }
}