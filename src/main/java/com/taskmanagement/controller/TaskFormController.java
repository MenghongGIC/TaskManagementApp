package com.taskmanagement.controller;

import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.ActivityLogService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.utils.CurrentUser;
import com.taskmanagement.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class TaskFormController {

    @FXML private Label formTitleLabel;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<User> assigneeCombo;
    @FXML private Label messageLabel;

    private final TaskService taskService = new TaskService();
    private final UserService userService = new UserService();

    private Task task;
    private Stage dialogStage;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        // Status values must match database constraint: 'Backlog', 'To Do', 'In Progress', 'Blocked', 'Done'
        statusCombo.getItems().addAll("Backlog", "To Do", "In Progress", "Blocked", "Done");
        // Priority values must match database constraint: 'Critical', 'High', 'Medium', 'Low', 'None'
        priorityCombo.getItems().addAll("None", "Low", "Medium", "High", "Critical");
        statusCombo.setValue("To Do");
        priorityCombo.setValue("Medium");
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        assigneeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user == null ? "Unassigned" : user.getUsername();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        loadUsers();
    }

    public void setTask(Task task) {
        this.task = task;
        if (task == null) {
            if (formTitleLabel != null) {
                formTitleLabel.setText("Create Task");
            }
            return;
        }

        if (formTitleLabel != null) {
            formTitleLabel.setText("Edit Task");
        }

        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription() == null ? "" : task.getDescription());
        statusCombo.setValue(task.getStatus() == null ? "To Do" : task.getStatus());
        priorityCombo.setValue(task.getPriority() == null ? "Medium" : task.getPriority());
        dueDatePicker.setValue(task.getDueDate());
        assigneeCombo.setValue(task.getAssignee());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @FXML
    private void handleSave() {
        String title = titleField.getText() == null ? "" : titleField.getText().trim();

        if (title.isEmpty()) {
            UIUtils.setErrorStyle(messageLabel, "Task title is required");
            messageLabel.setVisible(true);
            return;
        }

        // Validate status and priority are selected
        if (statusCombo.getValue() == null || statusCombo.getValue().isEmpty()) {
            UIUtils.setErrorStyle(messageLabel, "Please select a task status");
            messageLabel.setVisible(true);
            return;
        }

        if (priorityCombo.getValue() == null || priorityCombo.getValue().isEmpty()) {
            UIUtils.setErrorStyle(messageLabel, "Please select a task priority");
            messageLabel.setVisible(true);
            return;
        }

        try {
            if (task == null) {
                Task created = taskService.createTask(title, descriptionField.getText());
                created.setStatus(statusCombo.getValue());
                created.setPriority(priorityCombo.getValue());
                created.setDueDate(dueDatePicker.getValue());
                created.setAssignee(assigneeCombo.getValue());
                taskService.updateTask(created);
                Long assigneeId = created.getAssignee() != null ? created.getAssignee().getId() : null;
                ActivityLogService.logTaskCreated(created.getId(), created.getTitle(), 
                    created.getPriority().toString(), assigneeId);
            } else {
                task.setTitle(title);
                task.setDescription(descriptionField.getText());
                task.setStatus(statusCombo.getValue());
                task.setPriority(priorityCombo.getValue());
                task.setDueDate(dueDatePicker.getValue());
                task.setAssignee(assigneeCombo.getValue());
                taskService.updateTask(task);
                ActivityLogService.logTaskUpdated(task.getId(), task.getTitle(), "Updated from TaskForm");
            }

            if (onSaved != null) {
                onSaved.run();
            }

            if (dialogStage != null) {
                dialogStage.close();
            }
        } catch (Exception e) {
            UIUtils.setErrorStyle(messageLabel, e.getMessage());
            messageLabel.setVisible(true);
        }
    }

    @FXML
    private void handleCancel() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void loadUsers() {
        try {
            assigneeCombo.getItems().setAll(userService.getAllUsers());
        } catch (Exception ignored) {
            User current = CurrentUser.getInstance();
            if (current != null) {
                assigneeCombo.getItems().setAll(current);
                assigneeCombo.setValue(current);
            }
        }
    }
}
