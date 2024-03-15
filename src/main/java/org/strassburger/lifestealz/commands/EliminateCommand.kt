package org.strassburger.lifestealz.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.player.PlayerKickEvent
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManagePlayerdata
import org.strassburger.lifestealz.util.Replaceable

class EliminateCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val targetPlayerName = args?.getOrNull(0)

        if (targetPlayerName == null) {
            throwUsageError(sender)
            return false
        }

        val targetPlayer = Bukkit.getPlayer(targetPlayerName)

        if (targetPlayer == null) {
            throwUsageError(sender)
            return false
        }

        ManagePlayerdata().manageHearts(player = targetPlayer, amount = 0.0, direction = "set")

        for (item in targetPlayer.inventory.contents) {
            if (item != null) targetPlayer.world.dropItem(targetPlayer.location, item)
        }

        targetPlayer.inventory.clear()
        val kickmsg = Lifestealz.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!")
        targetPlayer.kick(kickmsg, PlayerKickEvent.Cause.BANNED)

        sender.sendMessage(Lifestealz.getAndFormatMsg(true, "messages.eliminateSuc", "&7You successfully eliminated &c%player%&7!", Replaceable("%player%", targetPlayer.name)))

        if (Lifestealz.instance.config.getBoolean("announceElimination")) {
            val elimAannouncementMsg = Lifestealz.getAndFormatMsg(true, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", Replaceable("%player%", targetPlayer.name))
            Bukkit.broadcast(elimAannouncementMsg)
        }
        return false
    }

    private fun throwUsageError(sender: CommandSender) {
        val usageMessage = Lifestealz.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", Replaceable("%usage%", "/eliminate <player>"))
        sender.sendMessage(usageMessage)
    }
}