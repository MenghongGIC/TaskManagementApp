package com.taskmanagement.controller;

import com.taskmanagement.service.ProjectService;
import com.taskmanagement.service.TaskService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label totalProjectsLabel;
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label overdueTasksLabel;

    private final ProjectService projectService = new ProjectService();
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        refresh();
    }

    @FXML
    private void refresh() {
        try {
            totalProjectsLabel.setText(String.valueOf(projectService.getAllProjects().size()));
            totalTasksLabel.setText(String.valueOf(taskService.getAllTasks().size()));
            completedTasksLabel.setText(String.valueOf(taskService.getCompletedTasks().size()));
            overdueTasksLabel.setText(String.valueOf(taskService.getOverdueTasks().size()));
        } catch (Exception e) {
            totalProjectsLabel.setText("0");
            totalTasksLabel.setText("0");
            completedTasksLabel.setText("0");
            overdueTasksLabel.setText("0");
        }
    }
}
