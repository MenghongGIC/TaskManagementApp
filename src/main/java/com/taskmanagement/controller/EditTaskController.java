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
import com.taskmanagement.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EditTaskController {
    private static final String[] STATUS_VALUES = {"To Do", "In Progress", "Done"};
    private static final String[] PRIORITY_VALUES = {"Critical", "High", "Medium", "Low", "None"};
    private static final String ERROR_BORDER_STYLE = "-fx-border-color: #e74c3c; -fx-border-width: 2;";
    private static final String ERROR_TEXT_COLOR = "-fx-text-fill: #e74c3c;";
    private static final String INFO_TEXT_COLOR = "-fx-text-fill: #3498db;";
    
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
        setupButtonHandlers();
        setupComboBoxes();
        setupTextFields();
        loadUsers();
        validationLabel.setText("");
        validationLabel.setStyle(ERROR_TEXT_COLOR);
    }
    
    private void setupButtonHandlers() {
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
        undoBtn.setOnAction(e -> handleUndo());
    }
    
    private void setupComboBoxes() {
        statusCombo.getItems().addAll(STATUS_VALUES);
        statusCombo.setOnAction(e -> validateForm());
        
        priorityCombo.getItems().addAll(PRIORITY_VALUES);
        priorityCombo.setOnAction(e -> validateForm());
    }
    
    private void setupTextFields() {
        titleField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        descriptionField.setWrapText(true);
        dueDatePicker.setOnAction(e -> validateForm());
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
            System.err.println("Error loading users: " + e.getMessage());
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
        this.stage = stage;
        setTask(task);
    }
    
    public void setTask(Task task) {
        this.originalTask = task;
        this.workingTask = copyTask(task);
        backupOriginalValues(task);
        populateFields();
        System.out.println("📝 Editing task: " + task.getTitle());
    }
    private Task copyTask(Task task) {
        Task copy = new Task();
        copy.setId(task.getId());
        copy.setTitle(task.getTitle());
        copy.setDescription(task.getDescription());
        copy.setStatus(task.getStatus());
        copy.setPriority(task.getPriority());
        copy.setDueDate(task.getDueDate());
        copy.setAssignee(task.getAssignee());
        copy.setCreatedBy(task.getCreatedBy());
        return copy;
    }
    
    private void backupOriginalValues(Task task) {
        originalTitle = task.getTitle();
        originalDescription = task.getDescription();
        originalStatus = task.getStatus();
        originalPriority = task.getPriority();
        originalDueDate = task.getDueDate();
        originalAssignee = task.getAssignee();
        originalCreatedBy = task.getCreatedBy();
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
            descriptionField.setText(getOrEmpty(workingTask.getDescription()));
            statusCombo.setValue(getOrDefault(workingTask.getStatus(), "To Do"));
            priorityCombo.setValue(getOrDefault(workingTask.getPriority(), "Medium"));
            dueDatePicker.setValue(workingTask.getDueDate());
            assigneeCombo.setValue(workingTask.getAssignee());
            createdByCombo.setValue(workingTask.getCreatedBy());

        });
    }
    private String getOrEmpty(String value) {
        return value != null ? value : "";
    }
    
    private String getOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
    private boolean validateForm() {
        validationLabel.setText("");
        
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            setValidationError(titleField, "Task title is required");
            return false;
        }
        clearFieldError(titleField);
        
        String status = statusCombo.getValue();
        if (status == null || status.isEmpty()) {
            setValidationError(statusCombo, "Status is required");
            return false;
        }
        clearFieldError(statusCombo);
        
        String priority = priorityCombo.getValue();
        if (priority == null || priority.isEmpty()) {
            setValidationError(priorityCombo, "Priority is required");
            return false;
        }
        clearFieldError(priorityCombo);
        
        return true;
    }
    
    private void setValidationError(Control control, String message) {
        validationLabel.setText(message);
        control.setStyle(ERROR_BORDER_STYLE);
    }
    
    private void clearFieldError(Control control) {
        control.setStyle("");
    }
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        if (!hasAnyChanges()) {
            System.out.println("No changes detected");
            stage.close();
            return;
        }
        
        try {
            updateTask();
            System.out.println("Task saved successfully");
            showAlert("Success", "Task '" + titleField.getText().trim() + "' updated successfully!", 
                     Alert.AlertType.INFORMATION);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            stage.close();
        } catch (Exception e) {
            System.err.println("Error saving task: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to save task: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void updateTask() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String status = statusCombo.getValue();
        String priority = priorityCombo.getValue();
        LocalDate dueDate = dueDatePicker.getValue();
        User assignee = assigneeCombo.getValue();
        User createdBy = createdByCombo.getValue();
        
        logChanges(title, description, status, priority, dueDate, assignee, createdBy);
        
        // Update working task
        workingTask.setTitle(title);
        workingTask.setDescription(description);
        workingTask.setStatus(status);
        workingTask.setPriority(priority);
        workingTask.setDueDate(dueDate);
        workingTask.setAssignee(assignee);
        
        if (CurrentUser.isAdmin() && createdBy != null) {
            workingTask.setCreatedBy(createdBy);
        }

        taskService.updateTask(workingTask);
        // Update original task reference
        originalTask.setTitle(title);
        originalTask.setDescription(description);
        originalTask.setStatus(status);
        originalTask.setPriority(priority);
        originalTask.setDueDate(dueDate);
        originalTask.setAssignee(assignee);
        if (CurrentUser.isAdmin() && createdBy != null) {
            originalTask.setCreatedBy(createdBy);
        }
    }
    
    private void logChanges(String title, String description, String status, String priority, 
                           LocalDate dueDate, User assignee, User createdBy) {
        boolean titleChanged = !title.equals(originalTitle);
        boolean descChanged = !description.equals(originalDescription != null ? originalDescription : "");
        boolean statusChanged = !status.equals(originalStatus != null ? originalStatus : "");
        boolean priorityChanged = !priority.equals(originalPriority != null ? originalPriority : "");
        boolean dueDateChanged = !dueDate.equals(originalDueDate);
        boolean assigneeChanged = !areUsersEqual(assignee, originalAssignee);
        boolean createdByChanged = !areUsersEqual(createdBy, originalCreatedBy);
        
        System.out.println("💾 Saving task: " + title);
        System.out.println("   Changes: Title=" + titleChanged + ", Desc=" + descChanged + 
                         ", Status=" + statusChanged + ", Priority=" + priorityChanged + 
                         ", DueDate=" + dueDateChanged + ", Assignee=" + assigneeChanged + 
                         ", CreatedBy=" + createdByChanged);
    }
    
    private boolean areUsersEqual(User user1, User user2) {
        return (user1 == null && user2 == null) || (user1 != null && user1.equals(user2));
    }
    
    private boolean hasAnyChanges() {
        String currentTitle = titleField.getText().trim();
        String currentDesc = descriptionField.getText().trim();
        String currentStatus = statusCombo.getValue();
        String currentPriority = priorityCombo.getValue();
        LocalDate currentDueDate = dueDatePicker.getValue();
        User currentAssignee = assigneeCombo.getValue();
        User currentCreatedBy = createdByCombo.getValue();
        
        return !currentTitle.equals(originalTitle) ||
               !currentDesc.equals(originalDescription != null ? originalDescription : "") ||
               !currentStatus.equals(originalStatus != null ? originalStatus : "") ||
               !currentPriority.equals(originalPriority != null ? originalPriority : "") ||
               !currentDueDate.equals(originalDueDate) ||
               !areUsersEqual(currentAssignee, originalAssignee) ||
               !areUsersEqual(currentCreatedBy, originalCreatedBy);
    }
    @FXML
    private void handleCancel() {
        System.out.println(" Edit cancelled");
        
        if (hasAnyChanges()) {
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
        assigneeCombo.setValue(originalAssignee);
        createdByCombo.setValue(originalCreatedBy);
        
        validationLabel.setText("↶ Changes reverted to original");
        validationLabel.setStyle(INFO_TEXT_COLOR);
        
        clearFieldError(titleField);
        clearFieldError(statusCombo);
        clearFieldError(priorityCombo);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        switch (type) {
            case ERROR -> UIUtils.showError(title, message);
            case INFORMATION -> UIUtils.showSuccess(title, message);
            case WARNING -> UIUtils.showWarning(title, message);
            default -> UIUtils.showConfirmation(title, message);
        }
    }
}
