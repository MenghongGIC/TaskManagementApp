package com.taskmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.taskmanagement.model.ActionType;
import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.model.User;

public class ActivityLogRepository extends BaseRepository {

    // SQL Queries
    private static final String SQL_INSERT = """
            INSERT INTO ActivityLog (action, entity_type, entity_id, entity_name, user_id, details, [timestamp])
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_SELECT_ALL = """
            SELECT id, action, entity_type, entity_id, entity_name, user_id, details, [timestamp]
            FROM ActivityLog
            ORDER BY [timestamp] DESC
            """;

    private static final String SQL_SELECT_BY_ID = """
            SELECT id, action, entity_type, entity_id, entity_name, user_id, details, [timestamp]
            FROM ActivityLog
            WHERE id = ?
            """;

    private static final String SQL_SELECT_BY_USER = """
            SELECT id, action, entity_type, entity_id, entity_name, user_id, details, [timestamp]
            FROM ActivityLog
            WHERE user_id = ?
            ORDER BY [timestamp] DESC
            """;

    private static final String SQL_SELECT_BY_ENTITY = """
            SELECT id, action, entity_type, entity_id, entity_name, user_id, details, [timestamp]
            FROM ActivityLog
            WHERE entity_type = ? AND entity_id = ?
            ORDER BY [timestamp] DESC
            """;

    private static final String SQL_SELECT_BY_ACTION = """
            SELECT id, action, entity_type, entity_id, entity_name, user_id, details, [timestamp]
            FROM ActivityLog
            WHERE action = ?
            ORDER BY [timestamp] DESC
            """;

    private static final String SQL_SELECT_RECENT = """
            SELECT TOP ? id, action, entity_type, entity_id, entity_name, user_id, details, [timestamp]
            FROM ActivityLog
            ORDER BY [timestamp] DESC
            """;

    private static final String SQL_DELETE = "DELETE FROM ActivityLog WHERE id = ?";

    private static final String SQL_DELETE_ALL = "DELETE FROM ActivityLog";

    // Column Names
    private static final String COL_ID = "id";
    private static final String COL_ACTION = "action";
    private static final String COL_ENTITY_TYPE = "entity_type";
    private static final String COL_ENTITY_ID = "entity_id";
    private static final String COL_ENTITY_NAME = "entity_name";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_DETAILS = "details";
    private static final String COL_TIMESTAMP = "timestamp";

    // Error Messages
    private static final String ERR_SAVE = "Error saving activity log: ";
    private static final String ERR_LOAD_ALL = "Error loading all activity logs";
    private static final String ERR_FIND_BY_ID = "Error finding activity log by ID: ";
    private static final String ERR_FIND_BY_USER = "Error loading activity logs for user: ";
    private static final String ERR_FIND_BY_ENTITY = "Error loading activity logs for entity: ";
    private static final String ERR_FIND_BY_ACTION = "Error loading activity logs by action: ";
    private static final String ERR_FIND_RECENT = "Error loading recent activity logs";
    private static final String ERR_DELETE = "Error deleting activity log ID: ";
    private static final String ERR_DELETE_ALL = "Error deleting all activity logs";

    public ActivityLog save(ActivityLog log) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            // Convert ActionType enum to string for storage
            String actionString = log.getAction() != null ? log.getAction().name() : null;
            
            pstmt.setString(1, actionString);
            pstmt.setString(2, log.getEntityType());
            pstmt.setObject(3, log.getEntityId());
            pstmt.setString(4, log.getEntityName());
            pstmt.setObject(5, log.getUser() != null ? log.getUser().getId() : null);
            pstmt.setString(6, log.getDetails());
            pstmt.setTimestamp(7, Timestamp.valueOf(log.getTimestamp()));

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    log.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_SAVE + (log.getAction() != null ? log.getAction().name() : "NULL"), e);
        }
        return log;
    }

    public List<ActivityLog> findAll() {
        List<ActivityLog> logs = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                logs.add(mapRowToActivityLog(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERR_LOAD_ALL, e);
        }
        return logs;
    }

    public ActivityLog findById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToActivityLog(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_ID + id, e);
        }
        return null;
    }

    public List<ActivityLog> findByUserId(Long userId) {
        List<ActivityLog> logs = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_USER)) {

            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToActivityLog(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_USER + userId, e);
        }
        return logs;
    }

    public List<ActivityLog> findByEntity(String entityType, Long entityId) {
        List<ActivityLog> logs = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ENTITY)) {

            pstmt.setString(1, entityType);
            pstmt.setLong(2, entityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToActivityLog(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_ENTITY + entityType + ":" + entityId, e);
        }
        return logs;
    }

    public List<ActivityLog> findByAction(String action) {
        List<ActivityLog> logs = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ACTION)) {

            pstmt.setString(1, action);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToActivityLog(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_ACTION + action, e);
        }
        return logs;
    }

    public List<ActivityLog> findRecent(int count) {
        List<ActivityLog> logs = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_RECENT)) {

            pstmt.setInt(1, count);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToActivityLog(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_RECENT, e);
        }
        return logs;
    }

    public void delete(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ERR_DELETE + id, e);
        }
    }

    public void deleteAll() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(SQL_DELETE_ALL);

        } catch (SQLException e) {
            throw new RuntimeException(ERR_DELETE_ALL, e);
        }
    }

    private ActivityLog mapRowToActivityLog(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getLong(COL_ID));
        
        // Convert string from database to ActionType enum
        String actionString = rs.getString(COL_ACTION);
        if (actionString != null) {
            try {
                ActionType action = ActionType.fromString(actionString);
                log.setAction(action);
            } catch (IllegalArgumentException e) {
                System.err.println("Unknown action type from database: " + actionString);
                log.setAction(null);
            }
        }
        
        log.setEntityType(rs.getString(COL_ENTITY_TYPE));
        log.setEntityId((Long) rs.getObject(COL_ENTITY_ID));
        log.setEntityName(rs.getString(COL_ENTITY_NAME));

        // User - only set ID, don't load full user details
        Long userId = (Long) rs.getObject(COL_USER_ID);
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            log.setUser(user);
        }

        log.setDetails(rs.getString(COL_DETAILS));

        Timestamp ts = rs.getTimestamp(COL_TIMESTAMP);
        if (ts != null) {
            log.setTimestamp(ts.toLocalDateTime());
        }

        return log;
    }
}
