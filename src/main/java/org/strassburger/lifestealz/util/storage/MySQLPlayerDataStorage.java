package org.strassburger.lifestealz.util.storage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.strassburger.lifestealz.LifeStealZ;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLPlayerDataStorage implements PlayerDataStorage {
    private static final String CSV_SEPARATOR = ",";

    @Override
    public void init() {
        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS hearts (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "name VARCHAR(255), " +
                        "maxhp DOUBLE, " +
                        "hasbeenRevived INT, " +
                        "craftedHearts INT, " +
                        "craftedRevives INT, " +
                        "killedOtherPlayers INT)");
            } catch (SQLException e) {
                LifeStealZ.getInstance().getLogger().severe("Failed to initialize MySQL database: " + e.getMessage());
            }
        } catch (SQLException e) {
            LifeStealZ.getInstance().getLogger().severe("Failed to initialize MySQL database: " + e.getMessage());
        }
    }

    private Connection createConnection() {
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

    @Override
    public void save(PlayerData playerData) {
        try (Connection connection = createConnection()) {
            if (connection == null) return;
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                            "name = VALUES(name), maxhp = VALUES(maxhp), hasbeenRevived = VALUES(hasbeenRevived), " +
                            "craftedHearts = VALUES(craftedHearts), craftedRevives = VALUES(craftedRevives), killedOtherPlayers = VALUES(killedOtherPlayers)")) {
                statement.setString(1, playerData.getUuid().toString());
                statement.setString(2, playerData.getName());
                statement.setDouble(3, playerData.getMaxhp());
                statement.setInt(4, playerData.getHasbeenRevived());
                statement.setInt(5, playerData.getCraftedHearts());
                statement.setInt(6, playerData.getCraftedRevives());
                statement.setInt(7, playerData.getKilledOtherPlayers());
                statement.executeUpdate();
            } catch (SQLException e) {
                LifeStealZ.getInstance().getLogger().severe("Failed to save player data to MySQL database: " + e.getMessage());
            }
        } catch (SQLException e) {
            LifeStealZ.getInstance().getLogger().severe("Failed to save player data to MySQL database: " + e.getMessage());
        }
    }

    @Override
    public PlayerData load(UUID uuid) {
        try (Connection connection = createConnection()) {
            if (connection == null) return null;
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM hearts WHERE uuid = ?")) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) return null;
                        PlayerData newPlayerData = new PlayerData(player.getName(), uuid);
                        save(newPlayerData);
                        return newPlayerData;
                    }

                    PlayerData playerData = new PlayerData(resultSet.getString("name"), uuid);
                    playerData.setMaxhp(resultSet.getDouble("maxhp"));
                    playerData.setHasbeenRevived(resultSet.getInt("hasbeenRevived"));
                    playerData.setCraftedHearts(resultSet.getInt("craftedHearts"));
                    playerData.setCraftedRevives(resultSet.getInt("craftedRevives"));
                    playerData.setKilledOtherPlayers(resultSet.getInt("killedOtherPlayers"));

                    return playerData;
                } catch (SQLException e) {
                    LifeStealZ.getInstance().getLogger().severe("Failed to load player data from MySQL database: " + e.getMessage());
                    return null;
                }
            } catch (SQLException e) {
                LifeStealZ.getInstance().getLogger().severe("Failed to load player data from MySQL database: " + e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            LifeStealZ.getInstance().getLogger().severe("Failed to load player data from MySQL database: " + e.getMessage());
            return null;
        }
    }

    @Override
    public PlayerData load(String uuid) {
        return load(UUID.fromString(uuid));
    }

    @Override
    public List<UUID> getEliminatedPlayers() {
        List<UUID> eliminatedPlayers = new ArrayList<>();

        int minHearts = LifeStealZ.getInstance().getConfig().getInt("minHearts");

        try (Connection connection = createConnection()) {
            if (connection == null) return eliminatedPlayers;

            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(30);

                ResultSet resultSet = statement.executeQuery("SELECT uuid FROM hearts WHERE maxhp <= " + minHearts * 2 + ".0");

                while (resultSet.next()) {
                    eliminatedPlayers.add(UUID.fromString(resultSet.getString("uuid")));
                }
            } catch (SQLException e) {
                LifeStealZ.getInstance().getLogger().severe("Failed to load player data from MySQL database: " + e.getMessage());
            }
        } catch (SQLException e) {
            LifeStealZ.getInstance().getLogger().severe("Failed to load player data from MySQL database: " + e.getMessage());
        }

        return eliminatedPlayers;
    }

    @Override
    public String export(String fileName) {
        String filePath = LifeStealZ.getInstance().getDataFolder().getPath() + "/" + fileName + ".csv";
        System.out.println(filePath);
        try (Connection connection = createConnection()) {
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
                                resultSet.getInt("killedOtherPlayers");
                        writer.write(line);
                        writer.newLine();
                    }
                }
            } catch (SQLException | IOException e) {
                LifeStealZ.getInstance().getLogger().severe("Failed to export player data to CSV file: " + e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            LifeStealZ.getInstance().getLogger().severe("Failed to export player data to CSV file: " + e.getMessage());
            return null;
        }
        return filePath;
    }

    @Override
    public void importData(String fileName) {
        String filePath = LifeStealZ.getInstance().getDataFolder().getPath() + "/" + fileName;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(CSV_SEPARATOR);

                if (data.length != 7) {
                    LifeStealZ.getInstance().getLogger().severe("Invalid CSV format.");
                    continue;
                }

                try (Connection connection = createConnection()) {
                    if (connection == null) return;
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                                    "name = VALUES(name), maxhp = VALUES(maxhp), hasbeenRevived = VALUES(hasbeenRevived), " +
                                    "craftedHearts = VALUES(craftedHearts), craftedRevives = VALUES(craftedRevives), killedOtherPlayers = VALUES(killedOtherPlayers)")) {
                        statement.setString(1, data[0]);
                        statement.setString(2, data[1]);
                        statement.setDouble(3, Double.parseDouble(data[2]));
                        statement.setInt(4, Integer.parseInt(data[3]));
                        statement.setInt(5, Integer.parseInt(data[4]));
                        statement.setInt(6, Integer.parseInt(data[5]));
                        statement.setInt(7, Integer.parseInt(data[6]));
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        LifeStealZ.getInstance().getLogger().severe("Failed to import player data from CSV file: " + e.getMessage());
                    }
                } catch (SQLException e) {
                    LifeStealZ.getInstance().getLogger().severe("Failed to import player data from CSV file: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            LifeStealZ.getInstance().getLogger().severe("Failed to read CSV file: " + e.getMessage());
        }
    }
}