package com.taskmanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Eagerly initialized singleton instance
    private static final DBConnection INSTANCE = new DBConnection();

    // Constants for database connection
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=TaskManagementDB;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "loginTimeout=30;";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "Hong2412#tictic";

    private Connection connection;
    private DBConnection() {
        connect();
    }

    
    private void connect() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(5)) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connected to Microsoft SQL Server successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to SQL Server:");
            e.printStackTrace(System.err);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    public static DBConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(5)) {
                System.out.println("Connection lost or invalid. Reconnecting...");
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection validity. Attempting reconnect...");
            connect();
        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    // Shutdown method to clean up singleton
    public static void shutdown() {
        INSTANCE.close();
        System.out.println("DBConnection shutdown complete.");
    }
}
