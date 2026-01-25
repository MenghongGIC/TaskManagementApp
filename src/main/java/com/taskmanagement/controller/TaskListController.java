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

import java.util.List;

import com.taskmanagement.App;
import com.taskmanagement.model.Task;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.utils.UIUtils;

public class TaskListController implements TaskAwareController {
    
    // UI Labels
    private static final String LABEL_EMPTY = "No tasks available";
    private static final String LABEL_STATUS = "Status: ";
    private static final String LABEL_PRIORITY = "Priority: ";
    private static final String LABEL_DUE = "Due: ";
    private static final String LABEL_NO_TASKS = "No tasks";
    private static final String LABEL_SHOWING = "Showing ";
    private static final String LABEL_TASK = " task(s)";
    
    // Button Labels
    private static final String BTN_VIEW = "View";
    private static final String BTN_EDIT = "Edit";
    private static final String BTN_DELETE = "Delete";
    
    // Dialog Titles
    private static final String TITLE_CREATE_TASK = "Create New Task";
    private static final String TITLE_TASK_DETAILS = "Task Details - ";
    private static final String TITLE_EDIT_TASK = "Edit Task";
    private static final String TITLE_DELETE = "Delete Task";
    
    // Messages
    private static final String MSG_OPENING_CREATE = "Opening create task dialog";
    private static final String MSG_REFRESHING = "Refreshing task list after task creation";
    private static final String MSG_EDITING = "Editing task: ";
    private static final String MSG_DELETED = "Task deleted successfully!";
    
    // Error Messages
    private static final String MSG_ERROR_CREATE = "Error opening create task dialog: ";
    private static final String MSG_ERROR_LOAD = "Failed to load tasks: ";
    private static final String MSG_ERROR_DETAILS = "Failed to open task details: ";
    private static final String MSG_ERROR_EDIT = "Failed to edit task: ";
    private static final String MSG_ERROR_DELETE = "Failed to delete task: ";
    
    // Confirmation Messages
    private static final String MSG_DELETE_CONFIRM = "Are you sure you want to delete '%s'? This action cannot be undone.";
    
    // Style Constants
    private static final String STYLE_TASK_ITEM = "-fx-padding: 12; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-cursor: hand;";
    private static final String STYLE_STATUS_INDICATOR = "-fx-background-color: %s;";
    private static final String STYLE_TITLE = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;";
    private static final String STYLE_INFO_LABEL = "-fx-font-size: 11px; -fx-text-fill: #7f8c8d;";
    private static final String STYLE_PRIORITY_LABEL = "-fx-font-size: 11px; -fx-text-fill: %s; -fx-font-weight: bold;";
    private static final String STYLE_DUE_LABEL = "-fx-font-size: 11px; -fx-text-fill: #e74c3c;";
    private static final String STYLE_BUTTON = "-fx-padding: 5 10; -fx-font-size: 10px; -fx-text-fill: white; -fx-background-radius: 3;";
    private static final String STYLE_EMPTY_LABEL = "-fx-font-size: 14px; -fx-text-fill: #95a5a6; -fx-padding: 20;";
    
    // Colors
    private static final String COLOR_DEFAULT = "#95a5a6";
    private static final String COLOR_VIEW_BTN = "#3498db";
    private static final String COLOR_EDIT_BTN = "#f39c12";
    private static final String COLOR_DELETE_BTN = "#e74c3c";
    
    // Status Colors Map
    private static final java.util.Map<String, String> STATUS_COLORS = java.util.Map.ofEntries(
        java.util.Map.entry("to do", "#3498db"),
        java.util.Map.entry("in progress", "#f39c12"),
        java.util.Map.entry("done", "#27ae60")
    );
    
    // Priority Colors Map
    private static final java.util.Map<String, String> PRIORITY_COLORS = java.util.Map.ofEntries(
        java.util.Map.entry("CRITICAL", "#c0392b"),
        java.util.Map.entry("HIGH", "#e74c3c"),
        java.util.Map.entry("MEDIUM", "#f39c12"),
        java.util.Map.entry("LOW", "#27ae60")
    );
    
