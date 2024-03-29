package org.strassburger.lifestealz.commands

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.ManageCustomItems
import org.strassburger.lifestealz.util.ManagePlayerdata
import org.strassburger.lifestealz.util.Replaceable

class SettingsCommand(private val plugin: JavaPlugin) : CommandExecutor {
    val config = Lifestealz.instance.config
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        val optionOne = args?.getOrNull(0)

        if (args == null || args.isEmpty() || optionOne == null) {
            sender.sendMessage(Lifestealz.getAndFormatMsg(true, "messages.versionMsg", "FALLBACK&7You are using version %version%", Replaceable("%version%", Lifestealz.instance.description.version)))
        }

        if (optionOne == "reload") {
            if (!sender.hasPermission("lifestealz.admin.reload")) {
                throwPermissionError(sender)
                return false
            }

            Lifestealz.instance.reloadConfig()

            Bukkit.resetRecipes()
            registerHeartRecipe()
            registerReviveRecipe()
            sender.sendMessage(Lifestealz.getAndFormatMsg(true, "messages.reloadMsg", "&7Successfully reloaded the plugin!"))
        }

        if (optionOne == "help") {
            if (!sender.hasPermission("lifestealz.help")) {
                throwPermissionError(sender)
                return false
            }

            var helpMessage: String = "&r \n&8----------------------------------------------------\n&c&lLifeStealZ &7help page\n&8----------------------------------------------------"
            helpMessage += "\n&c/lifestealz help &8- &7open this menu"
            if (sender.hasPermission("lifestealz.admin.reload")) helpMessage += "\n&c/lifestealz reload &8- &7reload the config"
            if (sender.hasPermission("lifestealz.admin.setlife")) helpMessage += "\n&c/lifestealz hearts &8- &7modify how many hearts a player has"
            if (sender.hasPermission("lifestealz.admin.giveitem")) helpMessage += "\n&c/lifestealz giveItem &8- &7give other players custom items, such as hearts"
            if (sender.hasPermission("lifestealz.admin.viewrecipes")) helpMessage += "\n&c/lifestealz recipe &8- &7view all recipes"
            if (sender.hasPermission("lifestealz.admin.revive")) helpMessage += "\n&c/revive &8- &7revive a player without a revive item"
            if (sender.hasPermission("lifestealz.admin.eliminate")) helpMessage += "\n&c/eliminate &8- &7eliminate a player"
            if (sender.hasPermission("lifestealz.withdraw")) helpMessage += "\n&c/withdrawheart &8- &7withdraw a heart"
            helpMessage += "\n&8----------------------------------------------------\n&r "

            val helpMessageFormatted = Lifestealz.formatMsg(helpMessage)

            sender.sendMessage(helpMessageFormatted)
        }

        if (optionOne == "recipe") {
            if (!sender.hasPermission("lifestealz.viewrecipes")) {
                throwPermissionError(sender)
                return false
            }

            if (sender !is Player) return false

            val availableRecipes = listOf("heart", "revivecrystal")

            val recipe = args.getOrNull(1)

            if (recipe == null || !availableRecipes.contains(recipe)) {
                throwRecipeUsageError(sender)
                return false
            }

            renderRecipe(player = sender, recipe = recipe)
        }

