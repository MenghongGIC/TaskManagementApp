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

    // SQL Queries
    // Create new project
    private static final String SQL_INSERT = """
            INSERT INTO Projects (name, description, color, created_by)
            VALUES (?, ?, ?, ?)
            """;
    
    // Retrieves all projects along with their creator information.
    private static final String SQL_SELECT_ALL = """
            SELECT p.*, u.id AS creator_id, u.username AS creator_name
            FROM Projects p
            JOIN Users u ON p.created_by = u.id
            ORDER BY p.created_at DESC
            """;
    
    // Retrieves a project by its ID along with creator information.
    private static final String SQL_SELECT_BY_ID = """
            SELECT p.*, u.id AS creator_id, u.username AS creator_name
            FROM Projects p
            JOIN Users u ON p.created_by = u.id
            WHERE p.id = ?
            """;
    
    // Deletes a project by ID.
    private static final String SQL_DELETE = "DELETE FROM Projects WHERE id = ?";
    
    // Updates an existing project's details: name, description, color.
    private static final String SQL_UPDATE = """
            UPDATE Projects SET name = ?, description = ?, color = ? WHERE id = ?
            """;
    
    // Column Names
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_COLOR = "color";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_CREATOR_ID = "creator_id";
    private static final String COL_CREATOR_NAME = "creator_name";
    
    private static final String ERR_SAVE = "Error saving project: ";
    private static final String ERR_LOAD_ALL = "Error loading projects";
    private static final String ERR_FIND_BY_ID = "Error finding project by ID: ";
    private static final String ERR_DELETE = "Error deleting project ID: ";
    private static final String ERR_UPDATE = "Error updating project ID: ";

    private final TaskRepository taskRepository = new TaskRepository();
    
    public Project save(Project project) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

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
            throw new RuntimeException(ERR_SAVE + project.getName(), e);
        }
        return project;
    }

    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                projects.add(mapRowToProject(rs));
            }
            loadTasksForProjects(projects);

        } catch (SQLException e) {
            throw new RuntimeException(ERR_LOAD_ALL, e);
        }
        return projects;
    }

    public Project findById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Project project = mapRowToProject(rs);
                    List<Task> projectTasks = taskRepository.findByProjectId(id);
                    if (projectTasks != null && !projectTasks.isEmpty()) {
                        projectTasks.forEach(project::addTask);
                    }
                    return project;
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

    public void update(Project project) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setString(3, project.getColor());
            pstmt.setLong(4, project.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(ERR_UPDATE + project.getId(), e);
        }
    }

    private Project mapRowToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong(COL_ID));
        project.setName(rs.getString(COL_NAME));
        project.setDescription(rs.getString(COL_DESCRIPTION));
        project.setColor(rs.getString(COL_COLOR));

        User creator = new User();
        creator.setId(rs.getLong(COL_CREATOR_ID));
        creator.setUsername(rs.getString(COL_CREATOR_NAME));
        project.setCreatedBy(creator);

        Timestamp ts = rs.getTimestamp(COL_CREATED_AT);
        if (ts != null) {
            project.setCreatedAt(ts.toLocalDateTime());
        }

        return project;
    }
    private void loadTasksForProjects(List<Project> projects) {
        for (Project project : projects) {
            List<Task> projectTasks = taskRepository.findByProjectId(project.getId());
            if (projectTasks != null && !projectTasks.isEmpty()) {
                projectTasks.forEach(project::addTask);
            }
        }
    }
}