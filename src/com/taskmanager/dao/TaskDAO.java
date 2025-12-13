package com.taskmanager.dao;

import com.taskmanager.database.DBConnection;
import com.taskmanager.model.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public List<Task> getTasksByAssignee(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE assignee_id = ? ORDER BY due_date";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet resultset = ps.executeQuery();

            while (resultset.next()) {
                Task task = new Task();
                task.setId(resultset.getInt("id"));
                task.setTitle(resultset.getString("title"));
                task.setDescription(resultset.getString("description"));
                task.setStatus(resultset.getString("status"));
                task.setPriority(resultset.getString("priority"));
                task.setDueDate(resultset.getDate("due_date") != null ? resultset.getDate("due_date").toLocalDate() : null);
                task.setProjectId(resultset.getInt("project_id"));
                task.setAssigneeId(resultset.getObject("assignee_id") != null ? resultset.getInt("assignee_id") : null);
                task.setCreatedAt(resultset.getTimestamp("created_at") != null ?resultset.getTimestamp("created_at").toLocalDateTime() : null);
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return tasks;
    }

    public boolean updateStatus(int taskId, String newStatus) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, taskId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    public boolean createTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, status, priority, due_date, project_id, assignee_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus());
            ps.setString(4, task.getPriority());
            ps.setDate(5, task.getDueDate() != null ? Date.valueOf(task.getDueDate()) : null);
            ps.setInt(6, task.getProjectId());
            ps.setObject(7, task.getAssigneeId());

            if (ps.executeUpdate() > 0) {
                ResultSet resultset = ps.getGeneratedKeys();
                if (resultset.next()) {
                    task.setId(resultset.getInt(1));
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
}