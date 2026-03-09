package com.taskmanagement.controller;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Team;
import com.taskmanagement.model.User;
import com.taskmanagement.model.Workspace;
import com.taskmanagement.service.ActivityLogService;
import com.taskmanagement.service.WorkspaceService;
import com.taskmanagement.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorkspaceController {

    @FXML private TextField workspaceNameField;
    @FXML private TextArea workspaceDescriptionField;
    @FXML private ComboBox<User> ownerCombo;
    @FXML private ListView<User> membersSelector;

    @FXML private Label workspaceNameLabel;
    @FXML private Label workspaceMembersLabel;
    @FXML private Label workspaceProjectsLabel;
    @FXML private Label workspaceTeamsLabel;

    @FXML private ListView<String> projectsList;
    @FXML private ListView<String> teamsList;
    @FXML private ListView<String> membersList;
    @FXML private ListView<String> activityList;

    private final WorkspaceService workspaceService = new WorkspaceService();
    private Workspace currentWorkspace;

    @FXML
    public void initialize() {
        List<User> users = workspaceService.getAvailableUsers();
        ownerCombo.setItems(FXCollections.observableArrayList(users));
        membersSelector.setItems(FXCollections.observableArrayList(users));
        membersSelector.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        List<Workspace> existing = workspaceService.getAllWorkspaces();
        if (!existing.isEmpty()) {
            loadWorkspace(existing.getFirst());
        }
    }

    @FXML
    public void createWorkspace() {
        try {
            User owner = ownerCombo.getValue();
            List<Long> memberIds = membersSelector.getSelectionModel().getSelectedItems().stream()
                    .map(User::getId)
                    .toList();

            currentWorkspace = workspaceService.createWorkspace(
                    workspaceNameField.getText(),
                    workspaceDescriptionField.getText(),
                    owner != null ? owner.getId() : null,
                    memberIds
            );

            loadWorkspace(currentWorkspace);
            UIUtils.showSuccess("Workspace created", currentWorkspace.getWorkspaceName());
        } catch (Exception e) {
            UIUtils.showError("Create workspace failed", e.getMessage());
        }
    }

    public void loadWorkspace() {
        List<Workspace> all = workspaceService.getAllWorkspaces();
        if (all.isEmpty()) {
            UIUtils.showWarning("No workspace", "Create a workspace first");
            return;
        }
        loadWorkspace(all.getFirst());
    }

    public void loadWorkspace(Workspace workspace) {
        this.currentWorkspace = workspace;
        if (workspace == null) {
            return;
        }

        workspaceNameLabel.setText(workspace.getWorkspaceName());
        workspaceMembersLabel.setText(String.valueOf(workspace.getMembers().size()));
        loadProjects();
        loadTeams();
        loadMembers();
        loadActivity();
    }

    public void loadProjects() {
        if (currentWorkspace == null) {
            workspaceProjectsLabel.setText("0");
            projectsList.getItems().clear();
            return;
        }

        workspaceProjectsLabel.setText(String.valueOf(currentWorkspace.getProjects().size()));
        projectsList.getItems().setAll(currentWorkspace.getProjects().stream().map(Project::getName).toList());
    }

    public void loadTeams() {
        if (currentWorkspace == null) {
            workspaceTeamsLabel.setText("0");
            teamsList.getItems().clear();
            return;
        }

        workspaceTeamsLabel.setText(String.valueOf(currentWorkspace.getTeams().size()));
        teamsList.getItems().setAll(currentWorkspace.getTeams().stream().map(Team::getTeamName).toList());
    }

    public void loadMembers() {
        if (currentWorkspace == null) {
            workspaceMembersLabel.setText("0");
            membersList.getItems().clear();
            return;
        }

        workspaceMembersLabel.setText(String.valueOf(currentWorkspace.getMembers().size()));
        membersList.getItems().setAll(currentWorkspace.getMembers().stream().map(User::getUsername).toList());
    }

    public void loadActivity() {
        if (currentWorkspace == null || currentWorkspace.getWorkspaceId() == null) {
            activityList.getItems().clear();
            return;
        }

        activityList.getItems().setAll(
                ActivityLogService.getEntityHistory("WORKSPACE", currentWorkspace.getWorkspaceId()).stream()
                        .map(Object::toString)
                        .toList()
        );
    }

    @FXML
    public void addMember() {
        if (!requireWorkspace()) {
            return;
        }

        List<User> users = workspaceService.getAvailableUsers();
        if (users.isEmpty()) {
            return;
        }

        ChoiceDialog<User> dialog = new ChoiceDialog<>(users.getFirst(), users);
        dialog.setTitle("Add Member");
        dialog.setHeaderText("Add member to workspace");
        dialog.setContentText("User:");

        Optional<User> selected = dialog.showAndWait();
        selected.ifPresent(user -> {
            try {
                workspaceService.addMember(currentWorkspace.getWorkspaceId(), user.getId());
                loadWorkspace(workspaceService.getWorkspaceById(currentWorkspace.getWorkspaceId()));
            } catch (Exception e) {
                UIUtils.showError("Add member failed", e.getMessage());
            }
        });
    }

    @FXML
    public void removeMember() {
        if (!requireWorkspace()) {
            return;
        }

        List<User> members = new ArrayList<>(currentWorkspace.getMembers());
        if (members.isEmpty()) {
            return;
        }

        ChoiceDialog<User> dialog = new ChoiceDialog<>(members.getFirst(), members);
        dialog.setTitle("Remove Member");
        dialog.setHeaderText("Remove member from workspace");
        dialog.setContentText("User:");

        Optional<User> selected = dialog.showAndWait();
        selected.ifPresent(user -> {
            try {
                workspaceService.removeMember(currentWorkspace.getWorkspaceId(), user.getId());
                loadWorkspace(workspaceService.getWorkspaceById(currentWorkspace.getWorkspaceId()));
            } catch (Exception e) {
                UIUtils.showError("Remove member failed", e.getMessage());
            }
        });
    }

    @FXML
    public void addProject() {
        if (!requireWorkspace()) {
            return;
        }

        List<Project> projects = workspaceService.getAvailableProjects();
        if (projects.isEmpty()) {
            UIUtils.showWarning("No projects", "No projects available");
            return;
        }

        ChoiceDialog<Project> dialog = new ChoiceDialog<>(projects.getFirst(), projects);
        dialog.setTitle("Add Project");
        dialog.setHeaderText("Add project to workspace");
        dialog.setContentText("Project:");

        Optional<Project> selected = dialog.showAndWait();
        selected.ifPresent(project -> {
            try {
                workspaceService.addProject(currentWorkspace.getWorkspaceId(), project.getId());
                loadWorkspace(workspaceService.getWorkspaceById(currentWorkspace.getWorkspaceId()));
            } catch (Exception e) {
                UIUtils.showError("Add project failed", e.getMessage());
            }
        });
    }

    @FXML
    public void viewProjects() {
        if (!requireWorkspace()) {
            return;
        }
        projectsList.getItems().setAll(currentWorkspace.getProjects().stream().map(Project::getName).toList());
    }

    private boolean requireWorkspace() {
        if (currentWorkspace == null) {
            UIUtils.showWarning("No workspace", "Create or load a workspace first");
            return false;
        }
        return true;
    }
}
