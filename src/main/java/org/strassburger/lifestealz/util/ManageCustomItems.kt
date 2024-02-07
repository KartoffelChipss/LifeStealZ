package org.strassburger.lifestealz.util

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.strassburger.lifestealz.Lifestealz

class ManageCustomItems {
    fun createHeartItem(): ItemStack {
        val material = Material.valueOf(Lifestealz.instance.config.getString("items.heart.material") ?: "NETHER_STAR")
        val itemStack = ItemStack(material, 1)

        val itemMeta: ItemMeta? = itemStack.itemMeta

        // Set the sword identifier using NBT tags
        val predefinedIdentifier = "heart"
        itemMeta?.persistentDataContainer?.set(Lifestealz.HEART_KEY, PersistentDataType.STRING, predefinedIdentifier)

        // Set the display name and lore
        val itemDisplayName = Lifestealz.formatMsg(false, "items.heart.name", "&cHeart")
        itemMeta?.displayName(Component.text(itemDisplayName))

        if (Lifestealz.instance.config.getBoolean("items.heart.enchanted")) {
            itemMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta?.addEnchant(Enchantment.DURABILITY, 1, true)
        }

        val heartLorelist = Lifestealz.instance.config.getList("items.heart.lore") ?: listOf<String>("&7Rightclick to use")
        val itemLore = mutableListOf<String>()
        for (loreItem in heartLorelist) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', loreItem as String))
        }
        itemMeta?.lore = itemLore

        itemMeta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        itemMeta?.setCustomModelData(Lifestealz.instance.config.getInt("items.heart.customModelData"))

        itemStack.itemMeta = itemMeta

        return itemStack
    }

    fun createReviveItem(): ItemStack {
        val material = Material.valueOf(Lifestealz.instance.config.getString("items.revive.material") ?: "AMETHYST_SHARD")
        val itemStack = ItemStack(material, 1)

        val itemMeta: ItemMeta? = itemStack.itemMeta

        // Set the sword identifier using NBT tags
        val predefinedIdentifier = "reviveitem"
        itemMeta?.persistentDataContainer?.set(Lifestealz.REVIVEITEM_KEY, PersistentDataType.STRING, predefinedIdentifier)

        // Set the display name and lore
        val itemDisplayName = Lifestealz.formatMsg(false, "items.revive.name", "&dRevive Crystal")
        itemMeta?.displayName(Component.text(itemDisplayName))

        if (Lifestealz.instance.config.getBoolean("items.revive.enchanted")) {
            itemMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta?.addEnchant(Enchantment.DURABILITY, 1, true)
        }

        val heartLorelist = Lifestealz.instance.config.getList("items.revive.lore") ?: listOf<String>("&7Rightclick to use")
        val itemLore = mutableListOf<String>()
        for (loreItem in heartLorelist) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', loreItem as String))
        }
        itemMeta?.lore = itemLore

        itemMeta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        itemMeta?.setCustomModelData(Lifestealz.instance.config.getInt("items.revive.customModelData"))

        itemStack.itemMeta = itemMeta

        return itemStack
    }
}