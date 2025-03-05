package org.strassburger.lifestealz.util.storage.connectionPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A connection pool that manages connections to a database.
 */
public interface ConnectionPool {
    /**
     * Returns a connection from the connection pool.
     * @return a connection from the connection pool
     * @throws SQLException if an error occurs while getting a connection
     */
    Connection getConnection() throws SQLException;

    /**
     * Releases a connection back to the connection pool.
     * @param connection the connection to release
     * @throws SQLException if an error occurs while releasing the connection
     */
    void releaseConnection(Connection connection) throws SQLException;

    /**
     * Shuts down the connection pool.
     * @throws SQLException if an error occurs while shutting down the connection pool
     */
    void shutdown() throws SQLException;
}
