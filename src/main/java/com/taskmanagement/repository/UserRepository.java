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

    // SQL Queries
    // Insert new user with username, password hash, email, role, and creation timestamp
    private static final String SQL_INSERT = """
            INSERT INTO Users (username, password_hash, email, role, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;
    
    // Update user password hash, email, and role
    private static final String SQL_UPDATE = """
            UPDATE Users 
            SET password_hash = ?, email = ?, role = ?
            WHERE id = ?
            """;
    
    // Select user by username
    private static final String SQL_SELECT_BY_USERNAME = "SELECT * FROM Users WHERE username = ?";
    
    // Select user by ID
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM Users WHERE id = ?";
    
    // Select all users ordered by username
    private static final String SQL_SELECT_ALL = "SELECT * FROM Users ORDER BY username";
    
    // Update user's last login timestamp
    private static final String SQL_UPDATE_LAST_LOGIN = "UPDATE Users SET last_login = GETDATE() WHERE id = ?";
    
    // Delete user by ID
    private static final String SQL_DELETE = "DELETE FROM Users WHERE id = ?";
    
    // Column Names
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD_HASH = "password_hash";
    private static final String COL_ROLE = "role";
    private static final String COL_POSITION = "position";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_LAST_LOGIN = "last_login";
    
    // Error Messages
    private static final String ERR_INSERT_FAILED = "Failed to insert user: ";
    private static final String ERR_INSERT = "Error inserting user: ";
    private static final String ERR_UPDATE_FAILED = "Failed to update user: ";
    private static final String ERR_UPDATE = "Error updating user: ";
    private static final String ERR_FIND_BY_USERNAME = "Error finding user by username: ";
    private static final String ERR_FIND_BY_ID = "Error finding user by ID: ";
    private static final String ERR_LOAD_ALL = "Error loading all users";
    private static final String ERR_DELETE = "Error deleting user ID: ";
    private static final String WARN_UPDATE_LAST_LOGIN = "Failed to update last login for user ID: ";

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
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, getRoleName(user.getRole()));
            pstmt.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException(ERR_INSERT_FAILED + user.getUsername());
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(ERR_INSERT + user.getUsername(), e);
        }
    }

    private User update(User user) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, user.getPasswordHash());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, getRoleName(user.getRole()));
            pstmt.setLong(4, user.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException(ERR_UPDATE_FAILED + user.getUsername());
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(ERR_UPDATE + user.getUsername(), e);
        }
    }
    private String getRoleName(Role role) {
        return role != null ? role.name() : Role.USER.name();
    }

    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_USERNAME)) {

            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_USERNAME + username, e);
        }
        return null;
    }

    public User findById(Long id) {
        if (id == null) return null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_ID + id, e);
        }
        return null;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERR_LOAD_ALL, e);
        }
        return users;
    }

    public void updateLastLogin(User user) {
        if (user == null || user.getId() == null) return;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_LAST_LOGIN)) {

            pstmt.setLong(1, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(WARN_UPDATE_LAST_LOGIN + user.getId());
        }
    }

    public User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong(COL_ID));
        user.setUsername(rs.getString(COL_USERNAME));
        user.setEmail(rs.getString(COL_EMAIL));
        user.setPasswordHash(rs.getString(COL_PASSWORD_HASH));
        try {
            user.setPosition(rs.getString(COL_POSITION));
        } catch (SQLException e) {
            // Column doesn't exist, skip it
            user.setPosition(null);
        }
        
        String roleStr = rs.getString(COL_ROLE);
        user.setRole(roleStr != null ? Role.valueOf(roleStr.toUpperCase()) : Role.USER);

        Timestamp createdTs = rs.getTimestamp(COL_CREATED_AT);
        if (createdTs != null) {
            user.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp lastLoginTs = rs.getTimestamp(COL_LAST_LOGIN);
        if (lastLoginTs != null) {
            user.setLastLogin(lastLoginTs.toLocalDateTime());
        }

        return user;
    }

    public void delete(Long id) {
        if (id == null) return;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ERR_DELETE + id, e);
        }
    }
}