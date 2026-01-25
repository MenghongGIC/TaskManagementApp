package com.taskmanagement.controller;

import com.taskmanagement.App;
import com.taskmanagement.model.Project;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.UIUtils;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.List;
import java.util.Optional;
public class ProjectListViewController {
    
    // Button Styles
    private static final String VIEW_BUTTON_STYLE = "-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #3498db; -fx-text-fill: white;";
    private static final String EDIT_BUTTON_STYLE = "-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #f39c12; -fx-text-fill: white;";
    private static final String DELETE_BUTTON_STYLE = "-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #e74c3c; -fx-text-fill: white;";
    private static final String ACTIONS_HBOX_STYLE = "-fx-padding: 5;";
    
    // Messages
    private static final String MSG_LOADED_PROJECTS = "✓ Loaded %d projects";
    private static final String MSG_ERROR_LOADING = "✗ Error loading projects";
    private static final String MSG_DELETED_SUCCESS = "✓ Project deleted successfully";
    private static final String MSG_DELETE_CONFIRM = "Are you sure you want to delete '%s'?";
    private static final String MSG_EDIT_COMING_SOON = "Edit feature coming soon for: ";
    private static final String MSG_CREATE_COMING_SOON = "Create project feature coming soon";
    private static final String MSG_NAVIGATION_ERROR = "Failed to navigate to project: ";
    private static final String MSG_DELETE_ERROR = "Failed to delete project: ";
    private static final String MSG_LOAD_ERROR = "Failed to load projects: ";
    
    // Labels
    private static final String TITLE_DELETE = "Delete Project";
    private static final String TITLE_ERROR = "Error";
    private static final String TITLE_INFO = "Info";
    
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

    @FXML
    public void initialize() {
        projectService = new ProjectService();
        
        if (projectsTable != null) {
            setupTableColumns();
            loadProjects();
        }
    }
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        taskCountColumn.setCellValueFactory(cellData -> {
            int count = cellData.getValue().getTasks() != null ? 
                       cellData.getValue().getTasks().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });
        setupActionsColumn();
        projectsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Project selected = projectsTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    navigateToProject(selected);
                }
            }
        });
    }
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<Project, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : createActionButtonsBox(getTableView().getItems().get(getIndex())));
            }
        });
    }
    
    private HBox createActionButtonsBox(Project project) {
        Button viewBtn = createActionButton("👁️ View", VIEW_BUTTON_STYLE, 
            e -> handleViewProject(project));
        Button editBtn = createActionButton("✏️ Edit", EDIT_BUTTON_STYLE, 
            e -> handleEditProject(project));
        Button deleteBtn = createActionButton("🗑️ Delete", DELETE_BUTTON_STYLE, 
            e -> handleDeleteProject(project));
        
        HBox hbox = new HBox(5, viewBtn, editBtn, deleteBtn);
        hbox.setAlignment(Pos.CENTER);
        hbox.setStyle(ACTIONS_HBOX_STYLE);
        return hbox;
    }
    
    private Button createActionButton(String text, String style, EventHandler<ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setStyle(style);
        btn.setOnAction(handler);
        return btn;
    }
    private void loadProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            projectService.setProjects(projects);
            projectsTable.setItems(projectService.getProjects());
            updateStatus(String.format(MSG_LOADED_PROJECTS, projects.size()));
        } catch (Exception e) {
            showErrorAlert(TITLE_ERROR, MSG_LOAD_ERROR + e.getMessage());
            updateStatus(MSG_ERROR_LOADING);
        }
    }
    private void handleViewProject(Project project) {
        navigateToProject(project);
    }
    
    private void navigateToProject(Project project) {
        projectService.selectProject(project);
        try {
            App.setRoot("main/ProjectDetailView");
        } catch (Exception e) {
            showErrorAlert(TITLE_ERROR, MSG_NAVIGATION_ERROR + e.getMessage());
        }
    }
    
    private void handleEditProject(Project project) {
        showInfoAlert(TITLE_INFO, MSG_EDIT_COMING_SOON + project.getName());
    }
    
    private void handleDeleteProject(Project project) {
        Optional<ButtonType> result = showConfirmation(TITLE_DELETE,
            String.format(MSG_DELETE_CONFIRM, project.getName()));
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                projectService.deleteProject(project.getId());
                loadProjects();
                updateStatus(MSG_DELETED_SUCCESS);
            } catch (Exception e) {
                showErrorAlert(TITLE_ERROR, MSG_DELETE_ERROR + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleNewProject() {
        showInfoAlert(TITLE_INFO, MSG_CREATE_COMING_SOON);
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
    
    private void showErrorAlert(String title, String message) {
        UIUtils.showError(title, message);
    }
    
    private void showInfoAlert(String title, String message) {
        UIUtils.showSuccess(title, message);
    }
    
    private Optional<ButtonType> showConfirmation(String title, String content) {
        return Optional.of(UIUtils.showCustomConfirmation(title, null, content) ? ButtonType.OK : ButtonType.CANCEL);
    }
}
