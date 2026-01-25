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

    public Task save(Task task) {
        String sql = """
            INSERT INTO Tasks (title, description, status, priority, due_date, project_id, assignee_id, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            throw new RuntimeException("Error saving task: " + task.getTitle(), e);
        }
        return task;
    }

    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT t.*, p.id AS project_id, p.name AS project_name,
                   a.id AS assignee_id, a.username AS assignee_name,
                   c.id AS creator_id, c.username AS creator_name
            FROM Tasks t
            LEFT JOIN Projects p ON t.project_id = p.id
            LEFT JOIN Users a ON t.assignee_id = a.id
            JOIN Users c ON t.created_by = c.id
            ORDER BY t.created_at DESC
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(mapRowToTask(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading all tasks", e);
        }
        return tasks;
    }

    public List<Task> findByProjectId(Long projectId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT t.*, p.id AS project_id, p.name AS project_name,
                   a.id AS assignee_id, a.username AS assignee_name,
                   c.id AS creator_id, c.username AS creator_name
            FROM Tasks t
            LEFT JOIN Projects p ON t.project_id = p.id
            LEFT JOIN Users a ON t.assignee_id = a.id
            JOIN Users c ON t.created_by = c.id
            WHERE t.project_id = ?
            ORDER BY t.created_at DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading tasks for project: " + projectId, e);
        }
        return tasks;
    }

    public Task findById(Long id) {
        String sql = """
            SELECT t.*, p.id AS project_id, p.name AS project_name,
                   a.id AS assignee_id, a.username AS assignee_name,
                   c.id AS creator_id, c.username AS creator_name
            FROM Tasks t
            LEFT JOIN Projects p ON t.project_id = p.id
            LEFT JOIN Users a ON t.assignee_id = a.id
            JOIN Users c ON t.created_by = c.id
            WHERE t.id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Task task = mapRowToTask(rs);
                    return task;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding task by ID: " + id, e);
        }
        return null;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM Tasks WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting task ID: " + id, e);
        }
    }

    public Task update(Task task) {
        // Validate task has an ID before update
        if (task == null || task.getId() == null) {
            throw new IllegalArgumentException("Cannot update task: ID is missing");
        }
        
        String sql = """
            UPDATE Tasks 
            SET title = ?, description = ?, status = ?, priority = ?, due_date = ?,
                project_id = ?, assignee_id = ?, created_by = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
                throw new RuntimeException("No task found with ID: " + task.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating task: " + task.getTitle(), e);
        }
        return task;
    }

    public void updateStatus(Long taskId, String newStatus) {
        if (taskId == null || newStatus == null) {
            throw new IllegalArgumentException("Task ID and status are required");
        }

        String sql = "UPDATE Tasks SET status = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setLong(2, taskId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating task status for ID: " + taskId, e);
        }
    }

    public List<Task> findByAssigneeId(Long assigneeId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT t.*, p.id AS project_id, p.name AS project_name,
                   a.id AS assignee_id, a.username AS assignee_name,
                   c.id AS creator_id, c.username AS creator_name
            FROM Tasks t
            LEFT JOIN Projects p ON t.project_id = p.id
            LEFT JOIN Users a ON t.assignee_id = a.id
            JOIN Users c ON t.created_by = c.id
            WHERE t.assignee_id = ?
            ORDER BY t.created_at DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, assigneeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading tasks for assignee: " + assigneeId, e);
        }
        return tasks;
    }

    public List<Task> findByCreatorId(Long creatorId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT t.*, p.id AS project_id, p.name AS project_name,
                   a.id AS assignee_id, a.username AS assignee_name,
                   c.id AS creator_id, c.username AS creator_name
            FROM Tasks t
            LEFT JOIN Projects p ON t.project_id = p.id
            LEFT JOIN Users a ON t.assignee_id = a.id
            JOIN Users c ON t.created_by = c.id
            WHERE t.created_by = ?
            ORDER BY t.created_at DESC
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, creatorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading tasks for creator: " + creatorId, e);
        }
        return tasks;
    }

    public Task mapRowToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setStatus(rs.getString("status"));
        task.setPriority(rs.getString("priority"));

        Date dueSqlDate = rs.getDate("due_date");
        if (dueSqlDate != null) {
            task.setDueDate(dueSqlDate.toLocalDate());
        }

        // Project
        Long projectId = (Long) rs.getObject("project_id");
        if (projectId != null) {
            Project project = new Project();
            project.setId(projectId);
            project.setName(rs.getString("project_name"));
            task.setProject(project);
        }

        // Assignee
        Long assigneeId = (Long) rs.getObject("assignee_id");
        if (assigneeId != null) {
            User assignee = new User();
            assignee.setId(assigneeId);
            assignee.setUsername(rs.getString("assignee_name"));
            task.setAssignee(assignee);
        }

        // Creator
        User creator = new User();
        creator.setId(rs.getLong("creator_id"));
        creator.setUsername(rs.getString("creator_name"));
        task.setCreatedBy(creator);

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            task.setCreatedAt(createdTs.toLocalDateTime());
        }

        return task;
    }
}