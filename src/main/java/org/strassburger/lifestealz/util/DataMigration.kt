package org.strassburger.lifestealz.util

import com.google.gson.Gson
import org.strassburger.lifestealz.Lifestealz
import org.strassburger.lifestealz.util.data.PlayerData
import java.io.File
import java.io.FileReader
import java.sql.DriverManager
import java.sql.PreparedStatement

object DataMigration {

    // This object is used to migrate data from the old storage (JSON) to the new storage (SQLite)
    // to avoid data loss when the plugin is updated. Can be removed after the first update.

    fun migrateData() {
        val plugin = Lifestealz.instance

        val userDataFolder = File(plugin.dataFolder, "userData")
        val jsonFiles = userDataFolder.listFiles { dir, name -> name.endsWith(".json") }
        if (jsonFiles == null || jsonFiles.isEmpty()) {
            plugin.logger.info("No JSON files found. Skipping data migration.")
            return
        }

        val dbPath = File(plugin.dataFolder, "userData.db").path
        try {
            DriverManager.getConnection("jdbc:sqlite:$dbPath").use { conn ->
                val sql = "INSERT OR REPLACE INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers) VALUES (?, ?, ?, ?, ?, ?, ?)"
                val pstmt: PreparedStatement = conn.prepareStatement(sql)
                val gson = Gson()

                jsonFiles.forEach { file ->
                    val playerData = gson.fromJson(FileReader(file), PlayerData::class.java)
                    pstmt.setString(1, playerData.uuid)
                    pstmt.setString(2, playerData.name)
                    pstmt.setDouble(3, playerData.maxhp)
                    pstmt.setInt(4, playerData.hasbeenRevived)
                    pstmt.setInt(5, playerData.craftedHearts)
                    pstmt.setInt(6, playerData.craftedRevives)
                    pstmt.setInt(7, playerData.killedOtherPlayers)
                    pstmt.executeUpdate()

                    // Delete the JSON file after successful migration
                    file.delete()
                }
                pstmt.close()

                if (userDataFolder.listFiles().isNullOrEmpty()){
                    // Delete the userData folder after successful migration
                    userDataFolder.delete()
                    plugin.logger.info("Data migration completed.")
                }
            }
        } catch (e: Exception) {
            plugin.logger.severe("Error during data migration: ${e.message}")
        }
    }
}
