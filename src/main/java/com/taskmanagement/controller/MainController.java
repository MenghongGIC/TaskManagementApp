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

    @FXML
    public void initialize() {
        if (usernameLabel != null) {
            String username = CurrentUser.getUsername();
            usernameLabel.setText(username == null ? "Guest" : username);
        }
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/com/taskmanagement/fxml/main/Dashboard.fxml");
    }

    @FXML
    private void showTasks() {
        loadView("/com/taskmanagement/fxml/task/TaskList.fxml");
    }

    @FXML
    private void showKanban() {
        loadView("/com/taskmanagement/fxml/main/KanbanBoard.fxml");
    }

    @FXML
    private void showProjects() {
        loadView("/com/taskmanagement/fxml/project/ProjectList.fxml");
    }

    @FXML
    private void showTeam() {
        loadView("/com/taskmanagement/fxml/team/TeamListView.fxml");
    }

    @FXML
    private void showActivity() {
        loadView("/com/taskmanagement/fxml/main/ActivityView.fxml");
    }

    @FXML
    private void showWorkspace() {
        loadView("/com/taskmanagement/fxml/main/WorkspaceView.fxml");
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

    private void loadView(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            String details = buildErrorDetails(e);
            Label error = new Label("Failed to load view: " + path + "\n" + details);
            error.setWrapText(true);
            contentArea.getChildren().setAll(error);
            e.printStackTrace();
        }
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
