package com.taskmanagement.controller;

import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML private Label totalUsersLabel;
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label overdueTasksLabel;
    @FXML private Label inProgressTasksLabel;
    @FXML private Label pendingTasksLabel;

    private final UserService userService = new UserService();
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        refresh();
    }

    @FXML
    private void refresh() {
        try {
            totalUsersLabel.setText(String.valueOf(userService.getAllUsers().size()));
            totalTasksLabel.setText(String.valueOf(taskService.getAllTasks().size()));
            completedTasksLabel.setText(String.valueOf(taskService.getCompletedTasks().size()));
            overdueTasksLabel.setText(String.valueOf(taskService.getOverdueTasks().size()));
            inProgressTasksLabel.setText(String.valueOf(taskService.getInProgressTasks().size()));
            pendingTasksLabel.setText(String.valueOf(
                taskService.getTasksByStatus("To Do").size()
            ));
        } catch (Exception e) {
            resetLabels();
        }
    }

    private void resetLabels() {
        totalUsersLabel.setText("0");
        totalTasksLabel.setText("0");
        completedTasksLabel.setText("0");
        overdueTasksLabel.setText("0");
        inProgressTasksLabel.setText("0");
        pendingTasksLabel.setText("0");
    }

    @FXML
    private void navigateToAllUsers() {
        MainController main = MainController.getInstance();
        if (main != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/main/UserManagement.fxml"));
            try {
                Parent view = loader.load();
                main.getContentArea().getChildren().setAll(view);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void navigateToAllTasks() {
        MainController main = MainController.getInstance();
        if (main != null) {
            main.loadViewWithFilter("/com/taskmanagement/fxml/task/TaskList.fxml", "all");
        }
    }

    @FXML
    private void navigateToCompletedTasks() {
        MainController main = MainController.getInstance();
        if (main != null) {
            main.loadViewWithFilter("/com/taskmanagement/fxml/task/TaskList.fxml", "completed");
        }
    }

    @FXML
    private void navigateToOverdueTasks() {
        MainController main = MainController.getInstance();
        if (main != null) {
            main.loadViewWithFilter("/com/taskmanagement/fxml/task/TaskList.fxml", "overdue");
        }
    }

    @FXML
    private void navigateToInProgressTasks() {
        MainController main = MainController.getInstance();
        if (main != null) {
            main.loadViewWithFilter("/com/taskmanagement/fxml/task/TaskList.fxml", "in-progress");
        }
    }

    @FXML
    private void navigateToPendingTasks() {
        MainController main = MainController.getInstance();
        if (main != null) {
            main.loadViewWithFilter("/com/taskmanagement/fxml/task/TaskList.fxml", "pending");
        }
    }

    private void navigateToView(String fxmlPath, String filter) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Pass filter to controller if it's a task list
            if (fxmlPath.contains("TaskList.fxml") && filter != null) {
                TaskController controller = loader.getController();
                controller.applyFilter(filter);
            }

            // Get the root contentArea from the parent hierarchy
            StackPane contentArea = getContentArea();
            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StackPane getContentArea() {
        try {
            // Navigate through scene hierarchy to find contentArea
            Parent parent = totalUsersLabel.getScene().getRoot();
            while (parent != null) {
                if (parent instanceof StackPane) {
                    // Try to find contentArea by checking it has the right structure
                    StackPane pane = (StackPane) parent;
                    if (pane.getId() != null && pane.getId().equals("contentArea")) {
                        return pane;
                    }
                }
                // This is a simplified approach; in practice you might use a better way
                // to locate the contentArea, such as injecting the MainController
                parent = parent.getParent() != null ? (Parent) parent.getParent() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
