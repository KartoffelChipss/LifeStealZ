package org.strassburger.lifestealz.util

import com.google.gson.GsonBuilder
import org.bukkit.entity.Player
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.data.PlayerData
import java.io.File
import java.io.IOException

class ManagePlayerdata {
    fun getPlayerData(uuid: String, name: String) : PlayerData {
        val playerdata = PlayerData(name = name, uuid = uuid)

        val file: File
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(playerdata)

        val dir = File(Lifestealz.instance.dataFolder, "userData")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        file = File(dir, "${uuid}.json")
        if (!file.exists()) {
            try {
                file.createNewFile()
                file.writeText(json)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val newJson = file.readText()
        return gson.fromJson(newJson, PlayerData::class.java)
    }

    fun checkForPlayer(uuid: String) : Boolean {
        val dir = File(Lifestealz.instance.dataFolder, "userData")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file: File = File(dir, "${uuid}.json")

        return file.exists()
    }

    fun savePlayerData(playerData: PlayerData) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(playerData)
        val userDataFolder = File(Lifestealz.instance.dataFolder, "userData")
        File(userDataFolder, "${playerData.uuid}.json").writeText(json)
    }

    fun manageHearts(player: Player, direction: String, amount: Double) : PlayerData {
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

    fun manageOfflineHearts(name: String, uuid: String, direction: String, amount: Double) : PlayerData {
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

    fun addRevive(name: String, uuid: String) : PlayerData {
        val playerdata = getPlayerData(uuid = uuid, name = name)

        playerdata.hasbeenRevived += 1

        savePlayerData(playerData = playerdata)
        return playerdata
    }

    fun addHeartCraft(player: Player) : PlayerData {
        val playerdata = getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

        playerdata.craftedHearts += 1

        savePlayerData(playerData = playerdata)
        return playerdata
    }

    fun addReviveCraft(player: Player) : PlayerData {
        val playerdata = getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

        playerdata.craftedRevives += 1

        savePlayerData(playerData = playerdata)
        return playerdata
    }

    fun addKill(player: Player) : PlayerData {
        val playerdata = getPlayerData(uuid = player.uniqueId.toString(), name = player.name)

        playerdata.killedOtherPlayers += 1

        savePlayerData(playerData = playerdata)
        return playerdata
    }
}