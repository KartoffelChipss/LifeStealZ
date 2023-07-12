package org.strassburger.lifestealz.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.strassburger.lifestealz.Lifestealz

class InventoryCloseListener : Listener {
    @EventHandler
    fun inventoryCloseFunction(event: InventoryCloseEvent) {
        val playerUUID = event.player.uniqueId

        if (Lifestealz.recipeGuiMap.containsKey(playerUUID)) Lifestealz.recipeGuiMap.remove(playerUUID)
        if (Lifestealz.reviveGuiMap.containsKey(playerUUID)) Lifestealz.reviveGuiMap.remove(playerUUID)
    }
}