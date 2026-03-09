package com.taskmanagement.controller;

import java.io.IOException;
import java.util.List;

import com.taskmanagement.App;
import com.taskmanagement.model.Workspace;
import com.taskmanagement.service.WorkspaceService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.CurrentWorkspace;
import com.taskmanagement.utils.UIUtils;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class WorkspaceSelectionController {

    @FXML private ListView<Workspace> workspaceList;
    @FXML private Label helperLabel;

    private final WorkspaceService workspaceService = new WorkspaceService();

    @FXML
    public void initialize() {
        Long userId = CurrentUser.getId();
        List<Workspace> userWorkspaces = workspaceService.getWorkspacesForUser(userId);
        workspaceList.setItems(FXCollections.observableArrayList(userWorkspaces));

        if (!userWorkspaces.isEmpty()) {
            workspaceList.getSelectionModel().selectFirst();
            helperLabel.setText("Select a workspace to continue");
        } else {
            helperLabel.setText("No workspace assigned. You can continue and create one.");
        }
    }

    @FXML
    public void continueToDashboard() {
        Workspace selected = workspaceList.getSelectionModel().getSelectedItem();
        CurrentWorkspace.set(selected);
        try {
            App.setRoot("main/WorkspaceDashboard");
        } catch (IOException e) {
            UIUtils.showError("Navigation Error", "Failed to open workspace dashboard: " + e.getMessage());
        }
    }

    @FXML
    public void logout() {
        CurrentWorkspace.clear();
        CurrentUser.clear();
        try {
            App.setRoot("auth/LoginView");
        } catch (IOException e) {
            UIUtils.showError("Navigation Error", "Failed to open login page: " + e.getMessage());
        }
    }
}
