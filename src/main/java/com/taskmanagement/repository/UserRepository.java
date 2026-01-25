package com.taskmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.taskmanagement.database.DBConnection;
import com.taskmanagement.model.Role;
import com.taskmanagement.model.User;

public class UserRepository {

    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }
    public User save(User user) {
        if (user.getId() != null) {
            // Update existing user
            return update(user);
        } else {
            // Insert new user
            return insert(user);
        }
    }
    private User insert(User user) {
        String sql = """
            INSERT INTO Users (username, password_hash, email, role, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole() != null ? user.getRole().name() : Role.USER.name());
            pstmt.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to insert user: " + user.getUsername());
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user: " + user.getUsername(), e);
        }
    }

    private User update(User user) {
        String sql = """
            UPDATE Users 
            SET password_hash = ?, email = ?, role = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getPasswordHash());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole() != null ? user.getRole().name() : Role.USER.name());
            pstmt.setLong(4, user.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update user: " + user.getUsername());
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + user.getUsername(), e);
        }
    }

    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return null;

        String sql = "SELECT * FROM Users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + username, e);
        }
        return null;
    }

    public User findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM Users WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID: " + id, e);
        }
        return null;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY username";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading all users", e);
        }
        return users;
    }

    public void updateLastLogin(User user) {
        if (user == null || user.getId() == null) return;

        String sql = "UPDATE Users SET last_login = GETDATE() WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to update last login for user ID: " + user.getId());
        }
    }

    public User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        
        // Try to read position field - it may not exist in older databases
        try {
            user.setPosition(rs.getString("position"));
        } catch (SQLException e) {
            // Column doesn't exist, skip it
            user.setPosition(null);
        }
        
        String roleStr = rs.getString("role");
        user.setRole(roleStr != null ? Role.valueOf(roleStr.toUpperCase()) : Role.USER);

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            user.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp lastLoginTs = rs.getTimestamp("last_login");
        if (lastLoginTs != null) {
            user.setLastLogin(lastLoginTs.toLocalDateTime());
        }

        return user;
    }

    public void delete(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM Users WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user ID: " + id, e);
        }
    }
}