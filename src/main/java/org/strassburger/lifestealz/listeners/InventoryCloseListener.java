package org.strassburger.lifestealz.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.GuiManager;

import java.util.UUID;

public final class InventoryCloseListener implements Listener {
    private final LifeStealZ plugin;

    public InventoryCloseListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        plugin.getRecipeManager().cancelAnimations(event.getInventory());
        if (GuiManager.RECIPE_GUI_MAP.get(playerUUID) != null) {
            GuiManager.RECIPE_GUI_MAP.remove(playerUUID);
        }

        if (GuiManager.REVIVE_BEACON_GUI_MAP.get(playerUUID) != null) {
            GuiManager.REVIVE_BEACON_GUI_MAP.remove(playerUUID);
        }
        if (GuiManager.REVIVE_BEACON_INVENTORY_LOCATIONS.get(playerUUID) != null) {
            GuiManager.REVIVE_BEACON_INVENTORY_LOCATIONS.remove(playerUUID);
        }

        if (GuiManager.REVIVE_GUI_MAP.get(playerUUID) != null) {
            GuiManager.REVIVE_GUI_MAP.remove(playerUUID);
        }
    }
}
