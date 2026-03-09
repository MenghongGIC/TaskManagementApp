package com.taskmanagement.controller;

import com.taskmanagement.model.ActivityLog;
import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.ActivityLogService;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProjectDetailController {

    @FXML private Label projectNameLabel;
    @FXML private Label projectDescriptionLabel;
    @FXML private Label projectOwnerLabel;
    @FXML private Label memberCountLabel;
    @FXML private Label taskCountLabel;
    @FXML private Label projectStatusLabel;
    @FXML private Label createdDateLabel;

    @FXML private ListView<String> projectTasksList;
    @FXML private ListView<String> projectMembersList;
    @FXML private ListView<String> activityLogList;

    private final ProjectService projectService = new ProjectService();
    private final TaskService taskService = new TaskService();
    private final UserService userService = new UserService();

    private Project project;
    private Stage dialogStage;
    private Runnable onProjectChanged;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOnProjectChanged(Runnable onProjectChanged) {
        this.onProjectChanged = onProjectChanged;
    }

    public void loadProject(Project project) {
        this.project = project;
        if (project == null) {
            return;
        }

        Project refreshed = projectService.getProjectById(project.getId());
        if (refreshed != null) {
            this.project = refreshed;
        }

        projectNameLabel.setText(nullSafe(this.project.getName()));
        projectDescriptionLabel.setText(nullSafe(this.project.getDescription()));
        projectOwnerLabel.setText(this.project.getCreatedBy() != null ? this.project.getCreatedBy().getUsername() : "-");
        taskCountLabel.setText(String.valueOf(this.project.getTaskCount()));
        projectStatusLabel.setText(this.project.getTaskCount() == 0 ? "Not Started" : "Active");
        createdDateLabel.setText(this.project.getCreatedAt() != null ? this.project.getCreatedAt().toLocalDate().toString() : "-");

        List<Task> tasks = taskService.getTasksByProject(this.project.getId());
        projectTasksList.getItems().setAll(tasks.stream().map(Task::toString).toList());

        Set<String> members = new LinkedHashSet<>();
        if (this.project.getCreatedBy() != null) {
            members.add(this.project.getCreatedBy().getUsername() + " (Owner)");
        }
        tasks.stream()
                .map(Task::getAssignee)
                .filter(user -> user != null && user.getUsername() != null)
                .map(User::getUsername)
                .forEach(members::add);
        projectMembersList.getItems().setAll(members);
        memberCountLabel.setText(String.valueOf(members.size()));

        activityLogList.getItems().setAll(
                ActivityLogService.getEntityHistory("PROJECT", this.project.getId()).stream()
                        .map(ActivityLog::toString)
                        .toList()
        );
    }

    @FXML
    private void handleEditProject() {
        if (project == null) {
            return;
        }

        DialogProjectForm helper = new DialogProjectForm();
        Optional<DialogProjectForm.ProjectPayload> payload = helper.open("Edit Project", project);
        payload.ifPresent(data -> {
            try {
                project.setName(data.name());
                project.setDescription(data.description());
                project.setColor(data.color());
                projectService.updateProject(project);
                ActivityLogService.logProjectUpdated(project.getId(), project.getName(), "Edited in project details");
                loadProject(project);
                if (onProjectChanged != null) {
                    onProjectChanged.run();
                }
            } catch (Exception e) {
                UIUtils.showError("Error", e.getMessage());
            }
        });
    }

    @FXML
    private void handleAddMember() {
        if (project == null) {
            return;
        }

        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                UIUtils.showWarning("No users", "No users available to add");
                return;
            }

            ChoiceDialog<User> dialog = new ChoiceDialog<>(users.getFirst(), users);
            dialog.setTitle("Add Member");
            dialog.setHeaderText("Add member to project");
            dialog.setContentText("User:");

            Optional<User> selected = dialog.showAndWait();
            selected.ifPresent(user -> {
                ActivityLogService.logActivity(
                        "PROJECT_MEMBER_ADDED",
                        "PROJECT",
                        project.getId(),
                        project.getName(),
                        "Member: " + user.getUsername()
                );
                loadProject(project);
            });
        } catch (Exception e) {
            UIUtils.showError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProject() {
        if (project == null) {
            return;
        }

        if (!UIUtils.showCustomConfirmation("Delete Project", "Confirm deletion", "Delete '" + project.getName() + "'?")) {
            return;
        }

        try {
            projectService.deleteProject(project.getId());
            ActivityLogService.logProjectDeleted(project.getId(), project.getName());
            if (onProjectChanged != null) {
                onProjectChanged.run();
            }
            closeDialog();
        } catch (Exception e) {
            UIUtils.showError("Error", e.getMessage());
        }
    }

    @FXML
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