        if (optionOne == "hearts") {
            if (!sender.hasPermission("lifestealz.admin.setlife")) {
                throwPermissionError(sender)
                return false
            }

            val optionTwo = args.getOrNull(1)
            val possibleOptionTwo = listOf("add", "set", "remove", "get")

            if (optionTwo == null || !possibleOptionTwo.contains(optionTwo)) {
                throwUsageError(sender)
                return false
            }

            val targetPlayerName = args.getOrNull(2)

            if (targetPlayerName == null) {
                throwUsageError(sender)
                return false
            }

            val targetPlayer = Bukkit.getPlayer(targetPlayerName)

            if (targetPlayer == null) {
                throwUsageError(sender)
                return false
            }

            val targetplayerData = ManagePlayerdata().getPlayerData(uuid=targetPlayer.uniqueId.toString(), name=targetPlayer.name)

            if (optionTwo == "get") {
                val heartsString = (targetplayerData.maxhp / 2).toInt().toString()
                sender.sendMessage(Lifestealz.getAndFormatMsg(true, "messages.getHearts", "&c%player% &7currently has &c%amount% &7hearts!", Replaceable("%player%", targetPlayer.name), Replaceable("%amount%", heartsString)))
                return false
            }

            val amount = args.getOrNull(3)?.toIntOrNull()

            if (amount == null || amount < 0) {
                throwUsageError(sender)
                return false
            }

            if (optionTwo == "remove") {
                if ((targetplayerData.maxhp / 2) - amount.toDouble() <= 0) {
                    sender.sendMessage(Component.text("§cYou cannot set the lives below or to zero"))
                    return false
                }
            } else if (optionTwo == "set") {
                if (amount <= 0) {
                    sender.sendMessage(Component.text("§cYou cannot set the lives below or to zero"))
                    return false
                }
            }

            ManagePlayerdata().manageHearts(targetPlayer, optionTwo, amount.toDouble() * 2)

            when (optionTwo) {
                "add" -> {
                    if (config.getBoolean("enforceMaxHeartsOnAdminCommands") && targetPlayer.maxHealth + amount.toDouble() * 2 > config.getInt("maxHearts") * 2) {
                        val maxHeartsMsg = Lifestealz.getAndFormatMsg(true, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", Replaceable("%limit%", config.getInt("maxHearts").toString()))
                        sender.sendMessage(maxHeartsMsg)
                        return false
                    }

                    targetPlayer.maxHealth += amount.toDouble() * 2
                    targetPlayer.health += amount.toDouble() * 2
                }
                "set" -> {
                    if (config.getBoolean("enforceMaxHeartsOnAdminCommands") && amount > config.getInt("maxHearts")) {
                        val maxHeartsMsg = Lifestealz.getAndFormatMsg(true, "messages.maxHeartLimitReached", "&cYou already reached the limit of %limit% hearts!", Replaceable("%limit%", config.getInt("maxHearts").toString()))
                        sender.sendMessage(maxHeartsMsg)
                        return false
                    }
                    setMaxHealth(targetPlayer, amount.toDouble() * 2)
                }
                else -> {
                    targetPlayer.maxHealth -= amount.toDouble() * 2
                }
            }

            val setHeartsConfirmMessage = Lifestealz.getAndFormatMsg(true, "messages.setHeartsConfirm", "&7You successfully %option% &c%player%' hearts to &7%amount% hearts!", Replaceable("%option%", optionTwo), Replaceable("%player%", targetPlayer.name), Replaceable("%amount%", amount.toString()))
            sender.sendMessage(setHeartsConfirmMessage)

        }

        if (optionOne == "giveItem") {
            if (!sender.hasPermission("lifestealz.admin.giveitem")) {
                throwPermissionError(sender)
                return false
            }

            val targetPlayerName = args.getOrNull(1)

            if (targetPlayerName == null) {
                throwGiveItemUsageError(sender)
                return false
            }

            val targetPlayer = Bukkit.getPlayer(targetPlayerName)

            if (targetPlayer == null) {
                throwGiveItemUsageError(sender)
                return false
            }

            val item = args.getOrNull(2)
            val validItems = listOf("heart", "revivecrystal")

            if (item == null || !validItems.contains(item)) {
                throwGiveItemUsageError(sender)
                return false
            }

            var amount = args.getOrNull(3)?.toIntOrNull()

            if (amount == null) amount = 1

            when(item) {
                "heart" -> {
                    val heartItem = ManageCustomItems().createHeartItem()
                    heartItem.amount = amount
                    targetPlayer.inventory.addItem(heartItem)
                }

                "revivecrystal" -> {
                    val revivetItem = ManageCustomItems().createReviveItem()
                    revivetItem.amount = amount
                    targetPlayer.inventory.addItem(revivetItem)
                }

                else -> {}
            }

        }

        return true
    }

