package org.strassburger.lifestealz.util.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.strassburger.lifestealz.LifeStealZ;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Storage class for MariaDB.
 */
public final class MariaDBStorage extends MySQLSyntaxStorage {
    public MariaDBStorage(LifeStealZ plugin) {
        super(plugin);
    }

    Connection createConnection() {
        FileConfiguration config = getPlugin().getConfigManager().getStorageConfig();

        final String HOST = config.getString("host");
        final String PORT = config.getString("port");
        final String DATABASE = config.getString("database");
        final String USERNAME = config.getString("username");
        final String PASSWORD = config.getString("password");

        try {
            return DriverManager.getConnection("jdbc:mariadb://" + HOST + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD);
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to create connection to MariaDB database: " + e.getMessage());
            return null;
        }
    }
}
