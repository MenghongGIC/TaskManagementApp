package com.taskmanager.dao;

import com.taskmanager.database.DBConnection;
import com.taskmanager.model.Project;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects ORDER BY name";

        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultset = statement.executeQuery(sql)) {

            while (resultset.next()) {
                Project project = new Project();
                project.setId(resultset.getInt("id"));
                project.setName(resultset.getString("name"));
                project.setDescription(resultset.getString("description"));
                project.setColor(resultset.getString("color"));
                project.setWorkspaceId(resultset.getInt("workspace_id"));
                projects.add(project);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return projects;
    }
}