    private fun throwUsageError(sender: CommandSender) {
        val msg = Lifestealz.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", Replaceable("%usage%", "/lifestealz hearts <add | set | remove> <player> [amount]"))
        sender.sendMessage(msg)
    }

    private fun throwGiveItemUsageError(sender: CommandSender) {
        val msg = Lifestealz.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", Replaceable("%usage%", "/lifestealz giveItem <player> <item> [amount]"))
        sender.sendMessage(msg)
    }

    private fun throwRecipeUsageError(sender: CommandSender) {
        val msg = Lifestealz.getAndFormatMsg(false, "messages.usageError", "&cUsage: %usage%", Replaceable("%usage%", "/lifestealz recipe <heart | revivecrystal>"))
        sender.sendMessage(msg)
    }

    private fun throwPermissionError(sender: CommandSender) {
        val msg = Lifestealz.getAndFormatMsg(false, "messages.noPermissionError", "&cYou don't have permission to use this!")
        sender.sendMessage(msg)
    }

    fun setMaxHealth(player: Player, maxHealth: Double) {
        val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        if (attribute != null) {
            attribute.baseValue = maxHealth
            player.health = maxHealth
        }
    }

    fun registerHeartRecipe() {
        if (!Lifestealz.instance.config.getBoolean("allowHeartCrafting")) return

        //Heart recipe
        val heartRecipeKey = NamespacedKey(plugin, "heartrecipe")
        val heartItem = ManageCustomItems().createHeartItem()
        val heartRecipe = ShapedRecipe(heartRecipeKey, heartItem)

        heartRecipe.shape("ABC", "DEF", "GHI")
        val rowOne = Lifestealz.instance.config.getStringList("items.heart.recipe.rowOne")
        val rowTwo = Lifestealz.instance.config.getStringList("items.heart.recipe.rowTwo")
        val rowThree = Lifestealz.instance.config.getStringList("items.heart.recipe.rowThree")
        heartRecipe.setIngredient('A', Material.valueOf(rowOne[0]))
        heartRecipe.setIngredient('B', Material.valueOf(rowOne[1]))
        heartRecipe.setIngredient('C', Material.valueOf(rowOne[2]))
        heartRecipe.setIngredient('D', Material.valueOf(rowTwo[0]))
        heartRecipe.setIngredient('E', Material.valueOf(rowTwo[1]))
        heartRecipe.setIngredient('F', Material.valueOf(rowTwo[2]))
        heartRecipe.setIngredient('G', Material.valueOf(rowThree[0]))
        heartRecipe.setIngredient('H', Material.valueOf(rowThree[1]))
        heartRecipe.setIngredient('I', Material.valueOf(rowThree[2]))

        Bukkit.addRecipe(heartRecipe)
    }

    fun registerReviveRecipe() {
        if (!Lifestealz.instance.config.getBoolean("allowReviveCrafting")) return

        //Revive crystal recipe
        val reviveRecipeKey = NamespacedKey(plugin, "reviverecipe")
        val reviveItem = ManageCustomItems().createReviveItem()
        val reviveRecipe = ShapedRecipe(reviveRecipeKey, reviveItem)

        reviveRecipe.shape("ABC", "DEF", "GHI")
        val reviverowOne = Lifestealz.instance.config.getStringList("items.revive.recipe.rowOne")
        val reviverowTwo = Lifestealz.instance.config.getStringList("items.revive.recipe.rowTwo")
        val reviverowThree = Lifestealz.instance.config.getStringList("items.revive.recipe.rowThree")
        reviveRecipe.setIngredient('A', Material.valueOf(reviverowOne[0]))
        reviveRecipe.setIngredient('B', Material.valueOf(reviverowOne[1]))
        reviveRecipe.setIngredient('C', Material.valueOf(reviverowOne[2]))
        reviveRecipe.setIngredient('D', Material.valueOf(reviverowTwo[0]))
        reviveRecipe.setIngredient('E', Material.valueOf(reviverowTwo[1]))
        reviveRecipe.setIngredient('F', Material.valueOf(reviverowTwo[2]))
        reviveRecipe.setIngredient('G', Material.valueOf(reviverowThree[0]))
        reviveRecipe.setIngredient('H', Material.valueOf(reviverowThree[1]))
        reviveRecipe.setIngredient('I', Material.valueOf(reviverowThree[2]))

        Bukkit.addRecipe(reviveRecipe)
    }

