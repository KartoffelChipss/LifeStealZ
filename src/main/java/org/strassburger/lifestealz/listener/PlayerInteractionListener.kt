package org.strassburger.lifestealz.listener

import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManagePlayerdata
import java.io.File
import java.util.*

class PlayerInteractionListener : Listener {
    @EventHandler
    fun playerInteractionListener(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action.isRightClick) { // Check if it's a right-click event
            val item = event.item

            if (item != null) {
                if (isHeart(item)) {
                    val playerdata = ManagePlayerdata().getPlayerData(name = player.name, uuid = player.uniqueId.toString())

                    val configLimit = Lifestealz.instance.config.getInt("maxHearts")
                    if (playerdata.maxhp >= (configLimit * 2).toDouble()) {
                        player.sendMessage(Component.text(Lifestealz.formatMsg(false, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!").replace("%limit%", configLimit.toString())))
                        return
                    }

                    val itemStack = item.clone() // Create a copy of the item
                    itemStack.amount = itemStack.amount - 1

                    if (player.inventory.itemInMainHand == item) {
                        player.inventory.setItemInMainHand(itemStack)
                    } else if (player.inventory.itemInOffHand == item) {
                        player.inventory.setItemInOffHand(itemStack)
                    }

                    ManagePlayerdata().manageHearts(player = player, amount = 2.0, direction = "inc")
                    player.maxHealth += 2.0
                    player.health += 2.0
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500.0f, 1.0f)

                    if (Lifestealz.instance.config.getBoolean("playTotemEffect")) player.playEffect(EntityEffect.TOTEM_RESURRECT)
                }

                if (isReviveCrystal(item)) {
                    val dir = File("./plugins/lifestealz/userData")
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }

                    val uuidList = mutableListOf<String>()

                    for (file in dir.walkTopDown()) {
                        val filename = file.name.substringBefore(".")
                        if (!uuidList.contains(filename) && filename != "userData") uuidList.add(filename)
                    }

                    val inventory: Inventory =  Bukkit.createInventory(null, 6 * 9, Lifestealz.formatMsg(false, "messages.reviveTitle", "&8Revive a player"))

                    addNavbar(inventory)

                    for (uuidString in uuidList) {
                        val uuid = convertStringToUUID(uuidString) ?: break

                        val offlinePlayer = Bukkit.getOfflinePlayer(uuid)

                        if (offlinePlayer.name == null) break

                        val offlinePlayerData = ManagePlayerdata().getPlayerData(name = offlinePlayer.name!!, uuid = offlinePlayer.uniqueId.toString())

                        if (offlinePlayerData.maxhp > 0.0) break

                        inventory.addItem(getPlayerHead(offlinePlayer))
                    }

                    player.openInventory(inventory)
                    Lifestealz.reviveGuiMap[player.uniqueId] = inventory
                }
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

    private fun addNavbar(inventory: Inventory) {
        inventory.setItem(49, makeCustomItem(material = Material.BARRIER, amount = 1, name = Lifestealz.formatMsg(false, "messages.closeBtn", "&cClose"), lore = mutableListOf()))

        val glass = ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1)
        val glassMeta = glass.itemMeta
        glassMeta.displayName(Component.text("§r "))
        glass.itemMeta = glassMeta
        val glassSlots = listOf(45,46,47,48,50,51,52,53)
        for (slot in glassSlots) {
            inventory.setItem(slot, glass)
        }
    }

    private fun makeCustomItem(material: Material, name: String, amount: Int, lore: MutableList<String>) : ItemStack {
        val customItem = ItemStack(material, amount)
        val customItemMeta = customItem.itemMeta
        customItemMeta.displayName(Component.text(name))
        customItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        customItemMeta.lore = lore
        customItem.itemMeta = customItemMeta

        return customItem
    }

    private fun convertStringToUUID(uuidString: String): UUID? {
        return try {
            UUID.fromString(uuidString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun getPlayerHead(offlinePlayer: OfflinePlayer) : ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val skullMeta = head.itemMeta as SkullMeta

        skullMeta.displayName(Component.text("§d" + offlinePlayer.name))
        skullMeta.lore(mutableListOf(Component.text(Lifestealz.formatMsg(false, "messages.revivePlayerDesc", "&7Click to revive this player")), Component.text("§8" + offlinePlayer.uniqueId.toString())))
        skullMeta.owningPlayer = offlinePlayer
        head.itemMeta = skullMeta
        return head
    }
}