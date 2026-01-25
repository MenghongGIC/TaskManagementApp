package com.taskmanagement.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EditTaskController {
    
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<User> assigneeCombo;
    @FXML private ComboBox<User> createdByCombo;
    @FXML private Label headerLabel;
    @FXML private Label validationLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Button undoBtn;
    
    private Task originalTask;
    private Task workingTask;
    private TaskService taskService;
    private UserService userService;
    private Stage stage;
    private Runnable onSaveCallback;
    
    
    private String originalTitle;
    private String originalDescription;
    private String originalStatus;
    private String originalPriority;
    private LocalDate originalDueDate;
    private User originalAssignee;
    private User originalCreatedBy;
    
    public EditTaskController() {
        this.taskService = new TaskService();
        this.userService = new UserService();
    }
    
    @FXML
    public void initialize() {
        System.out.println("🔧 EditTaskController initialized");
        
        
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
        undoBtn.setOnAction(e -> handleUndo());
        
        
        statusCombo.getItems().addAll("To Do", "In Progress", "Done");
        statusCombo.setOnAction(e -> validateForm());
        
        
        priorityCombo.getItems().addAll("Critical", "High", "Medium", "Low", "None");
        priorityCombo.setOnAction(e -> validateForm());
        
        
        loadUsers();
        
        
        titleField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        
        
        descriptionField.setWrapText(true);
        
        
        dueDatePicker.setOnAction(e -> validateForm());
        
        validationLabel.setText("");
        validationLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
    
    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            ObservableList<User> userList = FXCollections.observableArrayList(users);
            assigneeCombo.setItems(userList);
            createdByCombo.setItems(FXCollections.observableArrayList(users));
            
            setupUserComboBoxes(assigneeCombo);
            setupUserComboBoxes(createdByCombo);
            
            
            createdByCombo.setVisible(CurrentUser.isAdmin());
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
        this.originalTask = task;
        this.workingTask = new Task();
        this.stage = stage;
        
        
        workingTask.setId(task.getId());
        workingTask.setTitle(task.getTitle());
        workingTask.setDescription(task.getDescription());
        workingTask.setStatus(task.getStatus());
        workingTask.setPriority(task.getPriority());
        workingTask.setDueDate(task.getDueDate());
        workingTask.setAssignee(task.getAssignee());
        workingTask.setCreatedBy(task.getCreatedBy());
        
        
        originalTitle = task.getTitle();
        originalDescription = task.getDescription();
        originalStatus = task.getStatus();
        originalPriority = task.getPriority();
        originalDueDate = task.getDueDate();
        originalAssignee = task.getAssignee();
        originalCreatedBy = task.getCreatedBy();
        
        populateFields();
        
        System.out.println("📝 Editing task: " + task.getTitle());
    }
    
    

    public void setTask(Task task) {
        this.originalTask = task;
        this.workingTask = new Task();
        
        
        workingTask.setId(task.getId());
        workingTask.setTitle(task.getTitle());
        workingTask.setDescription(task.getDescription());
        workingTask.setStatus(task.getStatus());
        workingTask.setPriority(task.getPriority());
        workingTask.setDueDate(task.getDueDate());
        workingTask.setAssignee(task.getAssignee());
        workingTask.setCreatedBy(task.getCreatedBy());
        
        
        originalTitle = task.getTitle();
        originalDescription = task.getDescription();
        originalStatus = task.getStatus();
        originalPriority = task.getPriority();
        originalDueDate = task.getDueDate();
        originalAssignee = task.getAssignee();
        originalCreatedBy = task.getCreatedBy();
        
        populateFields();
        
        System.out.println("📝 Editing task: " + task.getTitle());
    }
    
    

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    

    public void setOnTaskUpdated(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    

    private void populateFields() {
        if (workingTask == null) return;
        
        Platform.runLater(() -> {
            headerLabel.setText("Edit Task: " + originalTask.getTitle());
            titleField.setText(workingTask.getTitle());
            descriptionField.setText(workingTask.getDescription() != null ? workingTask.getDescription() : "");
            statusCombo.setValue(workingTask.getStatus() != null ? workingTask.getStatus() : "To Do");
            priorityCombo.setValue(workingTask.getPriority() != null ? workingTask.getPriority() : "Medium");
            dueDatePicker.setValue(workingTask.getDueDate());
            
            
            if (workingTask.getAssignee() != null) {
                assigneeCombo.setValue(workingTask.getAssignee());
            }
            
            
            if (workingTask.getCreatedBy() != null) {
                createdByCombo.setValue(workingTask.getCreatedBy());
            }
        });
    }
    
    

    private boolean validateForm() {
        String title = titleField.getText().trim();
        String status = statusCombo.getValue();
        String priority = priorityCombo.getValue();
        
        
        validationLabel.setText("");
        
        
        if (title.isEmpty()) {
            validationLabel.setText("⚠️ Task title is required");
            titleField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            titleField.setStyle("");
        }
        
        
        if (status == null || status.isEmpty()) {
            validationLabel.setText("⚠️ Status is required");
            statusCombo.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            statusCombo.setStyle("");
        }
        
        
        if (priority == null || priority.isEmpty()) {
            validationLabel.setText("⚠️ Priority is required");
            priorityCombo.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            priorityCombo.setStyle("");
        }
        
        return true;
    }
    
    

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String status = statusCombo.getValue();
        String priority = priorityCombo.getValue();
        LocalDate dueDate = dueDatePicker.getValue();
        
        
        boolean titleChanged = !title.equals(originalTitle);
        boolean descChanged = !description.equals(originalDescription != null ? originalDescription : "");
        boolean statusChanged = !status.equals(originalStatus != null ? originalStatus : "");
        boolean priorityChanged = !priority.equals(originalPriority != null ? originalPriority : "");
        boolean dueDateChanged = !dueDate.equals(originalDueDate);
        
        User assignee = assigneeCombo.getValue();
        User createdBy = createdByCombo.getValue();
        boolean assigneeChanged = (assignee == null && originalAssignee != null) || 
                                  (assignee != null && !assignee.equals(originalAssignee));
        boolean createdByChanged = (createdBy == null && originalCreatedBy != null) || 
                                   (createdBy != null && !createdBy.equals(originalCreatedBy));
        
        if (!titleChanged && !descChanged && !statusChanged && !priorityChanged && !dueDateChanged && 
            !assigneeChanged && !createdByChanged) {
            System.out.println("ℹ️ No changes detected");
            stage.close();
            return;
        }
        
        try {
            System.out.println("💾 Saving task: " + title);
            System.out.println("   Changes: Title=" + titleChanged + ", Desc=" + descChanged + 
                             ", Status=" + statusChanged + ", Priority=" + priorityChanged + 
                             ", DueDate=" + dueDateChanged + ", Assignee=" + assigneeChanged + 
                             ", CreatedBy=" + createdByChanged);
            
            
            workingTask.setTitle(title);
            workingTask.setDescription(description);
            workingTask.setStatus(status);
            workingTask.setPriority(priority);
            workingTask.setDueDate(dueDate);
            workingTask.setAssignee(assigneeCombo.getValue());
            
            
            if (CurrentUser.isAdmin() && createdByCombo.getValue() != null) {
                workingTask.setCreatedBy(createdByCombo.getValue());
            }
            
            
            taskService.updateTask(workingTask);
            
            
            originalTask.setTitle(title);
            originalTask.setDescription(description);
            originalTask.setStatus(status);
            originalTask.setPriority(priority);
            originalTask.setDueDate(dueDate);
            originalTask.setAssignee(assigneeCombo.getValue());
            if (CurrentUser.isAdmin() && createdByCombo.getValue() != null) {
                originalTask.setCreatedBy(createdByCombo.getValue());
            }
            
            System.out.println("✅ Task saved successfully");
            showAlert("Success", "✅ Task '" + title + "' updated successfully!", Alert.AlertType.INFORMATION);
            
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            
            stage.close();
            
        } catch (Exception e) {
            System.err.println("❌ Error saving task: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to save task: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    

    @FXML
    private void handleCancel() {
        System.out.println("❌ Edit cancelled");
        
        
        if (hasChanges()) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Discard Changes");
            confirmAlert.setHeaderText("You have unsaved changes");
            confirmAlert.setContentText("Do you want to discard your changes and close?");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                System.out.println("↺ Keeping dialog open");
                return; 
            }
        }
        
        stage.close();
    }
    
    

    @FXML
    private void handleUndo() {
        System.out.println("↶ Undoing changes");
        
        
        titleField.setText(originalTitle);
        descriptionField.setText(originalDescription != null ? originalDescription : "");
        statusCombo.setValue(originalStatus != null ? originalStatus : "To Do");
        priorityCombo.setValue(originalPriority != null ? originalPriority : "Medium");
        dueDatePicker.setValue(originalDueDate);
        
        validationLabel.setText("↶ Changes reverted to original");
        validationLabel.setStyle("-fx-text-fill: #3498db;");
        
        
        titleField.setStyle("");
        statusCombo.setStyle("");
        priorityCombo.setStyle("");
    }
    
    

    private boolean hasChanges() {
        String currentTitle = titleField.getText().trim();
        String currentDesc = descriptionField.getText().trim();
        String currentStatus = statusCombo.getValue();
        String currentPriority = priorityCombo.getValue();
        LocalDate currentDueDate = dueDatePicker.getValue();
        
        return !currentTitle.equals(originalTitle) ||
               !currentDesc.equals(originalDescription != null ? originalDescription : "") ||
               !currentStatus.equals(originalStatus != null ? originalStatus : "") ||
               !currentPriority.equals(originalPriority != null ? originalPriority : "") ||
               !currentDueDate.equals(originalDueDate);
    }
    
    

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
