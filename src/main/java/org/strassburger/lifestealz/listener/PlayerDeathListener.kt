package org.strassburger.lifestealz.listener

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerKickEvent
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManageCustomItems
import org.strassburger.lifestealz.util.ManagePlayerdata

class PlayerDeathListener : Listener {
    val disabledBanOnDeath = Lifestealz.instance.config.getBoolean("disablePlayerBanOnElimination")
    @EventHandler
    fun playerDeathFunction(event: PlayerDeathEvent) {
        val player = event.entity as? Player ?: return
        val killer = player.killer

        val worldWhitelisted = Lifestealz.instance.config.getList("worlds")?.contains(player.location.world.name)
        if (worldWhitelisted == null || !worldWhitelisted) return

        if (killer != null && Lifestealz.instance.config.getBoolean("looseHeartsToPlayer")) {
            //If player was killed by other player

            val killerPlayerdata = ManagePlayerdata().getPlayerData(name = killer.name, uuid = killer.uniqueId.toString())

            val configLimit = Lifestealz.instance.config.getInt("maxHearts")
            if (killerPlayerdata.maxhp >= (configLimit * 2).toDouble()) {
                if (Lifestealz.instance.config.getBoolean("dropHeartsIfMax")) {
                    player.world.dropItemNaturally(player.location, ManageCustomItems().createHeartItem())
                } else {
                    killer.sendMessage(Component.text(Lifestealz.formatMsg(false, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!").replace("%limit%", configLimit.toString())))
                }
            } else {
                ManagePlayerdata().manageHearts(player = killer, direction = "inc", amount = 2.0)
                killer.maxHealth += 2.0
                killer.health += 2.0
            }

            val playerData = ManagePlayerdata().getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

            if (playerData.maxhp - 2.0 <= 0.0) {

                if (!disabledBanOnDeath) {
                    player.inventory.clear()
                    val kickmsg = Lifestealz.formatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!")
                    player.kick(Component.text(kickmsg), PlayerKickEvent.Cause.BANNED)
                    if (Lifestealz.instance.config.getBoolean("announceElimination")) {
                        val elimAannouncementMsg = Lifestealz.formatMsg(true, "messages.eliminationAnnouncement", "&c%player% &7has been eliminated by &c%killer%&7!").replace("%player%", player.name).replace("%killer%", killer.name)
                        Bukkit.broadcast(Component.text(elimAannouncementMsg))
                    }
                    ManagePlayerdata().manageHearts(player = player, direction = "set", amount = 0.0)
                    return
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (Lifestealz.instance.config.getString("eliminationCommand")?: "say &player& got eliminated").replace("&player&", player.name))
                    var respawnHP = (Lifestealz.instance.config.getInt("respawnHP") * 2).toDouble()

                    if (respawnHP < 2.0) respawnHP = 2.0

                    ManagePlayerdata().manageHearts(player = player, direction = "set", amount = respawnHP)
                    player.maxHealth = respawnHP
                    return
                }
            }

            ManagePlayerdata().manageHearts(player = player, direction = "dec", amount = 2.0)
            player.maxHealth -= 2.0
            return
        }

        if (Lifestealz.instance.config.getBoolean("looseHeartsToNature")) {
            val playerData = ManagePlayerdata().getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

            if (playerData.maxhp - 2.0 <= 0.0) {

                if (!disabledBanOnDeath) {
                    val kickmsg = Lifestealz.formatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!")
                    player.kick(Component.text(kickmsg), PlayerKickEvent.Cause.BANNED)
                    if (Lifestealz.instance.config.getBoolean("announceElimination")) {
                        val elimAannouncementMsg = Lifestealz.formatMsg(true, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!").replace("%player%", player.name)
                        Bukkit.broadcast(Component.text(elimAannouncementMsg))
                    }
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say Test")
                }

                ManagePlayerdata().manageHearts(player = player, direction = "set", amount = 0.0)
                return
            }

            ManagePlayerdata().manageHearts(player = player, direction = "dec", amount = 2.0)
            player.maxHealth -= 2.0
        }
    }
}