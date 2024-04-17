package org.strassburger.lifestealz.util

import net.kyori.adventure.text.Component
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

        val itemMeta: ItemMeta = itemStack.itemMeta

        // Set the sword identifier using NBT tags
        val predefinedIdentifier = "heart"
        itemMeta.persistentDataContainer.set(Lifestealz.HEART_KEY, PersistentDataType.STRING, predefinedIdentifier)

        // Set the display name and lore
        val itemDisplayName : Component = Lifestealz.getAndFormatMsg(false, "items.heart.name", "&cHeart")
        itemMeta.displayName(itemDisplayName)

        if (Lifestealz.instance.config.getBoolean("items.heart.enchanted")) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
        }

        val heartLorelist = Lifestealz.instance.config.getStringList("items.heart.lore")
        val itemLore = mutableListOf<Component>()
        for (loreItem in heartLorelist) {
            itemLore.add(Lifestealz.formatMsg(loreItem))
        }
        itemMeta.lore(itemLore)

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        itemMeta.setCustomModelData(Lifestealz.instance.config.getInt("items.heart.customModelData"))

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
        val itemDisplayName = Lifestealz.getAndFormatMsg(false, "items.revive.name", "&dRevive Crystal")
        itemMeta?.displayName(itemDisplayName)

        if (Lifestealz.instance.config.getBoolean("items.revive.enchanted")) {
            itemMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemMeta?.addEnchant(Enchantment.DURABILITY, 1, true)
        }

        val heartLorelist = Lifestealz.instance.config.getStringList("items.revive.lore")
        val itemLore = mutableListOf<Component>()
        for (loreItem in heartLorelist) {
            itemLore.add(Lifestealz.formatMsg(loreItem))
        }
        itemMeta?.lore(itemLore)

        itemMeta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        itemMeta?.setCustomModelData(Lifestealz.instance.config.getInt("items.revive.customModelData"))

        itemStack.itemMeta = itemMeta

        return itemStack
    }

    fun createCloseItem(): ItemStack {
        return makeCustomItem(material = Material.BARRIER, amount = 1, name = Lifestealz.getAndFormatMsg(false, "messages.closeBtn", "&cClose"), lore = mutableListOf(), customModelData = 999)
    }

    fun makeCustomItem(material: Material, name: Component, amount: Int, lore: MutableList<String>) : ItemStack {
        val customItem = ItemStack(material, amount)
        val customItemMeta = customItem.itemMeta
        customItemMeta.displayName(name)
        customItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        customItemMeta.lore(lore.map { Lifestealz.formatMsg(it) })
        customItem.itemMeta = customItemMeta

        return customItem
    }

    fun makeCustomItem(material: Material, name: Component, amount: Int, lore: MutableList<String>, customModelData: Int) : ItemStack {
        val customItem = makeCustomItem(material, name, amount, lore)
        val customItemMeta = customItem.itemMeta
        customItemMeta.setCustomModelData(customModelData)
        customItem.itemMeta = customItemMeta

        return customItem
    }
}