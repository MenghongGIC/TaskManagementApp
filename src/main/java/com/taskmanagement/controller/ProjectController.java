package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.Project;
import com.taskmanagement.service.ActivityLogService;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class ProjectController {

    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> nameColumn;
    @FXML private TableColumn<Project, String> descriptionColumn;
    @FXML private TableColumn<Project, String> colorColumn;
    @FXML private TableColumn<Project, String> taskCountColumn;
    @FXML private Label statusLabel;

    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDescription() == null ? "" : data.getValue().getDescription()
        ));
        colorColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getColor() == null ? "" : data.getValue().getColor()
        ));
        taskCountColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTaskCount())));

        loadProjects();

        projectTable.setRowFactory(tv -> {
            TableRow<Project> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openProjectDetails(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    private void handleCreate() {
        Optional<DialogProjectForm.ProjectPayload> payload = openProjectDialog("Create Project", null);
        payload.ifPresent(data -> {
            try {
                Project project = projectService.createProject(data.name(), data.description(), data.color());
                ActivityLogService.logProjectCreated(project.getId(), project.getName());
                loadProjects();
            } catch (Exception e) {
                UIUtils.showError("Error", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEdit() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showWarning("Info", "Select a project first");
            return;
        }

        Optional<DialogProjectForm.ProjectPayload> payload = new DialogProjectForm().open("Edit Project", selected);
        payload.ifPresent(data -> {
            try {
                selected.setName(data.name());
                selected.setDescription(data.description());
                selected.setColor(data.color());
                projectService.updateProject(selected);
                ActivityLogService.logProjectUpdated(selected.getId(), selected.getName(), "Updated from ProjectController");
                loadProjects();
            } catch (Exception e) {
                UIUtils.showError("Error", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showWarning("Info", "Select a project first");
            return;
        }

        if (!UIUtils.showCustomConfirmation("Delete Project", null, "Delete '" + selected.getName() + "'?")) {
            return;
        }

        try {
            projectService.deleteProject(selected.getId());
            ActivityLogService.logProjectDeleted(selected.getId(), selected.getName());
            loadProjects();
        } catch (Exception e) {
            UIUtils.showError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadProjects();
    }

    @FXML
    private void handleViewDetails() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showWarning("Info", "Select a project first");
            return;
        }
        openProjectDetails(selected);
    }

    private void openProjectDetails(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/project/ProjectDetailView.fxml"));
            VBox root = loader.load();
            ProjectDetailController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Project Details");
            dialog.setScene(new Scene(root, 760, 700));

            controller.setDialogStage(dialog);
            controller.setOnProjectChanged(this::loadProjects);
            controller.loadProject(project);

            dialog.showAndWait();
        } catch (Exception e) {
            UIUtils.showError("Error", "Failed to open project details: " + e.getMessage());
        }
    }

    private void loadProjects() {
        try {
            projectTable.getItems().setAll(projectService.getAllProjects());
            statusLabel.setText("Loaded " + projectTable.getItems().size() + " projects");
        } catch (Exception e) {
            projectTable.getItems().clear();
            statusLabel.setText("Failed to load projects: " + e.getMessage());
        }
    }

    private Optional<DialogProjectForm.ProjectPayload> openProjectDialog(String title, Project existing) {
        return new DialogProjectForm().open(title, existing);
    }
}
