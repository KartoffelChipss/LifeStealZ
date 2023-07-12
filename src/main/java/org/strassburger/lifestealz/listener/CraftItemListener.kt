package org.strassburger.lifestealz.listener

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManagePlayerdata

class CraftItemListener(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun craftItemFunction(event: CraftItemEvent) {
        if (event.whoClicked !is Player) return
        val player = event.whoClicked as Player

        val heartRecipeKey = NamespacedKey(plugin, "heartrecipe")
        val reviveRecipeKey = NamespacedKey(plugin, "reviverecipe")

        if (isHeart(event.recipe.result)) {
            if (!Lifestealz.instance.config.getBoolean("allowHeartCrafting")) {
                event.isCancelled = true
            } else {
                ManagePlayerdata().addHeartCraft(player)
            }
        }

        if (isReviveCrystal(event.recipe.result)) {
            if (!Lifestealz.instance.config.getBoolean("allowReviveCrafting")) {
                event.isCancelled = true
            } else {
                ManagePlayerdata().addReviveCraft(player)
            }
        }
    }

    private fun isHeart(item: ItemStack): Boolean {
        val itemMeta = item.itemMeta

        if (itemMeta != null && itemMeta.persistentDataContainer.has(Lifestealz.HEART_KEY, PersistentDataType.STRING)) {
            val swordIdentifier = itemMeta.persistentDataContainer.get(Lifestealz.HEART_KEY, PersistentDataType.STRING)

            // Compare the sword identifier with your predefined value
            val predefinedIdentifier = "heart"
            return swordIdentifier == predefinedIdentifier
        }

        return false
    }

    private fun isReviveCrystal(item: ItemStack): Boolean {
        val itemMeta = item.itemMeta

        if (itemMeta != null && itemMeta.persistentDataContainer.has(Lifestealz.REVIVEITEM_KEY, PersistentDataType.STRING)) {
            val swordIdentifier = itemMeta.persistentDataContainer.get(Lifestealz.REVIVEITEM_KEY, PersistentDataType.STRING)

            // Compare the sword identifier with your predefined value
            val predefinedIdentifier = "reviveitem"
            return swordIdentifier == predefinedIdentifier
        }

        return false
    }
}