package org.strassburger.lifestealz.util.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLStorage extends SQLStorage {
    public MySQLStorage(Plugin plugin) {
        super(plugin);
    }

    Connection createConnection() {
        FileConfiguration config = getPlugin().getConfig();

        final String HOST = config.getString("storage.host");
        final String PORT = config.getString("storage.port");
        final String DATABASE = config.getString("storage.database");
        final String USERNAME = config.getString("storage.username");
        final String PASSWORD = config.getString("storage.password");

        try {
            return DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD);
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to create connection to MySQL database: " + e.getMessage());
            return null;
        }
    }
}