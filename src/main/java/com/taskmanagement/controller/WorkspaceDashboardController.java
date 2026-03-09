package com.taskmanagement.controller;

import java.io.IOException;
import java.util.List;

import com.taskmanagement.App;
import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.model.Project;
import com.taskmanagement.model.Workspace;
import com.taskmanagement.service.ActivityLogService;
import com.taskmanagement.service.WorkspaceService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.CurrentWorkspace;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class WorkspaceDashboardController {

    @FXML private Label currentUserLabel;
    @FXML private StackPane contentArea;
    @FXML private VBox workspaceSummaryPane;

    @FXML private Label workspaceNameValue;
    @FXML private Label totalProjectsValue;
    @FXML private Label totalTeamsValue;
    @FXML private Label totalMembersValue;
    @FXML private Label totalTasksValue;
    @FXML private ListView<String> recentActivityList;

    private final WorkspaceService workspaceService = new WorkspaceService();
    private Workspace currentWorkspace;

    @FXML
    public void initialize() {
        currentUserLabel.setText(CurrentUser.getUsername());
        resolveCurrentWorkspace();
        showWorkspace();
    }

    @FXML
    public void showWorkspace() {
        contentArea.getChildren().setAll(workspaceSummaryPane);
        loadWorkspace();
    }

    @FXML
    public void showProjects() {
        loadView("/com/taskmanagement/fxml/project/ProjectList.fxml");
    }

    @FXML
    public void showTeams() {
        loadView("/com/taskmanagement/fxml/team/TeamListView.fxml");
    }

    @FXML
    public void showTasks() {
        loadView("/com/taskmanagement/fxml/main/KanbanBoard.fxml");
    }

    @FXML
    public void showActivity() {
        loadView("/com/taskmanagement/fxml/main/ActivityView.fxml");
    }

    @FXML
    public void showSettings() {
        VBox placeholder = new VBox(8);
        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        Label message = new Label("Settings page is ready for integration.");
        placeholder.getChildren().addAll(title, message);
        contentArea.getChildren().setAll(placeholder);
    }

    @FXML
    public void logout() {
        CurrentWorkspace.clear();
        CurrentUser.clear();
        try {
            App.setRoot("auth/LoginView");
        } catch (IOException e) {
            Label error = new Label("Failed to logout: " + e.getMessage());
            contentArea.getChildren().setAll(error);
        }
    }

    public void loadWorkspace() {
        resolveCurrentWorkspace();

        if (currentWorkspace == null) {
            workspaceNameValue.setText("No workspace selected");
            totalProjectsValue.setText("0");
            totalTeamsValue.setText("0");
            totalMembersValue.setText("0");
            totalTasksValue.setText("0");
            recentActivityList.getItems().setAll("No workspace yet. Create one in Workspace module.");
            return;
        }

        workspaceNameValue.setText(currentWorkspace.getWorkspaceName());
        loadProjects();
        loadTeams();
        loadMembers();
        loadActivity();
        loadTasks();
    }

    public void loadProjects() {
        int totalProjects = currentWorkspace != null ? currentWorkspace.getProjects().size() : 0;
        totalProjectsValue.setText(String.valueOf(totalProjects));
    }

    public void loadTeams() {
        int totalTeams = currentWorkspace != null ? currentWorkspace.getTeams().size() : 0;
        totalTeamsValue.setText(String.valueOf(totalTeams));
    }

    public void loadMembers() {
        int totalMembers = currentWorkspace != null ? currentWorkspace.getMembers().size() : 0;
        totalMembersValue.setText(String.valueOf(totalMembers));
    }

    public void loadActivity() {
        if (currentWorkspace == null || currentWorkspace.getWorkspaceId() == null) {
            recentActivityList.getItems().setAll("No recent activity");
            return;
        }

        List<String> workspaceActivity = ActivityLogService.getEntityHistory("WORKSPACE", currentWorkspace.getWorkspaceId())
                .stream()
                .map(Object::toString)
                .toList();

        if (!workspaceActivity.isEmpty()) {
            recentActivityList.getItems().setAll(workspaceActivity);
            return;
        }

        List<String> recentGlobalActivity = ActivityLogService.getRecentActivities(10)
                .stream()
                .map(ActivityLog::toString)
                .toList();

        if (recentGlobalActivity.isEmpty()) {
            recentActivityList.getItems().setAll("No recent activity");
        } else {
            recentActivityList.getItems().setAll(recentGlobalActivity);
        }
    }

    private void loadTasks() {
        if (currentWorkspace == null) {
            totalTasksValue.setText("0");
            return;
        }

        int totalTasks = currentWorkspace.getProjects().stream()
                .map(Project::getTasks)
            .mapToInt(java.util.Set::size)
                .sum();
        totalTasksValue.setText(String.valueOf(totalTasks));
    }

    private void resolveCurrentWorkspace() {
        Workspace selected = CurrentWorkspace.get();
        if (selected != null) {
            currentWorkspace = workspaceService.getWorkspaceById(selected.getWorkspaceId());
            if (currentWorkspace != null) {
                CurrentWorkspace.set(currentWorkspace);
                return;
            }
        }

        Long userId = CurrentUser.getId();
        if (userId <= 0) {
            currentWorkspace = null;
            return;
        }

        List<Workspace> userWorkspaces = workspaceService.getWorkspacesForUser(userId);
        currentWorkspace = userWorkspaces.isEmpty() ? null : userWorkspaces.getFirst();
        CurrentWorkspace.set(currentWorkspace);
    }

    private void loadView(String path) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            Label error = new Label("Failed to load view: " + path + "\n" + e.getMessage());
            error.setWrapText(true);
            contentArea.getChildren().setAll(error);
        }
    }
}
