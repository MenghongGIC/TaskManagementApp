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
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;

import com.taskmanagement.App;
import com.taskmanagement.model.Project;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.UIUtils;

public class ProjectController implements TaskAwareController {
    // FXML Paths
    private static final String CREATE_PROJECT_VIEW = "/com/taskmanagement/fxml/dialog/CreateProjectView.fxml";
    private static final String PROJECT_DETAIL_VIEW = "/com/taskmanagement/fxml/main/ProjectDetailView.fxml";
    
    // Dialog Titles
    private static final String TITLE_CREATE_PROJECT = "Create New Project";
    private static final String TITLE_EDIT_PROJECT = "Edit Project";
    private static final String TITLE_DELETE_CONFIRM = "Delete Project";
    private static final String TITLE_PROJECT_DETAILS = "Project Details - ";
    
    // Button Styles
    private static final String BTN_VIEW_STYLE = "-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 3;";
    private static final String BTN_EDIT_STYLE = "-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 3;";
    private static final String BTN_DELETE_STYLE = "-fx-font-size: 10px; -fx-padding: 5 10; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 3;";
    private static final String BTN_SAVE_STYLE = "-fx-padding: 8 20; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;";
    private static final String BTN_CANCEL_STYLE = "-fx-padding: 8 20; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5;";
    
    // Messages
    private static final String MSG_DELETE_CONFIRM = "Are you sure you want to delete '%s'? This action cannot be undone.";
    private static final String MSG_PROJECT_CREATED = "Project created successfully!";
    private static final String MSG_PROJECT_UPDATED = "Project updated successfully!";
    private static final String MSG_PROJECT_DELETED = "Project deleted successfully!";
    private static final String MSG_NAME_REQUIRED = "Project name is required!";
    private static final String MSG_PLACEHOLDER = "Select a project to view details";
    
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
    @FXML
    private void handleAddProject() {
        showProjectDialog(null);
    }
    
    @FXML
    private void handleRefresh() {
        loadProjects();
        updateStatus();
    }
    
