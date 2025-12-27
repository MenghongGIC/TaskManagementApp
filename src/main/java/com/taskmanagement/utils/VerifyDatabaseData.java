package com.taskmanagement.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.taskmanagement.database.DBConnection;

public class VerifyDatabaseData {

    public static void main(String[] args) {
        System.out.println("=== Verifying Database Data ===\n");
        
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            Connection connection = dbConnection.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                Statement stmt = connection.createStatement();
                
                // Count users
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Users");
                rs.next();
                System.out.println("✓ Users: " + rs.getInt("count"));
                
                // Count projects
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Projects");
                rs.next();
                System.out.println("✓ Projects: " + rs.getInt("count"));
                
                // Count tasks
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Tasks");
                rs.next();
                System.out.println("✓ Tasks: " + rs.getInt("count"));
                
                // Count labels
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Labels");
                rs.next();
                System.out.println("✓ Labels: " + rs.getInt("count"));
                
                // Count teams
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Teams");
                rs.next();
                System.out.println("✓ Teams: " + rs.getInt("count"));
                
                // Count comments
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Comments");
                rs.next();
                System.out.println("✓ Comments: " + rs.getInt("count"));
                
                // Show all users
                System.out.println("\n=== All Users ===");
                rs = stmt.executeQuery("SELECT id, username, email, role FROM Users");
                while (rs.next()) {
                    System.out.println("  [" + rs.getLong("id") + "] " + rs.getString("username") + " (" + rs.getString("role") + ") - " + rs.getString("email"));
                }
                
                // Show all projects
                System.out.println("\n=== All Projects ===");
                rs = stmt.executeQuery("SELECT id, name, description FROM Projects");
                while (rs.next()) {
                    System.out.println("  [" + rs.getLong("id") + "] " + rs.getString("name") + " - " + rs.getString("description"));
                }
                
                // Show all tasks
                System.out.println("\n=== All Tasks ===");
                rs = stmt.executeQuery("SELECT id, title, status, priority FROM Tasks");
                while (rs.next()) {
                    System.out.println("  [" + rs.getLong("id") + "] " + rs.getString("title") + " (" + rs.getString("status") + ", " + rs.getString("priority") + ")");
                }
                
                System.out.println("\n✓ Database verification complete!");
                stmt.close();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
