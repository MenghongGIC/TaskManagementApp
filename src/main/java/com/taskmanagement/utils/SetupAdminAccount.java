package com.taskmanagement.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Comprehensive Admin Account Setup Utility
 * 
 * This single utility file handles:
 * 1. Creating admin account if it doesn't exist
 * 2. Updating admin password to plain text for login
 * 3. Verifying admin account exists and displays credentials
 * 4. Shows all users in the database
 */
public class SetupAdminAccount {

    // Database connection details
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=TaskManagementDB;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "loginTimeout=30;";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "Hong2412#tictic";

    public static void main(String[] args) {
        try {
            // Connect to database
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("✓ Connected to Microsoft SQL Server successfully!\n");

            // Step 1: Check if admin exists
            System.out.println("═══ STEP 1: Checking Admin Account ═══");
            User adminUser = findUserByUsername(conn, "admin");

            if (adminUser == null) {
                // Step 2: Create admin account
                System.out.println("✗ Admin account does not exist. Creating new admin account...\n");
                createAdminAccount(conn);
            } else {
                System.out.println("✓ Admin account exists!");
                System.out.println("  ID: " + adminUser.id);
                System.out.println("  Username: " + adminUser.username);
                System.out.println("  Email: " + adminUser.email);
                System.out.println("  Role: " + adminUser.role + "\n");
            }

            // Step 3: Update admin password to plain text
            System.out.println("═══ STEP 2: Updating Admin Password ═══");
            updateAdminPassword(conn);

            // Step 4: Verify all users in database
            System.out.println("\n═══ STEP 3: Verifying All Users ═══");
            listAllUsers(conn);

            // Step 5: Display login information
            System.out.println("║  📧 Username: admin                                        ║");
            System.out.println("║  🔐 Password: admin12345                                   ║");
            System.out.println("║  ✉️  Email:    admin@gmail.com                             ║");
            System.out.println("║  👤 Role:     ADMIN                                        ║");
            conn.close();

        } catch (Exception e) {
            System.err.println("✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Find user by username
     */
    private static User findUserByUsername(Connection conn, String username) {
        try {
            String sql = "SELECT id, username, email, role, created_at FROM Users WHERE username = '" + username + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                User user = new User();
                user.id = rs.getLong("id");
                user.username = rs.getString("username");
                user.email = rs.getString("email");
                user.role = rs.getString("role");
                user.createdAt = rs.getTimestamp("created_at").toString();
                rs.close();
                stmt.close();
                return user;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Create admin account
     */
    private static void createAdminAccount(Connection conn) {
        try {
            String sql = "INSERT INTO Users (username, password_hash, email, role, created_at) " +
                    "VALUES ('admin', 'admin12345', 'admin@gmail.com', 'ADMIN', GETDATE())";
            
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql);
            
            if (rowsAffected > 0) {
                System.out.println("✓ Admin account created successfully!");
                System.out.println("  Username: admin");
                System.out.println("  Password: admin12345");
                System.out.println("  Email: admin@gmail.com");
                System.out.println("  Role: ADMIN\n");
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println("Error creating admin account: " + e.getMessage());
        }
    }

    /**
     * Update admin password to plain text
     */
    private static void updateAdminPassword(Connection conn) {
        try {
            String sql = "UPDATE Users SET password_hash = 'admin12345' WHERE username = 'admin'";
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql);
            
            if (rowsAffected > 0) {
                System.out.println("✓ Admin password updated to plain text!");
                System.out.println("  Username: admin");
                System.out.println("  Password: admin12345");
                System.out.println("  Password Type: Plain Text");
                System.out.println("  Status: Ready for Login\n");
            } else {
                System.out.println("✗ Failed to update admin password");
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println("Error updating admin password: " + e.getMessage());
        }
    }

    /**
     * List all users in database
     */
    private static void listAllUsers(Connection conn) {
        try {
            String sql = "SELECT id, username, email, role, created_at FROM Users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n" + String.format("%-5s | %-15s | %-25s | %-10s | %-19s",
                    "ID", "Username", "Email", "Role", "Created At"));
            System.out.println("─".repeat(95));

            boolean hasUsers = false;
            while (rs.next()) {
                hasUsers = true;
                long id = rs.getLong("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String createdAt = rs.getTimestamp("created_at").toString();

                System.out.println(String.format("%-5d | %-15s | %-25s | %-10s | %-19s",
                        id, username, email, role, createdAt));
            }

            if (!hasUsers) {
                System.out.println("No users found in database");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("Error listing users: " + e.getMessage());
        }
    }

    /**
     * Inner class to represent a user
     */
    static class User {
        long id;
        String username;
        String email;
        String role;
        String createdAt;
    }
}