    @FXML
    private void clearSearch() {
        searchField.clear();
        filteredList.setPredicate(p -> true);
        updateStatus();
    }
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        tasksCountColumn.setCellValueFactory(cellData -> {
            int taskCount = cellData.getValue().getTasks() != null ? cellData.getValue().getTasks().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(taskCount).asObject();
        });
        setupActionsColumn();
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<Project, Void>() {
            private final Button viewBtn = createActionButton("👁️ View", BTN_VIEW_STYLE);
            private final Button editBtn = createActionButton("✏️ Edit", BTN_EDIT_STYLE);
            private final Button deleteBtn = createActionButton("🗑️ Delete", BTN_DELETE_STYLE);
            private final HBox hbox = new HBox(5, viewBtn, editBtn, deleteBtn);
            
            {
                hbox.setAlignment(Pos.CENTER);
                hbox.setStyle("-fx-padding: 5;");
                
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
    
    private Button createActionButton(String text, String style) {
        Button btn = new Button(text);
        btn.setStyle(style);
        return btn;
    }
    
    private void setupTableRowClickHandler() {
        projectsTable.setRowFactory(param -> {
            TableRow<Project> row = new TableRow<Project>() {
                @Override
                protected void updateItem(Project item, boolean empty) {
                    super.updateItem(item, empty);
                    setStyle(!empty ? "-fx-cursor: hand;" : "");
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
    private void handleViewProject(Project project) {
        try {
            loadViewInDialog(PROJECT_DETAIL_VIEW, TITLE_PROJECT_DETAILS + project.getName(), 900, 700, 
                loader -> {
                    ProjectDetailController controller = loader.getController();
                    controller.setProject(project);
                });
            loadProjects();
        } catch (Exception e) {
            showErrorAlert("Failed to open project details", e);
        }
    }
    
    private void handleEditProject(Project project) {
        showProjectDialog(project);
    }
    
    private void handleDeleteProject(Project project) {
        String confirmMsg = String.format(MSG_DELETE_CONFIRM, project.getName());
        Optional<ButtonType> result = UIUtils.showCustomConfirmation(TITLE_DELETE_CONFIRM, null, confirmMsg)
            ? Optional.of(ButtonType.OK) : Optional.of(ButtonType.CANCEL);
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                projectService.deleteProject(project.getId());
                showSuccessAlert(MSG_PROJECT_DELETED);
                loadProjects();
                updateStatus();
            } catch (Exception e) {
                showErrorAlert("Failed to delete project", e);
            }
        }
    }
    
    private void loadProjectDetailsPanel(Project project) {
        try {
            System.out.println("📂 Loading project details: " + project.getName());
            FXMLLoader loader = new FXMLLoader(App.class.getResource(PROJECT_DETAIL_VIEW));
            BorderPane detailView = loader.load();
            ProjectDetailController controller = loader.getController();
            controller.setProject(project);
            controller.setProjectController(this);
            
            detailsPanel.getChildren().clear();
            detailsPanel.getChildren().add(detailView);
            VBox.setVgrow(detailView, Priority.ALWAYS);
        } catch (Exception e) {
            showErrorAlert("Failed to load project details", e);
        }
    }
    
    public void clearProjectDetails() {
        detailsPanel.getChildren().clear();
        Label placeholderLabel = new Label(MSG_PLACEHOLDER);
        placeholderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #95a5a6;");
        placeholderLabel.setAlignment(Pos.CENTER);
        detailsPanel.getChildren().add(placeholderLabel);
        VBox.setVgrow(placeholderLabel, Priority.ALWAYS);
    }
    
    
    private void showProjectDialog(Project existingProject) {
        boolean isEdit = existingProject != null;
        Stage dialogStage = new Stage();
        dialogStage.setTitle(isEdit ? TITLE_EDIT_PROJECT : TITLE_CREATE_PROJECT);
        
        GridPane grid = createProjectFormGrid();
        TextField nameField = (TextField) grid.getChildren().get(1);
        TextArea descField = (TextArea) grid.getChildren().get(3);
        TextField colorField = (TextField) grid.getChildren().get(5);
        
        if (isEdit) {
            nameField.setText(existingProject.getName());
            descField.setText(getOrEmpty(existingProject.getDescription()));
            colorField.setText(getOrDefault(existingProject.getColor(), "#3498db"));
        } else {
            colorField.setText("#3498db");
        }
        
        HBox buttonBox = createButtonBox(dialogStage, nameField, descField, colorField, existingProject);
        grid.add(buttonBox, 0, 3, 2, 1);
        
        Scene scene = new Scene(grid, 500, 350);
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(projectsTable.getScene().getWindow());
        dialogStage.showAndWait();
    }
    
    private GridPane createProjectFormGrid() {
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
        
        return grid;
    }
    
    private HBox createButtonBox(Stage dialogStage, TextField nameField, TextArea descField, 
                                 TextField colorField, Project existingProject) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setStyle("-fx-padding: 10 0 0 0;");
        
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        
        saveBtn.setStyle(BTN_SAVE_STYLE);
        cancelBtn.setStyle(BTN_CANCEL_STYLE);
        
        saveBtn.setOnAction(e -> handleProjectSave(dialogStage, nameField, descField, colorField, existingProject));
        cancelBtn.setOnAction(e -> dialogStage.close());
        
        buttonBox.getChildren().addAll(saveBtn, cancelBtn);
        return buttonBox;
    }
    
    private void handleProjectSave(Stage dialogStage, TextField nameField, TextArea descField, 
                                   TextField colorField, Project existingProject) {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        String color = colorField.getText().trim();
        
        if (name.isEmpty()) {
            showWarningAlert(MSG_NAME_REQUIRED);
            return;
        }
        
        try {
            if (existingProject == null) {
                projectService.createProject(name, desc, color);
                showSuccessAlert(MSG_PROJECT_CREATED);
            } else {
                projectService.updateProject(existingProject.getId(), name, desc, color);
                showSuccessAlert(MSG_PROJECT_UPDATED);
            }
            loadProjects();
            updateStatus();
            dialogStage.close();
        } catch (Exception e) {
            showErrorAlert("Failed to save project", e);
        }
    }
    
    private void loadViewInDialog(String fxmlPath, String title, int width, int height,
                                  java.util.function.Consumer<FXMLLoader> setupController) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        BorderPane view = loader.load();
        setupController.accept(loader);
        
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(view, width, height));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(projectsTable.getScene().getWindow());
        stage.showAndWait();
    }
    
    private String getOrEmpty(String value) {
        return value != null ? value : "";
    }
    
    private String getOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
    
    private void loadProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            projectsList = FXCollections.observableArrayList(projects);
            filteredList = new FilteredList<>(projectsList, p -> true);
            projectsTable.setItems(filteredList);
            updateStatus();
        } catch (Exception e) {
            showErrorAlert("Error loading projects", e);
        }
    }
    
    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterProjects(newValue));
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
            statusLabel.setText("Showing " + total + " project(s)");
        } else {
            statusLabel.setText("Showing " + filtered + " of " + total + " project(s)");
        }
    }
    
    private void showSuccessAlert(String message) {
        UIUtils.showSuccess("Success", message);
    }
    
    private void showErrorAlert(String title, Exception e) {
        System.err.println(title + ": " + e.getMessage());
        e.printStackTrace();
        UIUtils.showError(title, e.getMessage());
    }
    
    private void showWarningAlert(String message) {
        UIUtils.showWarning("Validation", message);
    }
}
