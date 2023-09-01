package org.strassburger.lifestealz.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityResurrectEvent
import org.strassburger.lifestealz.Lifestealz

class EntityRessurectListener : Listener {
    @EventHandler
    fun entityResurrectFunction(event: EntityResurrectEvent) {
        val player = event.entity
        val worldWhitelisted = Lifestealz.instance.config.getList("worlds")?.contains(player.location.world.name)
        if (worldWhitelisted == null || !worldWhitelisted) return
        if (Lifestealz.instance.config.getBoolean("preventTotems")) event.isCancelled = true
    }
}