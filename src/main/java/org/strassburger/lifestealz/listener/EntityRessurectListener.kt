package org.strassburger.lifestealz.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityResurrectEvent
import org.strassburger.lifestealz.Lifestealz

class EntityRessurectListener : Listener {
    @EventHandler
    fun entityResurrectFunction(event: EntityResurrectEvent) {
        if (Lifestealz.instance.config.getBoolean("preventTotems")) event.isCancelled = true
    }
}