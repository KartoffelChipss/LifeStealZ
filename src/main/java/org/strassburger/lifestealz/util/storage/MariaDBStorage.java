package org.strassburger.lifestealz.util.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.storage.connectionPool.ConnectionPool;
import org.strassburger.lifestealz.util.storage.connectionPool.MariaDBConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Storage class for MariaDB.
 */
public final class MariaDBStorage extends MySQLSyntaxStorage {
    private final MariaDBConnectionPool connectionPool;

    public MariaDBStorage(LifeStealZ plugin) {
        super(plugin);

        FileConfiguration config = getPlugin().getConfigManager().getStorageConfig();

        final String HOST = config.getString("host");
        final String PORT = config.getString("port");
        final String DATABASE = config.getString("database");
        final String USERNAME = config.getString("username");
        final String PASSWORD = config.getString("password");

        connectionPool = new MariaDBConnectionPool(HOST, PORT, DATABASE, USERNAME, PASSWORD);
    }

    @Override
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }
}