    // Dialog Dimensions
    private static final int DIALOG_CREATE_WIDTH = 600;
    private static final int DIALOG_CREATE_HEIGHT = 700;
    private static final int DIALOG_DETAIL_WIDTH = 800;
    private static final int DIALOG_DETAIL_HEIGHT = 600;
    private static final int DIALOG_EDIT_WIDTH = 600;
    private static final int DIALOG_EDIT_HEIGHT = 700;
    
    // Layout Constants
    private static final int ITEM_SPACING = 10;
    private static final int INFO_SPACING = 15;
    private static final int BUTTON_SPACING = 5;
    private static final int DETAIL_SPACING = 3;
    private static final int STATUS_INDICATOR_WIDTH = 5;
    
    @FXML private VBox taskListContainer;
    @FXML private Label taskListStatusLabel;
    
    private TaskService taskService;
    private List<Task> tasksList;
    
    @Override
    public void setMainLayoutController(MainLayoutController controller) { }

    @FXML
    public void initialize() {
        taskService = new TaskService();
        loadTasks();
    }
    
    private void loadTasks() {
        try {
            tasksList = taskService.getAllTasks();
            displayTasksList();
            updateStatus();
        } catch (Exception e) {
            UIUtils.showError("Error", MSG_ERROR_LOAD + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void displayTasksList() {
        taskListContainer.getChildren().clear();
        
        if (tasksList == null || tasksList.isEmpty()) {
            Label emptyLabel = new Label(LABEL_EMPTY);
            emptyLabel.setStyle(STYLE_EMPTY_LABEL);
            taskListContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (Task task : tasksList) {
            taskListContainer.getChildren().add(createTaskListItem(task));
        }
        
        VBox.setVgrow(taskListContainer, Priority.ALWAYS);
    }
    
    private HBox createTaskListItem(Task task) {
        HBox taskItem = new HBox(ITEM_SPACING);
        taskItem.setStyle(STYLE_TASK_ITEM);
        
        VBox statusIndicator = createStatusIndicator(task.getStatus());
        VBox taskDetails = createTaskDetails(task);
        HBox actionButtons = createActionButtonsBox(task);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        taskItem.getChildren().addAll(statusIndicator, taskDetails, spacer, actionButtons);
        taskItem.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                handleViewTask(task);
            }
        });
        
        return taskItem;
    }
    
    private VBox createStatusIndicator(String status) {
        String statusColor = STATUS_COLORS.getOrDefault(status != null ? status.toLowerCase() : "", COLOR_DEFAULT);
        VBox indicator = new VBox();
        indicator.setPrefWidth(STATUS_INDICATOR_WIDTH);
        indicator.setStyle(String.format(STYLE_STATUS_INDICATOR, statusColor));
        return indicator;
    }
    
    private VBox createTaskDetails(Task task) {
        VBox details = new VBox(DETAIL_SPACING);
        
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle(STYLE_TITLE);
        
        HBox infoRow = createInfoRow(task);
        details.getChildren().addAll(titleLabel, infoRow);
        
        return details;
    }
    
    private HBox createInfoRow(Task task) {
        HBox infoRow = new HBox(INFO_SPACING);
        
        Label statusLabel = new Label(LABEL_STATUS + task.getStatus());
        statusLabel.setStyle(STYLE_INFO_LABEL);
        infoRow.getChildren().add(statusLabel);
        
        String priorityColor = PRIORITY_COLORS.getOrDefault(task.getPriority() != null ? task.getPriority().toUpperCase() : "", COLOR_DEFAULT);
        Label priorityLabel = new Label(LABEL_PRIORITY + task.getPriority());
        priorityLabel.setStyle(String.format(STYLE_PRIORITY_LABEL, priorityColor));
        infoRow.getChildren().add(priorityLabel);
        
        if (task.getDueDate() != null) {
            Label dueDateLabel = new Label(LABEL_DUE + task.getDueDate());
            dueDateLabel.setStyle(STYLE_DUE_LABEL);
            infoRow.getChildren().add(dueDateLabel);
        }
        
        return infoRow;
    }
    
    private HBox createActionButtonsBox(Task task) {
        HBox buttons = new HBox(BUTTON_SPACING);
        buttons.getChildren().addAll(
            createActionButton(BTN_VIEW, COLOR_VIEW_BTN, e -> handleViewTask(task)),
            createActionButton(BTN_EDIT, COLOR_EDIT_BTN, e -> handleEditTask(task)),
            createActionButton(BTN_DELETE, COLOR_DELETE_BTN, e -> handleDeleteTask(task))
        );
        return buttons;
    }
    
    private Button createActionButton(String label, String color, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(label);
        button.setStyle(STYLE_BUTTON + "-fx-background-color: " + color + ";");
        button.setOnAction(handler);
        return button;
    }
    
    @FXML
    private void refreshTaskList() {
        loadTasks();
    }
    
    @FXML
    private void handleAddTask() {
        System.out.println(MSG_OPENING_CREATE);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/dialog/CreateTaskView.fxml"));
            javafx.scene.layout.BorderPane dialogRoot = loader.load();
            CreateTaskController controller = loader.getController();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(TITLE_CREATE_TASK);
            dialogStage.setScene(new Scene(dialogRoot, DIALOG_CREATE_WIDTH, DIALOG_CREATE_HEIGHT));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setDialogStage(dialogStage);
            controller.setOnTaskCreated(() -> {
                System.out.println(MSG_REFRESHING);
                loadTasks();
            });
            
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            System.err.println("Error opening create task dialog: " + e.getMessage());
            e.printStackTrace();
            UIUtils.showError("Error", MSG_ERROR_CREATE + e.getMessage());
        }
    }
    
