package org.strassburger.lifestealz.listener

import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.strassburger.lifestealz.util.ManagePlayerdata

class PlayerJoinListener(private val plugin: JavaPlugin) : Listener {
    private val invulnerabilityDuration = 20L

    @EventHandler
    fun playerJoinFunction(event: PlayerJoinEvent) {
        val player = event.player
        val playerData = ManagePlayerdata().getPlayerData(name = player.name, uuid = player.uniqueId.toString())

        applyInvulnerability(player)

        setMaxHealth(player, playerData.maxhp)
    }

    fun setMaxHealth(player: Player, maxHealth: Double) {
        val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        if (attribute != null) {
            attribute.baseValue = maxHealth
            //player.health = maxHealth
        }
    }

    private fun applyInvulnerability(player: Player) {
        // Set player invulnerable
        player.isInvulnerable = true

        // Schedule a task to remove invulnerability after the specified duration
        object : BukkitRunnable() {
            override fun run() {
                player.isInvulnerable = false
            }
        }.runTaskLater(plugin, invulnerabilityDuration)
    }
}