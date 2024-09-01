package org.strassburger.lifestealz.util.storage;

import org.bukkit.plugin.Plugin;
import org.strassburger.lifestealz.LifeStealZ;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteStorage extends SQLStorage {
    public SQLiteStorage(LifeStealZ plugin) {
        super(plugin);
    }

    Connection createConnection() {
        try {
            String pluginFolderPath = getPlugin().getDataFolder().getPath();
            return DriverManager.getConnection("jdbc:sqlite:" + pluginFolderPath + "/userData.db");
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to create connection to SQLite database: " + e.getMessage());
            return null;
        }
    }
}
