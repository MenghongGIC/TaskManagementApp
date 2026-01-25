package com.taskmanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {
    
    // Singleton instance
    private static final DBConnection INSTANCE = new DBConnection();
    
    // Connection configuration
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=TaskManagementDB;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "loginTimeout=30;";
    
    private static final int VALIDATION_TIMEOUT_SECONDS = 5;
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "Hong2412#tictic";
    
    private static final String ERR_CONNECT_FAILED = "Failed to connect to SQL Server:";
    private static final String ERR_INVALID_CONNECTION = "Connection lost or invalid. Reconnecting...";
    private static final String ERR_CHECK_VALIDITY = "Error checking connection validity. Attempting reconnect...";
    private static final String ERR_CLOSE_CONNECTION = "Error closing connection: ";
    private static final String MSG_CONNECTED = "Connected to Microsoft SQL Server successfully!";
    private static final String MSG_CLOSED = "Database connection closed.";
    private static final String MSG_SHUTDOWN = "DBConnection shutdown complete.";

    private Connection connection;
    
    private DBConnection() {
        connect();
    }

    private void connect() {
        try {
            if (isConnectionInvalid()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println(MSG_CONNECTED);
            }
        } catch (SQLException e) {
            System.err.println(ERR_CONNECT_FAILED);
            e.printStackTrace(System.err);
            throw new RuntimeException("Database connection failed", e);
        }
    }
    private boolean isConnectionInvalid() throws SQLException {
        return connection == null || connection.isClosed() || !connection.isValid(VALIDATION_TIMEOUT_SECONDS);
    }

    public Connection getConnection() {
        try {
            if (isConnectionInvalid()) {
                System.out.println(ERR_INVALID_CONNECTION);
                connect();
            }
        } catch (SQLException e) {
            System.err.println(ERR_CHECK_VALIDITY);
            connect();
        }
        return connection;
    }
    public static DBConnection getInstance() {
        return INSTANCE;
    }
    public void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println(MSG_CLOSED);
                }
            } catch (SQLException e) {
                System.err.println(ERR_CLOSE_CONNECTION + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
    public static void shutdown() {
        INSTANCE.close();
        System.out.println(MSG_SHUTDOWN);
    }
}
