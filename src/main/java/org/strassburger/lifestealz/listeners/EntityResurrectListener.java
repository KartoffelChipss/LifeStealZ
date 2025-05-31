package org.strassburger.lifestealz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.WhitelistManager;
import org.strassburger.lifestealz.util.customitems.CustomItemManager;

import java.util.List;

public final class EntityResurrectListener implements Listener {
    private final LifeStealZ plugin;

    public EntityResurrectListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityRessurect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        ItemStack totem = null;
        if (player.getInventory().getItemInOffHand().getType().name().contains("TOTEM")) {
            totem = player.getInventory().getItemInOffHand();
        } else if (player.getInventory().getItemInMainHand().getType().name().contains("TOTEM")) {
            totem = player.getInventory().getItemInMainHand();
        }

        if (!WhitelistManager.isWorldWhitelisted(player)) return;

        System.out.println("EntityResurrectListener: " + player.getName() + " is trying to resurrect with a totem: " + (totem != null ? totem.getType().name() : "none") + "Is forbidden: " + (totem != null && CustomItemManager.isForbiddenItem(totem)));

        if (
                plugin.getConfig().getBoolean("preventTotems") ||
                (totem != null && CustomItemManager.isForbiddenItem(totem))
        )
            event.setCancelled(true);
    }
}
