package org.strassburger.lifestealz.listener

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.Lifestealz.Companion.getAndFormatMsg
import org.strassburger.lifestealz.util.ManagePlayerdata
import org.strassburger.lifestealz.util.Replaceable
import java.util.*

class InventoryClickListener : Listener {
    @EventHandler
    fun inventoryClickFunction(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (event.inventory == Lifestealz.recipeGuiMap[player.uniqueId]) {
            if (event.currentItem == null) return

            val item = event.currentItem

            when(event.currentItem!!.type) {
                Material.BARRIER -> {
                    if (item!!.itemMeta.hasCustomModelData() && item.itemMeta.customModelData == 999) event.inventory.close()
                }

                else -> {}
            }

            event.isCancelled = true
        }

        if (event.inventory == Lifestealz.reviveGuiMap[player.uniqueId]) {
            if (event.currentItem == null) return

            val item = event.currentItem

            when(event.currentItem!!.type) {
                Material.BARRIER -> {
                    if (item!!.itemMeta.hasCustomModelData() && item.itemMeta.customModelData == 999) event.inventory.close()
                }

                Material.PLAYER_HEAD -> {
                    if (!player.hasPermission("lifestealz.revive")) {
                        throwPermissionError(player)
                        return
                    }

                    val uuidString = item!!.lore!![1].replace("§8", "")
                    val targetUUID = convertStringToUUID(uuidString)

                    if (targetUUID == null) {
                        player.sendMessage(uuidString)
                        player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"))
                        event.isCancelled = true
                        return
                    }

                    val targetPlayer = Bukkit.getOfflinePlayer(targetUUID)

                    if (targetPlayer.name == null) {
                        player.sendMessage(Component.text("§cAn error occurred while fetching playerdata! Are you sure this is a real player?"))
                        event.isCancelled = true
                        return
                    }

                    val targetPlayerData = ManagePlayerdata().getPlayerData(name = targetPlayer.name!!, uuid = targetPlayer.uniqueId.toString())

                    val reviveMaximum = Lifestealz.instance.config.getInt("maxRevives")
                    if (reviveMaximum != -1 && targetPlayerData.hasbeenRevived >= reviveMaximum) {
                        player.sendMessage(getAndFormatMsg(false, "messages.reviveMaxReached", "&cThis player has already been revived %amount% times!", Replaceable("%amount%", targetPlayerData.hasbeenRevived.toString())))
                        event.isCancelled = true
                        return
                    }

                    if (targetPlayerData.maxhp > 0.0) {
                        player.sendMessage(getAndFormatMsg(false, "messages.onlyReviveElimPlayers","&cYou can only revive eliminated players!"))
                        event.isCancelled = true
                        return
                    }

                    ManagePlayerdata().manageOfflineHearts(name = targetPlayer.name!!, uuid = targetPlayer.uniqueId.toString(), amount = 2.0, direction = "set")
                    ManagePlayerdata().addRevive(name = targetPlayer.name!!, uuid = targetPlayer.uniqueId.toString())

                    event.inventory.close()

                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f)

                    player.sendMessage(getAndFormatMsg(true, "messages.reviveSuccess", "&7You successfully revived &c%player%&7!", Replaceable("%player%", targetPlayer.name!!)))

                    val mainHandItem = player.inventory.itemInMainHand
                    val offHandItem = player.inventory.itemInOffHand

                    if (isReviveCrystal(mainHandItem)) {
                        val itemStack = mainHandItem.clone()
                        itemStack.amount -= 1
                        player.inventory.setItemInMainHand(itemStack)
                    } else if (isReviveCrystal(offHandItem)) {
                        val itemStack = offHandItem.clone()
                        itemStack.amount -= 1
                        player.inventory.setItemInOffHand(itemStack)
                    }
                }

                else -> {}
            }

            event.isCancelled = true
        }
    }

    private fun throwPermissionError(sender: CommandSender) {
        val usageMessage = getAndFormatMsg(false, "messages.noPermissionError", "&cYou don't have permission to use this!")
        sender.sendMessage(usageMessage)
    }

    private fun convertStringToUUID(uuidString: String): UUID? {
        return try {
            UUID.fromString(uuidString)
        } catch (e: IllegalArgumentException) {
            null
        }
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