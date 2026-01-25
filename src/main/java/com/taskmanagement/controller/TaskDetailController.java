package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.DateUtils;
import com.taskmanagement.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;

public class TaskDetailController {
    
    // Status & Priority Options
    private static final String[] STATUS_OPTIONS = {"To Do", "In Progress", "Done"};
    private static final String[] PRIORITY_OPTIONS = {"Critical", "High", "Medium", "Low", "None"};
    
    // Messages
    private static final String MSG_INITIALIZED = "TaskDetailController initialized";
    private static final String MSG_SAVING = "Saving task changes";
    private static final String MSG_SAVED = "Task saved successfully";
    private static final String MSG_DELETING = "Deleting task: ";
    private static final String MSG_DELETED = "Task deleted successfully";
    
    // Error Titles & Messages
    private static final String TITLE_VALIDATION_ERROR = "Validation Error";
    private static final String MSG_TITLE_EMPTY = "Task title cannot be empty!";
    private static final String TITLE_ERROR = "Error";
    private static final String TITLE_SUCCESS = "Success";
    private static final String MSG_SAVE_FAILED = "Failed to save task";
    private static final String MSG_DELETE_FAILED = "Failed to delete task";
    private static final String TITLE_DELETE = "Delete Task";
    private static final String MSG_DELETE_CONFIRM = "Are you sure you want to delete '%s'? This action cannot be undone.";
    
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
    private Runnable onSaveCallback;
    private Runnable onDeleteCallback;
    private Stage stage;
    
    public TaskDetailController() {
        this.taskService = new TaskService();
        this.userService = new UserService();
    }
    
    @FXML
    public void initialize() {
        System.out.println(MSG_INITIALIZED);
        
        setupComboBoxes();
        loadUsers();
        setupButtonHandlers();
        setViewMode();
    }
    
    private void setupComboBoxes() {
        statusCombo.setItems(FXCollections.observableArrayList(STATUS_OPTIONS));
        priorityCombo.setItems(FXCollections.observableArrayList(PRIORITY_OPTIONS));
    }
    
    private void setupButtonHandlers() {
        if (editBtn != null) editBtn.setOnAction(e -> handleEdit());
        if (saveBtn != null) saveBtn.setOnAction(e -> handleSave());
        if (cancelBtn != null) cancelBtn.setOnAction(e -> handleCancel());
        if (deleteBtn != null) deleteBtn.setOnAction(e -> handleDelete());
    }
    
    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            ObservableList<User> userList = FXCollections.observableArrayList(users);
            assigneeCombo.setItems(userList);
            createdByCombo.setItems(FXCollections.observableArrayList(users));

            setupUserComboBoxes(assigneeCombo);
            setupUserComboBoxes(createdByCombo);
        } catch (Exception e) {
            System.err.println(" Error loading users: " + e.getMessage());
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
        
        System.out.println("Displaying task details for: " + task.getTitle());
        
        taskIdLabel.setText("Task #" + task.getId());
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription() != null ? task.getDescription() : "");
        statusCombo.setValue(task.getStatus() != null ? task.getStatus() : "To Do");
        priorityCombo.setValue(task.getPriority() != null ? task.getPriority() : "Medium");
        dueDatePicker.setValue(task.getDueDate());
        
        if (task.getAssignee() != null) {
            assigneeCombo.setValue(task.getAssignee());
            assigneeInfoLabel.setText("");
        } else {
            assigneeCombo.setValue(null);
            assigneeInfoLabel.setText("(Unassigned)");
        }
        if (task.getCreatedBy() != null) {
            createdByCombo.setValue(task.getCreatedBy());
            createdByLabel.setText(task.getCreatedBy().getUsername());
        } else {
            createdByLabel.setText("Unknown");
        }
        if (task.getProject() != null) {
            projectLabel.setText(task.getProject().getName());
        } else {
            projectLabel.setText("No Project");
        }
        if (task.getCreatedAt() != null) {
            createdAtLabel.setText(DateUtils.formatDateTime(task.getCreatedAt()));
        } else {
            createdAtLabel.setText("N/A");
        }
    }
    
    private void setViewMode() {
        setMode(false);
    }
    
    private void setEditMode() {
        setMode(true);
    }
    
    private void setMode(boolean editMode) {
        titleField.setEditable(editMode);
        descriptionArea.setEditable(editMode);
        statusCombo.setDisable(!editMode);
        priorityCombo.setDisable(!editMode);
        dueDatePicker.setDisable(!editMode);
        assigneeCombo.setDisable(!editMode);

        boolean isAdmin = CurrentUser.isAdmin();
        createdByCombo.setDisable(!editMode || !isAdmin);
        createdByCombo.setVisible(editMode && isAdmin);
        createdByLabel.setVisible(!editMode || !isAdmin);

        if (editBtn != null) editBtn.setVisible(!editMode);
        if (saveBtn != null) saveBtn.setVisible(editMode);
        if (cancelBtn != null) cancelBtn.setVisible(editMode);
        if (editMode) {
            titleField.requestFocus();
        }
    }
    
    @FXML
    private void handleEdit() {
        System.out.println(MSG_SAVING);
        setEditMode();
    }
    
    @FXML
    private void handleSave() {
        System.out.println("Saving task changes");
        if (titleField.getText().isEmpty()) {
            UIUtils.showError(TITLE_VALIDATION_ERROR, MSG_TITLE_EMPTY);
            return;
        }
        task.setTitle(titleField.getText());
        task.setDescription(descriptionArea.getText());
        task.setStatus(statusCombo.getValue());
        task.setPriority(priorityCombo.getValue());
        task.setDueDate(dueDatePicker.getValue());
        task.setAssignee(assigneeCombo.getValue());

        if (CurrentUser.isAdmin() && createdByCombo.getValue() != null) {
            task.setCreatedBy(createdByCombo.getValue());
        }
        
        try {
            taskService.updateTask(task);
            System.out.println(MSG_SAVED);
            UIUtils.showSuccess(TITLE_SUCCESS, MSG_SAVED);
            setViewMode();
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
        } catch (Exception e) {
            System.err.println(MSG_SAVE_FAILED + e.getMessage());
            UIUtils.showError(TITLE_ERROR, MSG_SAVE_FAILED);
        }
    }
    
    @FXML
    private void handleCancel() {
        System.out.println("Canceling edit mode");

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
        System.out.println(MSG_DELETING + task.getTitle());
        
        String confirmMessage = String.format(MSG_DELETE_CONFIRM, task.getTitle());
        if (UIUtils.showCustomConfirmation(TITLE_DELETE, null, confirmMessage)) {
            try {
                taskService.deleteTask(task.getId());
                System.out.println(MSG_DELETED);
                UIUtils.showSuccess(TITLE_SUCCESS, MSG_DELETED);
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
                if (stage != null) {
                    stage.close();
                }
                
            } catch (Exception e) {
                System.err.println(MSG_DELETE_FAILED + e.getMessage());
                UIUtils.showError(TITLE_ERROR, MSG_DELETE_FAILED);
            }
        }
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }
}
