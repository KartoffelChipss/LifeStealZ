package org.strassburger.lifestealz.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener : Listener {
    @EventHandler
    fun playerQuitFunction(event: PlayerQuitEvent) {
        val player = event.player

        removeInvulnerability(player)
    }
    private fun removeInvulnerability(player: Player) {
        // Remove invulnerability if the player is still invulnerable
        if (player.isInvulnerable) {
            player.isInvulnerable = false
        }
    }
}