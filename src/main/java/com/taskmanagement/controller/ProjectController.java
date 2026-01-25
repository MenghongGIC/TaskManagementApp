package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Optional;

import com.taskmanagement.App;
import com.taskmanagement.model.Project;
import com.taskmanagement.model.User;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.DateUtils;

public class ProjectController implements TaskAwareController {
    
    @FXML private TextField searchField;
    @FXML private TableView<Project> projectsTable;
    @FXML private VBox detailsPanel;
    @FXML private TableColumn<Project, Long> idColumn;
    @FXML private TableColumn<Project, String> nameColumn;
    @FXML private TableColumn<Project, String> descriptionColumn;
    @FXML private TableColumn<Project, Integer> tasksCountColumn;
    @FXML private TableColumn<Project, Void> actionsColumn;
    @FXML private Label statusLabel;
    
    private ProjectService projectService;
    private ObservableList<Project> projectsList;
    private FilteredList<Project> filteredList;
    private MainLayoutController mainLayoutController;

    @Override
    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }

    @FXML
    public void initialize() {
        projectService = new ProjectService();
        setupTableColumns();
        loadProjects();
        setupSearchListener();
        setupTableRowClickHandler();
    }
    
    /**
     * Setup all table column bindings
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Custom Tasks Count Column
        tasksCountColumn.setCellValueFactory(cellData -> {
            int taskCount = cellData.getValue().getTasks() != null ? cellData.getValue().getTasks().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(taskCount).asObject();
        });
        
        // Setup Actions Column with Edit/Delete/View buttons
        setupActionsColumn();
    }
    
    /**
     * Setup the Actions column with Edit, Delete, and View buttons
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
                
                viewBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 3;");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 3;");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 3;");
                
                viewBtn.setOnAction(event -> handleViewProject(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(event -> handleEditProject(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> handleDeleteProject(getTableView().getItems().get(getIndex())));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }
    
    /**
     * Setup row click handler to open project details
     */
    private void setupTableRowClickHandler() {
        projectsTable.setRowFactory(param -> {
            TableRow<Project> row = new TableRow<Project>() {
                @Override
                protected void updateItem(Project item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setCursor(javafx.scene.Cursor.HAND);
                        setStyle("-fx-cursor: hand;");
                    } else {
                        setStyle("");
                    }
                }
            };
            
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    loadProjectDetailsPanel(row.getItem());
                }
            });
            
            return row;
        });
    }
    
    @FXML
    private void clearSearch() {
        searchField.clear();
        filteredList.setPredicate(p -> true);
        updateStatus();
    }
    
    @FXML
    private void handleAddProject() {
        System.out.println("➕ Opening create project dialog");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/dialog/CreateProjectView.fxml"));
            javafx.scene.layout.BorderPane dialogRoot = loader.load();
            CreateProjectController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Project");
            dialogStage.setScene(new Scene(dialogRoot, 600, 500));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(projectsTable.getScene().getWindow());
            
            // Set dialog stage for closing
            controller.setDialogStage(dialogStage);
            
            // Set callback to refresh projects list
            controller.setOnProjectCreated(() -> {
                System.out.println("🔄 Refreshing projects list after project creation");
                loadProjects();
                updateStatus();
            });
            
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            System.err.println("❌ Error opening create project dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error opening create project dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadProjects();
        updateStatus();
    }
    
    private void handleViewProject(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/main/ProjectDetailView.fxml"));
            BorderPane detailView = loader.load();
            ProjectDetailController controller = loader.getController();
            controller.setProject(project);
            
            Stage detailStage = new Stage();
            detailStage.setTitle("Project Details - " + project.getName());
            detailStage.setScene(new Scene(detailView, 900, 700));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initOwner(projectsTable.getScene().getWindow());
            
            detailStage.showAndWait();
            
            // Refresh the project list after viewing
            loadProjects();
        } catch (Exception e) {
            showAlert("Error", "Failed to open project details: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void loadProjectDetailsPanel(Project project) {
        try {
            System.out.println("📂 Loading project details in right panel: " + project.getName());
            
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/main/ProjectDetailView.fxml"));
            BorderPane detailView = loader.load();
            ProjectDetailController controller = loader.getController();
            controller.setProject(project);
            controller.setProjectController(this);
            
            // Clear the details panel and add the project detail view
            detailsPanel.getChildren().clear();
            detailsPanel.getChildren().add(detailView);
            VBox.setVgrow(detailView, javafx.scene.layout.Priority.ALWAYS);
            
        } catch (Exception e) {
            System.err.println("❌ Error loading project details: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load project details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Clear the project details panel (called from ProjectDetailController when back button is clicked)
     */
    public void clearProjectDetails() {
        detailsPanel.getChildren().clear();
        Label placeholderLabel = new Label("Select a project to view details");
        placeholderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #95a5a6;");
        placeholderLabel.setAlignment(javafx.geometry.Pos.CENTER);
        detailsPanel.getChildren().add(placeholderLabel);
        VBox.setVgrow(placeholderLabel, javafx.scene.layout.Priority.ALWAYS);
    }
    
    private void handleEditProject(Project project) {
        showProjectDialog(project);
    }
    
    private void handleDeleteProject(Project project) {
        Optional<ButtonType> result = showConfirmation("Delete Project", 
            "Are you sure you want to delete '" + project.getName() + "'? This action cannot be undone.");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                projectService.deleteProject(project.getId());
                showAlert("Success", "Project deleted successfully!", Alert.AlertType.INFORMATION);
                loadProjects();
                updateStatus();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete project: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    /**
     * Show create/edit project dialog
     */
    private void showProjectDialog(Project existingProject) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(existingProject == null ? "Create New Project" : "Edit Project");
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20; -fx-font-size: 11px;");
        
        // Name field
        Label nameLabel = new Label("Project Name *");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter project name");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        
        // Description field
        Label descLabel = new Label("Description");
        TextArea descField = new TextArea();
        descField.setPromptText("Enter project description");
        descField.setPrefRowCount(4);
        descField.setWrapText(true);
        grid.add(descLabel, 0, 1);
        grid.add(descField, 1, 1);
        
        // Color field
        Label colorLabel = new Label("Color (Hex)");
        TextField colorField = new TextField();
        colorField.setPromptText("#3498db");
        colorField.setPrefWidth(100);
        grid.add(colorLabel, 0, 2);
        grid.add(colorField, 1, 2);
        
        // If editing, populate fields
        if (existingProject != null) {
            nameField.setText(existingProject.getName());
            descField.setText(existingProject.getDescription() != null ? existingProject.getDescription() : "");
            colorField.setText(existingProject.getColor() != null ? existingProject.getColor() : "#3498db");
        } else {
            colorField.setText("#3498db");
        }
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setStyle("-fx-padding: 10 0 0 0;");
        
        Button saveBtn = new Button("💾 Save");
        Button cancelBtn = new Button("❌ Cancel");
        
        saveBtn.setStyle("-fx-padding: 8 20; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");
        cancelBtn.setStyle("-fx-padding: 8 20; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5;");
        
        buttonBox.getChildren().addAll(saveBtn, cancelBtn);
        grid.add(buttonBox, 0, 3, 2, 1);
        
        // Handle save
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            String color = colorField.getText().trim();
            
            if (name.isEmpty()) {
                showAlert("Validation", "Project name is required!", Alert.AlertType.WARNING);
                return;
            }
            
            try {
                if (existingProject == null) {
                    // Create new
                    projectService.createProject(name, desc, color);
                    showAlert("Success", "Project created successfully!", Alert.AlertType.INFORMATION);
                } else {
                    // Update existing
                    projectService.updateProject(existingProject.getId(), name, desc, color);
                    showAlert("Success", "Project updated successfully!", Alert.AlertType.INFORMATION);
                }
                loadProjects();
                updateStatus();
                dialogStage.close();
            } catch (Exception ex) {
                showAlert("Error", "Failed to save project: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
        
        cancelBtn.setOnAction(e -> dialogStage.close());
        
        Scene scene = new Scene(grid, 500, 350);
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(projectsTable.getScene().getWindow());
        dialogStage.showAndWait();
    }
    
    private void loadProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            projectsList = FXCollections.observableArrayList(projects);
            filteredList = new FilteredList<>(projectsList, p -> true);
            projectsTable.setItems(filteredList);
            updateStatus();
        } catch (Exception e) {
            showAlert("Error", "Error loading projects: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProjects(newValue);
        });
    }
    
    private void filterProjects(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredList.setPredicate(p -> true);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredList.setPredicate(project -> 
                project.getName().toLowerCase().contains(lowerCaseFilter) ||
                (project.getDescription() != null && project.getDescription().toLowerCase().contains(lowerCaseFilter))
            );
        }
        updateStatus();
    }
    
    private void updateStatus() {
        int total = projectsList.size();
        int filtered = (int) filteredList.stream().count();
        if (filtered == total) {
            statusLabel.setText("✓ Showing " + total + " project(s)");
        } else {
            statusLabel.setText("✓ Showing " + filtered + " of " + total + " project(s)");
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
