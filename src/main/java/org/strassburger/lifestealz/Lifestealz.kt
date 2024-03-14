package org.strassburger.lifestealz

import net.kyori.adventure.text.Component
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.strassburger.lifestealz.commands.EliminateCommand
import org.strassburger.lifestealz.commands.ReviveCommand
import org.strassburger.lifestealz.commands.SettingsCommand
import org.strassburger.lifestealz.commands.WithdrawCommand
import org.strassburger.lifestealz.listener.*
import org.strassburger.lifestealz.util.LifestealZpapi
import org.strassburger.lifestealz.util.ManageCustomItems
import org.strassburger.lifestealz.util.MyTabCompleter
import org.strassburger.lifestealz.util.WorldGuardManager
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

class Lifestealz : JavaPlugin() {
    companion object {
        lateinit var instance: Lifestealz

        lateinit var worldGuardManager: WorldGuardManager

        val hasWorldGuard: Boolean = Bukkit.getPluginManager().getPlugin("WorldGuard") != null

        // Maps to check if a player has opened a custom gui
        val recipeGuiMap: MutableMap<UUID, Inventory> = mutableMapOf()
        val reviveGuiMap: MutableMap<UUID, Inventory> = mutableMapOf()

        // Namespace keys for custom items
        // Used to identify items
        val HEART_KEY = NamespacedKey("lifesetalz", "heart")
        val REVIVEITEM_KEY = NamespacedKey("lifesetalz", "reviveitem")

        fun formatMsg(addPrefix: Boolean, path: String, fallback: String): String {
            var msg = instance.config.getString(path) ?: fallback
            msg = ChatColor.translateAlternateColorCodes('&', msg)
            val prefix = ChatColor.translateAlternateColorCodes('&',instance.config.getString("messages.prefix") ?: "§8[§cLifestealZ§8]")
            if (addPrefix) msg = "$prefix $msg"
            return msg
        }
    }

    init {
        instance = this
    }

    override fun onEnable() {
        config.options().copyDefaults(true)
        saveDefaultConfig()

        registerCommands()
        registerEvents()

        initializeConfig()

        registerHeartRecipe()
        registerReviveRecipe()

        // Register placeholders if papi is installed
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            LifestealZpapi().register()
            logger.info("PlaceholderAPI found! Enabled PlaceholderAPI support!")
        }

        // Register bstats
        val pluginId = 18735
        val metrics = Metrics(this, pluginId)

