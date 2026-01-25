package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.Project;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.utils.DateUtils;
import com.taskmanagement.App;

public class ProjectDetailController {
    
    @FXML private Label projectNameLabel;
    @FXML private Label projectDescLabel;
    @FXML private Label projectCreatedByLabel;
    @FXML private Label projectCreatedAtLabel;
    @FXML private Label projectColorLabel;
    @FXML private Rectangle colorBox;
    
    @FXML private TextField taskSearchField;
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, Long> taskIdColumn;
    @FXML private TableColumn<Task, String> taskTitleColumn;
    @FXML private TableColumn<Task, String> taskStatusColumn;
    @FXML private TableColumn<Task, String> taskPriorityColumn;
    @FXML private TableColumn<Task, String> taskDueColumn;
    @FXML private TableColumn<Task, String> taskAssigneeColumn;
    
    @FXML private Label statTotalTasks;
    @FXML private Label statInProgress;
    @FXML private Label statCompleted;
    @FXML private Label statTodo;
    
    @FXML private Label detailProjectId;
    @FXML private Label detailCreatedBy;
    @FXML private Label detailCreatedAt;
    @FXML private Label detailDescription;
    
    private Project project;
    private TaskService taskService;
    private ProjectService projectService;
    private ObservableList<Task> tasksList;
    private FilteredList<Task> filteredTasks;
    private ProjectController projectController;
    
    public void initialize() {
        taskService = new TaskService();
        projectService = new ProjectService();
        setupTaskTableColumns();
        setupTaskSearchListener();
    }
    
    /**
     * Set the ProjectController reference for navigation callbacks
     */
    public void setProjectController(ProjectController controller) {
        this.projectController = controller;
    }
    
    /**
     * Set the project to display
     */
    public void setProject(Project project) {
        this.project = project;
        populateProjectDetails();
        loadProjectTasks();
    }
    
    /**
     * Populate project details in UI
     */
    private void populateProjectDetails() {
        projectNameLabel.setText(project.getName());
        projectDescLabel.setText(project.getDescription() != null ? project.getDescription() : "No description");
        
        if (project.getCreatedBy() != null) {
            projectCreatedByLabel.setText(project.getCreatedBy().getUsername());
            detailCreatedBy.setText(project.getCreatedBy().getUsername());
        } else {
            projectCreatedByLabel.setText("Unknown");
            detailCreatedBy.setText("Unknown");
        }
        
        projectCreatedAtLabel.setText(DateUtils.formatDateTime(project.getCreatedAt()));
        detailCreatedAt.setText(DateUtils.formatDateTime(project.getCreatedAt()));
        
        String color = project.getColor() != null ? project.getColor() : "#3498db";
        projectColorLabel.setText(color);
        colorBox.setStyle("-fx-fill: " + color + "; -fx-stroke: #bdc3c7; -fx-stroke-width: 1;");
        
        detailProjectId.setText(String.valueOf(project.getId()));
        detailDescription.setText(project.getDescription() != null ? project.getDescription() : "No description provided");
    }
    
