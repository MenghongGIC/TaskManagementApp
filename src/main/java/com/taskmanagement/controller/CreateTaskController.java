package com.taskmanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.stage.Stage;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.UIUtils;

import java.time.LocalDate;
import java.util.List;
public class CreateTaskController {
    
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<User> assigneeCombo;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Label validationLabel;
    
    private TaskService taskService;
    private UserService userService;
    private Stage dialogStage;
    private Project currentProject;
    private Runnable onTaskCreated;
    
    @FXML
    public void initialize() {
        taskService = new TaskService();
        userService = new UserService();
        
        // Setup buttons
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
        
        // Setup status combo
        statusCombo.getItems().addAll("To Do", "In Progress", "Done");
        statusCombo.setValue("To Do");
        
        // Setup priority combo
        priorityCombo.getItems().addAll("Critical", "High", "Medium", "Low", "None");
        priorityCombo.setValue("Medium");
        
        // Setup due date
        dueDatePicker.setValue(LocalDate.now().plusDays(7));
        
        // Setup assignee combo with user display
        setupAssigneeCombo();
        
        // Clear validation label
        validationLabel.setText("");
    }
    
    private void setupAssigneeCombo() {
        try {
            System.out.println("Loading users for assignee dropdown");
            List<User> users = userService.getAllUsers();
            assigneeCombo.getItems().addAll(users);
            
            System.out.println("📋 Users loaded: ");
            for (User u : users) {
                System.out.println("   - ID: " + u.getId() + ", Username: " + u.getUsername());
            }
            
            assigneeCombo.setConverter(new StringConverter<User>() {
                @Override
                public String toString(User user) {
                    return user == null ? "" : user.getUsername();
                }
                
                @Override
                public User fromString(String string) {
                    return null;
                }
            });
            
            // Set default to current user if available
            User currentUser = CurrentUser.getInstance();
            if (currentUser != null) {
                assigneeCombo.setValue(currentUser);
            } else if (!users.isEmpty()) {
                assigneeCombo.setValue(users.get(0));
            }
            
            System.out.println("✅ Loaded " + users.size() + " users");
        } catch (Exception e) {
            System.err.println("❌ Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void setProject(Project project) {
        this.currentProject = project;
    }
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    public void setOnTaskCreated(Runnable callback) {
        this.onTaskCreated = callback;
    }
    public void setCurrentProject(Project project) {
        this.currentProject = project;
    }
    private boolean validateForm() {
        validationLabel.setText("");
        
        String title = titleField.getText().trim();
        String status = statusCombo.getValue();
        String priority = priorityCombo.getValue();
        
        // Title validation
        if (title.isEmpty()) {
            validationLabel.setText("⚠️ Task title is required");
            titleField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            titleField.setStyle("");
        }
        
        // Status validation
        if (status == null || status.isEmpty()) {
            validationLabel.setText("⚠️ Status is required");
            statusCombo.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            statusCombo.setStyle("");
        }
        
        // Priority validation
        if (priority == null || priority.isEmpty()) {
            validationLabel.setText("⚠️ Priority is required");
            priorityCombo.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            return false;
        } else {
            priorityCombo.setStyle("");
        }
        
        // Project validation
        if (currentProject == null || currentProject.getId() == null) {
            validationLabel.setText("⚠️ Project is required");
            return false;
        }
        
        return true;
    }
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String status = statusCombo.getValue();
            String priority = priorityCombo.getValue();
            LocalDate dueDate = dueDatePicker.getValue();
            User assignee = assigneeCombo.getValue();
            
            System.out.println("Creating task: " + title);
            System.out.println("Project: " + currentProject.getName());
            System.out.println("Assignee: " + (assignee != null ? assignee.getUsername() : "Unassigned"));
            
            // Create task via service
            Task newTask = taskService.createTask(
                title,
                description.isEmpty() ? null : description,
                currentProject
            );
            
            // Set priority and status
            if (priority != null) {
                newTask.setPriority(priority);
            }
            if (status != null) {
                newTask.setStatus(status);
            }
            if (dueDate != null) {
                newTask.setDueDate(dueDate);
            }
            
            // Assign user if selected
            if (assignee != null) {
                newTask.setAssignee(assignee);
                taskService.updateTask(newTask);
            }
            
            System.out.println("Task created: " + newTask.getTitle());
            UIUtils.showSuccess("Success", "Task '" + title + "' created successfully!");
            
            // Execute callback to refresh parent view
            if (onTaskCreated != null) {
                onTaskCreated.run();
            }
            
            // Close dialog
            dialogStage.close();
            
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            e.printStackTrace();
            UIUtils.showError("Error", "Failed to create task: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        // Check if there's unsaved data
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        
        if (!title.isEmpty() || !description.isEmpty()) {
            if (UIUtils.showUnsavedChangesConfirmation()) {
                System.out.println("Create task cancelled");
                dialogStage.close();
            }
        } else {
            System.out.println("Create task cancelled");
            dialogStage.close();
        }
    }
    
}
