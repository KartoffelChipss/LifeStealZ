package org.strassburger.lifestealz.util

import org.bukkit.entity.Player
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.data.PlayerData
import java.sql.Connection
import java.sql.DriverManager


class ManagePlayerdata {

    companion object {
        fun initializeDatabase() {
            val plugin = Lifestealz.instance;
            val pluginFolderPath = plugin.dataFolder.path
            val connection = DriverManager.getConnection("jdbc:sqlite:$pluginFolderPath/userData.db")
            val statement = connection.createStatement()
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS hearts (uuid TEXT PRIMARY KEY, name TEXT, maxhp REAL, hasbeenRevived INTEGER, craftedHearts INTEGER, craftedRevives INTEGER, killedOtherPlayers INTEGER)")
        }
    }

    private fun createConnection(): Connection? {
        val pluginFolderPath = Lifestealz.instance.dataFolder.path
        return try {
            DriverManager.getConnection("jdbc:sqlite:$pluginFolderPath/userData.db");
        } catch (e: Exception) {
            Lifestealz.instance.logger.severe("Failed to create a database connection: " + e.message)
            null
        }
    }

    fun getPlayerData(uuid: String, name: String): PlayerData {
        val playerdata = PlayerData(name = name, uuid = uuid)

        createConnection().use { conn ->
            conn!!.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT * FROM hearts WHERE uuid = '$uuid'")
                if (rs.next()) {
                    playerdata.name = rs.getString("name")
                    playerdata.maxhp = rs.getDouble("maxhp")
                    playerdata.hasbeenRevived = rs.getInt("hasbeenRevived")
                    playerdata.craftedHearts = rs.getInt("craftedHearts")
                    playerdata.craftedRevives = rs.getInt("craftedRevives")
                    playerdata.killedOtherPlayers = rs.getInt("killedOtherPlayers")
                }
            }
        }
        return playerdata
    }


    fun checkForPlayer(uuid: String): Boolean {
        createConnection().use { conn ->
            conn!!.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT * FROM hearts WHERE uuid = '$uuid'")
                return rs.next()
            }
        }
    }

    fun savePlayerData(playerData: PlayerData) {
        createConnection().use { conn ->
            conn!!.createStatement().use { stmt ->
                stmt.executeUpdate("INSERT OR REPLACE INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers) VALUES ('${playerData.uuid}','${playerData.name}', ${playerData.maxhp}, ${playerData.hasbeenRevived}, ${playerData.craftedHearts}, ${playerData.craftedRevives}, ${playerData.killedOtherPlayers})")
            }
        }
    }

    fun manageHearts(player: Player, direction: String, amount: Double): PlayerData {
        val playerdata = getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

        if (direction == "inc" || direction == "add") {
            playerdata.maxhp += amount
        } else if (direction == "set") {
            playerdata.maxhp = amount
        } else {
            playerdata.maxhp -= amount
        }

        savePlayerData(playerData = playerdata)
        return playerdata
    }

    fun manageOfflineHearts(name: String, uuid: String, direction: String, amount: Double): PlayerData {
        val playerdata = getPlayerData(uuid = uuid, name = name)

        if (direction == "inc") {
            playerdata.maxhp += amount
        } else if (direction == "set") {
            playerdata.maxhp = amount
        } else {
            playerdata.maxhp -= amount
        }

        savePlayerData(playerData = playerdata)
        return playerdata
    }

    fun addRevive(name: String, uuid: String): PlayerData {
        val playerdata = getPlayerData(uuid = uuid, name = name)

        playerdata.hasbeenRevived += 1

        savePlayerData(playerData = playerdata)
        return playerdata
    }

    fun addHeartCraft(player: Player): PlayerData {
        val playerdata = getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

        playerdata.craftedHearts += 1

        savePlayerData(playerData = playerdata)
        return playerdata
    }

    fun addReviveCraft(player: Player): PlayerData {
        val playerdata = getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

        playerdata.craftedRevives += 1

        savePlayerData(playerData = playerdata)
        return playerdata
    }

    fun addKill(player: Player): PlayerData {
        val playerdata = getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

        playerdata.killedOtherPlayers += 1

        savePlayerData(playerData = playerdata)
        return playerdata
    }
}