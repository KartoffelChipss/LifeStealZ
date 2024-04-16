package org.strassburger.lifestealz.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManageCustomItems
import org.strassburger.lifestealz.util.ManagePlayerdata
import org.strassburger.lifestealz.util.Replaceable

class WithdrawCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val worldWhitelist = Lifestealz.instance.config.getList("worlds")
        if (sender is Player && worldWhitelist != null && !worldWhitelist.contains(sender.location.world.name)) {
            sender.sendMessage(Lifestealz.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"))
            return false
        }

        if (sender !is Player) return false

        val confirmOption = args?.getOrNull(0)

        val playerdata = ManagePlayerdata().getPlayerData(name = sender.name, uuid = sender.uniqueId.toString())

        val withdrawtoDeath: Boolean = Lifestealz.instance.config.getBoolean("allowDyingFromWithdraw")

        if (playerdata.maxhp <= 2.0 && (confirmOption == null || confirmOption != "confirm")) {
            sender.sendMessage(Lifestealz.getAndFormatMsg(false, "messages.noWithdraw", "&cYou would be eliminated, if you withdraw a heart!"))

            if (withdrawtoDeath) {
                sender.sendMessage(Lifestealz.getAndFormatMsg(false, "messages.withdrawConfirmmsg", "&8&oUse /withdrawheart confirm if you really want to withdraw a heart"))
            }

            return false
        }

        if (playerdata.maxhp <= 2.0) {
            if (!withdrawtoDeath) {
                sender.sendMessage(Lifestealz.getAndFormatMsg(false, "messages.noWithdraw", "&cYou would be eliminated, if you withdraw a heart!"))
                return false
            }

            sender.inventory.addItem(ManageCustomItems().createHeartItem())

            ManagePlayerdata().manageHearts(player = sender, amount = 0.0, direction = "set")

            val kickmsg = Lifestealz.getAndFormatMsg(false, "messages.eliminatedjoin", "&cYou don't have any hearts left!")
            sender.kick(kickmsg, PlayerKickEvent.Cause.BANNED)

            if (Lifestealz.instance.config.getBoolean("announceElimination")) {
                val elimAannouncementMsg = Lifestealz.getAndFormatMsg(true, "messages.eliminateionAnnouncementNature", "&c%player% &7has been eliminated!", Replaceable("%player%", sender.name))
                Bukkit.broadcast(elimAannouncementMsg)
            }

            return false
        }

        ManagePlayerdata().manageHearts(player = sender, amount = 2.0, direction = "dec")
        sender.maxHealth -= 2.0
        sender.playSound(sender.getLocation(), Sound.ENTITY_PLAYER_HURT, 500.0f, 1.0f)

        sender.inventory.addItem(ManageCustomItems().createHeartItem())

        return false
    }
}