package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class UserTasksDialogController {
    
    @FXML private Label dialogTitleLabel;
    @FXML private Label taskCountLabel;
    @FXML private Label messageLabel;
    @FXML private Button closeButton;
    
    @FXML private TableView<Task> userTasksTable;
    @FXML private TableColumn<Task, Long> taskIdColumn;
    @FXML private TableColumn<Task, String> taskTitleColumn;
    @FXML private TableColumn<Task, String> taskProjectColumn;
    @FXML private TableColumn<Task, String> taskStatusColumn;
    @FXML private TableColumn<Task, String> taskPriorityColumn;
    @FXML private TableColumn<Task, String> taskDueColumn;
    
    private TaskService taskService;
    private Stage dialogStage;
    
    @FXML
    public void initialize() {
        taskService = new TaskService();
        closeButton.setOnAction(e -> handleClose());
        initializeTable();
    }
    
    

    private void initializeTable() {
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        taskProjectColumn.setCellValueFactory(cellData -> {
            Task task = cellData.getValue();
            String projectName = task.getProject() != null ? task.getProject().getName() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(projectName);
        });
        
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        taskDueColumn.setCellValueFactory(cellData -> {
            Task task = cellData.getValue();
            String dueDate = task.getDueDate() != null ? task.getDueDate().toString() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(dueDate);
        });
    }
    
    

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    
    

    public void loadUserTasks(User user) {
        try {
            System.out.println("📋 Loading tasks for user: " + user.getUsername());
            
            dialogTitleLabel.setText("Tasks for " + user.getUsername());
            
            List<Task> userTasks = taskService.getTasksByAssignee(user.getId());
            
            ObservableList<Task> tasksList = FXCollections.observableArrayList(userTasks);
            userTasksTable.setItems(tasksList);
            
            int taskCount = userTasks.size();
            taskCountLabel.setText("(" + taskCount + " task" + (taskCount != 1 ? "s" : "") + ")");
            
            messageLabel.setText("Loaded " + taskCount + " assigned task" + (taskCount != 1 ? "s" : ""));
            messageLabel.setStyle("-fx-text-fill: #27ae60;");
            
            System.out.println("✅ Loaded " + taskCount + " tasks");
        } catch (Exception e) {
            System.err.println("❌ Error loading user tasks: " + e.getMessage());
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }
    
    

    @FXML
    private void handleClose() {
        dialogStage.close();
    }
}