    /**
     * Setup task table columns
     */
    private void setupTaskTableColumns() {
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        // Setup status column styling using TaskStatus enum
        taskStatusColumn.setCellFactory(param -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(status);
                    String bgColor = "#f0f0f0";
                    String textColor = "#000000";
                    switch(status.toLowerCase()) {
                        case "to do": bgColor = "#e8f4f8"; break;
                        case "in progress": bgColor = "#fff3cd"; break;
                        case "done": bgColor = "#d4edda"; break;
                    }
                    setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-padding: 5 10; -fx-border-radius: 3; -fx-font-weight: bold;");
                }
            }
        });
        
        // Setup priority column styling
        taskPriorityColumn.setCellFactory(param -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(priority);
                    setStyle("-fx-background-color: " + getColorForPriority(priority) + "; -fx-text-fill: " + getTextColorForPriority(priority) + "; -fx-padding: 3 8; -fx-border-radius: 2; -fx-font-weight: 600;");
                }
            }
        });
        
        taskDueColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                DateUtils.formatDate(cellData.getValue().getDueDate())
            );
        });
        
        taskAssigneeColumn.setCellValueFactory(cellData -> {
            User assignee = cellData.getValue().getAssignee();
            return new javafx.beans.property.SimpleStringProperty(
                assignee != null ? assignee.getUsername() : "Unassigned"
            );
        });
        
        // Add double-click handler on task to edit
        tasksTable.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<Task>() {
                @Override
                protected void updateItem(Task item, boolean empty) {
                    super.updateItem(item, empty);
                }
            };
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Task task = row.getItem();
                    editTask(task);
                }
            });
            return row;
        });
    }
    
    /**
     * Open edit task dialog
     */
    private void editTask(Task task) {
        try {
            System.out.println("📂 Opening task detail popup for: " + task.getTitle());
            
            // Load TaskDetailView.fxml
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/main/TaskDetailView.fxml"));
            BorderPane taskDetailView = loader.load();
            
            // Create a new Stage for the popup
            Stage taskDetailStage = new Stage();
            taskDetailStage.setTitle("Task: " + task.getTitle());
            taskDetailStage.setScene(new Scene(taskDetailView, 700, 600));
            taskDetailStage.initModality(Modality.WINDOW_MODAL);
            
            // Get the controller and set the task
            TaskDetailController controller = loader.getController();
            controller.setTask(task, taskDetailStage);
            
            // Set callback to refresh task list when task is saved/deleted
            controller.setOnSaveCallback(() -> {
                System.out.println("✅ Task updated, refreshing task list");
                loadProjectTasks();
                taskDetailStage.close();
            });
            controller.setOnDeleteCallback(() -> {
                System.out.println("✅ Task deleted, refreshing task list");
                loadProjectTasks();
                taskDetailStage.close();
            });
            
            // Show the popup as modal dialog
            taskDetailStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("❌ Error opening task detail popup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load tasks for this project
     */
    private void loadProjectTasks() {
        try {
            // Get all tasks and filter for this project
            List<Task> allTasks = taskService.getAllTasks();
            List<Task> projectTasks = allTasks.stream()
                .filter(t -> t.getProject() != null && t.getProject().getId().equals(project.getId()))
                .collect(Collectors.toList());
            
            tasksList = FXCollections.observableArrayList(projectTasks);
            filteredTasks = new FilteredList<>(tasksList, t -> true);
            tasksTable.setItems(filteredTasks);
            
            updateStatistics();
        } catch (Exception e) {
            showAlert("Error", "Failed to load tasks: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Update task statistics
     */
    private void updateStatistics() {
        long total = tasksList.size();
        long inProgress = tasksList.stream().filter(t -> "In Progress".equals(t.getStatus())).count();
        long completed = tasksList.stream().filter(t -> "Done".equals(t.getStatus())).count();
        long todo = tasksList.stream().filter(t -> "To Do".equals(t.getStatus())).count();
        
        statTotalTasks.setText(String.valueOf(total));
        statInProgress.setText(String.valueOf(inProgress));
        statCompleted.setText(String.valueOf(completed));
        statTodo.setText(String.valueOf(todo));
    }
    
    @FXML
    private void setupTaskSearchListener() {
        taskSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTasks(newValue);
        });
    }
    
    @FXML
    private void clearTaskSearch() {
        taskSearchField.clear();
        filteredTasks.setPredicate(t -> true);
    }
    
    private void filterTasks(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredTasks.setPredicate(t -> true);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredTasks.setPredicate(task ->
                task.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                (task.getDescription() != null && task.getDescription().toLowerCase().contains(lowerCaseFilter)) ||
                task.getStatus().toLowerCase().contains(lowerCaseFilter)
            );
        }
    }
    
    @FXML
    private void handleAddTaskToProject() {
        System.out.println("➕ Opening create task dialog for project: " + project.getName());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/dialog/CreateTaskView.fxml"));
            BorderPane dialogRoot = loader.load();
            CreateTaskController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Task in " + project.getName());
            dialogStage.setScene(new Scene(dialogRoot, 650, 700));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            // Get owner stage
            Stage ownerStage = (Stage) projectNameLabel.getScene().getWindow();
            if (ownerStage != null) {
                dialogStage.initOwner(ownerStage);
            }
            
            // Set project and callbacks
            controller.setProject(project);
            controller.setDialogStage(dialogStage);
            controller.setOnTaskCreated(() -> {
                System.out.println("🔄 Refreshing task list after task creation");
                loadProjectTasks();
                updateStatistics();
            });
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("❌ Error opening create task dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error opening create task dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void handleViewTask(Task task) {
        System.out.println("👁️ Opening task detail view for: " + task.getTitle());
        
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/main/TaskDetailView.fxml"));
            BorderPane taskDetailRoot = loader.load();
            TaskDetailController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Task Details - " + task.getTitle());
            dialogStage.setScene(new Scene(taskDetailRoot, 600, 700));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            // Get owner stage
            Stage ownerStage = (Stage) projectNameLabel.getScene().getWindow();
            if (ownerStage != null) {
                dialogStage.initOwner(ownerStage);
            }
            
            // Set task and callbacks
            controller.setTask(task, dialogStage);
            controller.setOnSaveCallback(() -> {
                System.out.println("🔄 Refreshing task list after save");
                loadProjectTasks();
                updateStatistics();
            });
            controller.setOnDeleteCallback(() -> {
                System.out.println("🔄 Refreshing task list after delete");
                loadProjectTasks();
                updateStatistics();
            });
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("❌ Error opening task detail view: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error opening task details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void handleEditTask(Task task) {
        showAlert("Info", "Task edit feature will open edit dialog", Alert.AlertType.INFORMATION);
    }
    
    private void handleDeleteTask(Task task) {
        Optional<ButtonType> result = showConfirmation("Delete Task", 
            "Are you sure you want to delete '" + task.getTitle() + "'?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                taskService.deleteTask(task.getId());
                loadProjectTasks();
                showAlert("Success", "Task deleted successfully!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to delete task: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleEditProject() {
        System.out.println("✏️ Opening edit project dialog for: " + project.getName());
        
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/main/EditProjectView.fxml"));
            BorderPane editRoot = loader.load();
            EditProjectController controller = loader.getController();
            
            // Create dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Project - " + project.getName());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(editRoot, 500, 400));
            
            controller.setProject(project, dialogStage);
            
            // Set a callback to refresh the project details after saving
            controller.setOnSaveCallback(() -> {
                System.out.println("🔄 Refreshing project details after edit");
                // Reload the project from database to get updated values
                Project updatedProject = projectService.getProjectById(project.getId());
                if (updatedProject != null) {
                    setProject(updatedProject);
                }
            });
            
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            System.err.println("❌ Error opening edit project dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open edit dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleDeleteProject() {
        Optional<ButtonType> result = showConfirmation("Delete Project", 
            "Are you sure you want to delete this project? This action cannot be undone.");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                projectService.deleteProject(project.getId());
                showAlert("Success", "Project deleted successfully!", Alert.AlertType.INFORMATION);
                handleClose();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete project: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleClose() {
        System.out.println("← Going back to Projects list");
        if (projectController != null) {
            projectController.clearProjectDetails();
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
    
    /**
     * Get priority color based on priority level
     */
    private String getColorForPriority(String priority) {
        if ("Low".equals(priority)) {
            return "#DBEAFE";
        } else if ("Medium".equals(priority)) {
            return "#FCD34D";
        } else if ("High".equals(priority)) {
            return "#FCA5A5";
        }
        return "#DBEAFE";
    }
    
    /**
     * Get text color based on priority level
     */
    private String getTextColorForPriority(String priority) {
        if ("Low".equals(priority)) {
            return "#0C4A6E";
        } else if ("Medium".equals(priority)) {
            return "#78350F";
        } else if ("High".equals(priority)) {
            return "#7F1D1D";
        }
        return "#0C4A6E";
    }
}

