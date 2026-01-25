package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;

import com.taskmanagement.App;
import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.utils.CurrentUser;

public class TaskListController implements TaskAwareController {
    
    @FXML private VBox taskListContainer;
    @FXML private Label taskListStatusLabel;
    
    private TaskService taskService;
    private ProjectService projectService;
    private List<Task> tasksList;
    private MainLayoutController mainLayoutController;
    
    @Override
    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }
    
    @FXML
    public void initialize() {
        taskService = new TaskService();
        projectService = new ProjectService();
        loadTasks();
    }
    
    /**
     * Load and display all tasks in list format
     */
    private void loadTasks() {
        try {
            tasksList = taskService.getAllTasks();
            displayTasksList();
            updateStatus();
        } catch (Exception e) {
            showAlert("Error", "Failed to load tasks: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Display tasks as individual items in a list
     */
    private void displayTasksList() {
        taskListContainer.getChildren().clear();
        
        if (tasksList == null || tasksList.isEmpty()) {
            Label emptyLabel = new Label("No tasks available");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #95a5a6; -fx-padding: 20;");
            taskListContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (Task task : tasksList) {
            taskListContainer.getChildren().add(createTaskListItem(task));
        }
        
        VBox.setVgrow(taskListContainer, Priority.ALWAYS);
    }
    
    /**
     * Create a visual task item for the list
     */
    private HBox createTaskListItem(Task task) {
        HBox taskItem = new HBox(10);
        taskItem.setStyle(
            "-fx-padding: 12; " +
            "-fx-border-color: #ecf0f1; " +
            "-fx-border-width: 0 0 1 0; " +
            "-fx-cursor: hand;"
        );
        
        // Status indicator color
        String statusColor = getStatusColor(task.getStatus());
        
        // Status indicator
        VBox statusIndicator = new VBox();
        statusIndicator.setPrefWidth(5);
        statusIndicator.setStyle("-fx-background-color: " + statusColor + ";");
        
        // Task details
        VBox taskDetails = new VBox(3);
        
        // Title
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Info row
        HBox infoRow = new HBox(15);
        
        Label statusLabel = new Label("Status: " + task.getStatus());
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        Label priorityLabel = new Label("Priority: " + task.getPriority());
        String priorityColor = getPriorityColor(task.getPriority());
        priorityLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + priorityColor + "; -fx-font-weight: bold;");
        
        Label dueDateLabel = new Label("");
        if (task.getDueDate() != null) {
            dueDateLabel.setText("Due: " + task.getDueDate());
            dueDateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");
        }
        
        infoRow.getChildren().addAll(statusLabel, priorityLabel);
        if (task.getDueDate() != null) {
            infoRow.getChildren().add(dueDateLabel);
        }
        
        taskDetails.getChildren().addAll(titleLabel, infoRow);
        
        // Action buttons
        HBox actionButtons = new HBox(5);
        
        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 10px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 3;");
        viewBtn.setOnAction(e -> handleViewTask(task));
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 10px; -fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 3;");
        editBtn.setOnAction(e -> handleEditTask(task));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 10px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 3;");
        deleteBtn.setOnAction(e -> handleDeleteTask(task));
        
        actionButtons.getChildren().addAll(viewBtn, editBtn, deleteBtn);
        
        // Assemble the item
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        taskItem.getChildren().addAll(statusIndicator, taskDetails, spacer, actionButtons);
        
        // Click to view
        taskItem.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                handleViewTask(task);
            }
        });
        
        return taskItem;
    }
    
    /**
     * Get color for task status
     */
    private String getStatusColor(String status) {
        if (status == null) return "#95a5a6";
        return switch (status.toLowerCase()) {
            case "to do" -> "#3498db";
            case "in progress" -> "#f39c12";
            case "done" -> "#27ae60";
            default -> "#95a5a6";
        };
    }
    
    /**
     * Get color for priority
     */
    private String getPriorityColor(String priority) {
        if (priority == null) return "#95a5a6";
        return switch (priority.toUpperCase()) {
            case "HIGH" -> "#e74c3c";
            case "MEDIUM" -> "#f39c12";
            case "LOW" -> "#27ae60";
            default -> "#95a5a6";
        };
    }
    
    @FXML
    private void refreshTaskList() {
        loadTasks();
    }
    
    @FXML
    private void handleAddTask() {
        System.out.println("➕ Opening create task dialog");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/dialog/CreateTaskView.fxml"));
            javafx.scene.layout.BorderPane dialogRoot = loader.load();
            CreateTaskController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Task");
            dialogStage.setScene(new Scene(dialogRoot, 600, 700));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setDialogStage(dialogStage);
            controller.setOnTaskCreated(() -> {
                System.out.println("🔄 Refreshing task list after task creation");
                loadTasks();
            });
            
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            System.err.println("❌ Error opening create task dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error opening create task dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void handleViewTask(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/main/TaskDetailView.fxml"));
            javafx.scene.layout.BorderPane detailView = loader.load();
            TaskDetailController controller = loader.getController();
            
            Stage detailStage = new Stage();
            detailStage.setTitle("Task Details - " + task.getTitle());
            detailStage.setScene(new Scene(detailView, 800, 600));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setTask(task, detailStage);
            
            detailStage.showAndWait();
            loadTasks();
        } catch (Exception e) {
            showAlert("Error", "Failed to open task details: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void handleEditTask(Task task) {
        System.out.println("✏️ Editing task: " + task.getTitle());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/main/EditTaskView.fxml"));
            javafx.scene.layout.BorderPane editView = loader.load();
            
            Stage editStage = new Stage();
            editStage.setTitle("Edit Task");
            editStage.setScene(new Scene(editView, 600, 700));
            editStage.initModality(Modality.APPLICATION_MODAL);
            
            editStage.showAndWait();
            loadTasks();
        } catch (Exception e) {
            showAlert("Error", "Failed to edit task: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void handleDeleteTask(Task task) {
        Optional<ButtonType> result = showConfirmation("Delete Task",
            "Are you sure you want to delete '" + task.getTitle() + "'? This action cannot be undone.");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                taskService.deleteTask(task.getId());
                showAlert("Success", "Task deleted successfully!", Alert.AlertType.INFORMATION);
                loadTasks();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete task: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    private void updateStatus() {
        int total = tasksList != null ? tasksList.size() : 0;
        if (total == 0) {
            taskListStatusLabel.setText("No tasks");
        } else {
            taskListStatusLabel.setText("✓ Showing " + total + " task(s)");
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
