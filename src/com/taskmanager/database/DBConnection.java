package com.taskmanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=TaskManagerDB;encrypt=true;trustServerCertificate=true;";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "GicOOP2025";   

    private DBConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to Microsoft SQL Server successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("SQL Server JDBC Driver JAR not found in /lib !");
            e.printStackTrace(System.err);
        } catch (SQLException e) {
            System.err.println("Connection failed! Check if Docker container is running and password is correct.");
            e.printStackTrace(System.err);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = new DBConnection(); // reconnect if closed
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return connection;
    }
}