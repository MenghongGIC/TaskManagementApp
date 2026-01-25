package com.taskmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;

public class ProjectRepository extends BaseRepository {

    private final TaskRepository taskRepository = new TaskRepository();
    
    public Project save(Project project) {
        String sql = """
            INSERT INTO Projects (name, description, color, created_by)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setString(3, project.getColor());
            pstmt.setLong(4, project.getCreatedBy().getId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    project.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving project: " + project.getName(), e);
        }
        return project;
    }

    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        String sql = """
            SELECT p.*, u.id AS creator_id, u.username AS creator_name
            FROM Projects p
            JOIN Users u ON p.created_by = u.id
            ORDER BY p.created_at DESC
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(mapRowToProject(rs));
            }

            // Load tasks for each project
            for (Project project : projects) {
                List<Task> projectTasks = taskRepository.findByProjectId(project.getId());
                if (projectTasks != null && !projectTasks.isEmpty()) {
                    for (Task task : projectTasks) {
                        project.addTask(task);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading projects", e);
        }
        return projects;
    }

    public Project findById(Long id) {
        String sql = """
            SELECT p.*, u.id AS creator_id, u.username AS creator_name
            FROM Projects p
            JOIN Users u ON p.created_by = u.id
            WHERE p.id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Project project = mapRowToProject(rs);
                    project.getTasks().addAll(taskRepository.findByProjectId(id));
                    return project;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding project by ID: " + id, e);
        }
        return null;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM Projects WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project ID: " + id, e);
        }
    }

    public void update(Project project) {
        String sql = """
            UPDATE Projects SET name = ?, description = ?, color = ? WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setString(3, project.getColor());
            pstmt.setLong(4, project.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating project ID: " + project.getId(), e);
        }
    }

    private Project mapRowToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong("id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setColor(rs.getString("color"));

        User creator = new User();
        creator.setId(rs.getLong("creator_id"));
        creator.setUsername(rs.getString("creator_name"));
        project.setCreatedBy(creator);

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) project.setCreatedAt(ts.toLocalDateTime());

        return project;
    }
}