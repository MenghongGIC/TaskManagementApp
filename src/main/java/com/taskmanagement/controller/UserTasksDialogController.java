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
    
    // Labels
    private static final String LABEL_TITLE_PREFIX = "Tasks for ";
    private static final String LABEL_TASK_SINGULAR = " task";
    private static final String LABEL_TASK_PLURAL = " tasks";
    
    // Messages
    private static final String MSG_LOADING = "Loading tasks for user: ";
    private static final String MSG_LOADED = "Loaded ";
    private static final String MSG_ASSIGNED = "assigned ";
    private static final String MSG_ERROR = "Error loading user tasks: ";
    private static final String MSG_ERROR_DISPLAY = "Error: ";
    
    // Colors
    private static final String COLOR_SUCCESS = "-fx-text-fill: #27ae60;";
    private static final String COLOR_ERROR = "-fx-text-fill: #e74c3c;";
    
    // Labels - N/A
    private static final String LABEL_NA = "N/A";
    
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
            String projectName = task.getProject() != null ? task.getProject().getName() : LABEL_NA;
            return new javafx.beans.property.SimpleStringProperty(projectName);
        });
        
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        taskDueColumn.setCellValueFactory(cellData -> {
            Task task = cellData.getValue();
            String dueDate = task.getDueDate() != null ? task.getDueDate().toString() : LABEL_NA;
            return new javafx.beans.property.SimpleStringProperty(dueDate);
        });
    }
    
    

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    
    

    public void loadUserTasks(User user) {
        try {
            System.out.println(MSG_LOADING + user.getUsername());
            
            dialogTitleLabel.setText(LABEL_TITLE_PREFIX + user.getUsername());
            
            List<Task> userTasks = taskService.getTasksByAssignee(user.getId());
            
            ObservableList<Task> tasksList = FXCollections.observableArrayList(userTasks);
            userTasksTable.setItems(tasksList);
            
            int taskCount = userTasks.size();
            String taskLabel = getPluralLabel(taskCount, LABEL_TASK_SINGULAR, LABEL_TASK_PLURAL);
            taskCountLabel.setText("(" + taskCount + " " + taskLabel + ")");
            
            displayMessage(MSG_LOADED + taskCount + MSG_ASSIGNED + taskLabel, COLOR_SUCCESS);
            System.out.println(MSG_LOADED + taskCount + " tasks");
        } catch (Exception e) {
            System.err.println(MSG_ERROR + e.getMessage());
            e.printStackTrace();
            displayMessage(MSG_ERROR_DISPLAY + e.getMessage(), COLOR_ERROR);
        }
    }
    private void displayMessage(String message, String colorStyle) {
        messageLabel.setText(message);
        messageLabel.setStyle(colorStyle);
    }
    private String getPluralLabel(int count, String singular, String plural) {
        return count == 1 ? singular : plural;
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }
}
