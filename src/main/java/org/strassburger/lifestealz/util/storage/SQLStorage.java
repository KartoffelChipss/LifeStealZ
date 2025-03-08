package org.strassburger.lifestealz.util.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.storage.connectionPool.ConnectionPool;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class SQLStorage extends Storage {
    private static final String CSV_SEPARATOR = ",";

    public SQLStorage(LifeStealZ plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        try (Connection connection = getConnection()) {
            if (connection == null) return;
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS hearts (uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(255), maxhp REAL, hasbeenRevived INTEGER, craftedHearts INTEGER, craftedRevives INTEGER, killedOtherPlayers INTEGER, firstJoin BIGINT)");

                migrateDatabase(connection);
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to initialize SQL database: " + e.getMessage());
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to initialize SQL database: " + e.getMessage());
        }
    }

    public abstract ConnectionPool getConnectionPool();

    public Connection getConnection() throws SQLException {
        return getConnectionPool().getConnection();
    }

    @Override
    public PlayerData load(UUID uuid) {
        try (Connection connection = getConnection()) {
            if (connection == null) return null;

            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(30);

                try (ResultSet resultSet = statement.executeQuery("SELECT * FROM hearts WHERE uuid = '" + uuid + "'")) {

                    if (!resultSet.next()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) return null;
                        PlayerData newPlayerData = new PlayerData(player.getName(), uuid);
                        save(newPlayerData);
                        return newPlayerData;
                    }

                    PlayerData playerData = new PlayerData(resultSet.getString("name"), uuid);
                    playerData.setMaxHealth(resultSet.getDouble("maxhp"));
                    playerData.setHasBeenRevived(resultSet.getInt("hasbeenRevived"));
                    playerData.setCraftedHearts(resultSet.getInt("craftedHearts"));
                    playerData.setCraftedRevives(resultSet.getInt("craftedRevives"));
                    playerData.setKilledOtherPlayers(resultSet.getInt("killedOtherPlayers"));
                    playerData.setFirstJoin(resultSet.getLong("firstJoin"));
                    playerData.clearModifiedFields();

                    return playerData;
                } catch (SQLException e) {
                    getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
                    return null;
                }
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(PlayerData playerData) {
        // This uses standard SQL syntax to work with all SQL databases, but may not be optimal for all (e.g. H2 or MySQL)
        if (!playerData.hasChanges()) return;

        try (Connection connection = getConnection()) {
            if (connection == null) return;

            boolean exists = checkIfEntryExists(connection, playerData.getUuid());

            if (exists) {
                updatePlayerData(connection, playerData);
            } else {
                insertPlayerData(connection, playerData);
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to save player data: " + e.getMessage());
        }
    }

    /**
     * Check if a player entry exists in the database
     * @param connection Connection to the database
     * @param uuid UUID of the player to check
     * @return True if the player entry exists, false otherwise
     */
    private boolean checkIfEntryExists(Connection connection, String uuid) {
        final String selectQuery = "SELECT 1 FROM hearts WHERE uuid = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);) {
            selectStmt.setString(1, uuid);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to check if player entry exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Insert player data into the database
     * @param connection Connection to the database
     * @param playerData Player data to insert
     * @return True if the insert was successful, false otherwise
     */
    private boolean insertPlayerData(Connection connection, PlayerData playerData) {
        final String insertQuery = "INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setString(1, playerData.getUuid());
            insertStmt.setString(2, playerData.getName());
            insertStmt.setDouble(3, playerData.getMaxHealth());
            insertStmt.setInt(4, playerData.getHasBeenRevived());
            insertStmt.setInt(5, playerData.getCraftedHearts());
            insertStmt.setInt(6, playerData.getCraftedRevives());
            insertStmt.setInt(7, playerData.getKilledOtherPlayers());
            insertStmt.setLong(8, playerData.getFirstJoin());
            insertStmt.executeUpdate();

            playerData.clearModifiedFields();

            return true;
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to insert player data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update player data in the database
     * @param connection Connection to the database
     * @param playerData Player data to update
     * @return True if the update was successful, false otherwise
     */
    private boolean updatePlayerData(Connection connection, PlayerData playerData) {
        StringBuilder updateQuery = new StringBuilder("UPDATE hearts SET ");
        List<Object> params = new ArrayList<>();

        for (String field : playerData.getModifiedFields()) {
            updateQuery.append(field).append(" = ?, ");
            switch (field) {
                case "maxhp":
                    params.add(playerData.getMaxHealth());
                    break;
                case "hasbeenRevived":
                    params.add(playerData.getHasBeenRevived());
                    break;
                case "craftedHearts":
                    params.add(playerData.getCraftedHearts());
                    break;
                case "craftedRevives":
                    params.add(playerData.getCraftedRevives());
                    break;
                case "killedOtherPlayers":
                    params.add(playerData.getKilledOtherPlayers());
                    break;
                case "firstJoin":
                    params.add(playerData.getFirstJoin());
                    break;
            }
        }

        updateQuery.setLength(updateQuery.length() - 2); // Remove the last comma and space
        updateQuery.append(" WHERE uuid = ?");
        params.add(playerData.getUuid());

        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery.toString())) {
            for (int i = 0; i < params.size(); i++) {
                updateStmt.setObject(i + 1, params.get(i));
            }
            updateStmt.executeUpdate();

            playerData.clearModifiedFields();
            return true;
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to update player data: " + e.getMessage());
            return false;
        }
    }

    @Override
    public PlayerData load(String uuid) {
        return load(UUID.fromString(uuid));
    }

    @Override
    public List<UUID> getEliminatedPlayers() {
        List<UUID> eliminatedPlayers = new ArrayList<>();

        int minHearts = getPlugin().getConfig().getInt("minHearts");

        try (Connection connection = getConnection()) {
            if (connection == null) return eliminatedPlayers;

            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(30);

                ResultSet resultSet = statement.executeQuery("SELECT uuid FROM hearts WHERE maxhp <= " + minHearts * 2 + ".0");

                while (resultSet.next()) {
                    eliminatedPlayers.add(UUID.fromString(resultSet.getString("uuid")));
                }
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
        }

        return eliminatedPlayers;
    }


    @Override
    public String export(String fileName) {
        String filePath = getPlugin().getDataFolder().getPath() + "/" + fileName + ".csv";
        try (Connection connection = getConnection()) {
            if (connection == null) return null;

            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM hearts");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    while (resultSet.next()) {
                        String line = resultSet.getString("uuid") + CSV_SEPARATOR +
                                resultSet.getString("name") + CSV_SEPARATOR +
                                resultSet.getDouble("maxhp") + CSV_SEPARATOR +
                                resultSet.getInt("hasbeenRevived") + CSV_SEPARATOR +
                                resultSet.getInt("craftedHearts") + CSV_SEPARATOR +
                                resultSet.getInt("craftedRevives") + CSV_SEPARATOR +
                                resultSet.getInt("killedOtherPlayers") + CSV_SEPARATOR +
                                resultSet.getLong("firstJoin");
                        writer.write(line);
                        writer.newLine();
                    }
                }
            } catch (SQLException | IOException e) {
                getPlugin().getLogger().severe("Failed to export player data to CSV file: " + e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to export player data to CSV file: " + e.getMessage());
            return null;
        }
        return filePath;
    }

    @Override
    public void importData(String fileName) {
        String filePath = getPlugin().getDataFolder().getPath() + "/" + fileName;
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             Connection connection = getConnection()) {

            if (connection == null) return;

            // We are disabling auto-commit to insert all data in a single transaction to improve performance
            connection.setAutoCommit(false);

            String sql = "INSERT OR REPLACE INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String line;
                int batchSize = 0;
                int totalRows = 0;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(CSV_SEPARATOR);

                    if (data.length != 8) {
                        getPlugin().getLogger().severe("Invalid CSV format. Expected 8 columns, but got " + data.length);
                        continue;
                    }

                    totalRows++;

                    statement.setString(1, data[0]);  // uuid
                    statement.setString(2, data[1]);  // name
                    statement.setDouble(3, Double.parseDouble(data[2])); // maxhp
                    statement.setInt(4, Integer.parseInt(data[3])); // hasbeenRevived
                    statement.setInt(5, Integer.parseInt(data[4])); // craftedHearts
                    statement.setInt(6, Integer.parseInt(data[5])); // craftedRevives
                    statement.setInt(7, Integer.parseInt(data[6])); // killedOtherPlayers
                    statement.setLong(8, Long.parseLong(data[7])); // firstJoin

                    statement.addBatch();
                    batchSize++;

                    // Execute batch every 500 inserts
                    if (batchSize % 500 == 0) {
                        statement.executeBatch();
                    }
                }
                statement.executeBatch();

                connection.commit();

                long endTime = System.currentTimeMillis();
                getPlugin().getLogger().info("Imported " + totalRows + " player data entries in " + (endTime - startTime) + "ms");

            } catch (SQLException e) {
                connection.rollback();
                getPlugin().getLogger().severe("Failed to import player data: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (IOException | SQLException e) {
            getPlugin().getLogger().severe("Failed to read CSV file: " + e.getMessage());
        }
    }

    @Override
    public int reviveAllPlayers(int minHearts, int reviveHearts, int maxRevives, boolean bypassReviveLimit) {
        int affectedPlayers = 0;

        String sql = "UPDATE hearts SET maxhp = ?, hasbeenRevived = hasbeenRevived + 1 WHERE maxhp <= ? AND (hasbeenRevived < ?)";

        if (bypassReviveLimit || maxRevives < 0) {
            sql = "UPDATE hearts SET maxhp = ?, hasbeenRevived = hasbeenRevived + 1 WHERE maxhp <= ?";
        }

        try (Connection connection = getConnection()) {
            if (connection == null) return affectedPlayers;

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDouble(1, reviveHearts * 2);
                pstmt.setDouble(2, minHearts * 2);

                if (!bypassReviveLimit && maxRevives >= 0) {
                    pstmt.setInt(3, maxRevives);
                }

                affectedPlayers = pstmt.executeUpdate();
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to revive all players in SQL database: " + e.getMessage());
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to revive all players in SQL database: " + e.getMessage());
        }

        return affectedPlayers;
    }

    @Override
    public List<String> getPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        try (Connection connection = getConnection()) {
            if (connection == null) return playerNames;

            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(30);

                ResultSet resultSet = statement.executeQuery("SELECT name FROM hearts");

                while (resultSet.next()) {
                    playerNames.add(resultSet.getString("name"));
                }
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
        }

        return playerNames;
    }

    @Override
    public List<String> getEliminatedPlayerNames() {
        List<String> eliminatedPlayerNames = new ArrayList<>();

        try (Connection connection = getConnection()) {
            if (connection == null) return eliminatedPlayerNames;

            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(30);

                ResultSet resultSet = statement.executeQuery("SELECT name FROM hearts WHERE maxhp <= 0.0");

                while (resultSet.next()) {
                    eliminatedPlayerNames.add(resultSet.getString("name"));
                }
            } catch (SQLException e) {
                getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
            }
        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to load player data from SQL database: " + e.getMessage());
        }

        return eliminatedPlayerNames;
    }

    private void migrateDatabase(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            boolean hasFirstJoin = false;
            String databaseType = connection.getMetaData().getDatabaseProductName().toLowerCase();

            if (databaseType.contains("sqlite")) {
                try (ResultSet resultSet = statement.executeQuery("PRAGMA table_info(hearts)")) {
                    while (resultSet.next()) {
                        if ("firstJoin".equalsIgnoreCase(resultSet.getString("name"))) {
                            hasFirstJoin = true;
                            break;
                        }
                    }
                }
            } else if (databaseType.contains("mysql")) {
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'hearts' AND COLUMN_NAME = 'firstJoin'")) {
                    if (resultSet.next()) {
                        hasFirstJoin = true;
                    }
                }
            }

            if (!hasFirstJoin) {
                getPlugin().getLogger().info("Adding 'firstJoin' column to 'hearts' table.");
                statement.executeUpdate("ALTER TABLE hearts ADD COLUMN firstJoin INTEGER DEFAULT 0");
            }

        } catch (SQLException e) {
            getPlugin().getLogger().severe("Failed to migrate SQL database: " + e.getMessage());
        }
    }
}
