package org.strassburger.lifestealz.commands

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManagePlayerdata

class ReviveCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        val targetPlayerName = args?.getOrNull(0)

        val bypassOption = args?.getOrNull(1)

        if (targetPlayerName == null) {
            throwUsageError(sender)
            return false
        }

        val targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName)

        if (!ManagePlayerdata().checkForPlayer(targetPlayer.uniqueId.toString())) {
            sender.sendMessage(Component.text(Lifestealz.formatMsg(false, "messages.noPlayerData", "&cThis player has not played on this server yet!")))
            return false
        }

        val playerdata = ManagePlayerdata().getPlayerData(name = targetPlayerName, uuid = targetPlayer.uniqueId.toString())

        val maxRevives = Lifestealz.instance.config.getInt("maxRevives")

        if (maxRevives != -1 && playerdata.hasbeenRevived >= maxRevives && (bypassOption == null || bypassOption != "bypass" || !sender.hasPermission("lifestealz.bypassrevivelimit"))) {
            sender.sendMessage(Component.text(
                Lifestealz.formatMsg(false, "messages.reviveMaxReached", "&cThis player has already been revived %amount% times!").replace("%amount%", playerdata.hasbeenRevived.toString())
            ))
            return false
        }

        if (playerdata.maxhp > 0.0) {
            sender.sendMessage(Component.text(Lifestealz.formatMsg(false, "messages.onlyReviveElimPlayers","&cYou can only revive eliminated players!")))
            return false
        }

        ManagePlayerdata().manageOfflineHearts(name = targetPlayerName, uuid = targetPlayer.uniqueId.toString(), amount = 2.0, direction = "set")
        ManagePlayerdata().addRevive(name = targetPlayerName, uuid = targetPlayer.uniqueId.toString())

        sender.sendMessage(Component.text(Lifestealz.formatMsg(true, "messages.reviveSuccess", "&7You successfully revived &c%player%&7!").replace("%player%", targetPlayerName)))

        return false
    }

    fun throwUsageError(sender: CommandSender) {
        val usageMessage = Lifestealz.formatMsg(false, "messages.usageError", "&cUsage: %usage%").replace("%usage%", "/revive <player>")
        sender.sendMessage(Component.text(usageMessage))
    }
}