package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.DateUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class TaskDetailController {
    
    @FXML private Label taskIdLabel;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<User> assigneeCombo;
    @FXML private Label assigneeInfoLabel;
    @FXML private ComboBox<User> createdByCombo;
    @FXML private Label createdByLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label projectLabel;
    
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Button deleteBtn;
    @FXML private Button editBtn;
    
    private Task task;
    private Task originalTask;
    private TaskService taskService;
    private UserService userService;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;
    private Runnable onDeleteCallback;
    private Stage stage;
    
    public TaskDetailController() {
        this.taskService = new TaskService();
        this.userService = new UserService();
    }
    
    @FXML
    public void initialize() {
        System.out.println("🔧 TaskDetailController initialized");
        
        // Setup status dropdown
        ObservableList<String> statuses = FXCollections.observableArrayList("To Do", "In Progress", "Done");
        statusCombo.setItems(statuses);
        
        // Setup priority dropdown
        ObservableList<String> priorities = FXCollections.observableArrayList("Critical", "High", "Medium", "Low", "None");
        priorityCombo.setItems(priorities);
        
        // Load users for assignee and createdBy dropdowns
        loadUsers();
        
        // Button actions
        if (editBtn != null) editBtn.setOnAction(e -> handleEdit());
        if (saveBtn != null) saveBtn.setOnAction(e -> handleSave());
        if (cancelBtn != null) cancelBtn.setOnAction(e -> handleCancel());
        if (deleteBtn != null) deleteBtn.setOnAction(e -> handleDelete());
        
        // Start in view mode
        setViewMode();
    }
    
    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            ObservableList<User> userList = FXCollections.observableArrayList(users);
            assigneeCombo.setItems(userList);
            createdByCombo.setItems(FXCollections.observableArrayList(users));
            
            // Custom cell factory to show usernames
            setupUserComboBoxes(assigneeCombo);
            setupUserComboBoxes(createdByCombo);
        } catch (Exception e) {
            System.err.println("❌ Error loading users: " + e.getMessage());
        }
    }
    
    private void setupUserComboBoxes(ComboBox<User> comboBox) {
        comboBox.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername());
            }
        });
        
        comboBox.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Unassigned" : item.getUsername());
            }
        });
    }
    
    public void setTask(Task task, Stage stage) {
        this.task = task;
        this.stage = stage;
        this.originalTask = new Task();
        
        // Copy original values for undo/cancel
        originalTask.setId(task.getId());
        originalTask.setTitle(task.getTitle());
        originalTask.setDescription(task.getDescription());
        originalTask.setStatus(task.getStatus());
        originalTask.setPriority(task.getPriority());
        originalTask.setDueDate(task.getDueDate());
        originalTask.setAssignee(task.getAssignee());
        originalTask.setProject(task.getProject());
        originalTask.setCreatedBy(task.getCreatedBy());
        originalTask.setCreatedAt(task.getCreatedAt());
        
        populateTaskDetails();
    }
    
    private void populateTaskDetails() {
        if (task == null) return;
        
        System.out.println("📋 Displaying task details for: " + task.getTitle());
        
        // Set title and ID
        taskIdLabel.setText("Task #" + task.getId());
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription() != null ? task.getDescription() : "");
        statusCombo.setValue(task.getStatus() != null ? task.getStatus() : "To Do");
        priorityCombo.setValue(task.getPriority() != null ? task.getPriority() : "Medium");
        dueDatePicker.setValue(task.getDueDate());
        
        // Set assignee
        if (task.getAssignee() != null) {
            assigneeCombo.setValue(task.getAssignee());
            assigneeInfoLabel.setText("");
        } else {
            assigneeCombo.setValue(null);
            assigneeInfoLabel.setText("(Unassigned)");
        }
        
        // Set created by - only show in edit mode for admins
        if (task.getCreatedBy() != null) {
            createdByCombo.setValue(task.getCreatedBy());
            createdByLabel.setText(task.getCreatedBy().getUsername());
        } else {
            createdByLabel.setText("Unknown");
        }
        
        // Set project
        if (task.getProject() != null) {
            projectLabel.setText(task.getProject().getName());
        } else {
            projectLabel.setText("No Project");
        }
        
        // Format created date properly
        if (task.getCreatedAt() != null) {
            createdAtLabel.setText(DateUtils.formatDateTime(task.getCreatedAt()));
        } else {
            createdAtLabel.setText("N/A");
        }
    }
    
    private void setViewMode() {
        isEditMode = false;
        titleField.setEditable(false);
        descriptionArea.setEditable(false);
        statusCombo.setDisable(true);
        priorityCombo.setDisable(true);
        dueDatePicker.setDisable(true);
        assigneeCombo.setDisable(true);
        createdByCombo.setDisable(true);
        createdByCombo.setVisible(false);
        createdByLabel.setVisible(true);
        
        // Button visibility for view mode
        if (editBtn != null) editBtn.setVisible(true);
        if (saveBtn != null) saveBtn.setVisible(false);
        if (cancelBtn != null) cancelBtn.setVisible(false);
    }
    
    private void setEditMode() {
        isEditMode = true;
        titleField.setEditable(true);
        descriptionArea.setEditable(true);
        statusCombo.setDisable(false);
        priorityCombo.setDisable(false);
        dueDatePicker.setDisable(false);
        assigneeCombo.setDisable(false);
        
        // Only admins can change who created the task
        boolean isAdmin = CurrentUser.isAdmin();
        createdByCombo.setDisable(!isAdmin);
        createdByCombo.setVisible(isAdmin);
        createdByLabel.setVisible(!isAdmin);
        
        // Button visibility for edit mode
        if (editBtn != null) editBtn.setVisible(false);
        if (saveBtn != null) saveBtn.setVisible(true);
        if (cancelBtn != null) cancelBtn.setVisible(true);
        
        // Focus on title field
        titleField.requestFocus();
    }
    
    @FXML
    private void handleEdit() {
        System.out.println("✏️ Entering edit mode");
        setEditMode();
    }
    
    @FXML
    private void handleSave() {
        System.out.println("💾 Saving task changes");
        
        // Validate input
        if (titleField.getText().isEmpty()) {
            showAlert("Validation Error", "Task title cannot be empty!", Alert.AlertType.ERROR);
            return;
        }
        
        // Update task with new values
        task.setTitle(titleField.getText());
        task.setDescription(descriptionArea.getText());
        task.setStatus(statusCombo.getValue());
        task.setPriority(priorityCombo.getValue());
        task.setDueDate(dueDatePicker.getValue());
        task.setAssignee(assigneeCombo.getValue());
        
        // Only admins can change created by
        if (CurrentUser.isAdmin() && createdByCombo.getValue() != null) {
            task.setCreatedBy(createdByCombo.getValue());
        }
        
        try {
            // Save to database
            taskService.updateTask(task);
            System.out.println("✅ Task saved successfully");
            showAlert("Success", "Task updated successfully!", Alert.AlertType.INFORMATION);
            
            setViewMode();
            
            // Call callback if set
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
        } catch (Exception e) {
            System.err.println("❌ Error saving task: " + e.getMessage());
            showAlert("Error", "Failed to save task: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleCancel() {
        System.out.println("❌ Canceling edit mode");
        
        // Restore original values
        task.setTitle(originalTask.getTitle());
        task.setDescription(originalTask.getDescription());
        task.setStatus(originalTask.getStatus());
        task.setPriority(originalTask.getPriority());
        task.setDueDate(originalTask.getDueDate());
        task.setAssignee(originalTask.getAssignee());
        
        populateTaskDetails();
        setViewMode();
    }
    
    @FXML
    private void handleDelete() {
        System.out.println("🗑️ Deleting task: " + task.getTitle());
        
        Optional<ButtonType> result = showConfirmation("Delete Task",
            "Are you sure you want to delete '" + task.getTitle() + "'? This action cannot be undone.");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                taskService.deleteTask(task.getId());
                System.out.println("✅ Task deleted successfully");
                showAlert("Success", "Task deleted successfully!", Alert.AlertType.INFORMATION);
                
                // Call callback if set
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
                
                // Close the window if it's a modal
                if (stage != null) {
                    stage.close();
                }
                
            } catch (Exception e) {
                System.err.println("❌ Error deleting task: " + e.getMessage());
                showAlert("Error", "Failed to delete task: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
