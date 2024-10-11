package org.strassburger.lifestealz.util.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.strassburger.lifestealz.LifeStealZ;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLStorage extends SQLStorage {
    public MySQLStorage(LifeStealZ plugin) {
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
            return DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD);
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to create connection to MySQL database: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(PlayerData playerData) {
        String query = "INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), maxhp = VALUES(maxhp), hasbeenRevived = VALUES(hasbeenRevived), " +
                "craftedHearts = VALUES(craftedHearts), craftedRevives = VALUES(craftedRevives), killedOtherPlayers = VALUES(killedOtherPlayers)";

        try (Connection connection = createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerData.getUuid());
            preparedStatement.setString(2, playerData.getName());
            preparedStatement.setDouble(3, playerData.getMaxHealth());
            preparedStatement.setInt(4, playerData.getHasbeenRevived());
            preparedStatement.setInt(5, playerData.getCraftedHearts());
            preparedStatement.setInt(6, playerData.getCraftedRevives());
            preparedStatement.setInt(7, playerData.getKilledOtherPlayers());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to save player data to SQL database: " + e.getMessage());
        }
    }
}