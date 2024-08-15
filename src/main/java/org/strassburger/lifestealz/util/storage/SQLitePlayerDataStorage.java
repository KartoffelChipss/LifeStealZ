package org.strassburger.lifestealz.util.storage;

import org.strassburger.lifestealz.LifeStealZ;

import java.sql.*;

public class SQLitePlayerDataStorage extends SQLPlayerDataStorage implements PlayerDataStorage {
    Connection createConnection() {
        try {
            LifeStealZ plugin = LifeStealZ.getInstance();
            String pluginFolderPath = plugin.getDataFolder().getPath();
            return DriverManager.getConnection("jdbc:sqlite:" + pluginFolderPath + "/userData.db");
        } catch (SQLException e) {
            LifeStealZ.getInstance().getLogger().severe("Failed to create connection to SQLite database: " + e.getMessage());
            return null;
        }
    }
}
