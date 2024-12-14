package org.strassburger.lifestealz.util.storage;

import org.strassburger.lifestealz.LifeStealZ;

import java.sql.*;

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

    @Override
    public void save(PlayerData playerData) {
        String sql = "INSERT OR REPLACE INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, playerData.getUuid());
                pstmt.setString(2, playerData.getName());
                pstmt.setDouble(3, playerData.getMaxHealth());
                pstmt.setInt(4, playerData.getHasbeenRevived());
                pstmt.setInt(5, playerData.getCraftedHearts());
                pstmt.setInt(6, playerData.getCraftedRevives());
                pstmt.setInt(7, playerData.getKilledOtherPlayers());
                pstmt.setLong(8, playerData.getFirstJoin());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to save player data to SQL database: " + e.getMessage());
        }
    }
}
