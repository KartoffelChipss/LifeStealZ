package org.strassburger.lifestealz.util

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.strassburger.lifestealz.Lifestealz

class LifestealZpapi : PlaceholderExpansion() {
    override fun getAuthor(): String {
        return "kartoffelchips"
    }

    override fun getIdentifier(): String {
        return "lifestealz"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun persist(): Boolean {
        return true // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    // Define all placeholders
    override fun onRequest(player: OfflinePlayer?, params: String): String? {

        if (params.equals("name", ignoreCase = true)) { // Test placeholder
            return player?.name
        }

        if (params.equals("hearts", ignoreCase = true)) {
            if (player == null) return ""
            if (player.name == null) return ""
            val playerData = ManagePlayerdata().getPlayerData(name= player.name!!, uuid = player.uniqueId.toString())
            return (playerData.maxhp / 2).toInt().toString()
        }

        if (params.equals("revived", ignoreCase = true)) {
            if (player == null) return ""
            if (player.name == null) return ""
            val playerData = ManagePlayerdata().getPlayerData(name= player.name!!, uuid = player.uniqueId.toString())
            return playerData.hasbeenRevived.toString()
        }

        if (params.equals("maxhearts", ignoreCase = true)) {
            val maxhearts = Lifestealz.instance.config.getInt("maxHearts")
            return maxhearts.toString()
        }

        if (params.equals("craftedhearts", ignoreCase = true)) {
            if (player == null) return ""
            if (player.name == null) return ""
            val playerData = ManagePlayerdata().getPlayerData(name= player.name!!, uuid = player.uniqueId.toString())
            return playerData.craftedHearts.toString()
        }

        if (params.equals("craftedrevives", ignoreCase = true)) {
            if (player == null) return ""
            if (player.name == null) return ""
            val playerData = ManagePlayerdata().getPlayerData(name= player.name!!, uuid = player.uniqueId.toString())
            return playerData.craftedRevives.toString()
        }

        return null // If the placeholder is unknown by the Expansion
    }
}