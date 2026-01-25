package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.Project;
import com.taskmanagement.service.ProjectService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * Displays list of all projects with details
 * Users can click a project to view its tasks
 */
public class ProjectListViewController {
    
    @FXML private VBox projectListContainer;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, Long> idColumn;
    @FXML private TableColumn<Project, String> nameColumn;
    @FXML private TableColumn<Project, String> descriptionColumn;
    @FXML private TableColumn<Project, Integer> taskCountColumn;
    @FXML private TableColumn<Project, Void> actionsColumn;
    
    private ProjectService projectService;
    private MainLayoutController mainLayoutController;

    @FXML
    public void initialize() {
        projectService = new ProjectService();
        
        if (projectsTable != null) {
            setupTableColumns();
            loadProjects();
        }
    }
    
    /**
     * Setup table columns for project list
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Task count column
        taskCountColumn.setCellValueFactory(cellData -> {
            int count = cellData.getValue().getTasks() != null ? 
                       cellData.getValue().getTasks().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });
        
        // Actions column
        setupActionsColumn();
        
        // Row click handler
        projectsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Project selected = projectsTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    navigateToProject(selected);
                }
            }
        });
    }
    
    /**
     * Setup actions column with View, Edit, Delete buttons
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<Project, Void>() {
            private final Button viewBtn = new Button("👁️ View");
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final HBox hbox = new HBox(5, viewBtn, editBtn, deleteBtn);
            
            {
                hbox.setAlignment(Pos.CENTER);
                hbox.setStyle("-fx-padding: 5;");
                
                viewBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #3498db; -fx-text-fill: white;");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #f39c12; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #e74c3c; -fx-text-fill: white;");
                
                viewBtn.setOnAction(e -> handleViewProject(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(e -> handleEditProject(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDeleteProject(getTableView().getItems().get(getIndex())));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }
    
    /**
     * Load all projects from database
     */
    private void loadProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            projectService.setProjects(projects);
            projectsTable.setItems(projectService.getProjects());
            updateStatus("✓ Loaded " + projects.size() + " projects");
        } catch (Exception e) {
            showAlert("Error", "Failed to load projects: " + e.getMessage(), Alert.AlertType.ERROR);
            updateStatus("✗ Error loading projects");
        }
    }
    
    /**
     * Navigate to project details and task view
     */
    private void handleViewProject(Project project) {
        navigateToProject(project);
    }
    
    private void navigateToProject(Project project) {
        projectService.selectProject(project);
        try {
            App.setRoot("main/ProjectDetailView");
        } catch (Exception e) {
            showAlert("Error", "Failed to navigate to project: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void handleEditProject(Project project) {
        showAlert("Info", "Edit feature coming soon for: " + project.getName(), Alert.AlertType.INFORMATION);
    }
    
    private void handleDeleteProject(Project project) {
        Optional<ButtonType> result = showConfirmation("Delete Project",
            "Are you sure you want to delete '" + project.getName() + "'?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                projectService.deleteProject(project.getId());
                loadProjects();
                updateStatus("✓ Project deleted successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to delete project: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleNewProject() {
        showAlert("Info", "Create project feature coming soon", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleRefresh() {
        loadProjects();
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}
