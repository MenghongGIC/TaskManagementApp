package com.taskmanagement.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestDataGenerator {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=TaskManagementDB;encrypt=true;trustServerCertificate=true;";
        String username = "sa";
        String password = "Hong2412#tictic";
        
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            
            // Read SQL script
            StringBuilder sqlScript = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new FileReader("generate_test_data.sql"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sqlScript.append(line).append("\n");
                }
            }
            
            // Split by GO and execute
            String[] batches = sqlScript.toString().split("\nGO\n");
            for (String batch : batches) {
                if (!batch.trim().isEmpty()) {
                    try {
                        stmt.execute(batch);
                    } catch (Exception e) {
                        System.err.println("Error executing batch: " + e.getMessage());
                    }
                }
            }
            
            stmt.close();
            conn.close();
            System.out.println("✅ Test data generated successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
