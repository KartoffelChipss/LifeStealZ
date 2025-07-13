package org.strassburger.lifestealz.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.strassburger.lifestealz.LifeStealZ;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetheriteItemListener implements Listener {

    private final LifeStealZ plugin;
    private Map<UUID, Boolean> notifiedInSession = new HashMap<>();

    public NetheriteItemListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        boolean preventNetheriteItems = plugin.getConfig().getBoolean("preventNetheriteItems");
        SmithingInventory inventory = event.getInventory();
        ItemStack template = inventory.getItem(0);

        if (template != null && template.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE && preventNetheriteItems) {
            event.setResult(null);

            event.getViewers().forEach(viewer -> {
                if (viewer instanceof Player) {
                    Player player = (Player) viewer;
                    UUID uuid = player.getUniqueId();
                    if (!notifiedInSession.getOrDefault(uuid, false)) {
                        player.sendMessage("§cNetherite Items are disabled!");
                        notifiedInSession.put(uuid, true);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() instanceof SmithingInventory && event.getPlayer() instanceof Player) {
            UUID uuid = event.getPlayer().getUniqueId();
            notifiedInSession.remove(uuid);
        }
    }
}