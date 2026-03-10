package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.utils.CurrentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private Label usernameLabel;
    @FXML private StackPane contentArea;
    
    private static MainController instance;

    @FXML
    public void initialize() {
        instance = this;
        if (usernameLabel != null) {
            String username = CurrentUser.getUsername();
            usernameLabel.setText(username == null ? "Guest" : username);
        }
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/com/taskmanagement/fxml/main/Dashboard.fxml", null);
    }

    @FXML
    private void showTasks() {
        loadView("/com/taskmanagement/fxml/task/TaskList.fxml", null);
    }

    @FXML
    private void showKanban() {
        loadView("/com/taskmanagement/fxml/main/KanbanBoard.fxml", null);
    }

    @FXML
    private void showUserManagement() {
        if (!CurrentUser.isAdmin()) {
            Label error = new Label("Access Denied: Admin privileges required");
            error.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            contentArea.getChildren().setAll(error);
            return;
        }
        loadView("/com/taskmanagement/fxml/main/UserManagement.fxml", null);
    }

    @FXML
    private void logout() {
        CurrentUser.clear();
        try {
            App.setRoot("auth/LoginView");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String path, String filter) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent view = loader.load();

            // Pass filter to TaskController if it's a task list
            if (path.contains("TaskList.fxml") && filter != null) {
                TaskController controller = loader.getController();
                controller.applyFilter(filter);
            }

            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            String details = buildErrorDetails(e);
            Label error = new Label("Failed to load view: " + path + "\n" + details);
            error.setWrapText(true);
            contentArea.getChildren().setAll(error);
            e.printStackTrace();
        }
    }

    public void loadViewWithFilter(String path, String filter) {
        loadView(path, filter);
    }

    public static MainController getInstance() {
        return instance;
    }

    public StackPane getContentArea() {
        return contentArea;
    }

    private String buildErrorDetails(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error";
        }

        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }

        String message = root.getMessage();
        if (message == null || message.isBlank()) {
            message = throwable.getMessage();
        }
        if (message == null || message.isBlank()) {
            message = root.getClass().getSimpleName();
        }

        return root.getClass().getSimpleName() + ": " + message;
    }
}
