package org.strassburger.lifestealz.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.strassburger.lifestealz.util.GuiManager;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (GuiManager.RECIPE_GUI_MAP.get(event.getPlayer().getUniqueId()) != null) GuiManager.RECIPE_GUI_MAP.remove(event.getPlayer().getUniqueId());
        if (GuiManager.REVIVE_GUI_MAP.get(event.getPlayer().getUniqueId()) != null) GuiManager.REVIVE_GUI_MAP.remove(event.getPlayer().getUniqueId());
    }
}
