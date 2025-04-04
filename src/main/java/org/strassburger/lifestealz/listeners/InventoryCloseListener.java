package org.strassburger.lifestealz.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.GuiManager;

public final class InventoryCloseListener implements Listener {
    private final LifeStealZ plugin;

    public InventoryCloseListener(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        plugin.getRecipeManager().cancelAnimations(event.getInventory());

        if (GuiManager.RECIPE_GUI_MAP.get(event.getPlayer().getUniqueId()) != null) {
            GuiManager.RECIPE_GUI_MAP.remove(event.getPlayer().getUniqueId());
        }
    }
}
