package com.taskmanagement.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;

public class TaskRepository extends BaseRepository {

    // SQL Queries - Common SELECT with joins for project, assignee, and creator
    private static final String BASE_SELECT = """
            SELECT t.*, p.id AS project_id, p.name AS project_name,
                   a.id AS assignee_id, a.username AS assignee_name,
                   c.id AS creator_id, c.username AS creator_name
            FROM Tasks t
            LEFT JOIN Projects p ON t.project_id = p.id
            LEFT JOIN Users a ON t.assignee_id = a.id
            JOIN Users c ON t.created_by = c.id
            """;
    
    // Insert new task with title, description, status, priority, due date, project, assignee, creator
    private static final String SQL_INSERT = """
            INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    
    // Select all tasks with related project, assignee, and creator info, ordered by most recent
    private static final String SQL_SELECT_ALL = BASE_SELECT + "ORDER BY t.created_at DESC";
    
    // Select task by ID with related project, assignee, and creator info
    private static final String SQL_SELECT_BY_ID = BASE_SELECT + "WHERE t.id = ?";
    
    // Delete task by ID
    private static final String SQL_DELETE = "DELETE FROM Tasks WHERE id = ?";
    
    // Update task with title, description, status, priority, due date, project, assignee, creator
    private static final String SQL_UPDATE = """
            UPDATE Tasks 
            SET title = ?, description = ?, status = ?, priority = ?, due_date = ?,
                project_id = ?, assignee_id = ?, created_by = ?
            WHERE id = ?
            """;
    
    // Update task status by ID
    private static final String SQL_UPDATE_STATUS = "UPDATE Tasks SET status = ? WHERE id = ?";
    
    // Select tasks by assignee with related project, assignee, and creator info
    private static final String SQL_SELECT_BY_ASSIGNEE = BASE_SELECT + "WHERE t.assignee_id = ? ORDER BY t.created_at DESC";
    
    // Select tasks by creator with related project, assignee, and creator info
    private static final String SQL_SELECT_BY_CREATOR = BASE_SELECT + "WHERE t.created_by = ? ORDER BY t.created_at DESC";
    
    // Column Names
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_STATUS = "status";
    private static final String COL_PRIORITY = "priority";
    private static final String COL_DUE_DATE = "due_date";
    private static final String COL_PROJECT_ID = "project_id";
    private static final String COL_PROJECT_NAME = "project_name";
    private static final String COL_ASSIGNEE_ID = "assignee_id";
    private static final String COL_ASSIGNEE_NAME = "assignee_name";
    private static final String COL_CREATOR_ID = "creator_id";
    private static final String COL_CREATOR_NAME = "creator_name";
    private static final String COL_CREATED_AT = "created_at";
    
    // Error Messages
    private static final String ERR_SAVE = "Error saving task: ";
    private static final String ERR_LOAD_ALL = "Error loading all tasks";
    private static final String ERR_FIND_BY_PROJECT = "Error loading tasks for project: ";
    private static final String ERR_FIND_BY_ID = "Error finding task by ID: ";
    private static final String ERR_DELETE = "Error deleting task ID: ";
    private static final String ERR_UPDATE = "Error updating task: ";
    private static final String ERR_UPDATE_STATUS = "Error updating task status for ID: ";
    private static final String ERR_FIND_BY_ASSIGNEE = "Error loading tasks for assignee: ";
    private static final String ERR_FIND_BY_CREATOR = "Error loading tasks for creator: ";
    private static final String ERR_INVALID_TASK = "Cannot update task: ID is missing";
    private static final String ERR_INVALID_STATUS = "Task ID and status are required";
    private static final String ERR_NO_TASK_FOUND = "No task found with ID: ";

    
    public Task save(Task task) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus());
            pstmt.setString(4, task.getPriority());

            LocalDate dueDate = task.getDueDate();
            pstmt.setDate(5, dueDate != null ? Date.valueOf(dueDate) : null);

            pstmt.setObject(6, task.getProject() != null ? task.getProject().getId() : null);
            pstmt.setObject(7, task.getAssignee() != null ? task.getAssignee().getId() : null);
            pstmt.setLong(8, task.getCreatedBy().getId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_SAVE + task.getTitle(), e);
        }
        return task;
    }

    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                tasks.add(mapRowToTask(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERR_LOAD_ALL, e);
        }
        return tasks;
    }

    public List<Task> findByProjectId(Long projectId) {
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            pstmt.setLong(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_PROJECT + projectId, e);
        }
        return tasks;
    }

    public Task findById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTask(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_ID + id, e);
        }
        return null;
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

    public Task update(Task task) {
        // Validate task has an ID before update
        if (task == null || task.getId() == null) {
            throw new IllegalArgumentException(ERR_INVALID_TASK);
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus());
            pstmt.setString(4, task.getPriority());

            LocalDate dueDate = task.getDueDate();
            pstmt.setDate(5, dueDate != null ? Date.valueOf(dueDate) : null);

            pstmt.setObject(6, task.getProject() != null ? task.getProject().getId() : null);
            pstmt.setObject(7, task.getAssignee() != null ? task.getAssignee().getId() : null);
            
            // Set created_by - only set if not null to preserve original creator
            if (task.getCreatedBy() != null) {
                pstmt.setLong(8, task.getCreatedBy().getId());
            } else {
                pstmt.setNull(8, java.sql.Types.BIGINT);
            }
            
            pstmt.setObject(9, task.getId());

            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated == 0) {
                throw new RuntimeException(ERR_NO_TASK_FOUND + task.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_UPDATE + task.getTitle(), e);
        }
        return task;
    }

    public void updateStatus(Long taskId, String newStatus) {
        if (taskId == null || newStatus == null) {
            throw new IllegalArgumentException(ERR_INVALID_STATUS);
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_STATUS)) {

            pstmt.setString(1, newStatus);
            pstmt.setLong(2, taskId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ERR_UPDATE_STATUS + taskId, e);
        }
    }

    public List<Task> findByAssigneeId(Long assigneeId) {
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ASSIGNEE)) {

            pstmt.setLong(1, assigneeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_ASSIGNEE + assigneeId, e);
        }
        return tasks;
    }

    public List<Task> findByCreatorId(Long creatorId) {
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_CREATOR)) {

            pstmt.setLong(1, creatorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(ERR_FIND_BY_CREATOR + creatorId, e);
        }
        return tasks;
    }

    public Task mapRowToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong(COL_ID));
        task.setTitle(rs.getString(COL_TITLE));
        task.setDescription(rs.getString(COL_DESCRIPTION));
        task.setStatus(rs.getString(COL_STATUS));
        task.setPriority(rs.getString(COL_PRIORITY));

        Date dueSqlDate = rs.getDate(COL_DUE_DATE);
        if (dueSqlDate != null) {
            task.setDueDate(dueSqlDate.toLocalDate());
        }

        // Project
        Long projectId = (Long) rs.getObject(COL_PROJECT_ID);
        if (projectId != null) {
            Project project = new Project();
            project.setId(projectId);
            project.setName(rs.getString(COL_PROJECT_NAME));
            task.setProject(project);
        }

        // Assignee
        Long assigneeId = (Long) rs.getObject(COL_ASSIGNEE_ID);
        if (assigneeId != null) {
            User assignee = new User();
            assignee.setId(assigneeId);
            assignee.setUsername(rs.getString(COL_ASSIGNEE_NAME));
            task.setAssignee(assignee);
        }

        // Creator
        User creator = new User();
        creator.setId(rs.getLong(COL_CREATOR_ID));
        creator.setUsername(rs.getString(COL_CREATOR_NAME));
        task.setCreatedBy(creator);

        Timestamp createdTs = rs.getTimestamp(COL_CREATED_AT);
        if (createdTs != null) {
            task.setCreatedAt(createdTs.toLocalDateTime());
        }

        return task;
    }
}