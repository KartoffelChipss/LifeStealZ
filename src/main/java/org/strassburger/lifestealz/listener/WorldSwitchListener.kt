package org.strassburger.lifestealz.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class WorldSwitchListener : Listener {
    @EventHandler
    fun worldSwitchFunction(event: PlayerChangedWorldEvent) {
        val player = event.player
    }
}