    private void handleViewTask(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/taskmanagement/fxml/main/TaskDetailView.fxml"));
            javafx.scene.layout.BorderPane detailView = loader.load();
            TaskDetailController controller = loader.getController();
            
            Stage detailStage = new Stage();
            detailStage.setTitle(TITLE_TASK_DETAILS + task.getTitle());
            detailStage.setScene(new Scene(detailView, DIALOG_DETAIL_WIDTH, DIALOG_DETAIL_HEIGHT));
            detailStage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setTask(task, detailStage);
            
            detailStage.showAndWait();
            loadTasks();
        } catch (Exception e) {
            UIUtils.showError("Error", MSG_ERROR_DETAILS + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleEditTask(Task task) {
        System.out.println(MSG_EDITING + task.getTitle());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanagement/fxml/main/EditTaskView.fxml"));
            javafx.scene.layout.BorderPane editView = loader.load();
            
            Stage editStage = new Stage();
            editStage.setTitle(TITLE_EDIT_TASK);
            editStage.setScene(new Scene(editView, DIALOG_EDIT_WIDTH, DIALOG_EDIT_HEIGHT));
            editStage.initModality(Modality.APPLICATION_MODAL);
            
            editStage.showAndWait();
            loadTasks();
        } catch (Exception e) {
            UIUtils.showError("Error", MSG_ERROR_EDIT + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteTask(Task task) {
        String confirmMessage = String.format(MSG_DELETE_CONFIRM, task.getTitle());
        if (UIUtils.showCustomConfirmation(TITLE_DELETE, null, confirmMessage)) {
            try {
                taskService.deleteTask(task.getId());
                UIUtils.showSuccess("Success", MSG_DELETED);
                loadTasks();
            } catch (Exception e) {
                UIUtils.showError("Error", MSG_ERROR_DELETE + e.getMessage());
            }
        }
    }
    
    private void updateStatus() {
        int total = tasksList != null ? tasksList.size() : 0;
        if (total == 0) {
            taskListStatusLabel.setText(LABEL_NO_TASKS);
        } else {
            taskListStatusLabel.setText(LABEL_SHOWING + total + LABEL_TASK);
        }
    }
}
