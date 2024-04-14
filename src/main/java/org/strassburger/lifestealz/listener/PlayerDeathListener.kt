package org.strassburger.lifestealz.listener

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
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
import org.strassburger.lifestealz.util.Replaceable


class PlayerDeathListener : Listener {
    val disabledBanOnDeath = Lifestealz.instance.config.getBoolean("disablePlayerBanOnElimination")
    @EventHandler
    fun playerDeathFunction(event: PlayerDeathEvent) {
        val player = event.entity as? Player ?: return
        val killer = player.killer

        val worldWhitelisted = Lifestealz.instance.config.getList("worlds")?.contains(player.location.world.name)
        if (worldWhitelisted == null || !worldWhitelisted) return

        if (Lifestealz.hasWorldGuard) {
            val localPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
            val loc: com.sk89q.worldedit.util.Location = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(player.location)
            val container = WorldGuard.getInstance().platform.regionContainer
            val query = container.createQuery()

            val set = query.getApplicableRegions(loc)

            val flagValue: Boolean = set.testState(localPlayer, Lifestealz.worldGuardManager.heartLossFlag)

            if (!flagValue) {
                return
            }
        }

        if (killer != null && Lifestealz.instance.config.getBoolean("looseHeartsToPlayer")) {
            //If player was killed by other player

            val killerPlayerdata = ManagePlayerdata().getPlayerData(name = killer.name, uuid = killer.uniqueId.toString())

            val configLimit = Lifestealz.instance.config.getInt("maxHearts")

            if (Lifestealz.instance.config.getBoolean("dropHearts")) {
                player.world.dropItemNaturally(player.location, ManageCustomItems().createHeartItem())
            } else {
                if (killerPlayerdata.maxhp >= (configLimit * 2).toDouble()) {
                    if (Lifestealz.instance.config.getBoolean("dropHeartsIfMax")) {
                        player.world.dropItemNaturally(player.location, ManageCustomItems().createHeartItem())
                    } else {
                        killer.sendMessage(Lifestealz.getAndFormatMsg(false, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", Replaceable("%limit%", configLimit.toString())))
                    }
                } else {
                    ManagePlayerdata().manageHearts(player = killer, direction = "inc", amount = 2.0)
                    killer.maxHealth += 2.0
                    killer.health += 2.0
                }
            }

            val playerData = ManagePlayerdata().getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

            if (playerData.maxhp - 2.0 <= 0.0) {
                val eleminationCommands = Lifestealz.instance.config.getStringList("eliminationCommands")
                eleminationCommands.forEach {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("&player&", player.name))
                }

                if (!disabledBanOnDeath) {
                    player.inventory.clear()
                    val kickmsg = Lifestealz.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!")
                    player.kick(kickmsg, PlayerKickEvent.Cause.BANNED)
                    if (Lifestealz.instance.config.getBoolean("announceElimination")) {
                        val elimAannouncementMsg = Lifestealz.getAndFormatMsg(true, "messages.eliminationAnnouncement", "&c%player% &7has been eliminated by &c%killer%&7!", Replaceable("%player%", player.name), Replaceable("%killer%", killer.name))
                        Bukkit.broadcast(elimAannouncementMsg)
                    }
                    ManagePlayerdata().manageHearts(player = player, direction = "set", amount = 0.0)
                    return
                } else {
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
                val eleminationCommands = Lifestealz.instance.config.getStringList("eliminationCommands")
                eleminationCommands.forEach {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("&player&", player.name))
                }

                if (!disabledBanOnDeath) {
                    val kickmsg = Lifestealz.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!")
                    player.kick(kickmsg, PlayerKickEvent.Cause.BANNED)
                    if (Lifestealz.instance.config.getBoolean("announceElimination")) {
                        val elimAannouncementMsg = Lifestealz.getAndFormatMsg(true, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", Replaceable("%player%", player.name))
                        Bukkit.broadcast(elimAannouncementMsg)
                    }
                    ManagePlayerdata().manageHearts(player = player, direction = "set", amount = 0.0)
                } else {
                    var respawnHP = (Lifestealz.instance.config.getInt("respawnHP") * 2).toDouble()
                    if (respawnHP < 2.0) respawnHP = 2.0

                    ManagePlayerdata().manageHearts(player = player, direction = "set", amount = respawnHP)
                    player.maxHealth = respawnHP
                    return
                }

                return
            }

            ManagePlayerdata().manageHearts(player = player, direction = "dec", amount = 2.0)
            player.maxHealth -= 2.0
        }
    }
}