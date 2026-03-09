package com.taskmanagement.controller;

import com.taskmanagement.model.Project;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.ActivityLogService;
import com.taskmanagement.service.ProjectService;
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
    @FXML private ComboBox<Project> projectCombo;
    @FXML private ComboBox<User> assigneeCombo;
    @FXML private Label messageLabel;

    private final TaskService taskService = new TaskService();
    private final ProjectService projectService = new ProjectService();
    private final UserService userService = new UserService();

    private Task task;
    private Stage dialogStage;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        statusCombo.getItems().addAll("To Do", "In Progress", "Done");
        priorityCombo.getItems().addAll("Low", "Medium", "High", "Critical");
        statusCombo.setValue("To Do");
        priorityCombo.setValue("Medium");
        dueDatePicker.setValue(LocalDate.now().plusDays(7));

        projectCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Project project) {
                return project == null ? "" : project.getName();
            }

            @Override
            public Project fromString(String string) {
                return null;
            }
        });

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

        loadProjects();
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
        projectCombo.setValue(task.getProject());
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
        Project selectedProject = projectCombo.getValue();

        if (title.isEmpty()) {
            UIUtils.setErrorStyle(messageLabel, "Task title is required");
            messageLabel.setVisible(true);
            return;
        }

        if (selectedProject == null) {
            UIUtils.setErrorStyle(messageLabel, "Project is required");
            messageLabel.setVisible(true);
            return;
        }

        try {
            if (task == null) {
                Task created = taskService.createTask(title, descriptionField.getText(), selectedProject);
                created.setStatus(statusCombo.getValue());
                created.setPriority(priorityCombo.getValue());
                created.setDueDate(dueDatePicker.getValue());
                created.setAssignee(assigneeCombo.getValue());
                taskService.updateTask(created);
                ActivityLogService.logTaskCreated(created.getId(), created.getTitle());
            } else {
                task.setTitle(title);
                task.setDescription(descriptionField.getText());
                task.setProject(selectedProject);
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

    private void loadProjects() {
        try {
            projectCombo.getItems().setAll(projectService.getAllProjects());
        } catch (Exception ignored) {
            projectCombo.getItems().clear();
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
