package org.strassburger.lifestealz.storage.connectionPool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A MariaDB connection pool that manages connections to a MariaDB database.
 */
public class MariaDBConnectionPool implements ConnectionPool {
    private final HikariDataSource dataSource;

    /**
     * Constructs a MariaDB connection pool with the specified host, port, database, username, and password.
     * @param host the host of the MariaDB database
     * @param port the port of the MariaDB database
     * @param database the name of the MariaDB database
     * @param username the username to connect to the MariaDB database
     * @param password the password to connect to the MariaDB database
     */
    public MariaDBConnectionPool(String host, String port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    /**
     * Returns a connection to the MariaDB database from the connection pool.
     * @return a connection to the MariaDB database
     * @throws SQLException if an error occurs while getting a connection
     */
    @Override
    public Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Failed to get connection from MariaDB connection pool: " + e.getMessage());
        }
    }

    /**
     * Releases a connection back to the MariaDB connection pool.
     * @param connection the connection to release
     * @throws SQLException if an error occurs while releasing the connection
     */
    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Shuts down the MariaDB connection pool and releases all resources.
     */
    @Override
    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
