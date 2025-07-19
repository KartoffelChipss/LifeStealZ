package com.zetaplugins.lifestealz.storage.connectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection pool for SQLite. This is not really a connection pool, but it is used to keep the same interface as the other
 */
public final class SQLiteConnectionPool implements ConnectionPool {
    private final String connectionUrl;

    public SQLiteConnectionPool(String path) {
        connectionUrl = "jdbc:sqlite:" + path;
    }

    /**
     * Returns a connection to the SQLite database.
     * @return a connection to the SQLite database
     * @throws SQLException if an error occurs while getting a connection
     */
    @Override
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create connection to SQLite database: " + e.getMessage());
        }
    }

    /**
     * This method does nothing, because SQLite does not support connection pooling.
     * @param connection the connection to release
     */
    @Override
    public void releaseConnection(Connection connection) {}

    /**
     * This method does nothing, because SQLite does not support connection pooling.
     */
    @Override
    public void shutdown() {}
}