    private fun renderRecipe(player: Player, recipe: String) {
        val inventory: Inventory =  Bukkit.createInventory(null, 5 * 9, "§8Crafting recipe")

        inventory.setItem(40, ManageCustomItems().createCloseItem())

        val glass = ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1)
        val glassMeta = glass.itemMeta
        glassMeta.displayName(Component.text("§r "))
        glass.itemMeta = glassMeta
        val glassSlots = listOf(0,1,2,3,4,5,6,7,8,9,13,14,15,16,17,18,22,23,25,26,27,31,32,33,34,35,36,37,38,39,41,42,43,44)
        for (slot in glassSlots) {
            inventory.setItem(slot, glass)
        }

        when(recipe) {
            "heart" -> {
                val rowOne = Lifestealz.instance.config.getStringList("items.heart.recipe.rowOne")
                val rowTwo = Lifestealz.instance.config.getStringList("items.heart.recipe.rowTwo")
                val rowThree = Lifestealz.instance.config.getStringList("items.heart.recipe.rowThree")
                inventory.setItem(10, ItemStack(Material.valueOf(rowOne[0]), 1))
                inventory.setItem(11, ItemStack(Material.valueOf(rowOne[1]), 1))
                inventory.setItem(12, ItemStack(Material.valueOf(rowOne[2]), 1))
                inventory.setItem(19, ItemStack(Material.valueOf(rowTwo[0]), 1))
                inventory.setItem(20, ItemStack(Material.valueOf(rowTwo[1]), 1))
                inventory.setItem(21, ItemStack(Material.valueOf(rowTwo[2]), 1))
                inventory.setItem(28, ItemStack(Material.valueOf(rowThree[0]), 1))
                inventory.setItem(29, ItemStack(Material.valueOf(rowThree[1]), 1))
                inventory.setItem(30, ItemStack(Material.valueOf(rowThree[2]), 1))

                inventory.setItem(24, ManageCustomItems().createHeartItem())
            }

            "revivecrystal" -> {
                val rowOne = Lifestealz.instance.config.getStringList("items.revive.recipe.rowOne")
                val rowTwo = Lifestealz.instance.config.getStringList("items.revive.recipe.rowTwo")
                val rowThree = Lifestealz.instance.config.getStringList("items.revive.recipe.rowThree")
                inventory.setItem(10, ItemStack(Material.valueOf(rowOne[0]), 1))
                inventory.setItem(11, ItemStack(Material.valueOf(rowOne[1]), 1))
                inventory.setItem(12, ItemStack(Material.valueOf(rowOne[2]), 1))
                inventory.setItem(19, ItemStack(Material.valueOf(rowTwo[0]), 1))
                inventory.setItem(20, ItemStack(Material.valueOf(rowTwo[1]), 1))
                inventory.setItem(21, ItemStack(Material.valueOf(rowTwo[2]), 1))
                inventory.setItem(28, ItemStack(Material.valueOf(rowThree[0]), 1))
                inventory.setItem(29, ItemStack(Material.valueOf(rowThree[1]), 1))
                inventory.setItem(30, ItemStack(Material.valueOf(rowThree[2]), 1))

                inventory.setItem(24, ManageCustomItems().createReviveItem())
            }
        }

        player.openInventory(inventory)
        Lifestealz.recipeGuiMap[player.uniqueId] = inventory
    }
}