package org.strassburger.lifestealz.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManagePlayerdata
import org.strassburger.lifestealz.util.Replaceable

class HeartCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val worldWhitelist = Lifestealz.instance.config.getList("worlds")
        if (sender is Player && worldWhitelist != null && !worldWhitelist.contains(sender.location.world.name)) {
            sender.sendMessage(Lifestealz.getAndFormatMsg(false, "messages.worldNotWhitelisted", "&cThis world is not whitelisted for LifeStealZ!"))
            return false
        }

        val targetName = args?.getOrNull(0)

        if (targetName == null) {
            if (sender !is Player) {
                sender.sendMessage(Lifestealz.getAndFormatMsg(false, "messages.specifyPlayerOrBePlayer", "&cYou need to either specify a player or be a player yourself!"))
                return false
            }
            val playerdata = ManagePlayerdata().getPlayerData(name = sender.name, uuid = sender.uniqueId.toString())
            sender.sendMessage(Lifestealz.getAndFormatMsg(true, "messages.viewheartsYou", "&7You have &c%amount% &7hearts!", Replaceable("%amount%", Math.floor(playerdata.maxhp / 2).toInt().toString())))
            return false
        }

        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.sendMessage(Lifestealz.getAndFormatMsg(false, "messages.playerNotFound", "&cPlayer not found!"))
            return false
        }

        val playerdata = ManagePlayerdata().getPlayerData(name = target.name, uuid = target.uniqueId.toString())
        sender.sendMessage(Lifestealz.getAndFormatMsg(true, "messages.viewheartsOther", "&c%player% &7currently has &c%amount% &7hearts!", Replaceable("%amount%", Math.floor(playerdata.maxhp / 2).toInt().toString()), Replaceable("%player%", target.name)))
        return false
    }
}