        logger.info("LifestealZ has been loaded!")
    }

    override fun onLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardManager = WorldGuardManager()
            logger.info("WorldGuard found! Enabled WorldGuard support!")
        }
    }

    override fun onDisable() {
        logger.info("LifestealZ has been disabled!")
    }

    private fun registerCommands() {
        // Register all Commands

        val settingsCommand = getCommand("lifestealz")
        settingsCommand!!.setExecutor(SettingsCommand(this))
        settingsCommand.tabCompleter = MyTabCompleter()
        settingsCommand.permissionMessage(
            Component.text(
                formatMsg(
                    false,
                    "messages.noPermissionError",
                    "&7You successfully eliminated &c%player%&7!"
                )
            )
        )

        val eliminateCommand = getCommand("eliminate")
        eliminateCommand!!.setExecutor(EliminateCommand())
        eliminateCommand.tabCompleter = MyTabCompleter()
        eliminateCommand.permissionMessage(Component.text(formatMsg(false,"messages.noPermissionError","&7You successfully eliminated &c%player%&7!")))

        val reviveCommand = getCommand("revive")
        reviveCommand!!.setExecutor(ReviveCommand())
        reviveCommand.tabCompleter = MyTabCompleter()
        reviveCommand.permissionMessage(
            Component.text(
                formatMsg(
                    false,
                    "messages.noPermissionError",
                    "&7You successfully eliminated &c%player%&7!"
                )
            )
        )

        val withdrawCommand = getCommand("withdrawheart")
        withdrawCommand!!.setExecutor(WithdrawCommand())
        withdrawCommand.tabCompleter = MyTabCompleter()
        withdrawCommand.permissionMessage(
            Component.text(
                formatMsg(
                    false,
                    "messages.noPermissionError",
                    "&7You successfully eliminated &c%player%&7!"
                )
            )
        )

        logger.info("Commands have been registered!")
    }

    private fun registerEvents() {
        // Register all events

        server.pluginManager.registerEvents(PlayerLoginListener(this), this)
        server.pluginManager.registerEvents(PlayerJoinListener(this), this)
        server.pluginManager.registerEvents(PlayerDeathListener(), this)
        server.pluginManager.registerEvents(PlayerQuitListener(), this)
        server.pluginManager.registerEvents(PlayerInteractionListener(), this)
        server.pluginManager.registerEvents(EntityDamageByEntityListener(), this)
        server.pluginManager.registerEvents(EntityRessurectListener(), this)
        server.pluginManager.registerEvents(InventoryClickListener(), this)
        server.pluginManager.registerEvents(InventoryCloseListener(), this)
        server.pluginManager.registerEvents(CraftItemListener(this), this)

        logger.info("Events have been registered!")
    }

    private fun registerHeartRecipe() {
        // Register Heart recipe if recipe is enabled
        if (!instance.config.getBoolean("allowHeartCrafting")) return

        //Heart recipe
        val heartRecipeKey = NamespacedKey(this, "heartrecipe")
        val heartItem = ManageCustomItems().createHeartItem()
        val heartRecipe = ShapedRecipe(heartRecipeKey, heartItem)

        heartRecipe.shape("ABC", "DEF", "GHI")
        val rowOne = instance.config.getStringList("items.heart.recipe.rowOne")
        val rowTwo = instance.config.getStringList("items.heart.recipe.rowTwo")
        val rowThree = instance.config.getStringList("items.heart.recipe.rowThree")
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
        // Register reviveitem recipe if recipe is enabled
        if (!instance.config.getBoolean("allowReviveCrafting")) return

        //Revive crystal recipe
        val reviveRecipeKey = NamespacedKey(this, "reviverecipe")
        val reviveItem = ManageCustomItems().createReviveItem()
        val reviveRecipe = ShapedRecipe(reviveRecipeKey, reviveItem)

        reviveRecipe.shape("ABC", "DEF", "GHI")
        val reviverowOne = instance.config.getStringList("items.revive.recipe.rowOne")
        val reviverowTwo = instance.config.getStringList("items.revive.recipe.rowTwo")
        val reviverowThree = instance.config.getStringList("items.revive.recipe.rowThree")
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

    private fun initializeConfig() {
        // Write config file if it is empty

        val f = File(this.dataFolder, "config.yml")

        val configData = """
            #     _      _  __        _____ _             _   ______
            #    | |    (_)/ _|      / ____| |           | | |___  /
            #    | |     _| |_ ___  | (___ | |_ ___  __ _| |    / /
            #    | |    | |  _/ _ \  \___ \| __/ _ \/ _` | |   / /
            #    | |____| | ||  __/  ____) | ||  __/ (_| | |  / /__
            #    |______|_|_| \___| |_____/ \__\___|\__,_|_| /_____|
            
            #A list of worlds, where the plugin should take effect
            worlds:
              - "world"
              - "world_nether"
              - "world_the_end"
            
            #The amount of hearts a player has, when joining for the first time
            startHearts: 10
            #The maximal amount of hearts, a player can have
            maxHearts: 20
            
            #If a heart should be dropped, when the killer already has the max amount of hearts
            dropHeartsIfMax: true
            #If a player should lose a heart, when dying to hostile mobs or falldamage, lava, etc
            looseHeartsToNature: true
            #If a player should lose a heart, when being killed by another player
            looseHeartsToPlayer: true
            #Whether it should be announced, when a player got eliminated (has no more hearts)
            announceElimination: true
            
            #Allows to craft hearts
            allowHeartCrafting: true
            #Allows players to withdraw a heart, even if they only have one left
            allowDyingFromWithdraw: true
            #If the totem effect should be played, when you use a heart
            playTotemEffect: false
            
            #How many times a player can be revived. Set to -1 to make it infinite
            maxRevives: -1
            #Allows to craft revive crystal
            allowReviveCrafting: true
            
            #If the use of totems of undying should be prevented
            preventTotems: false
            #If crystalpvp should be disabled
            preventCrystalPVP: false
            
            #Only disable this option if you want to add custom commands on elimination and don't want the player to get banned
            disablePlayerBanOnElimination: false
            # The amount of hp a player should have after getting eliminated
            respawnHP: 10
            # The command that should be executed when a player gets eliminated
            # You can use %player% to insert the player name
            # For example: tempban %player% banreason 1d
            eliminationCommand: %player% got eliminated
            
            #Here you can modify everything about the custom items
            items:
              heart:
                name: "&cHeart"
                lore:
                  - "&7Rightclick to use"
            #     - "This would be a second line"
            #     - "And this possibly a third line"
                material: "NETHER_STAR"
                enchanted: false
                recipe:
                  #Every item represents one slot in the crafting table
                  #The first item in a row is the left most item in the crafting table
                  #If you want a slot to be blant, use 'AIR'
                  rowOne:
                    - "GOLD_BLOCK"
                    - "GOLD_BLOCK"
                    - "GOLD_BLOCK"
                  rowTwo:
                    - "OBSIDIAN"
                    - "NETHER_STAR"
                    - "OBSIDIAN"
                  rowThree:
                    - "DIAMOND_BLOCK"
                    - "DIAMOND_BLOCK"
                    - "DIAMOND_BLOCK"
            
              revive:
                name: "&dRevive Crystal"
                lore:
                  - "&7Rightclick to use"
                material: "AMETHYST_SHARD"
                enchanted: true
                recipe:
                  rowOne:
                    - "AMETHYST_SHARD"
                    - "NETHERITE_BLOCK"
                    - "AMETHYST_SHARD"
                  rowTwo:
                    - "OBSIDIAN"
                    - "BEACON"
                    - "OBSIDIAN"
                  rowThree:
                    - "AMETHYST_SHARD"
                    - "NETHERITE_BLOCK"
                    - "AMETHYST_SHARD"
            
            #You can modify all messages here
            messages:
              prefix: "&8[&cLifeStealZ&8]"
              usageError: "&cUsage: %usage%"
              noPermissionError: "&cYou don't have permission to use this!"
              noPlayerData: "&cThis player has not played on this server yet!"
              eliminateSuccess: "&7You successfully eliminated &c%player%&7!"
              reviveSuccess: "&7You successfully revived &c%player%!"
              reviveMaxReached: "&cThis player has already been revived %amount% times!"
              onlyReviveElimPlayers: "&cYou can only revive eliminated players!"
              eliminatedJoin: "&cYou don't have any hearts left!"
              eliminationAnnouncement: "&c%player% &7has been eliminated by &c%killer%&7!"
              eliminateionAnnouncementNature: "&c%player% &7has been eliminated!"
              setHeartsConfirm: "&7Successfully set &c%player%&7's hearts to &c%amount%"
              getHearts: "&c%player% &7currently has &c%amount% &7hearts!"
              reloadMsg: "&7Successfully reloaded the plugin!"
              versionMsg: "&7You are using version %version%"
              noWithdraw: "&cYou would be eliminated, if you withdraw a heart!"
              withdrawConfirmmsg: "&8&oUse /withdrawheart confirm if you really want to withdraw a heart"
              maxHeartLimitReached: "&cYou already reached the limit of %limit% hearts!"
              closeBtn: "&cClose"
              reviveTitle: "&8Revive a player"
              revivePlayerDesc: "&7Click to revive this player"
        """.trimIndent()

        if (f.length().toInt() == 0) {
            logger.info("Initialized Config")
            try {
                FileWriter(f).use { writer ->
                    writer.write(configData)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
