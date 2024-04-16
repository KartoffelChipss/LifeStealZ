package org.strassburger.lifestealz.util

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MyTabCompleter : TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {

        // Fuck this is messy. sorry
        if (command.name.equals("lifestealz", ignoreCase = true)) {
            if (args.size == 1) {
                val availableoptions = mutableListOf<String>()
                if (sender.hasPermission("lifestealz.admin.reload")) availableoptions.add("reload")
                if (sender.hasPermission("lifestealz.admin.setlife")) availableoptions.add("hearts")
                if (sender.hasPermission("lifestealz.admin.giveitem")) availableoptions.add("giveItem")
                if (sender.hasPermission("lifestealz.viewrecipes")) availableoptions.add("recipe")
                if (sender.hasPermission("lifestealz.help")) availableoptions.add("help")
                return availableoptions
            } else if (args.size == 2) {
                if (args[0] == "hearts") {
                    return mutableListOf("add", "set", "remove", "get")
                } else if (args[0] == "giveItem") {
                    return null
                } else if (args[0] == "recipe") {
                    return mutableListOf("heart", "revivecrystal")
                }
            } else if (args.size == 3) {
                if (args[0] == "hearts") {
                    return null
                } else if (args[0] == "giveItem") {
                    return mutableListOf("heart", "revivecrystal")
                }
            } else if (args.size == 4) {
                if (args[0] == "hearts" || args[0] == "giveItem") {
                    return mutableListOf("1", "32", "64")
                }
            }
        }

        if (command.name.equals("eliminate", ignoreCase = true) && args.size == 1) return null
        if (command.name.equals("revive", ignoreCase = true)) {
            return if (args.size == 2) {
                mutableListOf("bypass")
            } else {
                null
            }
        }

        if (command.name.equals("hearts", ignoreCase = true) && args.size == 1) return null

        return mutableListOf<String>()
    }
}