package org.strassburger.lifestealz.listener

import net.kyori.adventure.text.Component
import org.bukkit.BanEntry
import org.bukkit.BanList
import org.bukkit.Bukkit
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
    private val disabledBanOnDeath = Lifestealz.instance.config.getBoolean("disablePlayerBanOnElimination")

    @EventHandler
    fun playerLoginFunction(event: PlayerLoginEvent) {
        val player = event.player

        val playerData = ManagePlayerdata().getPlayerData(name = player.name, uuid = player.uniqueId.toString())

        applyInvulnerability(player)

        val banList: BanList = Bukkit.getBanList(BanList.Type.NAME)

        if (banList.isBanned(player.name)) {
            val banEntry: BanEntry? = banList.getBanEntry(player.name)
            if (banEntry != null) {
                event.result = PlayerLoginEvent.Result.KICK_BANNED
                val reason = banEntry.reason?: "none"
                event.kickMessage(Component.text("§cYou have been banned for the following reason:\n\n§r$reason"))
            }
            return
        }

        if (playerData.maxhp <= 0.0 && !disabledBanOnDeath) {

            event.result = PlayerLoginEvent.Result.KICK_OTHER
            val kickmsg = Lifestealz.formatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!")
            event.kickMessage(Component.text(kickmsg))

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