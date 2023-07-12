package org.strassburger.lifestealz.listener

import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.strassburger.lifestealz.Lifestealz

class EntityDamageByEntityListener : Listener {
    @EventHandler
    fun entityDamageByEntityFunction(event: EntityDamageByEntityEvent) {
        val damagedEntity = event.entity
        val damagerEntity = event.damager

        val preventCrystalPVP: Boolean = Lifestealz.instance.config.getBoolean("preventCrystalPVP")

        if (damagedEntity is Player && damagerEntity.type == EntityType.ENDER_CRYSTAL && preventCrystalPVP) event.isCancelled = true
    }
}