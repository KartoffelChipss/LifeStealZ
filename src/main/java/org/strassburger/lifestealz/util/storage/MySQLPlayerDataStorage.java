package org.strassburger.lifestealz.util.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.strassburger.lifestealz.LifeStealZ;

import java.sql.*;

public class MySQLPlayerDataStorage extends SQLPlayerDataStorage implements PlayerDataStorage {
    Connection createConnection() {
        FileConfiguration config = LifeStealZ.getInstance().getConfig();

        final String HOST = config.getString("storage.host");
        final String PORT = config.getString("storage.port");
        final String DATABASE = config.getString("storage.database");
        final String USERNAME = config.getString("storage.username");
        final String PASSWORD = config.getString("storage.password");

        try {
            return DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LifeStealZ.getInstance().getLogger().severe("Failed to create connection to MySQL database: " + e.getMessage());
            return null;
        }
    }
}