package org.strassburger.lifestealz.listener

import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManagePlayerdata

class WorldSwitchListener : Listener {
    @EventHandler
    fun worldSwitchFunction(event: PlayerChangedWorldEvent) {
        val player = event.player

        val worldWhitelist = Lifestealz.instance.config.getList("worlds")

        if (worldWhitelist == null) return

        if (worldWhitelist.contains(player.location.world.name)) {
            val playerData = ManagePlayerdata().getPlayerData(name = player.name, uuid = player.uniqueId.toString())
            setMaxHealth(player, playerData.maxhp)

            if (!worldWhitelist.contains(event.from.name)) player.health = playerData.maxhp
        } else {
            setMaxHealth(player, 20.0)
        }
    }

    private fun setMaxHealth(player: Player, maxHealth: Double) {
        val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        if (attribute != null) {
            attribute.baseValue = maxHealth
        }
    }
}