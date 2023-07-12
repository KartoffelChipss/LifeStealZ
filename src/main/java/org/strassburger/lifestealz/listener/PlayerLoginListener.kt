package org.strassburger.lifestealz.listener

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManagePlayerdata

class PlayerLoginListener(private val plugin: JavaPlugin) : Listener {
    private val invulnerabilityDuration = 20L

    @EventHandler
    fun playerLoginFunction(event: PlayerLoginEvent) {
        val player = event.player

        val playerData = ManagePlayerdata().getPlayerData(name = player.name, uuid = player.uniqueId.toString())

        applyInvulnerability(player)

        if (playerData.maxhp <= 0.0) {
            event.result =PlayerLoginEvent.Result.KICK_OTHER
            val kickmsg = Lifestealz.formatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!")
            event.kickMessage(Component.text(kickmsg))
            return
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