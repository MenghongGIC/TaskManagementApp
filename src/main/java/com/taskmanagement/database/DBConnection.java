package com.taskmanagement.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
public class DBConnection {
    
    // Singleton instance
    private static final DBConnection INSTANCE = new DBConnection();

        private static final String CONFIG_FILE = "application.properties";

        private static final String DEFAULT_SERVER = "localhost";
        private static final String DEFAULT_PORT = "1433";
        private static final String DEFAULT_DATABASE = "TaskManagementDB";
        private static final String DEFAULT_USERNAME = "sa";
        private static final String DEFAULT_PASSWORD = "Hong2412#tictic";
    
    // Connection configuration
        private static final String URL = "jdbc:sqlserver://" + getProperty("database.server", DEFAULT_SERVER)
            + ":" + getProperty("database.port", DEFAULT_PORT) + ";"
            + "databaseName=" + getProperty("database.name", DEFAULT_DATABASE) + ";"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "loginTimeout=30;";
    
        private static final String USERNAME = getProperty("database.username", DEFAULT_USERNAME);
        private static final String PASSWORD = getProperty("database.password", DEFAULT_PASSWORD);
    
    private static final String ERR_CONNECT_FAILED = "Failed to connect to SQL Server:";
    private static final String MSG_SHUTDOWN = "DBConnection shutdown complete.";
    
    private DBConnection() {
    }

    private Connection createConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException e) {
            System.err.println(ERR_CONNECT_FAILED);
            e.printStackTrace(System.err);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    public Connection getConnection() {
        return createConnection();
    }
    public static DBConnection getInstance() {
        return INSTANCE;
    }
    public void close() {}
    public static void shutdown() {
        INSTANCE.close();
        System.out.println(MSG_SHUTDOWN);
    }

    private static String getProperty(String key, String defaultValue) {
        Properties properties = new Properties();
        try (InputStream fileInput = new FileInputStream(CONFIG_FILE)) {
            properties.load(fileInput);
        } catch (IOException fileReadError) {
            try (InputStream resourceInput = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (resourceInput != null) {
                    properties.load(resourceInput);
                }
            } catch (IOException ignored) {
                // Return default below
            }
        }

        return properties.getProperty(key, defaultValue);
    }
}
